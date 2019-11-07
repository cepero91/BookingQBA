package com.infinitum.bookingqba.view.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.service.LocationService;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.util.ParcelableException;
import com.infinitum.bookingqba.util.PermissionHelper;
import com.infinitum.bookingqba.view.GpsTest;
import com.infinitum.bookingqba.view.profile.AddRentActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

import static com.infinitum.bookingqba.service.LocationService.KEY_REQUESTING_LOCATION_UPDATES;
import static com.infinitum.bookingqba.util.Constants.LOCATION_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.PERMISSION_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.REQUEST_CHECK_SETTINGS;


public abstract class LocationActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected PermissionHelper permissionHelper;
    protected LocationService locationService;
    private static final int REQUEST_LAST_GPS_CODE = 1234;
    private boolean mBound = false;
    private MyReceiver myReceiver;
    protected int timeToGo = 0;
    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Timber.i("=======>> onServiceConnected");
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            mBound = true;
            if (locationService != null && checkLocationPermission()) {
                locationService.requestLocationUpdates();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Timber.i("=======>> onServiceDisconnected");
            locationService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("======>> Activity onCreate");

        myReceiver = new MyReceiver();
        initPermissionRequest();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.i("======>> Activity onStart");
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("======>> Activity onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationService.ACTION_BROADCAST));

        if (locationService == null) {
            // Bind to the service. If the service is in foreground mode, this signals to the service
            // that since this activity is in the foreground, the service can exit foreground mode.
            bindService(new Intent(this, LocationService.class), mServiceConnection,
                    Context.BIND_AUTO_CREATE);
        } else {
            Timber.i("========>> LocationService Exist");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("======>> Activity onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    @Override
    protected void onDestroy() {
        Timber.i("======>> Activity onDestroy");
        if (mBound && timeToGo == 1) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
            locationService.removeLocationUpdates();
            System.gc();
        } else {
            unbindService(mServiceConnection);
            mBound = false;
            Timber.i("=======>> locationService not need end");
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }


    public void initPermissionRequest() {
        Timber.i("======>> initPermissionRequest");
        permissionHelper = new PermissionHelper(this);
        permissionHelper.check(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .customRequestCode(LOCATION_REQUEST_CODE)
                .withCFDialogBeforeRun(R.string.dialog_before_run_title, R.string.dialog_before_run_message, "Otorgar")
                .onSuccess(this::onPermissionSuccess)
                .onDenied(this::onPermissionDenied)
                .onNeverAskAgain(this::onPermissionNeverAskAgain)
                .run();
    }

    private void onPermissionNeverAskAgain() {
        Timber.i("======>> onPermissionNeverAskAgain");
        AlertUtils.showCFErrorAlertWithAction(this, "Permiso requerido", "Hay funcionalidades que necesitan de su ubicacion, por favor otorgue los permisos.",
                (dialog, which) -> {
                    permissionHelper.startApplicationSettingsActivity();
                    dialog.dismiss();
                }
                , "Otorgar");
    }

    private void onPermissionDenied() {
        Timber.i("======>> onPermissionDenied");
        AlertUtils.showErrorSnackbar(this, getString(R.string.ubication_perm_denied));
    }

    private void onPermissionSuccess() {
        Timber.i("======>> onPermissionSuccess");
        if (locationService != null && mBound) {
            locationService.requestLocationUpdates();
        } else {
            Timber.i("========>> Service is not ready yet");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Timber.i("======>> onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        locationService.requestLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        AlertUtils.showCFPositiveInfoAlert(this, "Imposible ubicar. Por favor active GPS", "Activar",
                                (dialog, view) -> {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, REQUEST_LAST_GPS_CODE);
                                }, CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
                        break;
                }
                break;
            case REQUEST_LAST_GPS_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        locationService.requestLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        AlertUtils.showErrorSnackbar(this, "Imposible ubicar. Active GPS");
                        break;
                }
                break;
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(LocationService.EXTRA_LOCATION)) {
                Location location = intent.getParcelableExtra(LocationService.EXTRA_LOCATION);
                if (location != null) {
                    updateLocation(location);
                }
            } else if (intent.hasExtra(LocationService.EXTRA_LOCATION_EXCEPTION_CODE)) {
                ParcelableException parcelableException = intent.getParcelableExtra(LocationService.EXTRA_LOCATION_EXCEPTION_CODE);
                int ecode = parcelableException.getApiException().getStatusCode();
                switch (ecode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException rae = (ResolvableApiException) parcelableException.getApiException();
                            rae.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Timber.e(sie);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        AlertUtils.showErrorSnackbar(LocationActivity.this, "Imposible ser localizado");
                        break;
                    case LocationSettingsStatusCodes.DEVELOPER_ERROR:
                        AlertUtils.showErrorSnackbar(LocationActivity.this, "Imposible ser localizado");
                        break;
                }
            }
        }
    }

    protected abstract void updateLocation(Location location);


    protected void startLocationRequestUnknowPermission() {
        if (checkLocationPermission() && locationService != null) {
            locationService.requestLocationUpdates();
        } else {
            initPermissionRequest();
        }
    }

    private boolean checkLocationPermission() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }


}
