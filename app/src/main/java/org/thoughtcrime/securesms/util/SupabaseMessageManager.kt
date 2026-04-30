package org.thoughtcrime.securesms.util

import io.github.jan.tennert.supabase.postgrest.postgrest
import io.github.jan.tennert.supabase.realtime.Realtime
import io.github.jan.tennert.supabase.realtime.realtime
import kotlinx.serialization.Serializable
import org.thoughtcrime.securesms.database.SignalDatabase
import org.thoughtcrime.securesms.dependencies.AppDependencies
import org.thoughtcrime.securesms.mms.OutgoingMessage
import org.thoughtcrime.securesms.recipients.Recipient
import java.util.UUID

import io.github.jan.tennert.supabase.storage.storage
import org.thoughtcrime.securesms.attachments.Attachment
import org.thoughtcrime.securesms.mms.PartAuthority
import java.io.InputStream

@Serializable
data class SupabaseMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender_id: String,
    val receiver_id: String,
    val content: String,
    val attachment_url: String? = null,
    val content_type: String? = null,
    val created_at: Long = System.currentTimeMillis()
)

object SupabaseMessageManager {

    suspend fun sendMessage(recipient: Recipient, text: String, attachment: Attachment? = null) {
        val user = SupabaseHelper.client.gotrue.currentSessionOrNull()?.user ?: return
        var attachmentUrl: String? = null

        if (attachment != null) {
            val bucket = SupabaseHelper.client.storage["attachments"]
            val fileName = "${user.id}/${UUID.randomUUID()}"
            val inputStream: InputStream? = PartAuthority.getAttachmentStream(AppDependencies.application, attachment.uri)
            
            if (inputStream != null) {
                val bytes = inputStream.readBytes()
                bucket.upload(fileName, bytes) {
                    upsert = true
                }
                attachmentUrl = bucket.publicUrl(fileName)
            }
        }
        
        val message = SupabaseMessage(
            sender_id = user.id,
            receiver_id = recipient.requireServiceId().toString(),
            content = text,
            attachment_url = attachmentUrl,
            content_type = attachment?.contentType
        )

        SupabaseHelper.client.postgrest["messages"].insert(message)
    }

    fun startListeningForMessages() {
        val user = SupabaseHelper.client.gotrue.currentSessionOrNull()?.user ?: return
        
        val channel = SupabaseHelper.client.realtime.createChannel("public:messages")
        
        // No es posible filtrar por receiver_id directamente en el cliente Realtime de Supabase v2 fácilmente sin RLS
        // Pero asumiremos que RLS en Supabase se encarga de que solo recibamos nuestros mensajes.
        
        val broadcastFlow = channel.broadcastFlow<SupabaseMessage>("message")
        
        AppDependencies.appForegroundObserver.let { observer ->
            // Aquí deberíamos usar un CoroutineScope de larga duración
        }
    }

    suspend fun fetchNewMessages() {
        val user = SupabaseHelper.client.gotrue.currentSessionOrNull()?.user ?: return
        
        val messages = SupabaseHelper.client.postgrest["messages"]
            .select {
                filter {
                    eq("receiver_id", user.id)
                }
            }
            .decodeList<SupabaseMessage>()

        for (msg in messages) {
            processIncomingMessage(msg)
        }
    }

    private fun processIncomingMessage(msg: SupabaseMessage) {
        val context = AppDependencies.application
        val recipient = Recipient.externalPush(context, msg.sender_id)
        val threadId = SignalDatabase.threads.getOrCreateThreadIdFor(recipient)
        
        SignalDatabase.messages.insertMessageInbox(
            msg.content,
            recipient,
            msg.created_at,
            threadId,
            false
        )
        
        // Opcional: Borrar de Supabase tras recibir
        // SupabaseHelper.client.postgrest["messages"].delete { filter { eq("id", msg.id) } }
    }
}
