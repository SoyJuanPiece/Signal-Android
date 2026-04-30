package org.thoughtcrime.securesms.jobs

import org.signal.core.util.logging.Log
import org.thoughtcrime.securesms.database.SignalDatabase
import org.thoughtcrime.securesms.dependencies.AppDependencies
import org.thoughtcrime.securesms.jobmanager.Job
import org.thoughtcrime.securesms.jobmanager.JsonJobData
import org.thoughtcrime.securesms.jobmanager.impl.NetworkConstraint
import org.thoughtcrime.securesms.util.SupabaseMessageManager
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking

class SupabaseSendJob(private val messageId: Long) : BaseJob(
    Parameters.Builder()
        .setQueue("SupabaseSendJob")
        .addConstraint(NetworkConstraint.KEY)
        .setLifespan(TimeUnit.DAYS.toMillis(1))
        .setMaxAttempts(Parameters.UNLIMITED)
        .build()
) {

    companion object {
        const val KEY = "SupabaseSendJob"
        private val TAG = Log.tag(SupabaseSendJob::class.java)
        private const val KEY_MESSAGE_ID = "message_id"
    }

    override fun serialize(): ByteArray? {
        return JsonJobData.Builder().putLong(KEY_MESSAGE_ID, messageId).serialize()
    }

    override fun getFactoryKey(): String = KEY

    override fun onAdded() {
        SignalDatabase.messages.markAsSending(messageId)
    }

    override fun onRun() {
        val message = SignalDatabase.messages.getOutgoingMessage(messageId)
        val recipient = message.threadRecipient
        val attachment = if (message.attachments.isNotEmpty()) message.attachments[0] else null

        Log.i(TAG, "Sending message $messageId to Supabase with ${message.attachments.size} attachments")

        runBlocking {
            SupabaseMessageManager.sendMessage(recipient, message.body, attachment)
        }

        SignalDatabase.messages.markAsSent(messageId, true)
    }

    override fun onShouldRetry(e: Exception): Boolean = true

    override fun onFailure() {
        SignalDatabase.messages.markAsSentFailed(messageId)
    }

    class Factory : Job.Factory<SupabaseSendJob> {
        override fun create(parameters: Parameters, serializedData: ByteArray?): SupabaseSendJob {
            val data = JsonJobData.deserialize(serializedData)
            return SupabaseSendJob(data.getLong(KEY_MESSAGE_ID))
        }
    }
}
