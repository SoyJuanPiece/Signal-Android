package org.thoughtcrime.securesms.components.settings.app.privacy

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.thoughtcrime.securesms.dependencies.AppDependencies
import org.thoughtcrime.securesms.keyvalue.SignalStore
import org.thoughtcrime.securesms.util.SupabaseUserSettings
import kotlinx.coroutines.BuildersKt
import org.thoughtcrime.securesms.util.TextSecurePreferences
import org.thoughtcrime.securesms.util.livedata.Store

class PrivacySettingsViewModel(
  private val sharedPreferences: SharedPreferences,
  private val repository: PrivacySettingsRepository
) : ViewModel() {

  private val store = Store(getState())

  val state: LiveData<PrivacySettingsState> = store.stateLiveData

  fun refreshBlockedCount() {
    repository.getBlockedCount { count ->
      store.update { it.copy(blockedCount = count) }
      refresh()
    }
  }

  fun setReadReceiptsEnabled(enabled: Boolean) {
    BuildersKt.runBlocking(kotlinx.coroutines.EmptyCoroutineContext.INSTANCE,
                             { scope, continuation -> SupabaseUserSettings.INSTANCE.updateReadReceipts(enabled, continuation) })
    repository.syncReadReceiptState()
    refresh()
  }

  fun setTypingIndicatorsEnabled(enabled: Boolean) {
    BuildersKt.runBlocking(kotlinx.coroutines.EmptyCoroutineContext.INSTANCE,
                             { scope, continuation -> SupabaseUserSettings.INSTANCE.updateTypingIndicators(enabled, continuation) })
    repository.syncTypingIndicatorsState()
    refresh()
  }

  fun setScreenSecurityEnabled(enabled: Boolean) {
    sharedPreferences.edit().putBoolean(TextSecurePreferences.SCREEN_SECURITY_PREF, enabled).apply()
    refresh()
  }

  fun setIncognitoKeyboard(enabled: Boolean) {
    sharedPreferences.edit().putBoolean(TextSecurePreferences.INCOGNITO_KEYBOARD_PREF, enabled).apply()
    refresh()
  }





  fun refresh() {
    store.update(this::updateState)
  }

  private fun getState(): PrivacySettingsState {
    return PrivacySettingsState(
      blockedCount = 0,
      readReceipts = SupabaseUserSettings.INSTANCE.isReadReceiptsEnabled(),
      typingIndicators = SupabaseUserSettings.INSTANCE.isTypingIndicatorsEnabled(),
      screenLock = false,
      screenLockActivityTimeout = 0L,
      screenSecurity = TextSecurePreferences.isScreenSecurityEnabled(AppDependencies.application),
      incognitoKeyboard = TextSecurePreferences.isIncognitoKeyboardEnabled(AppDependencies.application),
      paymentLock = false,
      isObsoletePasswordEnabled = false,
      isObsoletePasswordTimeoutEnabled = false,
      obsoletePasswordTimeout = 0,
      universalExpireTimer = 0
    )
  }

  private fun updateState(state: PrivacySettingsState): PrivacySettingsState {
    return getState().copy(blockedCount = state.blockedCount)
  }

  class Factory(
    private val sharedPreferences: SharedPreferences,
    private val repository: PrivacySettingsRepository
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return requireNotNull(modelClass.cast(PrivacySettingsViewModel(sharedPreferences, repository)))
    }
  }
}
