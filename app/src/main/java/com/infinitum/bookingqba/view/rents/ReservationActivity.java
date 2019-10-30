package com.infinitum.bookingqba.view.rents;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityReservationBinding;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.BookRequest;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.util.Constants;
import com.infinitum.bookingqba.util.DateUtils;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.reservation.ReservationDetailActivity;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.net.ConnectException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.crowdfire.cfalertdialog.CFAlertDialog.CFAlertActionStyle.NEGATIVE;
import static com.crowdfire.cfalertdialog.CFAlertDialog.CFAlertActionStyle.POSITIVE;
import static com.infinitum.bookingqba.util.Constants.DATE_RANGE;
import static com.infinitum.bookingqba.util.Constants.END_DATE;
import static com.infinitum.bookingqba.util.Constants.MAXCAPABILITY;
import static com.infinitum.bookingqba.util.Constants.NIGHT_COUNT;
import static com.infinitum.bookingqba.util.Constants.PRICE;
import static com.infinitum.bookingqba.util.Constants.RENT_ID;
import static com.infinitum.bookingqba.util.Constants.START_DATE;
import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class ReservationActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityReservationBinding reservationBinding;
    private String rentId, userId, token;
    private double price;
    private int maxCapability;
    private int nightCount;
    private String dateRange;
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    private String startDate;
    private String endDate;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    NetworkHelper networkHelper;

    private RentViewModel rentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        reservationBinding = DataBindingUtil.setContentView(this, R.layout.activity_reservation);
        reservationBinding.setVisibility(GONE);

        compositeDisposable = new CompositeDisposable();
        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        if (getIntent().hasExtra(RENT_ID) && getIntent().hasExtra(PRICE)
                && getIntent().hasExtra(MAXCAPABILITY) && getIntent().hasExtra(NIGHT_COUNT)
                && getIntent().hasExtra(DATE_RANGE) && getIntent().hasExtra(START_DATE)
                && getIntent().hasExtra(END_DATE)) {
            rentId = getIntent().getStringExtra(RENT_ID);
            userId = sharedPreferences.getString(USER_ID, "");
            token = sharedPreferences.getString(USER_TOKEN, "");
            price = getIntent().getFloatExtra(PRICE, 0);
            nightCount = getIntent().getIntExtra(NIGHT_COUNT, 0);
            dateRange = getIntent().getStringExtra(DATE_RANGE);
            startDate = getIntent().getStringExtra(START_DATE);
            endDate = getIntent().getStringExtra(END_DATE);
            maxCapability = getIntent().getIntExtra(MAXCAPABILITY, 0);
        }
        setupUIViews();
    }

    private void setupUIViews() {
        PushDownAnim.setPushDownAnimTo(reservationBinding.tvBtnSend).setOnClickListener(this);
        reservationBinding.quantityHost.setRange(1, maxCapability);
        Date stDate = DateUtils.dateStringToDate(startDate,"yyyy-MM-dd");
        Date ndDate = DateUtils.dateStringToDate(endDate,"yyyy-MM-dd");
        updateCalendarDayCard(stDate, ndDate);
        reservationBinding.tvPriceTitle.setText(String.format("%s noches a %.2f cuc = ", nightCount, price));
        reservationBinding.tvPriceValue.setText(String.format("%.2f", price * nightCount));
        updateDrawChange(price * nightCount);
    }

    private void updateCalendarDayCard(Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            SimpleDateFormat dayShortFormat = new SimpleDateFormat("dd", Locale.getDefault());
            SimpleDateFormat monthShortFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            SimpleDateFormat dayNameShortFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            String dayShortFrom = dayShortFormat.format(startDate);
            String dayShortUntil = dayShortFormat.format(endDate);
            reservationBinding.tvDayWeekCalueFrom.setText(dayShortFrom);
            reservationBinding.tvDayWeekCalueUntil.setText(dayShortUntil);
            String monthShortFrom = monthShortFormat.format(startDate);
            String monthShortUntil = monthShortFormat.format(endDate);
            reservationBinding.tvMonthFromValue.setText(monthShortFrom);
            reservationBinding.tvMonthUntilValue.setText(monthShortUntil);
            String dayNameShortFrom = dayNameShortFormat.format(startDate);
            String dayNameShortUntil = dayNameShortFormat.format(endDate);
            reservationBinding.tvDayShortFrom.setText(dayNameShortFrom);
            reservationBinding.tvDayShortUntil.setText(dayNameShortUntil);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_send:
                if (networkHelper.isNetworkAvailable()) {
                    sendReservation();
                } else {
                    showErrorToUser(new ConnectException());
                }
                break;
        }
    }

    private void sendReservation() {
        reservationBinding.setVisibility(VISIBLE);
        BookRequest bookRequest = getBookRequest();
        disposable = rentViewModel.sendBookRequest(token, bookRequest).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    reservationBinding.setVisibility(GONE);
                    showNotificationToUser(resultResource);
                }, throwable -> {
                    Timber.e(throwable);
                    showErrorToUser(throwable);
                });
        compositeDisposable.add(disposable);
    }


    @NonNull
    private BookRequest getBookRequest() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setId(UUID.randomUUID().toString());
        bookRequest.setUserid(userId);
        bookRequest.setRentId(rentId);
        bookRequest.setStartDate(startDate);
        bookRequest.setEndDate(endDate);
        bookRequest.setCapability(reservationBinding.quantityHost.getNumber());
        bookRequest.setAditional(reservationBinding.etAditionalNote.getText().toString());
        return bookRequest;
    }

    private void showErrorToUser(@Nullable Throwable throwable) {
        View msgView;
        if (throwable instanceof ConnectException || throwable instanceof SocketException) {
            msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
            ((TextView) msgView.findViewById(R.id.tv_error_msg)).setText(Constants.CONNEXION_ERROR_MSG);
        } else {
            msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
        }
        showAlert(msgView, "Ok, lo entiedo", "#F44336", NEGATIVE);
    }

    private void showNotificationToUser(Resource<ResponseResult> resultResource) {
        View msgView;
        if (resultResource.data != null) {
            if (resultResource.data.getCode() == 200) {
                msgView = getLayoutInflater().inflate(R.layout.reservation_success_dialog, null);
                showAlert(msgView, "Ok, volver", "#009688", POSITIVE);
            } else {
                msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
                ((TextView) msgView.findViewById(R.id.tv_error_msg)).setText(resultResource.data.getMsg());
                showAlert(msgView, "Ok, lo entiedo", "#F44336", NEGATIVE);
            }
        } else {
            msgView = getLayoutInflater().inflate(R.layout.reservation_error_dialog, null);
            showAlert(msgView, "Ok, lo entiedo", "#F44336", NEGATIVE);
        }
    }

    private void showAlert(View view, String buttonText, String parseColorButton,
                           CFAlertDialog.CFAlertActionStyle dialogStyle) {
        AlertUtils.showCFDialogWithCustomViewAndAction(this, view, buttonText,
                parseColorButton, dialogStyle,
                ((dialog, which) -> {
                    dialog.dismiss();
                    ReservationActivity.this.finish();
                }));
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void updateDrawChange(double finalPrice) {
        disposable = rentViewModel.drawChangeByFinalPrice(token, finalPrice)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mapResource -> {
                    if (mapResource.data != null) {
                        if (mapResource.data.containsKey("eur"))
                            reservationBinding.tvEuroConver.setText(String.format("= € %.2f", mapResource.data.get("eur")));
                        if (mapResource.data.containsKey("usd"))
                            reservationBinding.tvDollarConver.setText(String.format("= $ %.2f", mapResource.data.get("usd")));
                    } else {
                        reservationBinding.tvEuroConver.setText("= € -.-");
                        reservationBinding.tvDollarConver.setText("= $ -.-");
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);

    }
}
