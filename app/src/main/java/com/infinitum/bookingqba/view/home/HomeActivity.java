package com.infinitum.bookingqba.view.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.Chart;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityHomeBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.ReservationType;
import com.infinitum.bookingqba.service.SendDataWorker;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;
import com.infinitum.bookingqba.view.base.LocationActivity;
import com.infinitum.bookingqba.view.calendar.CalendarFragment;
import com.infinitum.bookingqba.view.customview.SuccessDialogContentView;
import com.infinitum.bookingqba.view.info.DialogFeedback;
import com.infinitum.bookingqba.view.info.InfoFragment;
import com.infinitum.bookingqba.view.interaction.FilterInteraction;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;
import com.infinitum.bookingqba.view.filter.FilterFragment;
import com.infinitum.bookingqba.view.interaction.InfoInteraction;
import com.infinitum.bookingqba.view.interaction.ProfileInteraction;
import com.infinitum.bookingqba.view.listwish.ListWishFragment;
import com.infinitum.bookingqba.view.map.MapFragment;
import com.infinitum.bookingqba.view.profile.AddRentActivity;
import com.infinitum.bookingqba.view.profile.MyRentsFragment;
import com.infinitum.bookingqba.view.profile.ProfileFragment;
import com.infinitum.bookingqba.view.auth.UserAuthActivity;
import com.infinitum.bookingqba.view.rents.DetailActivity;
import com.infinitum.bookingqba.view.rents.RentListFragment;
import com.infinitum.bookingqba.view.reservation.BookRequestListFragment;
import com.infinitum.bookingqba.view.reservation.ReservationDetailActivity;
import com.infinitum.bookingqba.view.reservation.ReservationListFragment;
import com.infinitum.bookingqba.view.sync.SyncActivity;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.infinitum.bookingqba.service.LocationService.KEY_REQUESTING_LOCATION_UPDATES;
import static com.infinitum.bookingqba.util.Constants.ALTERNATIVE_SYNC;
import static com.infinitum.bookingqba.util.Constants.BASE_URL_API;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_TO_MAP;
import static com.infinitum.bookingqba.util.Constants.FROM_RESERVATION_DETAIL_TO_LIST;
import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.LOGIN_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.MY_REQUEST_CODE;
import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_MOST_COMMENTED;
import static com.infinitum.bookingqba.util.Constants.PERIODICAL_WORK_NAME;
import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID;
import static com.infinitum.bookingqba.util.Constants.PROVINCE_UUID_DEFAULT;
import static com.infinitum.bookingqba.util.Constants.USER_AVATAR;
import static com.infinitum.bookingqba.util.Constants.USER_HAS_ACTIVE_RENT;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.WRITE_EXTERNAL_REQUEST_CODE;

