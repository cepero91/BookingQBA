package com.infinitum.bookingqba.di.module;


import com.infinitum.bookingqba.view.base.LocationActivity;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.profile.AddRentActivity;
import com.infinitum.bookingqba.view.profile.UserAuthActivity;
import com.infinitum.bookingqba.view.rents.DetailActivity;
import com.infinitum.bookingqba.view.rents.RentDetailActivity;
import com.infinitum.bookingqba.view.rents.ReservationActivity;
import com.infinitum.bookingqba.view.reservation.ReservationDetailActivity;
import com.infinitum.bookingqba.view.splash.SplashActivity;
import com.infinitum.bookingqba.view.sync.SyncActivity;
import com.infinitum.bookingqba.view.tutorial.TutorialActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {


    @ContributesAndroidInjector
    SplashActivity bindSplashActivity();

    @ContributesAndroidInjector
    TutorialActivity bindTutorialActivity();

    @ContributesAndroidInjector
    SyncActivity bindSyncActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    HomeActivity bindHomeActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    RentDetailActivity bindRentDetailActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    AddRentActivity bindAddRentActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    UserAuthActivity bindUserAuthActivity();

    @ContributesAndroidInjector
    LocationActivity bindLocationActivity();

    @ContributesAndroidInjector
    ReservationActivity bindReservationActivity();

    @ContributesAndroidInjector
    ReservationDetailActivity bindReservationDetailActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    DetailActivity bindDetailActivity();


}
