package com.infinitum.bookingqba.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.rxgps.RxGps;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infinitum.bookingqba.BuildConfig;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.service.LocationForeground;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.ParcelableException;
import com.infinitum.bookingqba.util.PermissionHelper;
import com.infinitum.bookingqba.view.base.LocationActivity;


import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.REQUEST_CHECK_SETTINGS;

public class GpsTest extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    // UI Widgets.
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;


    private static final String TAG = GpsTest.class.getSimpleName();
    private PermissionHelper permissionHelper;

    private LocationForeground mService;
    private boolean mBound;
    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationForeground.LocalBinder binder = (LocationForeground.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };
    private MyReceiver myReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("======>> Activity onCreate");

        initPermissionRequest();

        myReceiver = new MyReceiver();
        setContentView(R.layout.activity_gps_test);

        // Locate the UI widgets.
        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);
        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);

        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.requestLocationUpdates();
            }
        });

        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.removeLocationUpdates();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.i("======>> Activity onStart");
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationForeground.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("======>> Activity onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationForeground.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("======>> Activity onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Timber.i("======>> Activity onStop");
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(LocationForeground.EXTRA_LOCATION)) {
                Location location = intent.getParcelableExtra(LocationForeground.EXTRA_LOCATION);
                if (location != null) {
                    Toast.makeText(GpsTest.this, "Location Changes",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (intent.hasExtra(LocationForeground.EXTRA_LOCATION_EXCEPTION_CODE)) {
                ParcelableException parcelableException = intent.getParcelableExtra(LocationForeground.EXTRA_LOCATION_EXCEPTION_CODE);
                int ecode = parcelableException.getApiException().getStatusCode();
                switch (ecode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException rae = (ResolvableApiException) parcelableException.getApiException();
                            rae.startResolutionForResult(GpsTest.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Timber.e(sie);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        AlertUtils.showErrorSnackbar(GpsTest.this, "Imposible ser localizado");
                        break;
                    case LocationSettingsStatusCodes.DEVELOPER_ERROR:
                        AlertUtils.showErrorSnackbar(GpsTest.this, "Imposible ser localizado");
                        break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CHECK_SETTINGS:
                if(resultCode == Activity.RESULT_OK){
                    mService.requestLocationUpdates();
                }else if(resultCode == Activity.RESULT_CANCELED){
                    AlertUtils.showErrorSnackbar(GpsTest.this, "Imposible ser localizado. Active el GPS");
                }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
// Update the buttons state depending on whether location updates are being requested.

    }

    public void initPermissionRequest() {
        permissionHelper = new PermissionHelper(this);
        permissionHelper.check(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withCFDialogBeforeRun(R.string.dialog_before_run_title, R.string.dialog_before_run_message, "Otorgar")
                .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                .onSuccess(this::onPermissionSuccess)
                .onDenied(this::onPermissionDenied)
                .onNeverAskAgain(this::onPermissionNeverAskAgain)
                .run();
    }

    private void onPermissionNeverAskAgain() {
        AlertUtils.showCFErrorAlertWithAction(this, "Permiso requerido", "Hay funcionalidades que necesitan de su ubicacion, por favor otorgue los permisos.",
                (dialog, which) -> permissionHelper.startApplicationSettingsActivity(), "Otorgar");
    }

    private void onPermissionDenied() {
        AlertUtils.showErrorSnackbar(this, getString(R.string.ubication_perm_denied));
    }

    private void onPermissionSuccess() {
        Timber.i("======>> Permission Success");
//        mService.requestLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //    double roundedLat=(double)Math.round(location.getLatitude()*10000d)/10000d;
//    double roundedLon=(double)Math.round(location.getLongitude()*10000d)/10000d;
//        mLatitudeTextView.setText(String.valueOf(roundedLat));
//        mLongitudeTextView.setText(String.valueOf(roundedLon));


}
