package com.infinitum.bookingqba.view.interaction;

public interface OnStepFormEnd {
    void barNavigationEnabled(boolean isEnabled);
    void onLocationClick();
    void onLocationCatch(double latitude, double longitude);
    void submitSecondForm(String address, String referenceZone, String municipality);
}
