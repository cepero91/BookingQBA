package com.infinitum.bookingqba.model.repository.rentmode;

import com.infinitum.bookingqba.model.local.entity.RentModeEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface RentModeRepository {

    Single<List<RentModeEntity>> fetchRemoteAndTransform();

    Completable insertRentsMode(List<RentModeEntity> rentModeEntities);
}
