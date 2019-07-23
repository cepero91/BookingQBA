package com.infinitum.bookingqba.view.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.interaction.LoginInteraction;

import java.util.HashSet;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.LAST_EMAIL_REGISTER;
import static com.infinitum.bookingqba.util.Constants.USER_AVATAR;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_IS_RENTS_OWNER;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_RENTS;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class UserAuthActivity extends AppCompatActivity implements HasSupportFragmentInjector, AuthFragment.AuthInteraction {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    private AuthFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        mFragment = AuthFragment.newInstance(sharedPreferences.getString(IMEI,""),sharedPreferences.getString(LAST_EMAIL_REGISTER,""));
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_content, mFragment).commit();

    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void signinSuccess(User user) {
        saveUserDataToPreferences(user);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(USER_NAME,user.getUsername());
        setResult(Activity.RESULT_OK, intent);
        this.finish();
    }

    private void saveUserDataToPreferences(User user){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_IS_AUTH, true);
        editor.putBoolean(USER_IS_RENTS_OWNER, user.isRentOwner());
        editor.putString(USER_TOKEN, user.getToken());
        editor.putString(USER_NAME, user.getUsername());
        editor.putString(USER_ID, user.getUserid());
        editor.putString(USER_AVATAR, user.getAvatar());
        editor.apply();
    }

    @Override
    public void signupSuccess(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_EMAIL_REGISTER,email);
        editor.apply();
    }
}