public class HomeActivity extends LocationActivity implements HasSupportFragmentInjector,
        FragmentNavInteraction, NavigationView.OnNavigationItemSelectedListener,
        FilterInteraction, MapFragment.OnFragmentMapInteraction, InfoInteraction,
        DialogFeedback.FeedbackInteraction, MyRentsFragment.AddRentClick, ProfileInteraction {

    private static final String STATE_ACTIVE_FRAGMENT = "active_fragment";
    private ActivityHomeBinding homeBinding;
    private FragmentManager fragmentManager;
    private Fragment mFragment;
    private FilterFragment filterFragment;

    //------------------------- LOCATION VAR --------------------------//
    private Location currentLocation;
    //-------------------------- LOGING FRAGMENT ------------------------------//
    private boolean loginIsClicked = false;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    SharedPreferences sharedPreferences;
    private boolean filterActive;
    private Chart lastChartToExport;
    private String lastNameToExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        String uuid = sharedPreferences.getString(IMEI, "");
        if (uuid.equals("")) {
            uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(IMEI, uuid).apply();
        }

        setupToolbar();

        initDrawerLayout();

        initWorkRequest();

        initializeFragment(savedInstanceState);

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
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit);
        ft.replace(R.id.frame_container,
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
                this, homeBinding.drawerLayout, homeBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (drawerView.getId() == R.id.nav_view_notification) {
                    showFilterPanel();
                }
            }
        };
        homeBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        homeBinding.navView.getMenu().getItem(0).setChecked(true);
        homeBinding.navView.setNavigationItemSelectedListener(this);
        homeBinding.navView.getChildAt(0).setVerticalScrollBarEnabled(false);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) homeBinding.navViewNotification.getLayoutParams();
        params.width = metrics.widthPixels;
        homeBinding.navViewNotification.setLayoutParams(params);

        homeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

        if (sharedPreferences.getBoolean(USER_IS_AUTH, false)) {
            homeBinding.navView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            homeBinding.navView.getMenu().findItem(R.id.nav_wish_list).setVisible(true);
            homeBinding.navView.getMenu().findItem(R.id.nav_my_rents).setVisible(true);
            homeBinding.navView.getMenu().findItem(R.id.nav_my_book_request).setVisible(true);
            homeBinding.navView.getMenu().findItem(R.id.nav_auth).setVisible(false);
            updateNavHeader(sharedPreferences.getString(USER_NAME, ""), sharedPreferences.getString(USER_AVATAR, ""));
        }

        refreshMenuIfUserIsActiveHost(sharedPreferences.getBoolean(USER_HAS_ACTIVE_RENT, false));

        homeBinding.drawerLayout.useCustomBehavior(Gravity.START);

    }


    //------------------------------ ACTIVITY RESULT ---------------------- //

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_REQUEST_CODE:
                switch (resultCode) {
                    case FROM_DETAIL_TO_MAP:
                        if (data != null && data.getExtras() != null) {
                            ArrayList<GeoRent> geoRentList = data.getParcelableArrayListExtra("geoRents");
                            if (geoRentList != null && geoRentList.size() > 0) {
                                if (filterActive) {
                                    filterFragment.resetAllParams();
                                    onFilterClean();
                                }
                                mFragment = MapFragment.newInstance(geoRentList, true);
                                fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.frame_container, mFragment).commit();
                                homeBinding.navView.getMenu().findItem(R.id.nav_map).setChecked(true);
                            }
                        }
                        break;
                    case FROM_RESERVATION_DETAIL_TO_LIST:
                        if (data != null && data.hasExtra("refresh")) {
                            boolean needRefresh = data.getBooleanExtra("refresh", false);
                            if (needRefresh && mFragment instanceof ReservationListFragment)
                                ((ReservationListFragment) mFragment).onRefresh();
                        }
                        break;
                }
                break;
            case LOGIN_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (data != null && data.hasExtra(USER_NAME)) {
                            homeBinding.navView.getMenu().findItem(R.id.nav_logout).setVisible(true);
                            homeBinding.navView.getMenu().findItem(R.id.nav_wish_list).setVisible(true);
                            homeBinding.navView.getMenu().findItem(R.id.nav_my_rents).setVisible(true);
                            homeBinding.navView.getMenu().findItem(R.id.nav_my_book_request).setVisible(true);
                            homeBinding.navView.getMenu().findItem(R.id.nav_auth).setVisible(false);
                            refreshMenuIfUserIsActiveHost(data.getBooleanExtra(USER_HAS_ACTIVE_RENT, false));
                            showLoginSuccessAlert();
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
    public void onItemClick(View view, BaseItem baseItem) {
        if (baseItem != null) {
            navigateToDetailActivity(baseItem, view);
        }
    }

    @Override
    public void onViewAllClick(char orderType) {
        goToRentListWithOrderType(orderType);
    }


    private void navigateToDetailActivity(BaseItem baseItem, View view) {
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
        intent.putExtra("item", baseItem);
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
        menu.findItem(R.id.action_filter_panel).setVisible(mFragment instanceof RentListFragment);
        Drawable icon = getResources().getDrawable(R.drawable.ic_controls_4_set);
        if (filterActive) {
            icon.setTint(getResources().getColor(R.color.colorAccent));
            menu.findItem(R.id.action_filter_panel).setIcon(icon);
        } else {
            icon.setTint(getResources().getColor(R.color.White_100));
            menu.findItem(R.id.action_filter_panel).setIcon(icon);
        }
        menu.findItem(R.id.action_search).setVisible(mFragment instanceof RentListFragment);
        menu.findItem(R.id.action_refresh).setVisible(mFragment instanceof ProfileFragment);
        menu.findItem(R.id.action_block).setVisible(mFragment instanceof CalendarFragment);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_panel:
                homeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                homeBinding.drawerLayout.openDrawer(Gravity.END, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterPanel() {
        //FILTER
        if (filterFragment == null) {
            String provinceId = sharedPreferences.getString(PROVINCE_UUID, PROVINCE_UUID_DEFAULT);
            filterFragment = FilterFragment.newInstance(provinceId);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit);
        ft.replace(R.id.filter_container, filterFragment).commit();
    }

    private void confirmLogout() {
        AlertUtils.showCFInfoAlertWithAction(this, "Esta seguro que desea finalizar la sesion",
                "Si", (dialog, which) -> {
                    logoutPetition();
                    dialog.dismiss();
                });
    }

    private void logoutPetition() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_IS_AUTH, false);
        editor.putBoolean(USER_HAS_ACTIVE_RENT, false);
        editor.apply();
        homeBinding.navView.getMenu().findItem(R.id.nav_auth).setVisible(true);
        homeBinding.navView.getMenu().findItem(R.id.nav_wish_list).setVisible(false);
        homeBinding.navView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        homeBinding.navView.getMenu().findItem(R.id.nav_my_rents).setVisible(false);
        homeBinding.navView.getMenu().findItem(R.id.nav_my_book_request).setVisible(false);
        refreshMenuIfUserIsActiveHost(false);
        updateNavHeader("", "");
        MenuItem menuItem = homeBinding.navView.getMenu().findItem(R.id.nav_home);
        onNavigationItemSelected(menuItem);
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
            mFragment = RentListFragment.newInstance(provinceName, ORDER_TYPE_MOST_COMMENTED);
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
        } else if (id == R.id.nav_auth) {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(HomeActivity.this, UserAuthActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }, 500);
        } else if (id == R.id.nav_logout) {
            confirmLogout();
        } else if (id == R.id.nav_my_rents && !(mFragment instanceof MyRentsFragment)) {
            mFragment = MyRentsFragment.newInstance();
            sameFragment = false;
        } else if (id == R.id.nav_book_request && !(mFragment instanceof ReservationListFragment)) {
            mFragment = ReservationListFragment.newInstance();
            sameFragment = false;
        } else if (id == R.id.nav_my_book_request && !(mFragment instanceof BookRequestListFragment)) {
            mFragment = BookRequestListFragment.newInstance();
            sameFragment = false;
        } else if (id == R.id.nav_my_calendar && !(mFragment instanceof CalendarFragment)) {
            mFragment = CalendarFragment.newInstance();
            sameFragment = false;
        }
        if (mFragment != null && !sameFragment) {
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Close drawer
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
            homeBinding.drawerLayout.postDelayed(() -> {
                // Inflate the new Fragment with the new RecyclerView and a new Adapter
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit);
                ft.replace(R.id.frame_container, mFragment).commit();
            }, 700);


            return true;
        } else {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
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
        if (filterActive) {
            filterFragment.resetAllParams();
            onFilterClean();
            return;
        }
        if (mFragment instanceof HomeFragment) {
            if (timeToGo == 0) {
                Toast.makeText(this, "Presione de nuevo para salir", Toast.LENGTH_SHORT).show();
                timeToGo = 1;
                new Handler().postDelayed(() -> timeToGo = 0, 5000);
            } else {
                super.onBackPressed();
            }
        } else {
            MenuItem menuItem = homeBinding.navView.getMenu().findItem(R.id.nav_home);
            onNavigationItemSelected(menuItem);
        }
    }


    private void showLoginSuccessAlert() {
        SuccessDialogContentView successDialogContentView = new SuccessDialogContentView(this);
        String avatar = sharedPreferences.getString(USER_AVATAR, "");
        String name = sharedPreferences.getString(USER_NAME, "");
        successDialogContentView.updateUserView(avatar, name);
        AlertUtils.showSuccessLogin(HomeActivity.this, successDialogContentView);
        updateNavHeader(name, avatar);
    }

    private void updateNavHeader(String username, String avatar) {
        LinearLayout linearHeader = (LinearLayout) homeBinding.navView.getHeaderView(0);
        CircularImageView circularImageView = linearHeader.findViewById(R.id.user_avatar);
        TextView tvUsername = linearHeader.findViewById(R.id.tv_username);
        if (!username.equals("") && !avatar.equals("")) {
            String url = BASE_URL_API + "/" + avatar;
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.user_placeholder)
                    .into(circularImageView);
            tvUsername.setText(username);
        } else {
            circularImageView.setImageResource(R.drawable.user_placeholder);
            tvUsername.setText("Hola invitado");
        }
    }


    @Override
    public void onBookItemClick(ReservationItem item, ReservationType type) {
        Intent intent = new Intent(HomeActivity.this, ReservationDetailActivity.class);
        intent.putExtra("reservationItem", item);
        intent.putExtra("accepted", type == ReservationType.ACCEPTED);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    //---------------------------- MAP ----------------------------------------

    @Override
    public void shortCutToMap(List<GeoRent> geoRentList) {
        mFragment = MapFragment.newInstance(new ArrayList<>(geoRentList), false);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,
                mFragment).commit();
        homeBinding.navView.getMenu().getItem(3).setChecked(true);
    }

    @Override
    public void onMapInteraction(GeoRent geoRent) {
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
        intent.putExtra("item", geoRent);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    @Override
    public void getLastLocation() {
        if (sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)) {
            AlertUtils.showSuccessLocationToast(this, "Ubicando...");
        } else {
            startLocationRequestUnknowPermission();
        }
    }

    @Override
    protected void updateLocation(Location location) {
        currentLocation = location;
        if (mFragment instanceof MapFragment && ((MapFragment) mFragment).isMarkerLayerLoaded())
            ((MapFragment) mFragment).updateCurrentLocation(location);
        if (homeBinding.drawerLayout.isDrawerOpen(GravityCompat.END) && filterFragment != null)
            filterFragment.setUserLocation(location);
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
    public void onFilterElement(Resource<List<GeoRent>> resourceResult, @Nullable Location lastLocationKnow) {
        if (mFragment instanceof RentListFragment) {
            filterActive = true;
            ((RentListFragment) mFragment).filterListResult(resourceResult, lastLocationKnow);
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onFilterClean() {
        if (mFragment instanceof RentListFragment) {
            filterActive = false;
            ((RentListFragment) mFragment).needToRefresh(true);
            invalidateOptionsMenu();
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

    @Override
    public void onAddRentClick() {
        Intent intent = new Intent(this, AddRentActivity.class);
        startActivity(intent);
    }

    @Override
    public void onEditRent(String uuid) {
        Intent intent = new Intent(this, AddRentActivity.class);
        intent.putExtra("id", uuid);
        intent.putExtra("edit", true);
        startActivity(intent);
    }

    @Override
    public void refreshMenuIfUserIsActiveHost(boolean userHasRentActive) {
        if (userHasRentActive) {
            homeBinding.navView.getMenu().findItem(R.id.nav_book_request).setVisible(true);
            homeBinding.navView.getMenu().findItem(R.id.nav_my_calendar).setVisible(true);
            homeBinding.navView.getMenu().findItem(R.id.nav_profile).setVisible(true);
        } else {
            homeBinding.navView.getMenu().findItem(R.id.nav_book_request).setVisible(false);
            homeBinding.navView.getMenu().findItem(R.id.nav_my_calendar).setVisible(false);
            homeBinding.navView.getMenu().findItem(R.id.nav_profile).setVisible(false);
        }
    }

    private void goToRentListWithOrderType(char orderType) {
        String provinceName = sharedPreferences.getString(PROVINCE_UUID, PROVINCE_UUID_DEFAULT);
        mFragment = RentListFragment.newInstance(provinceName, orderType);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit);
        ft.replace(R.id.frame_container,
                mFragment).commit();
        homeBinding.navView.getMenu().getItem(1).setChecked(true);
    }

    //--------------------------- PROFILE --------------------------------------------

    @Override
    public void exportToGalery(Chart chart, String name) {
        lastChartToExport = chart;
        lastNameToExport = name;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            saveToGallery(chart, name);
        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertUtils.showCFInfoNotificationWithAction(this, "Aviso!!", "Permisos de escritura requeridos para exportar imagen.",
                    (dialog, view) -> {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_REQUEST_CODE);
                    }, "Otorgar");
        } else {
            AlertUtils.showErrorToast(getApplicationContext(), "Permisos requeridos");
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_REQUEST_CODE);
        }
    }

    protected void saveToGallery(Chart chart, String name) {
        if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70))
            AlertUtils.showSuccessToast(getApplicationContext(), "Guardada en galeria");
        else
            AlertUtils.showErrorToast(getApplicationContext(), "Error al guardar");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToGallery(lastChartToExport, lastNameToExport);
            } else {
                AlertUtils.showErrorToast(getApplicationContext(), "Error al guardar");
            }
        }
    }

    // -------------------------- LIVECYCLE ACTIVITY METHOD -------------------------- //

    @Override
    protected void onDestroy() {
        filterFragment = null;
        System.gc();
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

}
