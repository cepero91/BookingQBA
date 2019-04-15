package com.infinitum.bookingqba;


import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.infinitum.bookingqba.di.AppComponent;
import com.infinitum.bookingqba.di.DaggerAppComponent;
import com.infinitum.bookingqba.service.SendDataWorker;
import com.squareup.leakcanary.LeakCanary;

import java.util.concurrent.TimeUnit;

import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import timber.log.Timber;

public class BookingQBApp extends DaggerApplication{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }


    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        initWorkManager(appComponent);
        return appComponent;
    }

    private void initWorkManager(AppComponent appComponent) {
        Configuration configuration = new Configuration.Builder().setWorkerFactory(appComponent.daggerWorkerFactory()).build();
        WorkManager.initialize(this,configuration);
    }



}
