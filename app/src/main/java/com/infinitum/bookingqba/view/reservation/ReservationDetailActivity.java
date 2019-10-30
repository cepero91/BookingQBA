package com.infinitum.bookingqba.view.reservation;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityReservationDetailBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.remote.pojo.UserEsentialData;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.EmotionUtil;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.rents.ReservationActivity;
import com.infinitum.bookingqba.viewmodel.UserViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.squareup.picasso.Picasso;

import java.net.ConnectException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.crowdfire.cfalertdialog.CFAlertDialog.CFAlertActionStyle.NEGATIVE;
import static com.crowdfire.cfalertdialog.CFAlertDialog.CFAlertActionStyle.POSITIVE;
import static com.infinitum.bookingqba.util.Constants.FROM_DETAIL_TO_MAP;
import static com.infinitum.bookingqba.util.Constants.FROM_RESERVATION_DETAIL_TO_LIST;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class ReservationDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityReservationDetailBinding reservationDetailBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    NetworkHelper networkHelper;

    private UserViewModel userViewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private ReservationItem reservationItem;
    private boolean isAccepted = false;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        token = sharedPreferences.getString(USER_TOKEN, "");
        reservationDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_reservation_detail);
        reservationDetailBinding.setLoading(true);
        compositeDisposable = new CompositeDisposable();
        reservationItem = getIntent().getExtras().getParcelable("reservationItem");
        isAccepted = getIntent().getBooleanExtra("accepted", false);
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        updateBookUi(reservationItem);
    }

    private void updateBookUi(ReservationItem reservationItem) {
        reservationDetailBinding.btnAccept.setOnClickListener(this);
        reservationDetailBinding.btnDenied.setOnClickListener(this);
        reservationDetailBinding.btnAccept.setVisibility(isAccepted ? View.GONE : View.VISIBLE);
        if (reservationItem != null) {
            if (reservationItem.getAditionalNote() != null && !reservationItem.getAditionalNote().isEmpty()) {
                reservationDetailBinding.setNote(true);
                reservationDetailBinding.tvAditionalNoteValue.setText(reservationItem.getAditionalNote());
            }
            updateCalendarDayCard(reservationItem.getStartDate(), reservationItem.getEndDate());
            loadUserBookOwnerData(reservationItem);
        }
    }

    private void updateCalendarDayCard(Date startDate, Date endDate) {
        SimpleDateFormat dayShortFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthShortFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        SimpleDateFormat dayNameShortFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        String dayShortFrom = dayShortFormat.format(startDate);
        String dayShortUntil = dayShortFormat.format(endDate);
        reservationDetailBinding.tvDayWeekCalueFrom.setText(dayShortFrom);
        reservationDetailBinding.tvDayWeekCalueUntil.setText(dayShortUntil);
        String monthShortFrom = monthShortFormat.format(startDate);
        String monthShortUntil = monthShortFormat.format(endDate);
        reservationDetailBinding.tvMonthFromValue.setText(monthShortFrom);
        reservationDetailBinding.tvMonthUntilValue.setText(monthShortUntil);
        String dayNameShortFrom = dayNameShortFormat.format(startDate);
        String dayNameShortUntil = dayNameShortFormat.format(endDate);
        reservationDetailBinding.tvDayShortFrom.setText(dayNameShortFrom);
        reservationDetailBinding.tvDayShortUntil.setText(dayNameShortUntil);
    }

    private void loadUserBookOwnerData(ReservationItem reservationItem) {
        disposable = userViewModel.getUserBookRequestData(token, reservationItem.getUserId(), reservationItem.getRentId(), reservationItem.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEsentialDataResource -> {
                    if (userEsentialDataResource.data != null) {
                        updateUserBookOwnerUi(userEsentialDataResource.data);
                        reservationDetailBinding.setLoading(false);
                    } else {
                        AlertUtils.showErrorSnackbar(this, "Oops!! algo anda mal");
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    AlertUtils.showErrorSnackbar(this, "Oops!! algo anda mal");
                });
        compositeDisposable.add(disposable);
    }

    private void updateUserBookOwnerUi(UserEsentialData data) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        Date tempDate = null;
        try {
            tempDate = simpleDateFormat.parse(data.getUserLastLogin());
        } catch (Exception e) {
            Timber.e(e);
        }
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build();
        String dateRelative = tempDate != null ? TimeAgo.using(tempDate.getTime(), messages) : "";
        reservationDetailBinding.tvTimeValue.setText(dateRelative);
        if (Float.parseFloat(data.getRatingAverage()) > 0f) {
            reservationDetailBinding.setValoration(true);
            reservationDetailBinding.tvRatingDesc.setText(data.getRatingDescription());
            reservationDetailBinding.srScaleRating.setRating(Float.parseFloat(data.getRatingAverage()));
        }
        if (!data.getUserName().isEmpty())
            reservationDetailBinding.tvUsernameValue.setText(data.getUserName());
        if (!data.getName().isEmpty() && data.getName().length() > 5)
            reservationDetailBinding.tvUsernameValue.setText(data.getName());
        reservationDetailBinding.textView9.setText(data.getWish() ? "Si" : "No");
        reservationDetailBinding.textView10.setText(data.getReservedCount());
        if (Integer.parseInt(data.getCommentCount()) > 0) {
            reservationDetailBinding.setComment(true);
            reservationDetailBinding.tvCommentDesc.setText(data.getCommentLast());
            Float commentAv = Float.parseFloat(data.getCommentAverage());
            Drawable iconEmotion = getDrawable(EmotionUtil.getEmotionDrawableId(commentAv.intValue()));
            reservationDetailBinding.tvCommentDesc.setCompoundDrawablesWithIntrinsicBounds(null, null, iconEmotion, null);
        }
        Picasso.get()
                .load(reservationItem.getUserAvatar())
                .placeholder(R.drawable.placeholder)
                .into(reservationDetailBinding.userAvatar);
    }

    private void deniedBookRequest(String uuid) {
        AlertUtils.showCFInfoNotificationWithAction(this, "Aviso!!", "Está seguro que desea rechazar la solicitud de reserva",
                (dialog, which) -> {
                    dialog.dismiss();
                    deniedBook(uuid);
                }, "Si, rechazar");
    }

    private void deniedBook(String uuid) {
        disposable = userViewModel.deniedReservation(sharedPreferences.getString(USER_TOKEN, ""), uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showNotificationDeniedToUser, throwable -> {
                    Timber.e(throwable);
                    showErrorToUser(throwable);
                });
        compositeDisposable.add(disposable);
    }

    private void acceptBookRequest(String uuid) {
        disposable = userViewModel.acceptReservation(sharedPreferences.getString(USER_TOKEN, ""), uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showNotificationToUser, throwable -> {
                    Timber.e(throwable);
                    showErrorToUser(throwable);
                });
        compositeDisposable.add(disposable);
    }

    private void showErrorToUser(@Nullable Throwable throwable) {
        View msgView;
        if (throwable instanceof ConnectException || throwable instanceof SocketException) {
            msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
            ((TextView) msgView.findViewById(R.id.tv_error_msg)).setText(Constants.CONNEXION_ERROR_MSG);
        } else {
            msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
        }
        showAlert(msgView, "Ok, lo entiedo", "#F44336", NEGATIVE, false);
    }

    private void showNotificationToUser(Resource<ResponseResult> resultResource) {
        View msgView;
        if (resultResource.data != null) {
            if (resultResource.data.getCode() == 200) {
                msgView = getLayoutInflater().inflate(R.layout.reservation_accepted_dialog, null);
                showAlert(msgView, "Ok, volver", "#009688", POSITIVE, true);
            } else {
                msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
                ((TextView) msgView.findViewById(R.id.tv_error_msg)).setText(resultResource.data.getMsg());
                showAlert(msgView, "Ok, lo entiedo", "#F44336", NEGATIVE, false);
            }
        } else {
            msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
            showAlert(msgView, "Ok, lo entiedo", "#F44336", NEGATIVE, false);
        }
    }

    private void showNotificationDeniedToUser(Resource<ResponseResult> resultResource) {
        View msgView;
        if (resultResource.data != null) {
            if (resultResource.data.getCode() == 200) {
                msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
                ((TextView) msgView.findViewById(R.id.tv_error_title)).setText(R.string.reject_title);
                ((TextView) msgView.findViewById(R.id.tv_error_msg)).setTextColor(getResources().getColor(R.color.material_color_blue_grey_500));
                ((TextView) msgView.findViewById(R.id.tv_error_msg)).setText(R.string.reject_reservation_msg);
                showAlert(msgView, "Ok, volver", "#F44336", NEGATIVE, true);
            } else {
                msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
                ((TextView) msgView.findViewById(R.id.tv_error_msg)).setText(resultResource.data.getMsg());
                showAlert(msgView, "Ok, lo entiedo", "#F44336", NEGATIVE, false);
            }
        } else {
            msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
            showAlert(msgView, "Ok, lo entiedo", "#F44336", NEGATIVE, false);
        }
    }

    private void showAlert(View view, String buttonText, String parseColorButton,
                           CFAlertDialog.CFAlertActionStyle dialogStyle, boolean refreshNeeded) {
        AlertUtils.showCFDialogWithCustomViewAndAction(this, view, buttonText,
                parseColorButton, dialogStyle,
                ((dialog, which) -> {
                    dialog.dismiss();
                    bringToList(refreshNeeded);
                }));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!isAccepted) {
            bringToList(true);
        } else {
            super.onBackPressed();
        }
    }

    public void bringToList(boolean refresh){
        if(refresh) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("refresh", refresh);
            setResult(FROM_RESERVATION_DETAIL_TO_LIST, intent);
        }
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
                if(networkHelper.isNetworkAvailable()) {
                    acceptBookRequest(reservationItem.getId());
                }else{
                    showErrorToUser(new ConnectException());
                }
                break;
            case R.id.btn_denied:
                if(networkHelper.isNetworkAvailable()) {
                    deniedBookRequest(reservationItem.getId());
                }else{
                    showErrorToUser(new ConnectException());
                }
                break;
        }
    }
}

