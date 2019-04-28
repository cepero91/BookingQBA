package com.infinitum.bookingqba.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.sync.SyncActivity;
import com.muddzdev.styleabletoast.StyleableToast;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.Gravity.CENTER;

public class AlertUtils {

    public static void showErrorToast(Context context) {
        StyleableToast.makeText(context, "Un error a ocurrido", Toast.LENGTH_LONG, R.style.myErrorToast).show();
    }

    public static void showErrorToast(Context context, String message) {
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
}
