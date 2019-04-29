package com.infinitum.bookingqba.model.repository.rentpoi;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface RentPoiRepository {

    Single<List<RentPoiEntity>> fetchRemoteAndTransform(String dateValue);

    Completable insert(List<RentPoiEntity> entities);

    Single<OperationResult> syncronizeRentPoi(String dateValue);

}
