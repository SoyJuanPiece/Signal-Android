package org.thoughtcrime.securesms.keyvalue

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Currency
import java.util.Optional

class InAppPaymentValues internal constructor(store: KeyValueStore) : SignalStoreValues(store) {

  public override fun onFirstEverAppLaunch() {}

  public override fun getKeysToIncludeInBackup(): List<String> = emptyList()

  val observableRecurringDonationCurrency: Observable<Currency> = BehaviorSubject.createDefault(Currency.getInstance("USD"))
  val observableOneTimeCurrency: Observable<Currency> = BehaviorSubject.createDefault(Currency.getInstance("USD"))
  val observablePendingOneTimeDonation: Observable<Optional<Any>> = BehaviorSubject.createDefault(Optional.empty())

  fun getRecurringDonationCurrency(): Currency = Currency.getInstance("USD")
  fun getOneTimeCurrency(): Currency = Currency.getInstance("USD")
  fun setOneTimeCurrency(currency: Currency) {}
  fun setSubscriber(currencyCode: String, subscriberId: Any?) {}
  fun getSubscriber(currency: Currency): Any? = null
  fun setRecurringDonationCurrency(currency: Currency) {}
  fun getLevelOperation(level: String): Any? = null
  fun setLevelOperation(levelUpdateOperation: Any) {}
  fun clearLevelOperations() {}
  fun setExpiredBadge(badge: Any?) {}
  fun getExpiredBadge(): Any? = null
  fun setExpiredGiftBadge(badge: Any?) {}
  fun getExpiredGiftBadge(): Any? = null
  fun getLastKeepAliveLaunchTime(): Long = 0L
  fun setLastKeepAliveLaunchTime(timestamp: Long) {}
  fun getLastEndOfPeriod(): Long = 0L
  fun setLastEndOfPeriod(timestamp: Long) {}
  fun isLikelyASustainer(): Boolean = false
  fun isDonationSubscriptionManuallyCancelled(): Boolean = false
  fun isBackupSubscriptionManuallyCancelled(): Boolean = false
  fun setDisplayBadgesOnProfile(enabled: Boolean) {}
  fun getDisplayBadgesOnProfile(): Boolean = false
  fun getSubscriptionRedemptionFailed(): Boolean = false
  fun setUnexpectedSubscriptionCancelationChargeFailure(chargeFailure: Any?) {}
  fun getUnexpectedSubscriptionCancelationChargeFailure(): Any? = null
  
  var unexpectedSubscriptionCancelationReason: String? = null
  var unexpectedSubscriptionCancelationTimestamp: Long = 0L
  var unexpectedSubscriptionCancelationWatermark: Long = 0L
  var showMonthlyDonationCanceledDialog: Boolean = false
  var shouldCancelSubscriptionBeforeNextSubscribeAttempt: Boolean = false
  var isGooglePayReady: Boolean = false

  fun updateLocalStateForManualCancellation(subscriberType: Any) {}
  fun updateLocalStateForLocalSubscribe(subscriberType: Any) {}
  fun setSubscriptionPaymentSourceType(paymentSourceType: Any) {}
  fun getSubscriptionPaymentSourceType(): Any? = null
  fun appendToTerminalDonationQueue(terminalDonation: Any) {}
  fun consumeTerminalDonations(): List<Any> = emptyList()
  fun getPendingOneTimeDonation(): Any? = null
  fun setPendingOneTimeDonation(pendingOneTimeDonation: Any?) {}
  fun consumePending3DSData(): Any? = null
  fun consumeVerifiedSubscription3DSData(): Any? = null
  fun setVerifiedSubscription3DSData(stripe3DSData: Any?) {}
}
