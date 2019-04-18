package com.infinitum.bookingqba.view.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityHomeBinding;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.service.SendDataWorker;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.home.HeaderItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentNewItem;
import com.infinitum.bookingqba.view.adapters.items.home.RentPopItem;
import com.infinitum.bookingqba.view.adapters.items.home.RZoneItem;
import com.infinitum.bookingqba.view.adapters.items.rentlist.RentListItem;
import com.infinitum.bookingqba.view.interaction.FilterInteraction;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;
import com.infinitum.bookingqba.view.filter.FilterFragment;
import com.infinitum.bookingqba.view.interaction.LoginInteraction;
import com.infinitum.bookingqba.view.map.MapFragment;
import com.infinitum.bookingqba.view.profile.LoginFragment;
import com.infinitum.bookingqba.view.profile.ProfileFragment;
import com.infinitum.bookingqba.view.rents.RentDetailActivity;
import com.infinitum.bookingqba.view.rents.RentListFragment;
import com.infinitum.bookingqba.view.sync.SyncActivity;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_RENTS;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class HomeActivity extends DaggerAppCompatActivity implements HasSupportFragmentInjector,
        FragmentNavInteraction, NavigationView.OnNavigationItemSelectedListener,
        FilterInteraction, LoginInteraction, MapFragment.OnFragmentMapInteraction {

    private static final String STATE_ACTIVE_FRAGMENT = "active_fragment";
    private static final String NOTIFICATION_DEFAULT = "default";
    private static final int NOTIFICATION_ID = 1;
    private ActivityHomeBinding homeBinding;
    private FragmentManager fragmentManager;
    private Fragment mFragment;
    private FilterFragment filterFragment;


    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;


    @Inject
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        AndroidInjection.inject(this);

        setupToolbar();

        initializeFragment(savedInstanceState);

        initDrawerLayout();

        initWorkRequest();

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
        WorkManager.getInstance().enqueueUniquePeriodicWork("MyPeriodicalWork", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
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
        homeBinding.drawerLayout.setViewScale(Gravity.START, 0.9f);
        homeBinding.drawerLayout.setRadius(Gravity.START, 35);
        homeBinding.drawerLayout.setViewElevation(Gravity.START, 20);
    }

    private void setupToolbar() {
        setSupportActionBar(homeBinding.toolbar);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, STATE_ACTIVE_FRAGMENT, mFragment);
        super.onSaveInstanceState(outState);
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
    public void onItemClick(View view, BaseItem baseItem) {
        if (baseItem instanceof HeaderItem) {
            mFragment = RentListFragment.newInstance("", "", ((HeaderItem) baseItem).getOrderType());
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container,
                    mFragment).commit();
            homeBinding.navView.getMenu().getItem(1).setChecked(true);
        } else if (baseItem instanceof RZoneItem) {
            Toast.makeText(this, baseItem.getmName(), Toast.LENGTH_SHORT).show();
        } else if (baseItem instanceof RentPopItem) {
            Toast.makeText(this, baseItem.getmName(), Toast.LENGTH_SHORT).show();
        } else if (baseItem instanceof RentNewItem) {
            Toast.makeText(this, baseItem.getmName(), Toast.LENGTH_SHORT).show();
        } else if (baseItem instanceof RentListItem) {
            Intent intent = new Intent(HomeActivity.this, RentDetailActivity.class);
            intent.putExtra("uuid", baseItem.getId());
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        checkMenuItemsVisibility(menu);
        return super.onPrepareOptionsMenu(menu);
    }


    public void checkMenuItemsVisibility(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_filter_panel);
        menuItem.setVisible(false);
        boolean loginVisibility = sharedPreferences.getBoolean(USER_IS_AUTH, false);
        MenuItem login = menu.findItem(R.id.action_login);
        login.setVisible(!loginVisibility);
        MenuItem logout = menu.findItem(R.id.action_logout);
        logout.setVisible(loginVisibility);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_panel:
                //FILTER
                if (filterFragment == null) {
                    filterFragment = FilterFragment.newInstance();
                }
                fragmentManager.beginTransaction().replace(R.id.filter_container, filterFragment).commit();
                homeBinding.drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        homeBinding.drawerLayout.openDrawer(Gravity.END);
                    }
                }, 200);
                break;
            case R.id.action_login:
                LoginFragment lf = LoginFragment.newInstance();
                lf.show(fragmentManager, "LoginFragment");
                return true;
            case R.id.action_logout:
                logoutPetition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void logoutPetition() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_IS_AUTH, false);
        editor.apply();
        invalidateOptionsMenu();
        homeBinding.navView.getMenu().setGroupVisible(R.id.group_2, false);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            mFragment = HomeFragment.newInstance();
        } else if (id == R.id.nav_list) {
            mFragment = RentListFragment.newInstance("", "", ' ');
        } else if (id == R.id.nav_profile) {
            mFragment = ProfileFragment.newInstance();
        } else if (id == R.id.nav_map) {
            mFragment = MapFragment.newInstance();
        }
        if (mFragment != null) {
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Close drawer
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START, true);
            homeBinding.drawerLayout.postDelayed(() -> {
                // Inflate the new Fragment with the new RecyclerView and a new Adapter
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, mFragment).commit();

            }, 300);

            return true;
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
            return;
        }
        if (mFragment instanceof HomeFragment) {
            super.onBackPressed();
        } else {
            MenuItem menuItem = homeBinding.navView.getMenu().findItem(R.id.nav_home);
            onNavigationItemSelected(menuItem);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onFilterClick(Map<String, List<String>> map) {

    }

    @Override
    public void onLogin(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_IS_AUTH, true);
        editor.putString(USER_TOKEN, user.getToken());
        editor.putString(USER_NAME, user.getUserName());
        editor.putStringSet(USER_RENTS, new HashSet<>(user.getRentsId()));
        editor.apply();
        invalidateOptionsMenu();
    }

    @Override
    public void showNotificationToUpdate(String msg) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(NOTIFICATION_ID);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_DEFAULT, "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, SyncActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_DEFAULT)
                .setContentTitle("BookingQBA")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    @Override
    public void showGroupMenuProfile(boolean show) {
        homeBinding.navView.getMenu().setGroupVisible(R.id.group_2, show);
    }


    @Override
    public void onMapInteraction() {

    }


}
