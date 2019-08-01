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
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.SystemClock;
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
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityAddRentBinding;
import com.infinitum.bookingqba.model.remote.pojo.AddressResponse;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.GeocodeNominatim;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.view.base.LocationActivity;
import com.infinitum.bookingqba.view.map.MapFragment;
import com.infinitum.bookingqba.view.profile.dialogitem.FormSelectorItem;
import com.infinitum.bookingqba.view.profile.dialogitem.SearchableSelectorModel;
import com.infinitum.bookingqba.view.profile.uploaditem.AmenitiesRentFormObject;
import com.infinitum.bookingqba.view.profile.uploaditem.RentFormObject;
import com.infinitum.bookingqba.view.widgets.DialogLocationConfirmView;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.poi.android.storage.AndroidPoiPersistenceManagerFactory;
import org.mapsforge.poi.storage.ExactMatchPoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategory;
import org.mapsforge.poi.storage.PoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategoryManager;
import org.mapsforge.poi.storage.PoiPersistenceManager;
import org.mapsforge.poi.storage.PointOfInterest;
import org.mapsforge.poi.storage.UnknownPoiCategoryException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import timber.log.Timber;

import static android.view.Gravity.CENTER;
import static com.infinitum.bookingqba.service.LocationService.KEY_REQUESTING_LOCATION_UPDATES;
import static com.infinitum.bookingqba.util.Constants.LOCATION_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.REQUEST_CHECK_SETTINGS;
import static com.infinitum.bookingqba.util.Constants.THUMB_HEIGHT;
import static com.infinitum.bookingqba.util.Constants.THUMB_WIDTH;
import static com.infinitum.bookingqba.util.Constants.WRITE_EXTERNAL_CODE;

