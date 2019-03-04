package com.infinitum.bookingqba.view.splash;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivitySplashBinding;
import com.infinitum.bookingqba.view.MainActivity;
import com.infinitum.bookingqba.view.tutorial.TutorialActivity;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = DataBindingUtil.setContentView(this,R.layout.activity_splash);

        splashBinding.btnNext.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
        });


    }
}
