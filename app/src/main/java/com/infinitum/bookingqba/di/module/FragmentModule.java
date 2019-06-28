package com.infinitum.bookingqba.di.module;

import com.infinitum.bookingqba.view.filter.FilterFragment;
import com.infinitum.bookingqba.view.home.HomeFragment;
import com.infinitum.bookingqba.view.info.InfoFragment;
import com.infinitum.bookingqba.view.listwish.ListWishFragment;
import com.infinitum.bookingqba.view.map.MapFragment;
import com.infinitum.bookingqba.view.profile.AuthFragment;
import com.infinitum.bookingqba.view.profile.FirstStepFragment;
import com.infinitum.bookingqba.view.profile.FourStepFragment;
import com.infinitum.bookingqba.view.profile.LoginFragment;
import com.infinitum.bookingqba.view.profile.ProfileFragment;
import com.infinitum.bookingqba.view.profile.SecondStepFragment;
import com.infinitum.bookingqba.view.profile.ThreeStepFragment;
import com.infinitum.bookingqba.view.rents.RentListFragment;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface FragmentModule {

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

    @ContributesAndroidInjector
    ProfileFragment bindProfileFragment();

    @ContributesAndroidInjector
    ListWishFragment bindListWishFragment();

    @ContributesAndroidInjector
    InfoFragment bindInfoFragment();

    @ContributesAndroidInjector
    AuthFragment bindAuthFragment();

    @ContributesAndroidInjector
    FirstStepFragment bindFirstStepFragment();

    @ContributesAndroidInjector
    SecondStepFragment bindSecondStepFragment();

    @ContributesAndroidInjector
    ThreeStepFragment bindThreeStepFragment();

    @ContributesAndroidInjector
    FourStepFragment bindFourStepFragment();


}
