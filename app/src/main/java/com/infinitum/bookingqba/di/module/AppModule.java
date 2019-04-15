package com.infinitum.bookingqba.di.module;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

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

    @Provides
    TelephonyManager provideTelephonyManager(Context context){
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }
}
