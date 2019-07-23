package com.infinitum.bookingqba.view.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.florent37.shapeofview.shapes.CutCornerView;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.infinitum.bookingqba.BuildConfig;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityHomeBinding;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.service.SendDataWorker;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.LocationHelpers;
import com.infinitum.bookingqba.util.PermissionHelper;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.home.HeaderItem;
import com.infinitum.bookingqba.view.adapters.items.home.RZoneItem;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;
import com.infinitum.bookingqba.view.info.DialogFeedback;
import com.infinitum.bookingqba.view.info.InfoFragment;
import com.infinitum.bookingqba.view.interaction.FilterInteraction;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;
import com.infinitum.bookingqba.view.filter.FilterFragment;
import com.infinitum.bookingqba.view.interaction.InfoInteraction;
import com.infinitum.bookingqba.view.interaction.LoginInteraction;
import com.infinitum.bookingqba.view.listwish.ListWishFragment;
import com.infinitum.bookingqba.view.map.MapFragment;
import com.infinitum.bookingqba.view.profile.AddRentActivity;
import com.infinitum.bookingqba.view.profile.AuthFragment;
import com.infinitum.bookingqba.view.profile.LoginFragment;
import com.infinitum.bookingqba.view.profile.MyRentsFragment;
import com.infinitum.bookingqba.view.profile.ProfileFragment;
import com.infinitum.bookingqba.view.profile.UserAuthActivity;
import com.infinitum.bookingqba.view.rents.RentDetailActivity;
import com.infinitum.bookingqba.view.rents.RentListFragment;
import com.infinitum.bookingqba.view.sync.SyncActivity;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.yayandroid.locationmanager.base.LocationBaseActivity;
import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.ProviderType;
import com.yayandroid.locationmanager.providers.dialogprovider.SimpleMessageDialogProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static android.view.Gravity.CENTER;
import static com.infinitum.bookingqba.util.Constants.ALTERNATIVE_SYNC;
import static com.infinitum.bookingqba.util.Constants.BASE_URL_API;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_REFRESH;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_REFRESH_SHOW_GROUP;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_SHOW_GROUP;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_TO_MAP;
import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.IS_PROFILE_ACTIVE;
import static com.infinitum.bookingqba.util.Constants.LOCATION_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.LOGIN_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.LOGIN_TAG;
import static com.infinitum.bookingqba.util.Constants.MY_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.NOTIFICATION_DEFAULT;
import static com.infinitum.bookingqba.util.Constants.NOTIFICATION_ID;
import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_POPULAR;
import static com.infinitum.bookingqba.util.Constants.PERIODICAL_WORK_NAME;
import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID;
import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID_DEFAULT;
import static com.infinitum.bookingqba.util.Constants.READ_PHONE_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.REQUEST_CHECK_SETTINGS;
import static com.infinitum.bookingqba.util.Constants.USER_AVATAR;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_RENTS;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class HomeActivity extends LocationBaseActivity implements HasSupportFragmentInjector,
        FragmentNavInteraction, NavigationView.OnNavigationItemSelectedListener,
        FilterInteraction, MapFragment.OnFragmentMapInteraction, InfoInteraction,
        DialogFeedback.FeedbackInteraction, MyRentsFragment.AddRentClick {

    private static final String STATE_ACTIVE_FRAGMENT = "active_fragment";
    private ActivityHomeBinding homeBinding;
    private FragmentManager fragmentManager;
    private Fragment mFragment;
    private FilterFragment filterFragment;

    //------------------------- LOCATION VAR --------------------------//
    private Location mCurrentLocation;
    private Location lastKnowLocation;
    private boolean mRequestingLocationUpdates = false;
    private LocationHelpers locationHelper;


    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback locationCallback;


    //-------------------------- PERMISSION VAR --------------------------------//
    private String locationPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private String readPhonePerm = Manifest.permission.READ_PHONE_STATE;


    //-------------------------- LOGING FRAGMENT ------------------------------//
    private boolean loginIsClicked = false;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    SharedPreferences sharedPreferences;

    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        setupToolbar();

        initDrawerLayout();

        initWorkRequest();

        initializeFragment(savedInstanceState);

        getLocation();

//        setupLocationApi();

//        initPermissionRequest();

//        initLocationManager();
//        checkGpsAvailability();
//        ensureLastLocationInit();
//        updateCurrentLocation(null);

    }

    // ------------------ INITIALIZE UI ----------------------------------- //

    private void setupToolbar() {
        setSupportActionBar(homeBinding.toolbar);
    }

    private void initializeFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, STATE_ACTIVE_FRAGMENT);
        }
        if (mFragment == null) {
            mFragment = HomeFragment.newInstance();
        }
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,
                mFragment).commit();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    private void initWorkRequest() {
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(SendDataWorker.class, 20, TimeUnit.MINUTES)
                        .setConstraints(myConstraints)
                        .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(PERIODICAL_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }


    private void initDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, homeBinding.drawerLayout, homeBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        homeBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        homeBinding.navView.getMenu().getItem(0).setChecked(true);
        homeBinding.navView.setNavigationItemSelectedListener(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) homeBinding.navViewNotification.getLayoutParams();
        params.width = metrics.widthPixels;
        homeBinding.navViewNotification.setLayoutParams(params);

        homeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
    }

    // ---------------------- LOCATION METHOD ------------------------ //

