package org.thoughtcrime.securesms.util

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.jan.tennert.supabase.SupabaseClient
import io.github.jan.tennert.supabase.gotrue.Auth
import io.github.jan.tennert.supabase.postgrest.Postgrest
import io.github.jan.tennert.supabase.postgrest.from
import io.github.jan.tennert.supabase.postgrest.query.Columns
import io.github.jan.tennert.supabase.realtime.Realtime
import io.github.jan.tennert.supabase.realtime.realtime
import kotlinx.serialization.Serializable
import org.signal.core.util.logging.Log
import org.thoughtcrime.securesms.database.SignalDatabase
import org.thoughtcrime.securesms.dependencies.AppDependencies
import org.thoughtcrime.securesms.recipients.Recipient
import org.thoughtcrime.securesms.recipients.RecipientId
import java.util.UUID

@Serializable
data class UserSettings(
    val user_id: String,
    val read_receipts: Boolean = true,
    val typing_indicators: Boolean = true,
    val blocked_users: List<String> = emptyList()
)

object SupabaseUserSettings {
    private val TAG = Log.tag(SupabaseUserSettings::class.java)

    private val _liveUserSettings = MutableLiveData<UserSettings>()
    val liveUserSettings: LiveData<UserSettings> = _liveUserSettings

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var preferences: SharedPreferences

    fun init(context: Context, client: SupabaseClient) {
        this.supabaseClient = client
        this.preferences = context.getSharedPreferences("supabase_user_settings", Context.MODE_PRIVATE)
        
        // Load cached settings
        loadCachedSettings()

        // Start listening for changes (Realtime)
        startListeningForChanges()
    }

    private fun loadCachedSettings() {
        val userId = supabaseClient.gotrue.currentSessionOrNull()?.user?.id ?: return
        val cachedReadReceipts = preferences.getBoolean("read_receipts_$userId", true)
        val cachedTypingIndicators = preferences.getBoolean("typing_indicators_$userId", true)
        val cachedBlockedUsers = preferences.getStringSet("blocked_users_$userId", emptySet())?.toList() ?: emptyList()
        _liveUserSettings.postValue(UserSettings(userId, cachedReadReceipts, cachedTypingIndicators, cachedBlockedUsers))
    }

    private fun cacheSettings(settings: UserSettings) {
        preferences.edit().apply {
            putBoolean("read_receipts_${settings.user_id}", settings.read_receipts)
            putBoolean("typing_indicators_${settings.user_id}", settings.typing_indicators)
            putStringSet("blocked_users_${settings.user_id}", settings.blocked_users.toSet())
            apply()
        }
    }

    suspend fun fetchSettings(userId: String): UserSettings {
        val settings = supabaseClient.from("user_settings").select(Columns.List("user_id", "read_receipts", "typing_indicators", "blocked_users")) {
            filter {
                eq("user_id", userId)
            }
        }.decodeSingleOrNull<UserSettings>()

        if (settings == null) {
            // Create default settings if they don't exist
            val defaultSettings = UserSettings(userId)
            supabaseClient.from("user_settings").insert(defaultSettings)
            cacheSettings(defaultSettings)
            _liveUserSettings.postValue(defaultSettings)
            return defaultSettings
        } else {
            cacheSettings(settings)
            _liveUserSettings.postValue(settings)
            return settings
        }
    }

    private fun startListeningForChanges() {
        val userId = supabaseClient.gotrue.currentSessionOrNull()?.user?.id ?: return
        supabaseClient.realtime.createChannel("public:user_settings")
            .onPostgresChanges(schema = "public", table = "user_settings") { payload ->
                if (payload.eventType == "UPDATE" && payload.newRecord?.get("user_id") == userId) {
                    val updatedSettings = payload.newRecord.decodeFromJsonElement(UserSettings.serializer())
                    cacheSettings(updatedSettings)
                    _liveUserSettings.postValue(updatedSettings)
                    Log.i(TAG, "User settings updated via Realtime: $updatedSettings")
                }
            }
            .subscribe()
    }

    suspend fun updateReadReceipts(enabled: Boolean) {
        val userId = supabaseClient.gotrue.currentSessionOrNull()?.user?.id ?: return
        supabaseClient.from("user_settings").update(mapOf("read_receipts" to enabled)) {
            filter { eq("user_id", userId) }
        }
    }

    suspend fun updateTypingIndicators(enabled: Boolean) {
        val userId = supabaseClient.gotrue.currentSessionOrNull()?.user?.id ?: return
        supabaseClient.from("user_settings").update(mapOf("typing_indicators" to enabled)) {
            filter { eq("user_id", userId) }
        }
    }

    suspend fun blockUser(blockedUserId: String) {
        val userId = supabaseClient.gotrue.currentSessionOrNull()?.user?.id ?: return
        val currentSettings = _liveUserSettings.value ?: fetchSettings(userId)
        val updatedBlockedUsers = currentSettings.blocked_users + blockedUserId
        supabaseClient.from("user_settings").update(mapOf("blocked_users" to updatedBlockedUsers)) {
            filter { eq("user_id", userId) }
        }
    }

    suspend fun unblockUser(unblockedUserId: String) {
        val userId = supabaseClient.gotrue.currentSessionOrNull()?.user?.id ?: return
        val currentSettings = _liveUserSettings.value ?: fetchSettings(userId)
        val updatedBlockedUsers = currentSettings.blocked_users.filter { it != unblockedUserId }
        supabaseClient.from("user_settings").update(mapOf("blocked_users" to updatedBlockedUsers)) {
            filter { eq("user_id", userId) }
        }
    }

    fun isReadReceiptsEnabled(): Boolean {
        return _liveUserSettings.value?.read_receipts ?: true
    }

    fun isTypingIndicatorsEnabled(): Boolean {
        return _liveUserSettings.value?.typing_indicators ?: true
    }

    fun isUserBlocked(targetUserId: String): Boolean {
        return _liveUserSettings.value?.blocked_users?.contains(targetUserId) ?: false
    }

    // Adaptar esto para que funcione con el RecipientId de Signal
    fun isRecipientBlocked(recipientId: RecipientId): Boolean {
        val recipient = SignalDatabase.recipients.getRecipient(recipientId)
        val targetUserId = recipient?.requireServiceId()?.toString() ?: return false
        return isUserBlocked(targetUserId)
    }

    // Placeholder for now, will need to be properly implemented with Supabase
    fun getKeepMessagesDuration(): org.thoughtcrime.securesms.database.model.KeepMessagesDuration {
        return org.thoughtcrime.securesms.database.model.KeepMessagesDuration.THIRTY_DAYS
    }
}