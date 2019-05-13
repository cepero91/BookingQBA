package com.infinitum.bookingqba.view.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
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


import org.jetbrains.annotations.NotNull;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import ru.alexbykov.nopermission.PermissionHelper;
import timber.log.Timber;

public class LocationActivity extends AppCompatActivity {

    //---------------------- PERMISSION ----------------------------//
    private String locationPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private String readPhonePerm = Manifest.permission.READ_PHONE_STATE;

    public static final int PERMISSION_LOCATION_REQUEST_CODE = 1240;
    public static final int PERMISSION_READ_PHONE_REQUEST_CODE = 1241;

    //---------------------- LOCATION ------------------------------//
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationReference locationReference;
    private LocationCallback locationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);

        checkSinglePermission(readPhonePerm, PERMISSION_READ_PHONE_REQUEST_CODE);

        setupLocationApi();

    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            mLatitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", "Latitud",
                    mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", "Longitud",
                    mCurrentLocation.getLongitude()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        mRequestingLocationUpdates = false;
                        Toast.makeText(this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                        invalidateOptionsMenu();
                        break;
                }
                break;
        }
    }

    // -------------------------------- MENU --------------------------------- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        changeMenuIcon(menu.findItem(R.id.action_gps));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_gps:
                initLocation(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLocation(MenuItem item) {
        if (checkSinglePermission(locationPerm, PERMISSION_LOCATION_REQUEST_CODE)) {
            if (!mRequestingLocationUpdates) {
                mRequestingLocationUpdates = true;
                startLocationUpdates();
                changeMenuIcon(item);
            } else {
                stopLocationUpdates();
                changeMenuIcon(item);
            }
        }
    }

    private void changeMenuIcon(MenuItem item) {
        if (mRequestingLocationUpdates) {
            item.setIcon(R.drawable.ic_crosshairs_focus);
        } else {
            item.setIcon(R.drawable.ic_crosshairs);
        }
    }

    // ---------------------- LOCATION METHOD ------------------------ //

    private void setupLocationApi() {
        mRequestingLocationUpdates = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };
        locationReference = new LocationReference(locationCallback);
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
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            locationReference, Looper.myLooper());

                    updateLocationUI();
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Timber.e(sie.getMessage());
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Timber.e("Location settings are inadequate");
                            mRequestingLocationUpdates = false;
                    }
                    updateLocationUI();
                });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationReference)
                .addOnCompleteListener(this, task -> {
                    Timber.e("Location Callback removed");
                    mRequestingLocationUpdates = false;
                });
        mRequestingLocationUpdates = false;
        invalidateOptionsMenu();
    }

    //-------------- CLASE WRAPER EVITA LEAKS CON EL LOCATION CALLBACKS ---------------//

    private static class LocationReference extends LocationCallback{
        private WeakReference<LocationCallback> locationWeakReference;

        public LocationReference(LocationCallback locationCallback) {
            this.locationWeakReference = new WeakReference<>(locationCallback);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if(locationWeakReference != null && locationWeakReference.get() != null){
                locationWeakReference.get().onLocationResult(locationResult);
            }
        }
    }

    //-------------------------------- PERMISOS ----------------------------- //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkPermissionResult(permissions, PERMISSION_LOCATION_REQUEST_CODE, "Localizacion");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mRequestingLocationUpdates = true;
                invalidateOptionsMenu();
                startLocationUpdates();
            }
        } else if (requestCode == PERMISSION_READ_PHONE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkPermissionResult(permissions, PERMISSION_READ_PHONE_REQUEST_CODE, "Estado del Telefono");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso otorgado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkSinglePermission(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            String[] perms = new String[1];
            perms[0] = perm;
            ActivityCompat.requestPermissions(this, perms, requestCode);
            return false;
        }
        return true;
    }

    private void checkPermissionResult(String[] permissions, int requestCode, String permCodeName) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            String msg = String.format("Permiso de %s requerido, por favor otorguelo.", permCodeName);
            showDialog(msg, "Otorgar",
                    (dialog, which) -> {
                        dialog.dismiss();
                        checkSinglePermission(permissions[0], requestCode);
                    }, "No, cerrar", (dialog, which) -> dialog.dismiss(), false);
        } else {
            String msg = "Has negado permanentemente el permiso requerido. Puede que la aplicacion no funcione correctamente. Dirijase a SETTING ==> APLICATION y otorguelo.";
            showDialog(msg, "Ir",
                    (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }, "No, cerrar", (dialog, which) -> dialog.dismiss(), false);
        }
    }

    public void showDialog(String msg, String positiveLabel, DialogInterface.OnClickListener onClickListener,
                           String negativeLevel, DialogInterface.OnClickListener onCancelListener, boolean isCancelable) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aviso!!");
        builder.setMessage(msg);
        builder.setCancelable(isCancelable);
        builder.setPositiveButton(positiveLabel, onClickListener);
        builder.setNegativeButton(negativeLevel, onCancelListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // --------------------- LIFECYCLE ---------------------------//

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            mFusedLocationClient.removeLocationUpdates(locationReference);
        }catch (Exception e){
            Timber.e("onDestroy removeLocationUpdates %s",e.getMessage());
        }
        super.onDestroy();
    }
}
