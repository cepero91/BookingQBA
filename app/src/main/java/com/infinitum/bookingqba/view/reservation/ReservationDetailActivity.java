package com.infinitum.bookingqba.view.reservation;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityReservationDetailBinding;
import com.infinitum.bookingqba.viewmodel.RentViewModel;
import com.infinitum.bookingqba.viewmodel.UserViewModel;
import com.infinitum.bookingqba.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.USER_TOKEN;

public class ReservationDetailActivity extends AppCompatActivity {

    private ActivityReservationDetailBinding reservationDetailBinding;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SharedPreferences sharedPreferences;

    private UserViewModel userViewModel;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        reservationDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_reservation_detail);

        compositeDisposable = new CompositeDisposable();

        String uuid = getIntent().getExtras().getString("uuid");

        userViewModel = ViewModelProviders.of(this,viewModelFactory).get(UserViewModel.class);

        reservationDetailBinding.aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               disposable = userViewModel.acceptReservation(sharedPreferences.getString(USER_TOKEN,""),uuid)
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(resultResource -> {
                           if(resultResource.data!=null && resultResource.data.getCode() == 200){
                               Toast.makeText(ReservationDetailActivity.this, "Solicitud Aceptada", Toast.LENGTH_SHORT).show();
                           }
                       }, Timber::e);
               compositeDisposable.add(disposable);
            }
        });

        reservationDetailBinding.denegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disposable = userViewModel.deniedReservation(sharedPreferences.getString(USER_TOKEN,""),uuid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resultResource -> {
                            if(resultResource.data!=null && resultResource.data.getCode() == 200){
                                Toast.makeText(ReservationDetailActivity.this, "Solicitud Denegada", Toast.LENGTH_SHORT).show();
                            }
                        }, Timber::e);
                compositeDisposable.add(disposable);
            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}

