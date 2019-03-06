package com.infinitum.bookingqba.di.module;

import com.infinitum.bookingqba.view.tutorial.BasePageFragment;
import com.infinitum.bookingqba.view.tutorial.PageFourFragment;
import com.infinitum.bookingqba.view.tutorial.PageFourInterface;
import com.infinitum.bookingqba.view.tutorial.PageOneFragment;
import com.infinitum.bookingqba.view.tutorial.PageTwoFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface FragmentModule {

    @ContributesAndroidInjector
    BasePageFragment bindBasePageFragment();

    @ContributesAndroidInjector
    PageFourFragment bindPageFourFragment();


}
