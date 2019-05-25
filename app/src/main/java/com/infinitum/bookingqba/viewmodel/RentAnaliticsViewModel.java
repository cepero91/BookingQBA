package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.remote.pojo.AnaliticsGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.model.repository.rentanalitics.RentAnaliticsRepository;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;

public class RentAnaliticsViewModel extends ViewModel {

    private RentAnaliticsRepository rentAnaliticsRepository;

    @Inject
    public RentAnaliticsViewModel(RentAnaliticsRepository rentAnaliticsRepository) {
        this.rentAnaliticsRepository = rentAnaliticsRepository;
    }

    public Single<List<RentAnalitics>> getRentAnalitics(List<String> uuids){
        return rentAnaliticsRepository.getRentAnalitics(uuids);
    }

    public Single<AnaliticsGroup> rentAnalitics(String uuid){
        return rentAnaliticsRepository.rentAnalitics(uuid);
    }

    public Single<CommonSpinnerList> getRentSpinnerList(List<String> uuids){
        return rentAnaliticsRepository.rentByUuidCommaSeparate(uuids);
    }


}