//    private void setupLocationApi() {
//        locationHelper = new LocationHelpers(this);
//        locationHelper.setLocationCallback(new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                mCurrentLocation = locationResult.getLastLocation();
//                updateMapLocation(mCurrentLocation);
//            }
//        });
//    }

    private void updateMapLocation(Location mCurrentLocation) {
        if (mCurrentLocation != null && mFragment instanceof MapFragment) {
            ((MapFragment) mFragment).updateUserTracking(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationHelper.startLocationUpdate(e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        ResolvableApiException rae = (ResolvableApiException) e;
                        rae.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sie) {
                        Timber.e(sie);
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    mRequestingLocationUpdates = false;
                    AlertUtils.showErrorToast(HomeActivity.this, "Imposible ser localizado");
                    invalidateOptionsMenu();
                    break;
            }
        });
    }

//    private void stopLocationUpdates() {
//        locationHelper.stopLocationUpdates();
//        mRequestingLocationUpdates = false;
//        invalidateOptionsMenu();
//    }

    // ------------------------- PERMISSION -------------------------------- //

    public void initPermissionRequest() {
        permissionHelper = new PermissionHelper(this);
        permissionHelper.check(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withDialogBeforeRun(R.string.dialog_before_run_title, R.string.dialog_before_run_message, R.string.dialog_positive_button)
                .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                .onSuccess(this::onPermissionSuccess)
                .onDenied(this::onPermissionDenied)
                .onNeverAskAgain(this::onPermissionNeverAskAgain)
                .run();
    }

    private void onPermissionNeverAskAgain() {
        Toast.makeText(this, "onPermissionNeverAskAgain", Toast.LENGTH_SHORT).show();
    }

    private void onPermissionDenied() {
        Toast.makeText(this, "onPermissionDenied", Toast.LENGTH_SHORT).show();
    }

    private void onPermissionSuccess() {
        Toast.makeText(this, "onPermissionSuccess", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == LOCATION_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                checkPermissionResult(permissions, LOCATION_REQUEST_CODE, "Localizacion");
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mRequestingLocationUpdates = true;
//                MenuItem menuItem = homeBinding.toolbar.getMenu().findItem(R.id.action_gps);
//                if (menuItem != null) {
//                    changeMenuIcon(menuItem);
//                }
//                startLocationUpdates();
//            }
//        } else if (requestCode == READ_PHONE_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                checkPermissionResult(permissions, READ_PHONE_REQUEST_CODE, "Estado del Telefono");
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (sharedPreferences.getString(IMEI, "").equals("")) {
//                    deviceID = getDeviceUniversalID();
//                    sharedPreferences.edit().putString(IMEI, deviceID).apply();
//                }
//            }
//        }
//    }
//
//    private boolean checkSinglePermission(String perm, int requestCode) {
//        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
//            String[] perms = new String[1];
//            perms[0] = perm;
//            ActivityCompat.requestPermissions(this, perms, requestCode);
//            return false;
//        }
//        return true;
//    }
//
//    private boolean checkMultiplePermission(String perm, int requestCode) {
//        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
//            String[] perms = new String[1];
//            perms[0] = perm;
//            ActivityCompat.requestPermissions(this, perms, requestCode);
//            return false;
//        }
//        return true;
//    }
//
//    private void checkPermissionResult(String[] permissions, int requestCode, String permCodeName) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
//            String msg = String.format("Permiso de %s requerido, por favor otórguelo.", permCodeName);
//            showDialog(msg, "Otorgar",
//                    (dialog, which) -> {
//                        dialog.dismiss();
//                        checkSinglePermission(permissions[0], requestCode);
//                    }, "No, cerrar", (dialog, which) -> dialog.dismiss(), false);
//        } else {
//            String msg = "Has negado permanentemente el permiso requerido. Puede que la aplicación no funcione correctamente.";
//            showDialog(msg, "Ir",
//                    (dialog, which) -> {
//                        dialog.dismiss();
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                Uri.fromParts("package", getPackageName(), null));
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }, "No, cerrar", (dialog, which) -> dialog.dismiss(), false);
//        }
//    }
//
//    public void showDialog(String msg, String positiveLabel, DialogInterface.OnClickListener onClickListener,
//                           String negativeLevel, DialogInterface.OnClickListener onCancelListener, boolean isCancelable) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Aviso!!");
//        builder.setMessage(msg);
//        builder.setCancelable(isCancelable);
//        builder.setPositiveButton(positiveLabel, onClickListener);
//        builder.setNegativeButton(negativeLevel, onCancelListener);
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

    //------------------------------ ACTIVITY RESULT ---------------------- //

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_REQUEST_CODE:
                switch (resultCode) {
                    case FROM_DETAIL_TO_MAP:
                        if (data.getExtras() != null) {
                            ArrayList<GeoRent> geoRentList = data.getParcelableArrayListExtra("geoRents");
                            if (geoRentList != null && geoRentList.size() > 0) {
                                mFragment = MapFragment.newInstance(geoRentList, true);
                                fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.frame_container, mFragment).commit();
                                homeBinding.navView.getMenu().findItem(R.id.nav_map).setChecked(true);
                            }
                        }
                        break;
                    case FROM_DETAIL_REFRESH:
                        if (mFragment instanceof RentListFragment)
                            ((RentListFragment) mFragment).needToRefresh(data.getBooleanExtra("refresh", false));
                        break;
                    case FROM_DETAIL_REFRESH_SHOW_GROUP:
                        invalidateOptionsMenu();
                        if (mFragment instanceof RentListFragment)
                            ((RentListFragment) mFragment).needToRefresh(data.getBooleanExtra("refresh", false));
                        break;
                    case FROM_DETAIL_SHOW_GROUP:
                        invalidateOptionsMenu();
                        break;
                }
                break;
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        AlertUtils.showErrorToast(this, "Imposible ser localizado");
                        break;
                }
                break;
            case LOGIN_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (data != null && data.hasExtra(USER_NAME)) {
                            showLoginSuccessAlert(data.getStringExtra(USER_NAME));
                        }
                        break;
                }
                break;
        }
    }

    // ----------------------- INSTANCE STATE ----------------------------------//

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, STATE_ACTIVE_FRAGMENT, mFragment);
        super.onSaveInstanceState(outState);
    }

    // ------------------------ USER CLICKS EVENTS ---------------------------- //

    @Override
    public void onItemClick(View view, ViewModel baseItem) {
        if (baseItem instanceof HeaderItem) {
            String provinceName = sharedPreferences.getString(PROVINCE_UUID, PROVINCE_UUID_DEFAULT);
            mFragment = RentListFragment.newInstance(provinceName, "", ((HeaderItem) baseItem).getOrderType());
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container,
                    mFragment).commit();
            homeBinding.navView.getMenu().getItem(1).setChecked(true);
        } else if (baseItem instanceof RZoneItem) {
            String provinceName = sharedPreferences.getString(PROVINCE_UUID, PROVINCE_UUID_DEFAULT);
            mFragment = RentListFragment.newInstance(provinceName, ((RZoneItem) baseItem).getId(), ORDER_TYPE_POPULAR);
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container,
                    mFragment).commit();
            homeBinding.navView.getMenu().getItem(1).setChecked(true);
        } else if (baseItem instanceof BaseItem) {
            navigateToDetailActivity((BaseItem) baseItem, view);
        }
    }


    private void navigateToDetailActivity(BaseItem baseItem, View view) {
        Intent intent = new Intent(HomeActivity.this, RentDetailActivity.class);
        intent.putExtra("uuid", baseItem.getId());
        intent.putExtra("url", baseItem.getImagePath());
        intent.putExtra("wished", baseItem.getWished());
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    // ---------------------------- MENU ---------------------------------- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        checkMenuItemsVisibility(menu);
        return super.onPrepareOptionsMenu(menu);
    }


    public void checkMenuItemsVisibility(Menu menu) {
        MenuItem menuFilter = menu.findItem(R.id.action_filter_panel);
        menuFilter.setVisible(false);
        menu.findItem(R.id.action_filter_panel).setVisible(mFragment instanceof RentListFragment);
        menu.findItem(R.id.action_gps).setVisible(mFragment instanceof MapFragment);
        menu.findItem(R.id.action_refresh).setVisible(mFragment instanceof ProfileFragment);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_panel:
                //FILTER
                if (filterFragment == null) {
                    String provinceId = sharedPreferences.getString(PROVINCE_UUID, PROVINCE_UUID_DEFAULT);
                    filterFragment = FilterFragment.newInstance(provinceId);
                }
                fragmentManager.beginTransaction().replace(R.id.filter_container, filterFragment).commit();
                homeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                homeBinding.drawerLayout.postDelayed(() -> homeBinding.drawerLayout.openDrawer(Gravity.END), 200);
                return true;
            case R.id.action_gps:
//                initLocation(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void initLocation(MenuItem item) {
////        if (checkSinglePermission(locationPerm, LOCATION_REQUEST_CODE)) {
//        if (!mRequestingLocationUpdates) {
//            mRequestingLocationUpdates = true;
//            changeMenuIcon(item);
//            startLocationUpdates();
//        } else {
//            stopLocationUpdates();
//        }
////        }
//    }

    private void changeMenuIcon(MenuItem item) {
        if (mRequestingLocationUpdates) {
            item.setIcon(R.drawable.ic_crosshairs_focus);
        } else {
            item.setIcon(R.drawable.ic_crosshairs);
        }
    }

    private void confirmLogout() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Aviso!")
                .setContentText("Desea finalizar la sesión")
                .hideConfirmButton()
                .setCancelText("Si, lo deseo")
                .setCancelClickListener(sweetAlertDialog1 -> {
                    sweetAlertDialog1.dismissWithAnimation();
                    new Handler().postDelayed(HomeActivity.this::logoutPetition, 400);
                });
        sweetAlertDialog.setOnShowListener(dialog -> {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = CENTER;
            sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setLayoutParams(params);
            sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setPadding(15, 15, 15, 15);
        });
        sweetAlertDialog.show();
    }

    private void logoutPetition() {
        if (mFragment instanceof ProfileFragment) {
            onNavigationItemSelected(homeBinding.navView.getMenu().findItem(R.id.nav_home));
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_IS_AUTH, false);
        editor.putBoolean(IS_PROFILE_ACTIVE, false);
        editor.apply();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        boolean sameFragment = true;
        if (id == R.id.nav_home && !(mFragment instanceof HomeFragment)) {
            mFragment = HomeFragment.newInstance();
            sameFragment = false;
        } else if (id == R.id.nav_list && !(mFragment instanceof RentListFragment)) {
            String provinceName = sharedPreferences.getString(PROVINCE_UUID, PROVINCE_UUID_DEFAULT);
            mFragment = RentListFragment.newInstance(provinceName, "", ORDER_TYPE_POPULAR);
            sameFragment = false;
        } else if (id == R.id.nav_profile && !(mFragment instanceof ProfileFragment)) {
            mFragment = ProfileFragment.newInstance();
            sameFragment = false;
        } else if (id == R.id.nav_map && !(mFragment instanceof MapFragment)) {
            mFragment = MapFragment.newInstance(null, false);
            sameFragment = false;
        } else if (id == R.id.nav_wish_list && !(mFragment instanceof ListWishFragment)) {
            mFragment = ListWishFragment.newInstance();
            sameFragment = false;
        } else if (id == R.id.nav_info && !(mFragment instanceof InfoFragment)) {
            mFragment = InfoFragment.newInstance();
            sameFragment = false;
        } else if (id == R.id.nav_auth && !(mFragment instanceof AuthFragment)) {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(HomeActivity.this, UserAuthActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }, 500);
        } else if (id == R.id.nav_my_rents && !(mFragment instanceof MyRentsFragment)) {
            mFragment = MyRentsFragment.newInstance("1");
            sameFragment = false;
        }
        if (mFragment != null && !sameFragment) {
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Close drawer
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START, true);
            homeBinding.drawerLayout.postDelayed(() -> {
                // Inflate the new Fragment with the new RecyclerView and a new Adapter
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, mFragment).commit();

            }, 700);

            return true;
        } else {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START, true);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (homeBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (homeBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.END);
            homeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
            return;
        }
        if (mFragment instanceof HomeFragment) {
            AlertUtils.showInfoAlertAndFinishApp(this);
        } else {
            MenuItem menuItem = homeBinding.navView.getMenu().findItem(R.id.nav_home);
            onNavigationItemSelected(menuItem);
        }
    }


    private void showLoginSuccessAlert(String username) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle("En hora buena");
        builder.setMessage(String.format("%s, es un gusto recibirlo/a", username));
        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.setAutoDismissAfter(3000);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.show();
    }

    private void updateNavHeader(String username, @Nullable String avatar) {
        String url = null;
        if (avatar != null)
            url = BASE_URL_API + "/" + avatar;
        CutCornerView headerView = (CutCornerView) homeBinding.navView.getHeaderView(0);
        CircularImageView circularImageView = headerView.findViewById(R.id.user_avatar);
        TextView tvUsername = headerView.findViewById(R.id.tv_username);
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.user_placeholder)
                .into(circularImageView);
        tvUsername.setText(String.format(username));
    }

    @Override
    public void onMapInteraction(GeoRent geoRent) {
        Intent intent = new Intent(HomeActivity.this, RentDetailActivity.class);
        intent.putExtra("uuid", geoRent.getId());
        intent.putExtra("url", geoRent.getImagePath());
        intent.putExtra("wished", geoRent.getWished());
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    // -------------------------- FILTER --------------------------------------------- //

    @Override
    public void closeFilter() {
        if (homeBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.END);
            homeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
        }
    }

    @Override
    public void onFilterElement(PagedList<RentListItem> resourceResult) {
        if (mFragment instanceof RentListFragment) {
            ((RentListFragment) mFragment).filterListResult(resourceResult);
        }
    }

    @Override
    public void onFilterClean() {
        if (mFragment instanceof RentListFragment) {
            ((RentListFragment) mFragment).needToRefresh(true);
        }
    }

    //---------------------------- INFO ------------------------------------------------//

    @Override
    public void startSync() {
        Intent intent = new Intent(this, SyncActivity.class);
        intent.putExtra(ALTERNATIVE_SYNC, true);
        startActivity(intent);
    }

    @Override
    public void showPoliticsDialog() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://192.168.137.1:8000/infocuba/6"));
        startActivity(intent);
    }

    @Override
    public void showFeedbackDialog() {
        DialogFeedback dialogFeedback = DialogFeedback.newInstance();
        dialogFeedback.show(fragmentManager, "DialogFeedback");
    }

    @Override
    public void sendFeedback(String feedback) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "bookingqba.app@gmail.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, feedback);
        startActivity(Intent.createChooser(intent, "Enviar email..."));
    }

    // -------------------------- LIVECYCLE ACTIVITY METHOD -------------------------- //

    @Override
    public void onAddRentClick() {
        Intent intent = new Intent(this, AddRentActivity.class);
        startActivity(intent);
    }

    //--------------------------------------- OTHER LOCATION CONFIG --------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public LocationConfiguration getLocationConfiguration() {
        return new LocationConfiguration.Builder()
                .askForPermission(new PermissionConfiguration.Builder().rationaleMessage("Give permission").build())
                .useGooglePlayServices(new GooglePlayServicesConfiguration.Builder().build())
                .useDefaultProviders(new DefaultProviderConfiguration.Builder()
                        .acceptableAccuracy(5.0f)
                        .requiredDistanceInterval(0)
                        .gpsMessage("Turn GPS on?")
                        .build())
                .keepTracking(true)
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "onLocationChanged", Toast.LENGTH_SHORT).show();
        if(mFragment instanceof MapFragment)
            ((MapFragment) mFragment).updateCurrentLocation(location);
    }

    @Override
    public void onLocationFailed(int type) {
        switch (type) {
            case FailType.TIMEOUT: {
                showToast("Couldn't get location, and timeout!");
                break;
            }
            case FailType.PERMISSION_DENIED: {
                showToast("Couldn't get location, because user didn't give permission!");
                break;
            }
            case FailType.NETWORK_NOT_AVAILABLE: {
                showToast("Couldn't get location, because network is not accessible!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE: {
                showToast("Couldn't get location, because Google Play Services not available!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_CONNECTION_FAIL: {
                showToast("Couldn't get location, because Google Play Services connection failed!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DIALOG: {
                showToast("Couldn't display settingsApi dialog!");
                break;
            }
            case FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DENIED: {
                showToast("Couldn't get location, because user didn't activate providers via settingsApi!");
                break;
            }
            case FailType.VIEW_DETACHED: {
                showToast("Couldn't get location, because in the process view was detached!");
                break;
            }
            case FailType.VIEW_NOT_REQUIRED_TYPE: {
                showToast("Couldn't get location, "
                        + "because view wasn't sufficient enough to fulfill given configuration!");
                break;
            }
            case FailType.UNKNOWN: {
                showToast("Ops! Something went wrong!");
                break;
            }
        }
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
