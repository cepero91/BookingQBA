package com.infinitum.bookingqba.view.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.util.PermissionHelper;
import com.infinitum.bookingqba.view.profile.AddRentActivity;

import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.REQUEST_CHECK_SETTINGS;

public class LocationActivity extends AppCompatActivity {

    protected LocationHelpers locationHelpers;
    protected boolean isLocationRequest;
    protected PermissionHelper permissionHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLocationApi();
        initPermissionRequest();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initLocationApi() {
        isLocationRequest = false;
        locationHelpers = new LocationHelpers(this);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationHelpers.startLocationUpdate(e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        ResolvableApiException rae = (ResolvableApiException) e;
                        rae.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sie) {
                        Timber.e(sie);
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    isLocationRequest = false;
                    AlertUtils.showErrorTopToast(LocationActivity.this, "Imposible ser localizado");
                    break;
                case LocationSettingsStatusCodes.DEVELOPER_ERROR:
                    isLocationRequest = false;
                    AlertUtils.showErrorTopToast(LocationActivity.this, "Imposible ser localizado");
                    break;
            }
        });
    }

    private void stopLocationUpdates() {
        locationHelpers.stopLocationUpdates();
        isLocationRequest = false;
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
                (dialog, which) -> permissionHelper.startApplicationSettingsActivity(),"Otorgar");
    }

    private void onPermissionDenied() {
        AlertUtils.showErrorSnackbar(this,getString(R.string.ubication_perm_denied));
    }

    private void onPermissionSuccess() {
        startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        isLocationRequest = false;
                        AlertUtils.showErrorSnackbarWithAction(this, "Imposible ubicar. Active GPS",
                                v -> {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent,REQUEST_CHECK_SETTINGS);
                                }, "Activar");
                        break;
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        stopLocationUpdates();
        super.onStop();
    }
}
