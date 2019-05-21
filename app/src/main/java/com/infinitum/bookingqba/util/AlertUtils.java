package com.infinitum.bookingqba.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.sync.SyncActivity;
import com.muddzdev.styleabletoast.StyleableToast;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.Gravity.CENTER;
import static com.infinitum.bookingqba.util.Constants.IS_PROFILE_ACTIVE;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_IS_AUTH;
import static com.infinitum.bookingqba.util.Constants.USER_NAME;
import static com.infinitum.bookingqba.util.Constants.USER_RENTS;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class AlertUtils {

    public static void showErrorToast(Context context) {
        StyleableToast.makeText(context, "Un error a ocurrido", Toast.LENGTH_LONG, R.style.myErrorToast).show();
    }

    public static void showErrorToast(Context context, String message) {
        StyleableToast.makeText(context, message, Toast.LENGTH_LONG, R.style.myErrorToast).show();
    }

    public static void showErrorTopToast(Context context, String message) {
        StyleableToast.makeText(context, message, Toast.LENGTH_LONG, R.style.myErrorToast).show();
    }

    public static void showSuccessToast(Context context) {
        StyleableToast.makeText(context, "Proceso terminado con éxito", Toast.LENGTH_LONG, R.style.mySuccessToast).show();
    }

    public static void showSuccessToast(Context context, String message) {
        StyleableToast.makeText(context, message, Toast.LENGTH_LONG, R.style.mySuccessToast).show();
    }

    public static void showSuccessAlert(Context context) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Buen trabajo!")
                .setContentText("Tarea concluida con éxito!")
                .setConfirmText("Ok")
                .show();
    }

    public static void showSuccessAlert(Context context, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Buen trabajo!")
                .setContentText(message)
                .setConfirmText("Ok")
                .show();
    }

    public static void showSuccessAlertAndGoHome(Context context) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Buen trabajo!")
                .setContentText("Tarea concluida con éxito!")
                .setConfirmText("Comenzar")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    new Handler().postDelayed(() -> {
                        context.startActivity(new Intent(context, HomeActivity.class));
                        ((Activity) context).finish();
                    }, 500);
                })
                .show();
    }

    public static void showSuccessAlertAndFinish(Context context) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Buen trabajo!")
                .setContentText("Tarea concluida con éxito!")
                .setConfirmText("Finalizar")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    new Handler().postDelayed(((Activity) context)::finish, 500);
                })
                .show();
    }

    public static void showInfoAlert(Context context, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Aviso!")
                .setContentText(message)
                .setConfirmText("Ok")
                .show();
    }

    public static void showInfoAlertAndGoHome(Context context, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Aviso!")
                .setContentText(message)
                .setConfirmText("Ir a inicio")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    new Handler().postDelayed(() -> {
                        context.startActivity(new Intent(context, HomeActivity.class));
                        ((Activity) context).finish();
                    }, 500);

                })
                .show();
    }

    public static void showErrorAlert(Context context) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ooopss...!")
                .setContentText("Algo ha ido mal!")
                .hideConfirmButton()
                .setCancelText("Ok, lo entiendo");
        sweetAlertDialog.setOnShowListener(dialog -> {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = CENTER;
            sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setLayoutParams(params);
            sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setPadding(15, 15, 15, 15);
        });
        sweetAlertDialog.show();
    }

    public static void showErrorAlert(Context context, String message) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ooopss...!")
                .setContentText(message)
                .hideConfirmButton()
                .setCancelText("Ok, lo entiendo");
        sweetAlertDialog.setOnShowListener(dialog -> {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = CENTER;
            sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setLayoutParams(params);
            sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setPadding(15, 15, 15, 15);
        });
        sweetAlertDialog.show();
    }

    public static void showGPSErrorAndGoSetting(Context context, int requestCode) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ooopss...!")
                .setContentText("Active la Localizacion del Dispositivo!")
                .setConfirmText(" Ir a Localizacion ")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        ((Activity)context).startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),requestCode);
                    }
                });
        sweetAlertDialog.show();
    }

    public static void showInfoAlertAndFinishApp(Context context, SharedPreferences sharedPreferences) {
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Aviso!")
                .setContentText("Esta seguro que desea salir")
                .setConfirmText("Finalizar")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    new Handler().postDelayed(() -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(USER_IS_AUTH,false);
                        editor.putString(USER_NAME,"");
                        editor.putString(USER_ID,"");
                        editor.putString(USER_TOKEN,"");
                        editor.putStringSet(USER_RENTS,null);
                        editor.putBoolean(IS_PROFILE_ACTIVE,false);
                        editor.commit();
                        ((Activity) context).finish();
                    }, 400);
                })
                .show();
    }



}
