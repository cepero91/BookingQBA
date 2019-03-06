package com.infinitum.bookingqba.di.module;


import com.infinitum.bookingqba.view.MainActivity;
import com.infinitum.bookingqba.view.tutorial.TutorialActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector
    MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    TutorialActivity bindTutorialActivity();

}
