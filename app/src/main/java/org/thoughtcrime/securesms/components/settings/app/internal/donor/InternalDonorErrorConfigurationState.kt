package org.thoughtcrime.securesms.components.settings.app.internal.donor

import org.signal.donations.StripeDeclineCode
import org.thoughtcrime.securesms.badges.models.Badge

data class InternalDonorErrorConfigurationState(
  val badges: List<Badge> = emptyList(),
  val selectedBadge: Badge? = null,
  val selectedUnexpectedSubscriptionCancellation: UnexpectedSubscriptionCancellation? = null,
  val selectedStripeDeclineCode: StripeDeclineCode.Code? = null
)
