package com.infinitum.bookingqba.view.reservation;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityReservationDetailBinding;
import com.infinitum.bookingqba.model.remote.pojo.UserEsentialData;
import com.infinitum.bookingqba.view.adapters.items.reservation.ReservationItem;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.UserViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;
import com.squareup.picasso.Picasso;

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

import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class ReservationDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityReservationDetailBinding reservationDetailBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    private UserViewModel userViewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private ReservationItem reservationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        reservationDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_reservation_detail);
        compositeDisposable = new CompositeDisposable();
        reservationItem = getIntent().getExtras().getParcelable("reservationItem");
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        updateBookUi(reservationItem);
        loadUserBookOwnerData(reservationItem);
    }

    private void updateBookUi(ReservationItem reservationItem) {
        reservationDetailBinding.setLoading(true);
        reservationDetailBinding.btnAccept.setOnClickListener(this);
        reservationDetailBinding.btnDenied.setOnClickListener(this);
        if (reservationItem != null) {
            if (reservationItem.getAditionalNote() != null && !reservationItem.getAditionalNote().isEmpty())
                reservationDetailBinding.setNote(true);
            reservationDetailBinding.tvStartDateValue.setText(reservationItem.getStartDate());
            reservationDetailBinding.tvEndDateValue.setText(reservationItem.getEndDate());
            reservationDetailBinding.tvAditionalNoteValue.setText(reservationItem.getAditionalNote());
        }
    }

    private void loadUserBookOwnerData(ReservationItem reservationItem) {
        String token = sharedPreferences.getString(USER_TOKEN, "");
        disposable = userViewModel.getUserBookRequestData(token, reservationItem.getUserId(), reservationItem.getRentId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEsentialDataResource -> {
                    if (userEsentialDataResource.data != null) {
                        updateUserBookOwnerUi(userEsentialDataResource.data);
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void updateUserBookOwnerUi(UserEsentialData data) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate = null;
        try {
            tempDate = simpleDateFormat.parse(data.getUserTime());
        } catch (Exception e) {
            Timber.e(e);
        }
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build();
        String dateRelative = TimeAgo.using(tempDate.getTime(), messages);
        if (Float.parseFloat(data.getRatingAverage()) > 0f)
            reservationDetailBinding.setValoration(true);
        reservationDetailBinding.tvUsernameValue.setText(data.getName());
        reservationDetailBinding.tvTimeValue.setText(dateRelative);
        reservationDetailBinding.tvWishedValue.setText(data.getWish() ? "Si" : "No");
        reservationDetailBinding.tvCommentCountValue.setText(data.getCommentCount());
        reservationDetailBinding.tvBookCountValue.setText(data.getReservedCount());
        Picasso.get()
                .load(reservationItem.getUserAvatar())
                .placeholder(R.drawable.placeholder)
                .into(reservationDetailBinding.userAvatar);
        reservationDetailBinding.srScaleRating.setRating(Float.parseFloat(data.getRatingAverage()));
        reservationDetailBinding.tvUserValorationValue.setText("Esto falta en lo que me devuelves");
        reservationDetailBinding.setLoading(false);
    }

    private void deniedBookRequest(String uuid) {
        disposable = userViewModel.deniedReservation(sharedPreferences.getString(USER_TOKEN, ""), uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if (resultResource.data != null && resultResource.data.getCode() == 200) {
                        Toast.makeText(ReservationDetailActivity.this, "Solicitud Denegada", Toast.LENGTH_SHORT).show();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    private void acceptBookRequest(String uuid) {
        disposable = userViewModel.acceptReservation(sharedPreferences.getString(USER_TOKEN, ""), uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultResource -> {
                    if (resultResource.data != null && resultResource.data.getCode() == 200) {
                        Toast.makeText(ReservationDetailActivity.this, "Solicitud Aceptada", Toast.LENGTH_SHORT).show();
                    }
                }, Timber::e);
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
                acceptBookRequest(reservationItem.getId());
                break;
            case R.id.btn_denied:
                deniedBookRequest(reservationItem.getId());
                break;
        }
    }
}

