package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.remote.pojo.AnaliticsGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.model.repository.rentanalitics.RentAnaliticsRepository;
import com.infinitum.bookingqba.view.adapters.items.horizontal.HorizontalItem;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class RentAnaliticsViewModel extends ViewModel {

    private RentAnaliticsRepository rentAnaliticsRepository;

    @Inject
    public RentAnaliticsViewModel(RentAnaliticsRepository rentAnaliticsRepository) {
        this.rentAnaliticsRepository = rentAnaliticsRepository;
    }

    public Single<List<RentAnalitics>> getRentAnalitics(List<String> uuids) {
        return rentAnaliticsRepository.getRentAnalitics(uuids);
    }

    public Single<AnaliticsGroup> rentAnalitics(String uuid) {
        return rentAnaliticsRepository.rentAnalitics(uuid);
    }

    public Single<CommonSpinnerList> getRentSpinnerList(List<String> uuids) {
        return rentAnaliticsRepository.rentByUuidCommaSeparate(uuids);
    }

    public Single<ArrayList<HorizontalItem>> getRentByUuidList(List<String> uuids) {
        return rentAnaliticsRepository.rentByUuidList(uuids)
                .map(this::transformToHorizontalItems)
                .subscribeOn(Schedulers.io());
    }

    private ArrayList<HorizontalItem> transformToHorizontalItems(List<RentAndGalery> rentAndGaleries) {
        ArrayList<HorizontalItem> horizontalItems = new ArrayList<>();
        for (RentAndGalery rentAndGalery : rentAndGaleries) {
            horizontalItems.add(new HorizontalItem(rentAndGalery.getId(), rentAndGalery.getName()
                    , String.valueOf(rentAndGalery.getRating())));
        }
        return horizontalItems;
    }


}
