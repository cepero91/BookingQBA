package com.infinitum.bookingqba.view.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityAddRentBinding;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;
import com.infinitum.bookingqba.model.remote.pojo.RentEdit;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.view.base.LocationActivity;
import com.infinitum.bookingqba.view.profile.adapter.ImageFormAdapter;
import com.infinitum.bookingqba.view.profile.adapter.OfferFormAdapter;
import com.infinitum.bookingqba.view.profile.dialogitem.FormSelectorItem;
import com.infinitum.bookingqba.view.profile.dialogitem.SearchableSelectorModel;
import com.infinitum.bookingqba.view.profile.uploaditem.GaleryFormObject;
import com.infinitum.bookingqba.view.profile.uploaditem.OfferFormObject;
import com.infinitum.bookingqba.view.profile.uploaditem.RentFormObject;
import com.infinitum.bookingqba.view.widgets.DialogAddOfferView;
import com.infinitum.bookingqba.view.widgets.DialogLocationConfirmView;
import com.infinitum.bookingqba.viewmodel.RentFormViewModel;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import timber.log.Timber;

import static com.infinitum.bookingqba.service.LocationService.KEY_REQUESTING_LOCATION_UPDATES;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;
import static com.infinitum.bookingqba.util.Constants.WRITE_EXTERNAL_CODE;

