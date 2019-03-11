package com.infinitum.bookingqba.di.module;

import com.infinitum.bookingqba.view.home.HomeFragment;
import com.infinitum.bookingqba.view.base.BasePageFragment;
import com.infinitum.bookingqba.view.rents.RentListFragment;
import com.infinitum.bookingqba.view.tutorial.PageFourFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface FragmentModule {

    @ContributesAndroidInjector
    BasePageFragment bindBasePageFragment();

    @ContributesAndroidInjector
    PageFourFragment bindPageFourFragment();


    @ContributesAndroidInjector
    HomeFragment bindHomeFragment();


    @ContributesAndroidInjector
    RentListFragment bindRentListFragment();



}
