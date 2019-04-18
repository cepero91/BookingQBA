package com.infinitum.bookingqba.model.repository.rentanalitics;

import com.infinitum.bookingqba.model.remote.pojo.RentAnalitics;

import java.util.List;

import io.reactivex.Single;

public interface RentAnaliticsRepository {

    Single<RentAnalitics> getRentAnalitics(List<String> uuids);

}
