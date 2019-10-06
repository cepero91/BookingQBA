package com.infinitum.bookingqba.util;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.customview.SuccessDialogContentView;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.sync.SyncActivity;
import com.muddzdev.styleabletoast.StyleableToast;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.mateware.snacky.Snacky;

import static android.view.Gravity.CENTER;
import static com.infinitum.bookingqba.util.Constants.IS_PROFILE_ACTIVE;
import static com.infinitum.bookingqba.util.Constants.NOTIFICATION_DEFAULT;
import static com.infinitum.bookingqba.util.Constants.NOTIFICATION_ID;
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
        StyleableToast.makeText(context, message, Toast.LENGTH_LONG, R.style.myErrorTopToast).show();
    }

    public static void showInfoTopToast(Context context, String message) {
        StyleableToast.makeText(context, message, R.style.myInfoTopToast).show();
    }

    public static void showSuccessToast(Context context) {
        StyleableToast.makeText(context, "Proceso terminado con éxito", Toast.LENGTH_LONG, R.style.mySuccessToast).show();
    }

    public static void showSuccessToast(Context context, String message) {
        StyleableToast.makeText(context, message, Toast.LENGTH_LONG, R.style.mySuccessToast).show();
    }

    public static void showSuccessLocationToast(Context context, String message) {
        StyleableToast.makeText(context, message, Toast.LENGTH_LONG, R.style.mySuccessLocationToast).show();
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
                        ((Activity) context).startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), requestCode);
                    }
                });
        sweetAlertDialog.show();
    }

    public static void showInfoAlertAndFinishApp(Context context) {
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Aviso!")
                .setContentText("Esta seguro que desea salir")
                .setConfirmText("Finalizar")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    new Handler().postDelayed(() -> {
                        ((Activity) context).finish();
                    }, 400);
                })
                .show();
    }

    public static void showCFInfoAlertWithAction(Context context, String message, String buttonTitle, CFAlertDialog.OnClickListener onClickListener) {
        SoftReference<Context> weakReference = new SoftReference<>(context);
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(weakReference.get());
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle("Aviso!!");
        builder.setMessage(message);
        builder.setTextGravity(Gravity.CENTER);
        builder.setAutoDismissAfter(8000);
        builder.addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, onClickListener);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.show();
    }

    public static void showCFPositiveInfoAlert(Context context, String message, String buttonTitle, CFAlertDialog.OnClickListener onClickListener) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle("Aviso!!");
        builder.setMessage(message);
        builder.setTextGravity(Gravity.CENTER);
        builder.addButton(buttonTitle, -1, Color.parseColor("#00BFA5"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, onClickListener);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.show();
    }

    public static void showCFInfoAlert(Context context, String message) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION);
        // Title and message
        builder.setTitle("Informacion");
        builder.setMessage(message);
        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.setAutoDismissAfter(5000);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.show();
    }

    public static void notifyPendingProfileActivate(Application application, String msg) {
        NotificationManager notificationManager = (NotificationManager) application.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(NOTIFICATION_ID);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_DEFAULT, "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(application.getApplicationContext(), NOTIFICATION_DEFAULT)
                .setContentTitle("BookingQBA")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(application.getResources(), R.mipmap.ic_launcher))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    public static void showSuccessSnackbar(Activity activity, String message) {
        Snacky.builder()
                .setActivity(activity)
                .setText(message)
                .setDuration(8000)
                .setBackgroundColor(Color.parseColor("#009688"))
                .setIcon(R.drawable.ic_check_circle)
                .build()
                .show();
    }

    public static void showErrorSnackbar(Activity activity, String message) {
        Snacky.builder()
                .setActivity(activity)
                .setText(message)
                .setDuration(5000)
                .setBackgroundColor(Color.parseColor("#D32F2F"))
                .setIcon(R.drawable.ic_exclamation_triangle)
                .build()
                .show();
    }

    public static void showErrorSnackbarWithAction(Activity activity, String message, View.OnClickListener onClickListener, String action) {
        Snacky.builder()
                .setActivity(activity)
                .setDuration(Snacky.LENGTH_INDEFINITE)
                .setText(message)
                .setActionText(action)
                .setActionClickListener(onClickListener)
                .setActionTextColor(Color.WHITE)
                .setBackgroundColor(Color.parseColor("#D32F2F"))
                .setIcon(R.drawable.ic_exclamation_triangle)
                .build()
                .show();
    }

    public static void showCFErrorAlertWithAction(Context context, String title, String message, DialogInterface.OnClickListener onClickListener, String buttonTitle) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
        // Title and message
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setTextGravity(Gravity.START);
        builder.setIcon(R.drawable.ic_unlock_alt);
        builder.addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, onClickListener);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.show();
    }

    public static void showCFErrorAlertWithAction(Context context, String title, String message, DialogInterface.OnClickListener onClickListener, String buttonTitle, @DrawableRes int drawableIcon) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
        // Title and message
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setTextGravity(Gravity.START);
        builder.setIcon(drawableIcon);
        builder.addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, onClickListener);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.show();
    }

    public static void showCFErrorNotificationWithAction(Context context, String title, String message, DialogInterface.OnClickListener onClickListener, String buttonTitle) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setTextGravity(Gravity.CENTER);
        builder.addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, onClickListener);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.show();
    }

    public static void showCFErrorNotificationWithAction(Context context, String title, String message, DialogInterface.OnClickListener positiveClick,  DialogInterface.OnClickListener negativeClick, String positiveTitle, String negativeTitle) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setTextGravity(Gravity.CENTER);
        builder.addButton(positiveTitle, -1, Color.parseColor("#00BFA5"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, positiveClick);
        builder.addButton(negativeTitle, -1, Color.parseColor("#F44336"), CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, negativeClick);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.setCancelable(false);
        builder.show();
    }

    public static void showCFInfoNotificationWithAction(Context context, String title, String message, DialogInterface.OnClickListener positiveClick, String positiveTitle) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setTextGravity(Gravity.CENTER);
        builder.addButton(positiveTitle, -1, Color.parseColor("#00BFA5"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, positiveClick);
        builder.setTextColor(Color.parseColor("#607D8B"));
        builder.setCancelable(false);
        builder.show();
    }

    public static void showSuccessLogin(Context context, SuccessDialogContentView successDialogContentView) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setHeaderView(successDialogContentView);
        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.setAutoDismissAfter(3000);
        builder.show();
    }

    public static void showCFDialogWithCustomViewAndAction(Context context, @LayoutRes int layout, String buttonText, String parseButtonColor, CFAlertDialog.CFAlertActionStyle buttonType, DialogInterface.OnClickListener onClickListener) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        // Title and message
        builder.setHeaderView(layout);
        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.addButton(buttonText, -1, Color.parseColor(parseButtonColor), buttonType, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, onClickListener);
        builder.show();
    }


}
