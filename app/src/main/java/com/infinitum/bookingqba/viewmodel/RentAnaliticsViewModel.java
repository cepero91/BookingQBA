package com.infinitum.bookingqba.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.remote.pojo.AnaliticsGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.model.repository.rentanalitics.RentAnaliticsRepository;
import com.infinitum.bookingqba.view.adapters.items.horizontal.HorizontalItem;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class RentAnaliticsViewModel extends ViewModel {

    private RentAnaliticsRepository rentAnaliticsRepository;

    @Inject
    public RentAnaliticsViewModel(RentAnaliticsRepository rentAnaliticsRepository) {
        this.rentAnaliticsRepository = rentAnaliticsRepository;
    }

    public Single<AnaliticsGroup> rentAnalitics(String uuid) {
        return rentAnaliticsRepository.rentAnalitics(uuid);
    }

    public Single<CommonSpinnerList> getRentSpinnerList(List<String> uuids) {
        return rentAnaliticsRepository.rentByUuidCommaSeparate(uuids);
    }


    private List<HorizontalItem> transformToHorizontalItems(Resource<List<RentEsential>> rentEsentials) {
        List<HorizontalItem> horizontalItems = new ArrayList<>();
        if (rentEsentials.data != null && rentEsentials.data.size() > 0) {
            for (RentEsential rentEsential : rentEsentials.data) {
                horizontalItems.add(new HorizontalItem(rentEsential.getId(), rentEsential.getName(),
                        rentEsential.getPortrait(), rentEsential.getRentMode()));
            }
        }
        return horizontalItems;
    }

    public Flowable<Resource<List<HorizontalItem>>> allRentByUserId(String token, String userId) {
        return rentAnaliticsRepository.allRentByUserId(token, userId)
                .map(this::transformToHorizontalItems)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }


}
