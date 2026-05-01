package org.thoughtcrime.securesms.components.settings.app

import androidx.compose.runtime.Immutable
import org.thoughtcrime.securesms.util.Environment
import org.thoughtcrime.securesms.util.RemoteConfig

@Immutable
data class AppSettingsState(
  val isPrimaryDevice: Boolean,
  val unreadPaymentsCount: Int = 0,
  val hasExpiredGiftBadge: Boolean = false,
  val allowUserToGoToDonationManagementScreen: Boolean = false,
  val userUnregistered: Boolean,
  val clientDeprecated: Boolean,
  val showInternalPreferences: Boolean = RemoteConfig.internalUser,
  val showPayments: Boolean = false,
  val showAppUpdates: Boolean = Environment.IS_NIGHTLY,
  val backupFailureState: BackupFailureState = BackupFailureState.NONE
) {
  fun isRegisteredAndUpToDate(): Boolean {
    return !userUnregistered && !clientDeprecated
  }
}
