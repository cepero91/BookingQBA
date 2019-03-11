package com.infinitum.bookingqba.view.splash;

import android.content.Context;
import android.content.Intent;
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


public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                SplashActivity.this.finish();
            }
        }, 1500);

    }

}
