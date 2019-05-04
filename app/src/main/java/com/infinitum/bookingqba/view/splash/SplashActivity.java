package com.infinitum.bookingqba.view.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Bundle;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivitySplashBinding;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.sync.SyncActivity;
import com.infinitum.bookingqba.view.tutorial.TutorialActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

import static com.infinitum.bookingqba.util.Constants.PREF_DOWNLOAD_SUCCESS;
import static com.infinitum.bookingqba.util.Constants.PREF_FIRST_OPEN;
import static com.infinitum.bookingqba.util.Constants.PREF_LAST_DOWNLOAD_DATE;


public class SplashActivity extends DaggerAppCompatActivity {

    @Inject
    SharedPreferences sharedPreferences;

    private ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        splashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        boolean firstOpen = sharedPreferences.getBoolean(PREF_FIRST_OPEN, true);
        boolean downloadDateExist = !sharedPreferences.getString(PREF_LAST_DOWNLOAD_DATE, "").equals("");

        new Handler().postDelayed(() -> {
            if (firstOpen) {
                Intent intent = new Intent(SplashActivity.this, TutorialActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }else if(!firstOpen && !downloadDateExist) {
                Intent intent = new Intent(SplashActivity.this, SyncActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
            else {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 1500);

    }

    private void paraProbarLeaks() {
        //        PARA PROBAR LEAKS
//        new Thread() {
//            public void run() {
//                while(true) {
//                    SystemClock.sleep(100);
//                }
//            }
//        }.start();
    }


}
