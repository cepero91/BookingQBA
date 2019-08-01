package com.infinitum.bookingqba.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

public class LocationHelpers {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationReference locationReference;
    private boolean locationRunning;

    private Context context;

    public LocationHelpers(Context context) {
        this.context = context;
        setupLocationApi();
        locationRunning = false;
    }

    private void setupLocationApi() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);
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
    public void startLocationUpdate(OnFailureListener onFailureListener, Activity activity){
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(activity, locationSettingsResponse -> {
                    locationRunning = true;
                    AlertUtils.showSuccessLocationToast(activity,"Ubicando...");
                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            locationReference, Looper.myLooper());

                })
                .addOnFailureListener(onFailureListener);
    }

    @SuppressLint("MissingPermission")
    public void startForegroundLocationUpdate(OnFailureListener onFailureListener){
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    locationRunning = true;
                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            locationReference, Looper.myLooper());

                })
                .addOnFailureListener(onFailureListener);
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationReference);
        locationRunning = false;
    }

    public boolean isLocationRunning() {
        return locationRunning;
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

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            if (locationWeakReference != null && locationWeakReference.get() != null) {
                locationWeakReference.get().onLocationAvailability(locationAvailability);
            }
        }
    }


    public String getAddressByLocation(Location location){
        StringBuilder result = new StringBuilder();
        try{
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLatitude(),1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
            }
        }catch (IOException e){
            Timber.e(e);
        }
        return result.toString();
    }

    public List<Address> getAddressByNominatimApi(Location location){
        GeocodeNominatim geocodeNominatim = new GeocodeNominatim(context,Locale.getDefault());
        if (!GeocodeNominatim.isPresent()) { return new ArrayList<>(); }
        try
        {
            List<Address> result = geocodeNominatim.getFromLocation(location.getLatitude(),location.getLongitude(),10);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Location getLastLocation(){
        final Location[] locationResult = {null};
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            locationResult[0] = task.getResult();
                        } else {
                            Timber.e("Failed to get location.");
                        }
                    });
        } catch (SecurityException unlikely) {
            Timber.e("Lost location permission." + unlikely);
        }
        return locationResult[0];
    }
}
