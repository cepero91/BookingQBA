package com.infinitum.bookingqba;


import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;


import com.infinitum.bookingqba.di.AppComponent;
import com.infinitum.bookingqba.di.DaggerAppComponent;
import com.infinitum.bookingqba.service.SendDataWorker;
import com.infinitum.bookingqba.util.Constants;
import com.rey.material.app.ThemeManager;
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

//@AcraCore(buildConfigClass = BuildConfig.class, reportFormat = StringFormat.JSON)
//@AcraMailSender(mailTo = Constants.ACRA_MAIL_TO_REPORT)
//@AcraDialog(resText = R.string.dialog_acra_text,
//        resCommentPrompt = R.string.dialog_acra_comment, resTheme = android.R.style.Theme_DeviceDefault_Light_Dialog)
public class BookingQBApp extends DaggerApplication{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        ACRA.init(this);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ThemeManager.init(this, 1, 0, null);

        //METODO PARA INICIALIZAR LEAK CANARY
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
