package com.infinitum.bookingqba.model.repository.rentpoi;

import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface RentPoiRepository {

    Single<List<RentPoiEntity>> fetchRemoteAndTransform();

    Completable insertRentPoi(List<RentPoiEntity> rentPoiEntityList);

}
