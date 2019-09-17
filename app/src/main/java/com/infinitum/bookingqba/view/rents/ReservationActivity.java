package com.infinitum.bookingqba.view.rents;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityReservationBinding;
import com.infinitum.bookingqba.model.remote.pojo.BookRequest;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.thekhaeng.pushdownanim.PushDownAnim;

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

    private RentViewModel rentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        reservationBinding = DataBindingUtil.setContentView(this, R.layout.activity_reservation);
        reservationBinding.setVisibility(GONE);

        compositeDisposable = new CompositeDisposable();
        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        if (getIntent().hasExtra(RENT_ID)  && getIntent().hasExtra(PRICE)
                && getIntent().hasExtra(MAXCAPABILITY) && getIntent().hasExtra(NIGHT_COUNT)
                && getIntent().hasExtra(DATE_RANGE) && getIntent().hasExtra(START_DATE)
                && getIntent().hasExtra(END_DATE)) {
            rentId = getIntent().getStringExtra(RENT_ID);
            userId = sharedPreferences.getString(USER_ID,"");
            token = sharedPreferences.getString(USER_TOKEN,"");
            price = getIntent().getDoubleExtra(PRICE, 0);
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
        reservationBinding.quantityHost.setMaxQuantity(maxCapability);
        reservationBinding.quantityHost.setMinQuantity(maxCapability);
        reservationBinding.quantityHost.setOnQuantityChangeListener(quantity -> {
            if (quantity == 0) {
                Animation animation = AnimationUtils.loadAnimation(ReservationActivity.this, R.anim.shake_animation);
                reservationBinding.quantityHost.startAnimation(animation);
            }
        });
        reservationBinding.tvStartEndDate.setText(dateRange);
        reservationBinding.tvPriceTitle.setText(String.format("%s noches a %.2f cuc = ", nightCount, price));
        reservationBinding.tvPriceValue.setText(String.format("%.2f", price * nightCount));
        updateDrawChange(price * nightCount);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_send:
                sendReservation();
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
                    if (resultResource.data != null && resultResource.data.getCode() == 200) {
                        showAlert(R.layout.reservation_success_dialog, "Ok, volver", "#009688", POSITIVE);
                    } else {
                        showAlert(R.layout.reservation_error_dialog, "Ok, lo entiedo", "#F44336", NEGATIVE);
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    reservationBinding.setVisibility(GONE);
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
        bookRequest.setCapability(String.valueOf(reservationBinding.quantityHost.getQuantity()));
        bookRequest.setAditional(reservationBinding.etAditionalNote.getText().toString());
        return bookRequest;
    }

    private void showAlert(int reservation_success_dialog, String buttonText, String parseColorButton,
                           CFAlertDialog.CFAlertActionStyle dialogStyle) {
        AlertUtils.showCFDialogWithCustomViewAndAction(this, reservation_success_dialog, buttonText,
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
                            reservationBinding.tvEuroConver.setText(String.format("~ € %.2f", mapResource.data.get("eur")));
                        if (mapResource.data.containsKey("usd"))
                            reservationBinding.tvDollarConver.setText(String.format("~ $ %.2f", mapResource.data.get("usd")));
                    } else {
                        reservationBinding.tvEuroConver.setText("~ € -.-");
                        reservationBinding.tvDollarConver.setText("~ $ -.-");
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);

    }
}
