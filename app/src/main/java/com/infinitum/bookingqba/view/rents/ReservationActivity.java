package com.infinitum.bookingqba.view.rents;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.aminography.primecalendar.PrimeCalendar;
import com.aminography.primecalendar.common.CalendarFactory;
import com.aminography.primecalendar.common.CalendarType;
import com.aminography.primedatepicker.OnDayPickedListener;
import com.aminography.primedatepicker.PickType;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityReservationBinding;
import com.infinitum.bookingqba.model.remote.pojo.BookRequest;
import com.infinitum.bookingqba.util.AlertUtils;
import com.infinitum.bookingqba.view.customview.CalendarRangeDateView;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.USER_ID;
import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class ReservationActivity extends AppCompatActivity implements View.OnClickListener,
        CalendarRangeDateView.CalendarRangeDateInteraction {

    private ActivityReservationBinding reservationBinding;
    private String rentId, userId, token;
    private double price;
    private int maxCapability;
    private static final String RENT_ID = "rentId";
    private static final String PRICE = "price";
    private static final String MAXCAPABILITY = "maxcapability";
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    private String startDate;
    private String endDate;

    @Inject
    ViewModelFactory viewModelFactory;

    private RentViewModel rentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        reservationBinding = DataBindingUtil.setContentView(this, R.layout.activity_reservation);

        compositeDisposable = new CompositeDisposable();

        PushDownAnim.setPushDownAnimTo(reservationBinding.tvBtnSend).setOnClickListener(this);

        rentViewModel = ViewModelProviders.of(this, viewModelFactory).get(RentViewModel.class);

        if (getIntent().hasExtra(RENT_ID) && getIntent().hasExtra(USER_ID)
                && getIntent().hasExtra(USER_TOKEN) && getIntent().hasExtra(PRICE)
                && getIntent().hasExtra(MAXCAPABILITY)) {
            rentId = getIntent().getExtras().getString(RENT_ID);
            userId = getIntent().getExtras().getString(USER_ID);
            token = getIntent().getExtras().getString(USER_TOKEN);
            price = getIntent().getExtras().getDouble(PRICE);
            maxCapability = getIntent().getExtras().getInt(MAXCAPABILITY);
        }

        setupCalendarView();

        setupQuantityView();

    }

    private void setupQuantityView() {
        reservationBinding.quantityHost.setMaxQuantity(1);
        reservationBinding.quantityHost.setMaxQuantity(maxCapability);
    }

    private void setupCalendarView() {
        reservationBinding.calendarRangeDateView.setRangeDateInteraction(this);
        disposable = rentViewModel.disabledDays(token, rentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (listResource.data != null)
                        reservationBinding.calendarRangeDateView.setStrDisabledDays(listResource.data.getDates());
                }, Timber::e);
        compositeDisposable.add(disposable);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_send:
                sendReservation();
                break;
        }
    }

    /**
     * TODABIA FALTA LA BIBLIOTECA DEL INCREMENTO
     */
    private void sendReservation() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setId(UUID.randomUUID().toString());
        bookRequest.setUserid(userId);
        bookRequest.setRentId(rentId);
        bookRequest.setStartDate(startDate);
        bookRequest.setEndDate(endDate);
        bookRequest.setCapability(String.valueOf(reservationBinding.quantityHost.getQuantity()));
        bookRequest.setAditional(reservationBinding.etAditionalNote.getText().toString());
        disposable = rentViewModel.sendBookRequest(token, bookRequest).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if (resultResource.data != null && resultResource.data.getCode() == 200) {
                        AlertUtils.showCFDialogWithCustomViewAndAction(this, R.layout.reservation_success_dialog, "Ok, volver",
                                ((dialog, which) -> {
                                    dialog.dismiss();
                                    ReservationActivity.this.finish();
                                }));
                    } else {
                        AlertUtils.showCFDialogWithCustomViewAndAction(this, R.layout.reservation_error_dialog, "Ok, lo entiedo",
                                ((dialog, which) -> {
                                    dialog.dismiss();
                                    ReservationActivity.this.finish();
                                }));
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && disposable.isDisposed())
            disposable.dispose();
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void nightSelectedCount(int nightCount) {
        if (nightCount > 0) {
            double finalPrice = price * nightCount;
            reservationBinding.tvPriceTitle.setText(String.format("%s noches x %.2f = ", nightCount, price));
            reservationBinding.tvPriceValue.setText(String.format("%.2f", finalPrice));
            updateDrawChange(finalPrice);
        } else {
            reservationBinding.tvPriceTitle.setText("precio total");
            reservationBinding.tvPriceValue.setText("0");
        }
    }

    @Override
    public void validRangeSelected(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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
