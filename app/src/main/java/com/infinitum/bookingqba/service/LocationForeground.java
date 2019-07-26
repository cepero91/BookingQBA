package com.infinitum.bookingqba.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.util.ParcelableException;
import com.infinitum.bookingqba.view.GpsTest;
import com.infinitum.bookingqba.view.base.LocationActivity;
import com.infinitum.bookingqba.view.home.HomeActivity;


import dagger.android.AndroidInjection;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.REQUEST_CHECK_SETTINGS;

public class LocationForeground extends Service {

    private static final String PACKAGE_NAME =
            "com.infinitum.bookingqba";

    private static final String TAG = LocationForeground.class.getSimpleName();

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";

    public static final String EXTRA_LOCATION_EXCEPTION_CODE = PACKAGE_NAME + ".ecode";

    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    private static final String CHANNEL_ID = "channel_01";

    private static final int NOTIFICATION_ID = 12345678;

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

    LocationHelpers locationHelpers;
    SharedPreferences sharedPreferences;

    private Location currentLocation;
    private Location lastLocation;

    private NotificationManager notificationManager;
    private Handler serviceHandler;
    private boolean changingConfiguration;

    private final IBinder mBinder = new LocalBinder();


    public LocationForeground() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.i("=========>> Service onCreate");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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

        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("=======>> Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Timber.i("========>> Service in onBind()");
        stopForeground(true);
        changingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Timber.i("=========>> Service in onRebind()");
        stopForeground(true);
        changingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Timber.i("========>> Service Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!changingConfiguration && sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)) {
            Timber.i("========>> Service Starting foreground service");

            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        serviceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "========>> Service Requesting location updates");
        sharedPreferences.edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, true).apply();
        startService(new Intent(getApplicationContext(), LocationForeground.class));
        try {
            locationHelpers.startForegroundLocationUpdate(e -> {
                ParcelableException parcelableException = new ParcelableException((ApiException) e);
                // Notify anyone listening for broadcasts about the new location.
                Intent intent = new Intent(ACTION_BROADCAST);
                intent.putExtra(EXTRA_LOCATION_EXCEPTION_CODE, parcelableException);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            });
        } catch (SecurityException unlikely) {
            sharedPreferences.edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, false).apply();
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "=========>> Service Removing location updates");
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

        if (lastLocation == null) {
            lastLocation = currentLocation;
            // Notify anyone listening for broadcasts about the new location.
            notifyNewLocation(location);
        } else if (lastLocation.distanceTo(currentLocation) > 0.0) {
            lastLocation = currentLocation;
            notifyNewLocation(location);
        } else {
            Timber.i("Location is the same");
        }

    }

    private void notifyNewLocation(Location location) {
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            notificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    private void getLastLocation() {
        lastLocation = locationHelpers.getLastLocation();
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent(this, LocationForeground.class);

        CharSequence text = currentLocation == null ? "Unknown location" :
                "(" + currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + ")";

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GpsTest.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .addAction(R.drawable.ic_bandcamp_action, "Quiero ver",
                        activityPendingIntent)
                .addAction(R.drawable.ic_bandcamp_action, "Cerrar",
                        servicePendingIntent)
                .setContentText(text)
                .setContentTitle("BookingQBA")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationForeground getService() {
            return LocationForeground.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

}
