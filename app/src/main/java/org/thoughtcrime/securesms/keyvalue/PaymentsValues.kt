package org.thoughtcrime.securesms.keyvalue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.Currency

class PaymentsValues internal constructor(store: KeyValueStore) : SignalStoreValues(store) {

  var paymentLock: Boolean = false
  var paymentLockTimestamp: Long = 0
  var paymentLockSkipCount: Int = 0
  var showSaveRecoveryPhrase: Boolean = false
  var userConfirmedMnemonic: Boolean = false

  public override fun onFirstEverAppLaunch() {}

  public override fun getKeysToIncludeInBackup(): List<String> = emptyList()

  fun confirmMnemonic(confirmed: Boolean) {}

  fun mobileCoinPaymentsEnabled(): Boolean = false

  val paymentsAvailability: PaymentsAvailability = PaymentsAvailability.NOT_IN_REGION

  fun setMobileCoinPaymentsEnabled(isMobileCoinPaymentsEnabled: Boolean) {}

  fun hasPaymentsEntropy(): Boolean = false

  fun liveMobileCoinBalance(): LiveData<Any?> = MutableLiveData(null)

  fun setCurrentCurrency(currentCurrency: Currency) {}

  fun currentCurrency(): Currency = Currency.getInstance("USD")

  fun liveCurrentCurrency(): MutableLiveData<Currency> = MutableLiveData(Currency.getInstance("USD"))

  fun setEnclaveFailure(failure: Boolean) {}

  fun enclaveFailure(): LiveData<Boolean> = MutableLiveData(false)

  fun showAboutMobileCoinInfoCard(): Boolean = false

  fun showAddingToYourWalletInfoCard(): Boolean = false

  fun showCashingOutInfoCard(): Boolean = false

  fun isMnemonicConfirmed(): Boolean = false

  fun showUpdatePinInfoCard(): Boolean = false

  fun dismissAboutMobileCoinInfoCard() {}

  fun dismissAddingToYourWalletInfoCard() {}

  fun dismissCashingOutInfoCard() {}

  fun dismissRecoveryPhraseInfoCard() {}

  fun dismissUpdatePinInfoCard() {}

  fun setMobileCoinFullLedger(ledger: Any?) {}

  fun setEnabledAndEntropy(enabled: Boolean, entropy: Any?) {}
}

enum class PaymentsAvailability {
    NOT_IN_REGION,
    WITHDRAW_AND_SEND,
    WITHDRAW_ONLY,
    REGISTRATION_AVAILABLE,
    DISABLED_REMOTELY;

    fun showPaymentsMenu(): Boolean = false
    val isSendAllowed: Boolean = false
}
