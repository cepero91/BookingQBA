package com.infinitum.bookingqba.view.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityAddRentBinding;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.view.interaction.OnNavigationBarListener;
import com.infinitum.bookingqba.view.interaction.OnStepFormEnd;
import com.infinitum.bookingqba.view.map.MapFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.LOCATION_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.REQUEST_CHECK_SETTINGS;

public class AddRentActivity extends AppCompatActivity implements HasSupportFragmentInjector,
        StepperLayout.StepperListener, OnStepFormEnd{

    private ActivityAddRentBinding binding;
    private FragmentStepAdapter fragmentStepAdapter;
    //----Location
    private String locationPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private LocationHelpers locationHelpers;
    private boolean isLocationRequest;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_rent);
        AndroidInjection.inject(this);

        isLocationRequest = false;
        initLocationApi();

        fragmentStepAdapter = new FragmentStepAdapter(getSupportFragmentManager(),this);
        binding.stepperLayout.setAdapter(fragmentStepAdapter);
        binding.stepperLayout.setListener(this);
    }

    private void initLocationApi() {
        isLocationRequest = false;
        locationHelpers = new LocationHelpers(this);
        locationHelpers.setLocationCallback(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                updateMapLocation(mCurrentLocation);
            }
        });
    }

    private void updateMapLocation(Location mCurrentLocation) {
        Step step = fragmentStepAdapter.findStep(0);
        if(step!=null){
            ((FirstStepFragment)step).setGeoPointLocation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationHelpers.startLocationUpdate(e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        ResolvableApiException rae = (ResolvableApiException) e;
                        rae.startResolutionForResult(AddRentActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sie) {
                        Timber.e(sie);
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    isLocationRequest = false;
                    AlertUtils.showErrorTopToast(AddRentActivity.this, "Imposible ser localizado");
                    changeIconColor(false);
                    break;
            }
        });
    }

    private void stopLocationUpdates() {
        locationHelpers.stopLocationUpdates();
        isLocationRequest = false;
        changeIconColor(false);
    }

    private void changeIconColor(boolean isLocationRequest) {
        Step step = fragmentStepAdapter.findStep(0);
        if(step!=null){
            ((FirstStepFragment)step).changeIconColor(isLocationRequest);
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    //--------------------------------- PERMISSIONS --------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    checkPermissionResult(permissions, LOCATION_REQUEST_CODE, "Localizacion");
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationRequest = true;
                    changeIconColor(true);
                    startLocationUpdates();
                }
                break;
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
            String msg = String.format("Permiso de %s requerido, por favor otórguelo.", permCodeName);
            showDialog(msg, "Otorgar",
                    (dialog, which) -> {
                        dialog.dismiss();
                        checkSinglePermission(permissions[0], requestCode);
                    }, "No, cerrar", (dialog, which) -> dialog.dismiss(), false);
        } else {
            String msg = "Has negado permanentemente el permiso requerido. Puede que la aplicación no funcione correctamente.";
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

    //------------------------------ STEPPER IMPL -----------------------

    @Override
    public void onCompleted(View completeButton) {
    }

    @Override
    public void onError(VerificationError verificationError) {
        AlertUtils.showErrorTopToast(this,verificationError.getErrorMessage());
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        if(newStepPosition > 0 && isLocationRequest){
            locationHelpers.stopLocationUpdates();
        }
    }

    @Override
    public void onReturn() {
        finish();
    }

    //---------------------- OnStepFormEnd -----------------------

    @Override
    public void onLocationCatch(double latitude, double longitude) {
        Toast.makeText(this, "Location catch", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void submitSecondForm() {

    }

    @Override
    public void barNavigationEnabled(boolean isEnabled) {
        binding.stepperLayout.setNextButtonVerificationFailed(!isEnabled);
        binding.stepperLayout.setCompleteButtonVerificationFailed(!isEnabled);
    }

    @Override
    public void onLocationClick() {
        if (checkSinglePermission(locationPerm, LOCATION_REQUEST_CODE)) {
            if (!isLocationRequest) {
                isLocationRequest = true;
                changeIconColor(true);
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        }
    }
}
