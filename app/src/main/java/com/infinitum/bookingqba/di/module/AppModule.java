package com.infinitum.bookingqba.di.module;

import android.app.Application;
import android.content.Context;

import com.infinitum.bookingqba.BookingQBApp;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    Context provideContext(BookingQBApp application) {
        return application.getApplicationContext();
    }

    @Provides
    Application provideAplication(BookingQBApp application) { return application; }
}
