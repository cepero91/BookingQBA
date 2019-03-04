package com.infinitum.bookingqba.model.repository.rent;

import com.infinitum.bookingqba.model.local.entity.RentEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface RentRepository {

    Single<List<RentEntity>> fetchRemoteAndTransform();

    Completable insertRents(List<RentEntity> rentEntityList);
}
