package com.infinitum.bookingqba.di.module;

import com.infinitum.bookingqba.view.filter.FilterFragment;
import com.infinitum.bookingqba.view.home.HomeFragment;
import com.infinitum.bookingqba.view.base.BasePageFragment;
import com.infinitum.bookingqba.view.map.MapFragment;
import com.infinitum.bookingqba.view.profile.LoginFragment;
import com.infinitum.bookingqba.view.rents.RentListFragment;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface FragmentModule {

    @ContributesAndroidInjector
    BasePageFragment bindBasePageFragment();

    @ContributesAndroidInjector
    HomeFragment bindHomeFragment();

    @ContributesAndroidInjector
    RentListFragment bindRentListFragment();

    @ContributesAndroidInjector
    FilterFragment bindFilterFragment();

    @ContributesAndroidInjector
    LoginFragment bindLoginFragment();

    @ContributesAndroidInjector
    MapFragment bindMapFragment();



}
