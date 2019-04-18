package com.infinitum.bookingqba.model.repository.rentanalitics;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.view.profile.DataGenerator;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RentAnaliticsRepoImpl implements RentAnaliticsRepository{

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public RentAnaliticsRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    @Override
    public Single<RentAnalitics> getRentAnalitics(List<String> uuids) {
        return Single.just(DataGenerator.getRentAnalitic()).subscribeOn(Schedulers.io()).delay(2000, TimeUnit.MILLISECONDS);
    }

}
