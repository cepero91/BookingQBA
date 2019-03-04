package com.infinitum.bookingqba.model.repository.rentdrawtype;

import com.infinitum.bookingqba.model.local.entity.RentDrawTypeEntity;
import com.infinitum.bookingqba.model.remote.pojo.RentDrawType;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface RentDrawTypeRepository {

    Single<List<RentDrawTypeEntity>> fetchRemoteAndTransform();

    Completable insertRentDrawType(List<RentDrawTypeEntity> rentDrawTypeEntityList);

}
