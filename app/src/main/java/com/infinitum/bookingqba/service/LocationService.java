package com.infinitum.bookingqba.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.util.ParcelableException;

import java.sql.Time;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

public class LocationService extends Service {

    private static final String PACKAGE_NAME =
            "com.infinitum.bookingqba";

    private static final String TAG = LocationService.class.getSimpleName();

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";

    public static final String EXTRA_ADDRESS = PACKAGE_NAME + ".address";

    public static final String EXTRA_LOCATION_EXCEPTION_CODE = PACKAGE_NAME + ".ecode";

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

    LocationHelpers locationHelpers;

    SharedPreferences sharedPreferences;

    private Location currentLocation;
    private Location lastLocation;
    private IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.i("=========>> Service onCreate");

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        locationHelpers = new LocationHelpers(this);

        locationHelpers.setLocationCallback(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("========>> onStartCommand()");
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Timber.i("========>> Service in onBind()");
        return mBinder;
    }


    @Override
    public void onRebind(Intent intent) {
        Timber.i("========>> Service in onRebind()");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Timber.i("========>> Service in onUnbind()");
        return true;
    }

    @Override
    public void onDestroy() {
        Timber.i("========>> Service in onDestroy()");
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Timber.i("========>> Service Requesting location updates");
        if (!locationHelpers.isLocationRunning()) {
            sharedPreferences.edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, true).apply();
            startService(new Intent(getApplicationContext(), LocationService.class));
            try {
                locationHelpers.startForegroundLocationUpdate(e -> {
                    sharedPreferences.edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, false).apply();
                    ParcelableException parcelableException = new ParcelableException((ApiException) e);
                    // Notify anyone listening for broadcasts about the new location.
                    Intent intent = new Intent(ACTION_BROADCAST);
                    intent.putExtra(EXTRA_LOCATION_EXCEPTION_CODE, parcelableException);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                });

            } catch (SecurityException unlikely) {
                sharedPreferences.edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, false).apply();
                Timber.e("Lost location permission. Could not request updates. " + unlikely);
            }
        }else{
            Timber.i("========>> Service not need location request, already is running ");
        }
    }


    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Timber.i("=========>> Service Removing location updates");
        try {
            locationHelpers.stopLocationUpdates();
            sharedPreferences.edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, false).apply();
            stopSelf();
        } catch (SecurityException unlikely) {
            sharedPreferences.edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, true).apply();
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }


    private void onNewLocation(Location location) {
        Timber.i("New location: %s", location);
        currentLocation = location;
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, currentLocation);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
