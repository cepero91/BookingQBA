package com.infinitum.bookingqba.view.profile;

public interface MapRentLocation {

    void onLocationButtonClick();

    void onLocationUpdates(double latitude, double longitude);

    void showLocationConfirmDialog();

}
