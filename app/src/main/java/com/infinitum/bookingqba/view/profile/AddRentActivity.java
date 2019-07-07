package com.infinitum.bookingqba.view.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityAddRentBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.view.interaction.OnStepFormEnd;
import com.infinitum.bookingqba.view.profile.dialogitem.FormSelectorItem;
import com.infinitum.bookingqba.view.profile.dialogitem.SearchableSelectorModel;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.wshunli.assets.CopyAssets;
import com.wshunli.assets.CopyCreator;
import com.wshunli.assets.CopyListener;

import org.oscim.backend.CanvasAdapter;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.LOCATION_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.MAP_PATH;
import static com.infinitum.bookingqba.util.Constants.REQUEST_CHECK_SETTINGS;

public class AddRentActivity extends AppCompatActivity implements HasSupportFragmentInjector,
        MapRentLocation, View.OnClickListener {

    private ActivityAddRentBinding binding;
    //----Location
    private String locationPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private LocationHelpers locationHelpers;
    private boolean isLocationRequest;

    private RentFormObject rentFormObject;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    SharedPreferences sharedPreferences;

    private Location mCurrentLocation;

    private RentViewModel rentViewModel;
    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private String mapFilePath;
    private MarkerSymbol userMarker;
    private ItemizedLayer<MarkerItem> mMarkerLayer;
    private FirstStepFragment mapFragment;

    //-------- Boolean field for panel
    private boolean formLocationOpen = true;
    private boolean formMunicipalityOpen = true;
    private boolean formEsentialOpen = true;
    private boolean formCapabilityOpen = true;
    private boolean formFinallyOpen = true;


    //--------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_rent);
        AndroidInjection.inject(this);

        binding.flLocationBar.setOnClickListener(this);
        binding.flMunicipalityBar.setOnClickListener(this);
        binding.flEsentialBar.setOnClickListener(this);
        binding.flCapabilityBar.setOnClickListener(this);
        binding.flFinallyBar.setOnClickListener(this);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        compositeDisposable = new CompositeDisposable();

        rentFormObject = new RentFormObject();

        isLocationRequest = false;
        initLocationApi();

        binding.slidingLayout.setTouchEnabled(false);

        binding.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        mapFragment = FirstStepFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.map_content,
                mapFragment).commit();

    }


    @Override
    protected void onDestroy() {
        Timber.e("Activity onDestroy");
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if ((binding.slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || binding.slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            binding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            if(isLocationRequest) {
                locationHelpers.stopLocationUpdates();
                isLocationRequest = false;
            }
        } else {
            super.onBackPressed();
        }
    }

    //------------------------------------- LOCATION API ------------------------------------
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
        if (mapFragment != null) {
            mapFragment.setGeoPointLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
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
        if (mapFragment != null) {
            mapFragment.changeIconColor(isLocationRequest);
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
        switch (requestCode) {
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
            String msg = String.format(getString(R.string.perm_need_msg), permCodeName);
            showDialog(msg, "Otorgar",
                    (dialog, which) -> {
                        dialog.dismiss();
                        checkSinglePermission(permissions[0], requestCode);
                    }, "No, cerrar", (dialog, which) -> dialog.dismiss(), false);
        } else {
            String msg = getString(R.string.perm_denied_msg);
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

    @Override
    public void onLocationButtonClick() {
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

    @Override
    public void onLocationUpdates(double latitude, double longitude) {
        rentFormObject.setLatitude(latitude);
        rentFormObject.setLongitude(longitude);
        binding.tvLatitude.setText(String.format("Lat: %.5f",latitude));
        binding.tvLongitude.setText(String.format("Lon: %.5f",longitude));
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
        // Title and message
        builder.setTitle("Renta localizada!!");
        builder.setMessage("Coordenadas obtenidas con exito");
        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.addButton("Cerrar mapa", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, (dialog, which) ->{
            dialog.dismiss();
            onBackPressed();
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_location_bar:
                if(formLocationOpen){
                    binding.llLocationForm.setVisibility(View.GONE);
                    binding.ivArrowLocation.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    formLocationOpen = false;
                }else{
                    binding.llLocationForm.setVisibility(View.VISIBLE);
                    binding.ivArrowLocation.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    formLocationOpen = true;
                }
                break;
            case R.id.fl_municipality_bar:
                if(formMunicipalityOpen){
                    binding.llMunicipalityForm.setVisibility(View.GONE);
                    binding.ivArrowMunicipality.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    formMunicipalityOpen = false;
                }else{
                    binding.llMunicipalityForm.setVisibility(View.VISIBLE);
                    binding.ivArrowMunicipality.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    formMunicipalityOpen = true;
                }
                break;
            case R.id.fl_esential_bar:
                if (formEsentialOpen) {
                    binding.llEsentialForm.setVisibility(View.GONE);
                    binding.ivArrowEsential.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    formEsentialOpen = false;
                } else {
                    binding.llEsentialForm.setVisibility(View.VISIBLE);
                    binding.ivArrowEsential.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    formEsentialOpen = true;
                }
                break;
            case R.id.fl_capability_bar:
                if (formCapabilityOpen) {
                    binding.llCapabilityForm.setVisibility(View.GONE);
                    binding.ivArrowCapability.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    formCapabilityOpen = false;
                } else {
                    binding.llCapabilityForm.setVisibility(View.VISIBLE);
                    binding.ivArrowCapability.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    formCapabilityOpen = true;
                }
                break;
            case R.id.fl_finally_bar:
                if (formFinallyOpen) {
                    binding.llFinallyForm.setVisibility(View.GONE);
                    binding.ivArrowFinally.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    formFinallyOpen = false;
                } else {
                    binding.llFinallyForm.setVisibility(View.VISIBLE);
                    binding.ivArrowFinally.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    formFinallyOpen = true;
                }
                break;
        }
    }


}
