package com.infinitum.bookingqba.view.home;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.vivchar.rendererrecyclerviewadapter.CompositeViewHolder;
import com.github.vivchar.rendererrecyclerviewadapter.CompositeViewState;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.github.vivchar.rendererrecyclerviewadapter.ViewState;
import com.github.vivchar.rendererrecyclerviewadapter.binder.CompositeViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.CompositeViewStateProvider;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityHomeBinding;
import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.headeritem.HeaderItem;
import com.infinitum.bookingqba.view.adapters.composite.RecyclerViewItem;
import com.infinitum.bookingqba.view.adapters.rentitem.RentNewItem;
import com.infinitum.bookingqba.view.adapters.rentitem.RentPopItem;
import com.infinitum.bookingqba.view.adapters.rzoneitem.RZoneItem;
import com.infinitum.bookingqba.view.interaction.FilterInteraction;
import com.infinitum.bookingqba.view.interaction.FragmentNavInteraction;
import com.infinitum.bookingqba.view.rents.FilterFragment;
import com.infinitum.bookingqba.view.rents.RentListFragment;
import com.infinitum.bookingqba.view.widgets.BetweenSpacesItemDecoration;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Collections;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;

public class HomeActivity extends DaggerAppCompatActivity implements HasSupportFragmentInjector,
        FragmentNavInteraction, NavigationView.OnNavigationItemSelectedListener,
        FilterInteraction{

    private static final String STATE_ACTIVE_FRAGMENT = "active_fragment";
    private ActivityHomeBinding homeBinding;
    private FragmentManager fragmentManager;
    private Fragment mFragment;



    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        AndroidInjection.inject(this);

        setupToolbar();

        initializeFragment(savedInstanceState);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, homeBinding.drawerLayout, homeBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        homeBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        homeBinding.navView.setNavigationItemSelectedListener(this);

        homeBinding.drawerLayout.setViewScale(Gravity.START, 0.9f);
        homeBinding.drawerLayout.setRadius(Gravity.START, 35);
        homeBinding.drawerLayout.setViewElevation(Gravity.START, 20);


    }

    private void setupToolbar() {
        setSupportActionBar(homeBinding.toolbar);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
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
            mFragment = RentListFragment.newInstance("","");
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container,
                    mFragment).commit();
        }else if (baseItem instanceof RZoneItem) {
            Toast.makeText(this, baseItem.getmName(), Toast.LENGTH_SHORT).show();
        } else if (baseItem instanceof RentPopItem) {
            Toast.makeText(this, baseItem.getmName(), Toast.LENGTH_SHORT).show();
        }else if (baseItem instanceof RentNewItem) {
            Toast.makeText(this, baseItem.getmName(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_filter_panel:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.filter_container,
                        FilterFragment.newInstance()).commit();
                homeBinding.drawerLayout.openDrawer(Gravity.END);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (homeBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void pulse() {
        Toast.makeText(this, "Pulso el Boton", Toast.LENGTH_SHORT).show();
    }
}
