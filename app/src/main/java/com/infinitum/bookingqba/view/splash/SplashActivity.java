package com.infinitum.bookingqba.view.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivitySplashBinding;
import com.infinitum.bookingqba.view.MainActivity;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.tutorial.TutorialActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

import static com.infinitum.bookingqba.util.Constants.PREF_DOWNLOAD_LEVEL;


public class SplashActivity extends DaggerAppCompatActivity {

    @Inject
    SharedPreferences sharedPreferences;

    private ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        AndroidInjection.inject(this);

        int downloadLevel = sharedPreferences.getInt(PREF_DOWNLOAD_LEVEL, 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                navigateTo(downloadLevel);
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 1500);

    }

    private void navigateTo(int downloadLevel) {
        Intent intent = new Intent(SplashActivity.this, TutorialActivity.class);
        if (downloadLevel == 10)
            intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }


}
