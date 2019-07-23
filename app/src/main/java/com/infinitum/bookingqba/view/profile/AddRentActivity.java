package com.infinitum.bookingqba.view.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityAddRentBinding;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.view.profile.dialogitem.FormSelectorItem;
import com.infinitum.bookingqba.view.profile.dialogitem.SearchableSelectorModel;
import com.infinitum.bookingqba.view.profile.uploaditem.AmenitiesRentFormObject;
import com.infinitum.bookingqba.view.profile.uploaditem.RentFormObject;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import timber.log.Timber;

import static android.view.Gravity.CENTER;
import static com.infinitum.bookingqba.util.Constants.LOCATION_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.REQUEST_CHECK_SETTINGS;
import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;
import static com.infinitum.bookingqba.util.Constants.WRITE_EXTERNAL_CODE;

public class AddRentActivity extends AppCompatActivity implements HasSupportFragmentInjector,
        MapRentLocation, View.OnClickListener, ImageFormAdapter.OnImageDeleteClick {

    private ActivityAddRentBinding binding;
    private static final String STATE_ACTIVE_FRAGMENT = "active_fragment";
    private static final int PICK_PICTURE_CODE = 1560;
    //----Location
    private String locationPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private String storagePerm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
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

    private MapFormFragment mapFragment;

    //-------- Boolean field for panel
    private boolean formLocationOpen = true;
    private boolean formMunicipalityOpen = true;
    private boolean formEsentialOpen = true;
    private boolean formFinallyOpen = true;

    //------------------- Amenities many to many
    private boolean[] amenitiesSelected;
    private ArrayList<FormSelectorItem> amenitiesSelectorOptions;
    private ArrayList<String> amenitiesString;
    //------------------- Galery files
    private ArrayList<String> imagesFilesPath;
    private ArrayList<GaleryModelItem> galeryModelItems;
    private ImageFormAdapter imageFormAdapter;


    //--------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_rent);

        imagesFilesPath = new ArrayList<>();

        binding.flLocationBar.setOnClickListener(this);
        binding.flMunicipalityBar.setOnClickListener(this);
        binding.flEsentialBar.setOnClickListener(this);
        binding.flFinallyBar.setOnClickListener(this);
        binding.btnLocation.setOnClickListener(this);
        binding.flMunicipalities.setOnClickListener(this);
        binding.flReferenceZone.setOnClickListener(this);
        binding.flRentMode.setOnClickListener(this);
        binding.flAmenities.setOnClickListener(this);
        binding.flGalery.setOnClickListener(this);
        binding.btnUpload.setOnClickListener(this);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);
        compositeDisposable = new CompositeDisposable();

        rentFormObject = new RentFormObject(UUID.randomUUID().toString());

        isLocationRequest = false;
        initLocationApi();

        binding.slidingLayout.setTouchEnabled(false);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, STATE_ACTIVE_FRAGMENT, mapFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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
            if (isLocationRequest) {
                locationHelpers.stopLocationUpdates();
                isLocationRequest = false;
            }
        } else {
            super.onBackPressed();
        }
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
                        AlertUtils.showErrorToast(this, "Imposible ser localizado");
                        changeIconColor(false);
                        break;
                }
                break;
            case PICK_PICTURE_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                addImagePathToList(imageUri);
                            }
                            addImageToAdapter(imagesFilesPath);
                        } else if (data.getData() != null) {
                            Uri imagePath = data.getData();
                            addImagePathToList(imagePath);
                            addImageToAdapter(imagesFilesPath);
                        }
                        break;
                }
                break;
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
                case LocationSettingsStatusCodes.DEVELOPER_ERROR:
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
            case WRITE_EXTERNAL_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    checkPermissionResult(permissions, WRITE_EXTERNAL_CODE, "Lectura y Escritura");
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickGaleryFiles();
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
        rentFormObject.setLatitude(String.valueOf(latitude));
        rentFormObject.setLongitude(String.valueOf(longitude));
        binding.tvLatitude.setTextColor(Color.parseColor("#FFC400"));
        binding.tvLongitude.setTextColor(Color.parseColor("#FFC400"));
        binding.tvLatitude.setText(String.format("%.5f", latitude));
        binding.tvLongitude.setText(String.format("%.5f", longitude));
        showSuccessLocationDialog();
    }

    private void showSuccessLocationDialog() {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
        // Title and message
        builder.setTitle("Renta localizada!!");
        builder.setMessage("Coordenadas obtenidas con exito");
        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.addButton("Cerrar mapa", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, (dialog, which) -> {
            dialog.dismiss();
            onBackPressed();
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_location_bar:
                if (formLocationOpen) {
                    binding.llLocationForm.setVisibility(View.GONE);
                    binding.ivArrowLocation.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    formLocationOpen = false;
                } else {
                    binding.llLocationForm.setVisibility(View.VISIBLE);
                    binding.ivArrowLocation.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    formLocationOpen = true;
                }
                break;
            case R.id.fl_municipality_bar:
                if (formMunicipalityOpen) {
                    binding.llMunicipalityForm.setVisibility(View.GONE);
                    binding.ivArrowMunicipality.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    formMunicipalityOpen = false;
                } else {
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
            case R.id.btn_location:
                if (rentFormObject.getLatitude() != null && !rentFormObject.getLatitude().equals("") &&
                        rentFormObject.getLongitude() != null && !rentFormObject.getLongitude().equals("")) {
                    mapFragment = MapFormFragment.newInstance(rentFormObject.getLatitude(), rentFormObject.getLongitude());
                } else {
                    mapFragment = MapFormFragment.newInstance("", "");
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.map_content,
                        mapFragment).runOnCommit(() -> binding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED)).commit();
                break;
            case R.id.fl_municipalities:
                showMunicipalitiesDialog();
                break;
            case R.id.fl_reference_zone:
                showReferenceDialog();
                break;
            case R.id.fl_rent_mode:
                showRentModeDialog();
                break;
            case R.id.fl_amenities:
                showAmenitiesDialog();
                break;
            case R.id.fl_galery:
                if (checkSinglePermission(storagePerm, WRITE_EXTERNAL_CODE)) {
                    pickGaleryFiles();
                }
                break;
            case R.id.btn_upload:
                uploadRent();
                break;
        }
    }

    private void uploadRent() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_upload_rent, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
//        rentFormObject.setRentName(binding.etRentName.getText().toString());
//        rentFormObject.setAddress(binding.etAddress.getText().toString());
//        rentFormObject.setDescription(binding.etDescription.getText().toString());
//        rentFormObject.setEmail(binding.etEmail.getText().toString());
//        rentFormObject.setPhoneNumber(binding.etPersonalPhone.getText().toString());
//        rentFormObject.setPhoneHomeNumber(binding.etHomePhone.getText().toString());
//        rentFormObject.setMaxBaths(binding.etMaxBaths.getText().toString());
//        rentFormObject.setMaxBeds(binding.etMaxBeds.getText().toString());
//        rentFormObject.setMaxRooms(binding.etMaxRooms.getText().toString());
//        rentFormObject.setCapability(binding.etCapability.getText().toString());
//        rentFormObject.setRules(binding.etRules.getText().toString());
//        rentFormObject.setPrice(Float.parseFloat(binding.etPrice.getText().toString()));
//        rentFormObject.setLatitude(binding.tvLatitude.getText().toString());
//        rentFormObject.setLongitude(binding.tvLongitude.getText().toString());
//        ArrayList<String> amenitiesRentFormObjects = getAllAmenitiesRent();
//        disposable = rentViewModel.sendRentToServer(getString(R.string.device), rentFormObject,
//                amenitiesRentFormObjects, imagesFilesPath)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(operationResult -> {
//                    if (operationResult != null) {
//                        Timber.e(operationResult.getMessage());
//                    }
//                }, Timber::e);
//        compositeDisposable.add(disposable);
    }

    private ArrayList<String> getAllAmenitiesRent() {
        ArrayList<String> amenitiesRentFormObjects = new ArrayList<>();
        for (int i = 0; i < amenitiesSelected.length; i++) {
            if (amenitiesSelected[i]) {
                amenitiesRentFormObjects.add(amenitiesSelectorOptions.get(i).getUuid());
            }
        }
        return amenitiesRentFormObjects;
    }

    private void pickGaleryFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Seleccione las imagenes"), PICK_PICTURE_CODE);
    }


    private void addImagePathToList(Uri uri) {
        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String imageId = filePath[filePath.length - 1];

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + "=?", new String[]{imageId}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imagesFilesPath.add(imagePath);
            cursor.close();
        }
    }

    private void addImageToAdapter(ArrayList<String> filesPath) {
        imageFormAdapter = new ImageFormAdapter(filesPath, this);
        binding.rvGaleries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvGaleries.setAdapter(imageFormAdapter);
    }

    private void showAmenitiesDialog() {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
        builder.setTitle("Facilidades");
        builder.setMessage("Seleccione las facilidades de su renta");
        disposable = rentViewModel.getAllRemoteAmenities(getString(R.string.device))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        amenitiesSelectorOptions = (ArrayList<FormSelectorItem>) listResource.data;
                        amenitiesSelected = initBooleanSelected(listResource.data);
                        amenitiesString = initAmenitiesString(listResource.data);
                        builder.setMultiChoiceItems(amenitiesString.toArray(new String[amenitiesString.size()]), amenitiesSelected, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                amenitiesSelected[which] = isChecked;
                                binding.fbAmenities.removeAllViews();
                                populateAmenitiesFlexbox();
                            }
                        });
                        builder.show();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void populateAmenitiesFlexbox() {
        for (int i = 0; i < amenitiesSelected.length; i++) {
            if (amenitiesSelected[i]) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.flexbox_ships_form_item, null);
                textView.setText(amenitiesSelectorOptions.get(i).getTitle());
                params.setMargins(5, 5, 5, 5);
                textView.setLayoutParams(params);
                binding.fbAmenities.addView(textView);

            }
        }
    }

    private boolean[] initBooleanSelected(List<FormSelectorItem> data) {
        if (amenitiesSelected == null) {
            amenitiesSelected = new boolean[data.size()];
            for (int i = 0; i < data.size(); i++) {
                amenitiesSelected[i] = false;
            }
        } else if (data.size() > amenitiesSelected.length) {
            boolean[] amenitiesSelectedCopy = new boolean[data.size()];
            for (int i = 0; i < data.size(); i++) {
                if (i < amenitiesSelected.length) {
                    amenitiesSelectedCopy[i] = amenitiesSelected[i];
                } else {
                    amenitiesSelectedCopy[i] = false;
                }
            }
            amenitiesSelected = amenitiesSelectedCopy;
        } else if (data.size() < amenitiesSelected.length) {
            boolean[] amenitiesSelectedCopy = new boolean[data.size()];
            System.arraycopy(amenitiesSelected, 0, amenitiesSelectedCopy, 0, data.size());
            amenitiesSelected = amenitiesSelectedCopy;
        }
        return amenitiesSelected;
    }

    private ArrayList<String> initAmenitiesString(List<FormSelectorItem> data) {
        amenitiesString = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            amenitiesString.add(data.get(i).getTitle());
        }
        return amenitiesString;
    }

    private void showRentModeDialog() {
        disposable = rentViewModel.getAllRemoteRentMode(getString(R.string.device))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        ArrayList<SearchableSelectorModel> list = new ArrayList<>(listResource.data);
                        new SimpleSearchDialogCompat<>(this, "Modo de Renta",
                                "Buscar por nombre", null, list, (dialog, item, position) -> {
                            rentFormObject.setRentMode(item.getUuid());
                            binding.tvRentMode.setTextColor(Color.parseColor("#FFC400"));
                            binding.tvRentMode.setText(item.getTitle());
                            dialog.dismiss();
                        }).show();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void showReferenceDialog() {
        disposable = rentViewModel.getAllRemoteReferenceZone(getString(R.string.device))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        ArrayList<SearchableSelectorModel> list = new ArrayList<>(listResource.data);
                        new SimpleSearchDialogCompat<>(this, "Zona de Referencia",
                                "Buscar por nombre", null, list, (dialog, item, position) -> {
                            rentFormObject.setReferenceZone(item.getUuid());
                            binding.tvReferenceZone.setTextColor(Color.parseColor("#FFC400"));
                            binding.tvReferenceZone.setText(item.getTitle());
                            dialog.dismiss();
                        }).show();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void showMunicipalitiesDialog() {
        disposable = rentViewModel.getAllRemoteMunicipalities(getString(R.string.device))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        ArrayList<SearchableSelectorModel> list = new ArrayList<>(listResource.data);
                        new SimpleSearchDialogCompat<>(this, "Municipios",
                                "Buscar por nombre", null, list, (dialog, item, position) -> {
                            rentFormObject.setMunicipality(item.getUuid());
                            binding.tvMunicipality.setTextColor(Color.parseColor("#FFC400"));
                            binding.tvMunicipality.setText(item.getTitle());
                            dialog.dismiss();
                        }).show();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }


    @Override
    public void onImageDelete(String imagePath, int pos) {
        imageFormAdapter.removeItem(pos);
    }
}