public class AddRentActivity extends LocationActivity implements HasSupportFragmentInjector,
        MapRentLocation, View.OnClickListener, ImageFormAdapter.OnImageDeleteClick,
        DialogLocationConfirmView.DialogLocationConfirmListener, BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.ImageLoaderDelegate,
        OfferFormAdapter.OnOfferInteraction, DialogAddOfferView.AddOfferClick {

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

    private RentFormViewModel rentViewModel;
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
    private ArrayList<GaleryFormObject> imagesFilesPath;
    private ArrayList<GaleryModelItem> galeryModelItems;
    private ImageFormAdapter imageFormAdapter;
    private PoiPersistenceManager mPersistenceManager;
    private DialogLocationConfirmView dialogLocationConfirmView;
    private CFAlertDialog.Builder builder;
    private CFAlertDialog dialog;
    private boolean isBtnSaveClick;

    //Offer
    private DialogAddOfferView dialogAddOffer;
    private List<OfferFormObject> offerFormObjects;
    private OfferFormAdapter offerAdapter;
    private String userToken, userid;

    private boolean edit;
    private String uuid;


    //--------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_rent);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentFormViewModel.class);
        if (getIntent().hasExtra("edit") && getIntent().hasExtra("id")) {
            edit = getIntent().getBooleanExtra("edit", false);
            uuid = getIntent().getStringExtra("id");
        } else {
            edit = false;
        }

        initObject(edit);

        initViewOnClick();

        initSlidingLayout();

    }

    private void initSlidingLayout() {
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

    private void initObject(boolean isEdited) {
        userToken = sharedPreferences.getString(USER_TOKEN, "");
        userid = sharedPreferences.getString(USER_ID, "");
        compositeDisposable = new CompositeDisposable();
        if (!isEdited) {
            rentFormObject = new RentFormObject(UUID.randomUUID().toString());
            rentFormObject.setUserid(userid);
            imagesFilesPath = new ArrayList<>();
            offerFormObjects = new ArrayList<>();
            offerAdapter = new OfferFormAdapter((ArrayList<OfferFormObject>) offerFormObjects, this);
            binding.rvOffer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvOffer.setAdapter(offerAdapter);
        } else {
            prepareForEdit(uuid);
        }
    }

    private void prepareForEdit(String uuid) {
        disposable = rentViewModel.getRentById(userToken, uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rentEditResource -> {
                    if (rentEditResource.data != null && rentEditResource.data.size() > 0) {
                        updateRentFormObject(rentEditResource.data.get(0));
                        updateAllInput(rentEditResource.data.get(0));
                    }
                }, throwable -> Timber.e(throwable));
        compositeDisposable.add(disposable);
    }

    private void updateRentFormObject(RentEdit rentEdit) {
        rentFormObject = new RentFormObject(rentEdit.getUui());
        rentFormObject.setUserid(userid);
        rentFormObject.setLatitude(rentEdit.getLatitude());
        rentFormObject.setLongitude(rentEdit.getLongitude());
        rentFormObject.setRentName(rentEdit.getName());
        rentFormObject.setAddress(rentEdit.getAddress());
        rentFormObject.setPrice(rentEdit.getPrice());
        rentFormObject.setCapability(String.valueOf(rentEdit.getCapability()));
        rentFormObject.setMaxBaths(String.valueOf(rentEdit.getMaxBath()));
        rentFormObject.setMaxBeds(String.valueOf(rentEdit.getMaxBeds()));
        rentFormObject.setMaxRooms(String.valueOf(rentEdit.getMaxRooms()));
        rentFormObject.setEmail(rentEdit.getEmail());
        rentFormObject.setRules(rentEdit.getRules());
        rentFormObject.setDescription(rentEdit.getDescription());
        rentFormObject.setPhoneNumber(rentEdit.getPhoneNumber());
        rentFormObject.setPhoneHomeNumber(rentEdit.getPhoneHomeNumber());
        rentViewModel.setLocalAmenities(rentEdit.getAmenities());
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < rentEdit.getAmenities().size(); i++) {
            selected.add(rentEdit.getAmenities().get(i).getName());
        }
        updateAmenitiesFlexBox(selected);
        offerFormObjects = new ArrayList<>();
        for (int i = 0; i < rentEdit.getOffer().size(); i++) {
            offerFormObjects.add(new OfferFormObject(rentEdit.getOffer().get(i).getId(),
                    rentEdit.getOffer().get(i).getName(), rentEdit.getOffer().get(i).getDescription(),
                    String.valueOf(rentEdit.getOffer().get(i).getPrice()), rentEdit.getOffer().get(i).getRent()));
        }
        offerAdapter = new OfferFormAdapter((ArrayList<OfferFormObject>) offerFormObjects, this);
        binding.rvOffer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvOffer.setAdapter(offerAdapter);
        GaleryFormObject galeryFormObject;
        imagesFilesPath = new ArrayList<>();
        for (Galerie galerie:rentEdit.getGalerie()) {
            String filepath = Constants.BASE_URL_API+"/"+galerie.getImage();
            galeryFormObject = new GaleryFormObject(UUID.randomUUID().toString());
            galeryFormObject.setRemote(true);
            galeryFormObject.setUrl(filepath);
            imagesFilesPath.add(galeryFormObject);
        }
        addImageToAdapter(imagesFilesPath);
    }

    private void updateAllInput(RentEdit data) {
        binding.etAddress.setText(data.getAddress());
        updateMunicipality(data.getMunicipality().getId(), data.getMunicipality().getName());
        updateReferenceZone(data.getReferenceZone().getId(), data.getReferenceZone().getName());
        updateRentMode(data.getRentMode().getId(), data.getRentMode().getName());
        binding.etRentName.setText(data.getName());
        binding.etPrice.setText(String.valueOf(data.getPrice()));
        binding.etEmail.setText(data.getEmail());
        binding.etCapability.setText(String.valueOf(data.getCapability()));
        binding.etMaxBeds.setText(String.valueOf(data.getMaxBeds()));
        binding.etMaxBaths.setText(String.valueOf(data.getMaxBath()));
        binding.etMaxRooms.setText(String.valueOf(data.getMaxRooms()));
        binding.etRules.setText(String.valueOf(data.getRules()));
        binding.etDescription.setText(String.valueOf(data.getDescription()));
        binding.etPersonalPhone.setText(String.valueOf(data.getPhoneNumber()));
        binding.etHomePhone.setText(String.valueOf(data.getPhoneHomeNumber()));

    }

    private void initViewOnClick() {
        binding.flLocationBar.setOnClickListener(this);
        binding.flMunicipalityBar.setOnClickListener(this);
        binding.flEsentialBar.setOnClickListener(this);
        binding.btnLocation.setOnClickListener(this);
        binding.flMunicipalities.setOnClickListener(this);
        binding.flRentMode.setOnClickListener(this);
        binding.flAmenities.setOnClickListener(this);
        binding.flGalery.setOnClickListener(this);
        binding.flOffer.setOnClickListener(this);
        binding.btnUpload.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        Timber.e("Activity onDestroy");
        if (disposable != null)
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


    @Override
    public void onLocationButtonClick() {
        if (sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false) && currentLocation == null) {
            AlertUtils.showSuccessLocationToast(this, "Ubicando...");
        } else if (!sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)) {
            startLocationRequestUnknowPermission();
        }
    }

    //-------------------------- DIALOG LOCATION CONFIRM VIEW ----------------------------

    @Override
    public void onLocationUpdates(double latitude, double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
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
    public void onButtonConfirmClick() {
        String poiFile = getFilesDir() + File.separator + "map" + File.separator + "cuba.poi";
        dialogLocationConfirmView.isLoading(true);
        disposable = rentViewModel.poiAndReferenceZone(userToken, poiFile, mLatitude, mLongitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(map -> {
                    if (map.containsKey("poi")) {
                        dialogLocationConfirmView.setPoints((Collection<PointOfInterest>) map.get("poi"));
                    }
                    if (map.containsKey("uuid") && map.containsKey("name")) {
                        dialogLocationConfirmView.setReferenceZone((String) map.get("name"));
                        updateReferenceZone((String) map.get("uuid"), (String) map.get("name"));
                    }
                    dialogLocationConfirmView.isLoading(false);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onButtonSaveClick() {
        rentFormObject.setLatitude(String.valueOf(mLatitude));
        rentFormObject.setLongitude(String.valueOf(mLongitude));
        dialog.dismiss();
        binding.btnLocation.setImageResource(R.drawable.ic_map_localized);
        binding.tvTitleLocation.setText("Localizado");
        binding.tvTitleLocation.setTextColor(Color.parseColor("#26A69A"));
        getAddressByLocation();
    }

    private void getAddressByLocation() {
        disposable = rentViewModel.addressAndMunicipalityByLocation(userToken, mLatitude, mLongitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(map -> {
                    if (map.containsKey("address")) {
                        binding.etAddress.setText(map.get("address"));
                    }
                    if (map.containsKey("uuid") && map.containsKey("name")) {
                        rentFormObject.setMunicipality(map.get("uuid"));
                        binding.tvMunicipality.setText(map.get("name"));
                    }
                }, throwable -> {
                    Timber.e(throwable);
                });
        compositeDisposable.add(disposable);
    }

    //----------------------------------- VIEWS CLICKS ------------------------------------

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
                pickGaleryFiles();
                break;
            case R.id.fl_offer:
                showAddOfferDialog(new DialogAddOfferView(this));
                break;
            case R.id.btn_upload:
                uploadRent();
                break;
        }
    }

    private void uploadRent() {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_upload_rent, null);
//        dialogBuilder.setView(dialogView);
//        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
        updateRentFormFromInput();
        Map<String, Object> params = new HashMap<>();
        params.put("rent", rentFormObject);
        params.put("galery", imagesFilesPath);
        if (offerAdapter.getOfferFormObjects().size() > 0) {
            params.put("offer", offerAdapter.getOfferFormObjects());
        }
        disposable = rentViewModel.sendRentToServer(userToken, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    Timber.e("si");
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateRentFormFromInput() {
        rentFormObject.setRentName(binding.etRentName.getText().toString());
        rentFormObject.setAddress(binding.etAddress.getText().toString());
        rentFormObject.setDescription(binding.etDescription.getText().toString());
        rentFormObject.setEmail(binding.etEmail.getText().toString());
        rentFormObject.setPhoneNumber(binding.etPersonalPhone.getText().toString());
        rentFormObject.setPhoneHomeNumber(binding.etHomePhone.getText().toString());
        rentFormObject.setMaxBaths(binding.etMaxBaths.getText().toString());
        rentFormObject.setMaxBeds(binding.etMaxBeds.getText().toString());
        rentFormObject.setMaxRooms(binding.etMaxRooms.getText().toString());
        rentFormObject.setCapability(binding.etCapability.getText().toString());
        rentFormObject.setRules(binding.etRules.getText().toString());
        rentFormObject.setPrice(Float.parseFloat(binding.etPrice.getText().toString()));
    }

    private void showAmenitiesDialog() {
        binding.setLoadingAmenities(true);
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
        builder.setTitle("Facilidades");
        builder.setMessage("Seleccione las facilidades de su renta");
        disposable = rentViewModel.getAllRemoteAmenities(userToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairResource -> {
                    if (pairResource.data != null) {
                        binding.setLoadingAmenities(false);
                        builder.setMultiChoiceItems(pairResource.data.first, pairResource.data.second, (dialog, which, isChecked) -> {
                            rentViewModel.checkAmenity(which, isChecked);
                            binding.fbAmenities.removeAllViews();
                            populateAmenitiesFlexbox();
                        });
                        builder.show();
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    binding.setLoadingAmenities(false);
                });
        compositeDisposable.add(disposable);
    }

    private void populateAmenitiesFlexbox() {
        List<String> selected = rentViewModel.getAmenitiesSelectedNames();
        updateAmenitiesFlexBox(selected);
    }

    private void updateAmenitiesFlexBox(List<String> selected) {
        for (int i = 0; i < selected.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.flexbox_ships_form_item, null);
            textView.setText(selected.get(i));
            params.setMargins(5, 0, 5, 10);
            textView.setLayoutParams(params);
            binding.fbAmenities.addView(textView);
        }
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
                            updateRentMode(item.getUuid(), item.getTitle());
                            dialog.dismiss();
                        }).show();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateRentMode(String uuid, String name) {
        rentFormObject.setRentMode(uuid);
        binding.tvRentMode.setTextColor(Color.parseColor("#FFC400"));
        binding.tvRentMode.setText(name);
    }

    private void showReferenceDialog() {
        disposable = rentViewModel.getAllRemoteReferenceZone(userToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        ArrayList<SearchableSelectorModel> list = new ArrayList<>(listResource.data);
                        new SimpleSearchDialogCompat<>(this, "Zona de Referencia",
                                "Buscar por nombre", null, list, (dialog, item, position) -> {
                            updateReferenceZone(item.getUuid(), item.getTitle());
                            dialog.dismiss();
                        }).show();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateReferenceZone(String uuid, String name) {
        rentFormObject.setReferenceZone(uuid);
        binding.tvReferenceZone.setTextColor(Color.parseColor("#FFC400"));
        binding.tvReferenceZone.setText(name);
    }

    private void showMunicipalitiesDialog() {
        binding.setLoadingMunicipality(true);
        disposable = rentViewModel.getAllRemoteMunicipalities(getString(R.string.device))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        ArrayList<SearchableSelectorModel> list = new ArrayList<>(listResource.data);
                        binding.setLoadingMunicipality(false);
                        new SimpleSearchDialogCompat<>(this, "Municipios",
                                "Buscar por nombre", null, list, (dialog, item, position) -> {
                            updateMunicipality(item.getUuid(), item.getTitle());
                            dialog.dismiss();
                        }).show();
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    binding.setLoadingMunicipality(false);
                });
        compositeDisposable.add(disposable);
    }

    private void updateMunicipality(String uuid, String name) {
        rentFormObject.setMunicipality(uuid);
        binding.tvMunicipality.setTextColor(Color.parseColor("#FFC400"));
        binding.tvMunicipality.setText(name);
    }


    //--------------------------------- OFFER -------------------------------------

    private void showAddOfferDialog(DialogAddOfferView dialogAddOffer) {
        builder = new CFAlertDialog.Builder(this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
        // Title and message
        builder.setTitle("Nueva Oferta");
        builder.setTextGravity(Gravity.CENTER);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.setFooterView(dialogAddOffer);
        dialog = builder.show();
    }

    @Override
    public void onOfferEdit(OfferFormObject offerFormObject, int pos) {
        dialogAddOffer = new DialogAddOfferView(this);
        dialogAddOffer.setOfferFormObject(offerFormObject, pos);
        showAddOfferDialog(dialogAddOffer);
    }

    @Override
    public void onButtonSaveClick(OfferFormObject offerFormObject, boolean isEdit, int pos) {
        if (isEdit) {
            updateOffertoAdapter(offerFormObject, pos);
        } else {
            offerFormObject.setRent(rentFormObject.getUuid());
            addOffertoAdapter(offerFormObject);
        }
        dialog.dismiss();
    }

    private void addOffertoAdapter(OfferFormObject offerFormObject) {
        offerAdapter.addOffer(offerFormObject);
    }

    private void updateOffertoAdapter(OfferFormObject offerFormObject, int pos) {
        offerAdapter.updateItem(offerFormObject, pos);
    }


    //--------------------------------- IMAGES ------------------------------------

    @Override
    public void onImageDelete(String imagePath, int pos) {
        imageFormAdapter.removeItem(pos);
    }


    private void pickGaleryFiles() {
        BSImagePicker pickerDialog = new BSImagePicker.Builder("com.infinitum.bookingqba.fileprovider")
                .setMaximumDisplayingImages(Integer.MAX_VALUE)
                .isMultiSelect()
                .setMinimumMultiSelectCount(1)
                .setMaximumMultiSelectCount(5)
                .build();
        pickerDialog.show(getSupportFragmentManager(), "picker");
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        if (uriList != null && uriList.size() > 0) {
            GaleryFormObject galeryFormObject;
            for (Uri uri : uriList) {
                String filepath = uri.getPath();
                galeryFormObject = new GaleryFormObject(UUID.randomUUID().toString());
                galeryFormObject.setRemote(false);
                galeryFormObject.setUrl(filepath);
                imagesFilesPath.add(galeryFormObject);
            }
            addImageToAdapter(imagesFilesPath);
        }
    }

    @Override
    public void loadImage(File imageFile, ImageView ivImage) {
        Picasso.get().load(imageFile).resize(Constants.THUMB_WIDTH, Constants.THUMB_HEIGHT).placeholder(R.drawable.placeholder).into(ivImage);
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {

    }

    private void addImageToAdapter(ArrayList<GaleryFormObject> filesPath) {
        imageFormAdapter = new ImageFormAdapter(filesPath, this);
        binding.rvGaleries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvGaleries.setAdapter(imageFormAdapter);
    }

}
