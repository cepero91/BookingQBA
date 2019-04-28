package com.infinitum.bookingqba.di.module;


import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.rents.RentDetailActivity;
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

}
