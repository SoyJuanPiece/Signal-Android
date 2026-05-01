package org.thoughtcrime.securesms.components.settings.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.thoughtcrime.securesms.dependencies.AppDependencies
import org.thoughtcrime.securesms.keyvalue.SignalStore
import org.thoughtcrime.securesms.recipients.Recipient
import org.thoughtcrime.securesms.util.TextSecurePreferences
import org.thoughtcrime.securesms.util.livedata.Store

class AppSettingsViewModel : ViewModel() {

  private val store = Store(
    AppSettingsState(
      isPrimaryDevice = SignalStore.account.isPrimaryDevice,
      unreadPaymentsCount = 0,
      hasExpiredGiftBadge = false,
      allowUserToGoToDonationManagementScreen = false,
      userUnregistered = TextSecurePreferences.isUnauthorizedReceived(AppDependencies.application) || !SignalStore.account.isRegistered,
      clientDeprecated = SignalStore.misc.isClientDeprecated
    )
  )

  val state: LiveData<AppSettingsState> = store.stateLiveData
  val self: LiveData<BioRecipientState> = Recipient.self().live().liveData.map { BioRecipientState(it) }

  fun refreshDeprecatedOrUnregistered() {
    store.update {
      it.copy(
        clientDeprecated = SignalStore.misc.isClientDeprecated,
        userUnregistered = TextSecurePreferences.isUnauthorizedReceived(AppDependencies.application) || !SignalStore.account.isRegistered
      )
    }
  }

  fun refresh() {
    store.update {
      it.copy(
        hasExpiredGiftBadge = false,
        backupFailureState = getBackupFailureState()
      )
    }
  }

  private fun getBackupFailureState(): BackupFailureState {
    return when {
      !SignalStore.account.isRegistered || !SignalStore.backup.areBackupsEnabled -> BackupFailureState.NONE
      SignalStore.backup.isNotEnoughRemoteStorageSpace -> BackupFailureState.OUT_OF_STORAGE_SPACE
      SignalStore.backup.hasBackupCreationError -> BackupFailureState.COULD_NOT_COMPLETE_BACKUP
      SignalStore.backup.subscriptionStateMismatchDetected -> BackupFailureState.SUBSCRIPTION_STATE_MISMATCH
      SignalStore.backup.hasBackupAlreadyRedeemedError -> BackupFailureState.ALREADY_REDEEMED
      else -> BackupFailureState.NONE
    }
  }
}
