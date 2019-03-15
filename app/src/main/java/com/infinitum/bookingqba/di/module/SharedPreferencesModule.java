package com.infinitum.bookingqba.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {

    @Singleton
    @Provides
    SharedPreferences sharedPreferences(Context context){
        return  context.getSharedPreferences("Preferences",Context.MODE_PRIVATE);
    }
}