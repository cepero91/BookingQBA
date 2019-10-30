package com.infinitum.bookingqba.model.repository.rentanalitics;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.remote.pojo.AnaliticsGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.view.adapters.items.spinneritem.CommonSpinnerList;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface RentAnaliticsRepository {

    Single<AnaliticsGroup> rentAnalitics(String uuid);

    Single<CommonSpinnerList> rentByUuidCommaSeparate(List<String> uuid);

    Single<List<RentAndGalery>> rentByUuidList(List<String> uuid);

    Flowable<Resource<List<RentEsential>>> allRentByUserId(String token, String userId);

}
