package com.infinitum.bookingqba.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.infinitum.bookingqba.view.home.HomeActivity;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class LocationHelpers {
    private WeakReference<Activity> activityWeakReference;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationReference locationReference;

    public LocationHelpers(Activity activityWeakReference) {
        this.activityWeakReference = new WeakReference<>(activityWeakReference);
        setupLocationApi();
    }

    private void setupLocationApi() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activityWeakReference.get());
        mSettingsClient = LocationServices.getSettingsClient(activityWeakReference.get());
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    public void setLocationCallback(LocationCallback locationCallback){
        this.locationCallback = locationCallback;
        this.locationReference = new LocationReference(this.locationCallback);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdate(OnFailureListener onFailureListener){
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(activityWeakReference.get(), locationSettingsResponse -> {
                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            locationReference, Looper.myLooper());

                })
                .addOnFailureListener(onFailureListener);
    }


    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationReference)
                .addOnCompleteListener(activityWeakReference.get(), task -> {
                    Timber.e("Location Callback removed");
                });
    }


    private static class LocationReference extends LocationCallback {
        private WeakReference<LocationCallback> locationWeakReference;

        private LocationReference(LocationCallback locationCallback) {
            this.locationWeakReference = new WeakReference<>(locationCallback);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationWeakReference != null && locationWeakReference.get() != null) {
                locationWeakReference.get().onLocationResult(locationResult);
            }
        }
    }
}