public class AddRentActivity extends LocationActivity implements HasSupportFragmentInjector,
        MapRentLocation, View.OnClickListener, ImageFormAdapter.OnImageDeleteClick
        , DialogLocationConfirmView.DialogLocationConfirmListener {

    private ActivityAddRentBinding binding;
    private static final String STATE_ACTIVE_FRAGMENT = "active_fragment";
    private static final int PICK_PICTURE_CODE = 1560;
    //----Location
    private RentFormObject rentFormObject;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    SharedPreferences sharedPreferences;

    private Location currentLocation;
    private double mLatitude;
    private double mLongitude;

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
    private PoiPersistenceManager mPersistenceManager;
    private DialogLocationConfirmView dialogLocationConfirmView;
    private CFAlertDialog.Builder builder;
    private CFAlertDialog dialog;
    private boolean isBtnSaveClick;


    //--------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_rent);

        initPoiFile();

        compositeDisposable = new CompositeDisposable();

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

        rentFormObject = new RentFormObject(UUID.randomUUID().toString());

        binding.slidingLayout.setTouchEnabled(false);
        binding.slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
                    mapFragment = null;
                }
            }
        });

    }

    private void initPoiFile() {
        String file = getFilesDir() + File.separator + "map" + File.separator + "cuba.poi";
        mPersistenceManager = AndroidPoiPersistenceManagerFactory.getPoiPersistenceManager(file);
    }

    @Override
    protected void onDestroy() {
        Timber.e("Activity onDestroy");
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        dialog = null;
        builder = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if ((binding.slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || binding.slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            binding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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

    @Override
    protected void updateLocation(Location location) {
        if (binding.slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED &&
                mapFragment != null && mapFragment.getUserVisibleHint()) {
            mapFragment.updateGPSCurrentLocation(location);
        }
    }

    //------------------------------------- LOCATION API ------------------------------------

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    //--------------------------------- PERMISSIONS --------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    checkPermissionResult(permissions);
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

    private void checkPermissionResult(String[] permissions) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            AlertUtils.showErrorSnackbar(this, "Imposible agregar imágenes. Otorge los permisos.");
        } else {
            AlertUtils.showCFErrorAlertWithAction(this, "Permiso requerido", "Esta funcionalidad requiere del permiso 'Lectura y Escritura' de memoria interna. Por favor otorguelos.",
                    (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }, "Otorgar");
        }
    }

    @Override
    public void onLocationButtonClick() {
        if (sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false) && currentLocation == null) {
            AlertUtils.showSuccessLocationToast(this, "Ubicando...");
        } else if (!sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)) {
            startLocationRequestUnknowPermission();
        }
    }

    @Override
    public void onLocationUpdates(double latitude, double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    private void getAddressByLocation() {
        disposable = rentViewModel.addressByLocation(mLatitude, mLongitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addressResponse -> {
                    if (addressResponse.isSuccessful()) {
                        buildAddressText(addressResponse.body().getDisplayName());
                    }
                }, throwable -> {
                    Timber.e(throwable);
                });
        compositeDisposable.add(disposable);
    }

    private void buildAddressText(String displayName) {
        StringBuilder addressBuilder = new StringBuilder();
        String[] displayList = displayName.split(",");
        int size = displayList.length - 2;
        for (int i = 0; i < size; i++) {
            addressBuilder.append(displayList[i]);
            if (i < size - 1) {
                addressBuilder.append(", ");
            }
        }
        if (!addressBuilder.toString().isEmpty()) {
            binding.etAddress.setText(addressBuilder.toString());
        }
    }

    @Override
    public void showLocationConfirmDialog() {
        builder = new CFAlertDialog.Builder(this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
        // Title and message
        builder.setTitle("Ubicacion obtenida");
        builder.setTextGravity(Gravity.START);
        builder.setIcon(R.drawable.ic_map_marker_alt_blue_grey);
        builder.setTextColor(Color.parseColor("#607D8B"));

        dialogLocationConfirmView = new DialogLocationConfirmView(this);
        builder.setFooterView(dialogLocationConfirmView);

        dialog = builder.show();
    }

    @Override
    public void onButtonSaveClick() {
        rentFormObject.setLatitude(String.valueOf(mLatitude));
        rentFormObject.setLongitude(String.valueOf(mLongitude));
        dialog.dismiss();
        binding.ivArrowLocation.setImageResource(R.drawable.ic_map_localized);
        binding.tvTitleLocation.setText("Localizado");
    }

    @Override
    public void onButtonConfirmClick() {
        isBtnSaveClick = false;
        dialogLocationConfirmView.isLoading(true);
        disposable = Single.just(getPointOfInterestByLocation(mLatitude, mLongitude))
                .subscribeOn(Schedulers.io())
                .map(pointOfInterests -> {
                    String referenceZone = getReferenceNameByPoiCategory(pointOfInterests);
                    return new Pair<>(referenceZone, pointOfInterests);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                    dialogLocationConfirmView.setPoints(pair.second);
                    dialogLocationConfirmView.setReferenceZone(pair.first);
                    dialogLocationConfirmView.isLoading(false);
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private Collection<PointOfInterest> getPointOfInterestByLocation(double mLatitude, double mLongitude) {
        PoiCategoryManager categoryManager = mPersistenceManager.getCategoryManager();
        PoiCategoryFilter categoryFilter = new ExactMatchPoiCategoryFilter();
        for (String category : Constants.category) {
            try {
                categoryFilter.addCategory(categoryManager.getPoiCategoryByTitle(category));
            } catch (UnknownPoiCategoryException e) {
                e.printStackTrace();
            }
        }
        return mPersistenceManager.findNearPosition(new LatLong(mLatitude, mLongitude), 1500, categoryFilter, null, 50);
    }

    private String getReferenceNameByPoiCategory(Collection<PointOfInterest> pointOfInterests) {
        String referenceZoneResult = "Barriada";
        if (containBeach(pointOfInterests)) {
            referenceZoneResult = "Playa";
        }else if(containCategory(pointOfInterests,Constants.historic_category)){
            referenceZoneResult = "Historia";
        }else if(containCategory(pointOfInterests,Constants.natural_category)){
            referenceZoneResult = "Natural";
        }
        return referenceZoneResult;
    }

    private boolean containBeach(Collection<PointOfInterest> pointOfInterests) {
        for (PointOfInterest point : pointOfInterests) {
            PoiCategory[] poiCategories = point.getCategories().toArray(new PoiCategory[point.getCategories().size()]);
            for (PoiCategory poiCategory : poiCategories) {
                if (poiCategory.getTitle().equals("Marinas")) {
                    return true;
                } else if (poiCategory.getTitle().equals("Attractions") && point.getName().contains("Playa")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containCategory(Collection<PointOfInterest> pointOfInterests, String[] categories) {
        for (PointOfInterest point : pointOfInterests) {
            if (categoryIsInList(categories, point.getCategories())) {
                return true;
            }
        }
        return false;
    }

    private boolean categoryIsInList(String[] categoriesArray, Set<PoiCategory> categories) {
        PoiCategory[] poiCategories = categories.toArray(new PoiCategory[categories.size()]);
        for (PoiCategory poiCategory : poiCategories) {
            if (categoryExist(categoriesArray, poiCategory.getTitle())) {
                return true;
            }
        }
        return false;
    }

    private boolean categoryExist(String[] categories, String category) {
        for (String stringCategory : categories) {
            if (stringCategory.equals(category))
                return true;
        }
        return false;
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
                        mapFragment, "map").runOnCommit(() -> binding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED)).commit();
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
                if (checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_CODE)) {
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
