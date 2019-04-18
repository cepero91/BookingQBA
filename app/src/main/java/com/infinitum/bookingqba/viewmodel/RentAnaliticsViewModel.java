package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.model.repository.rentanalitics.RentAnaliticsRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class RentAnaliticsViewModel extends ViewModel {

    private RentAnaliticsRepository rentAnaliticsRepository;

    @Inject
    public RentAnaliticsViewModel(RentAnaliticsRepository rentAnaliticsRepository) {
        this.rentAnaliticsRepository = rentAnaliticsRepository;
    }

    public Single<RentAnalitics> getRentAnalitics(List<String> uuids){
        return rentAnaliticsRepository.getRentAnalitics(uuids);
    }
}
