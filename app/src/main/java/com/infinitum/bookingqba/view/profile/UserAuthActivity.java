package com.infinitum.bookingqba.view.profile;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.model.remote.pojo.User;
import com.infinitum.bookingqba.view.interaction.LoginInteraction;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.infinitum.bookingqba.util.Constants.IMEI;
import static com.infinitum.bookingqba.util.Constants.LAST_EMAIL_REGISTER;

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
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle("Logeado con exito");
        builder.setMessage(String.format("Hola %s, le damos la bienvenida",user.getUsername()));
        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.setAutoDismissAfter(3000);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.show();
    }

    @Override
    public void signupSuccess(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_EMAIL_REGISTER,email);
        editor.apply();
    }
}
