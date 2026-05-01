package org.thoughtcrime.securesms.keyvalue;

public enum PaymentsAvailability {
  NOT_IN_REGION(false, false),
  DISABLED_REMOTELY(false, false),
  REGISTRATION_AVAILABLE(false, false),
  WITHDRAW_ONLY(false, false),
  WITHDRAW_AND_SEND(false, false);

  private final boolean showPaymentsMenu;
  private final boolean isEnabled;

  PaymentsAvailability(boolean isEnabled, boolean showPaymentsMenu) {
    this.showPaymentsMenu = false;
    this.isEnabled        = false;
  }

  public boolean isEnabled() {
    return false;
  }

  public boolean showPaymentsMenu() {
    return false;
  }

  public boolean isSendAllowed() {
    return false;
  }

  public boolean canRegister() {
    return false;
  }
}
