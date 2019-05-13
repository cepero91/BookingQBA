package com.infinitum.bookingqba.view.home;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Date;

public class LocationUtils {
//    /**
//     * Constant used in the location settings dialog.
//     */
//    private static final int REQUEST_CHECK_SETTINGS = 0x1;
//
//    /**
//     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
//     */
//    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
//
//    /**
//     * The fastest rate for active location updates. Exact. Updates will never be more frequent
//     * than this value.
//     */
//    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
//            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
//
//    // Keys for storing activity state in the Bundle.
//    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
//    private final static String KEY_LOCATION = "location";
//    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
//
//    /**
//     * Provides access to the Fused Location Provider API.
//     */
//    private FusedLocationProviderClient mFusedLocationClient;
//
//    /**
//     * Provides access to the Location Settings API.
//     */
//    private SettingsClient mSettingsClient;
//
//    /**
//     * Stores parameters for requests to the FusedLocationProviderApi.
//     */
//    private LocationRequest mLocationRequest;
//
//    /**
//     * Stores the types of location services the client is interested in using. Used for checking
//     * settings to determine if the device has optimal location settings.
//     */
//    private LocationSettingsRequest mLocationSettingsRequest;
//
//    /**
//     * Callback for Location events.
//     */
//    private LocationCallback mLocationCallback;
//
//    /**
//     * Represents a geographical location.
//     */
//    private Location mCurrentLocation;
//
//    private WeakReference<Activity> activityWeakReference;
//
//    public LocationUtils(Activity activity) {
//        this.activityWeakReference = new WeakReference<>(activity);
//    }
//
//    public void initLocation(){
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mSettingsClient = LocationServices.getSettingsClient(this);
//
//        // Kick off the process of building the LocationCallback, LocationRequest, and
//        // LocationSettingsRequest objects.
//        createLocationCallback();
//        createLocationRequest();
//        buildLocationSettingsRequest();
//    }
//
//    private void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//
//    private void createLocationCallback() {
//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//
//                mCurrentLocation = locationResult.getLastLocation();
//            }
//        };
//    }
//
//    private void buildLocationSettingsRequest() {
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(mLocationRequest);
//        mLocationSettingsRequest = builder.build();
//    }
}
