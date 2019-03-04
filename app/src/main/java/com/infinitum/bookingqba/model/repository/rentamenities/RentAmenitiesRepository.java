package com.infinitum.bookingqba.model.repository.rentamenities;

import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface RentAmenitiesRepository {

    Single<List<RentAmenitiesEntity>> fetchRemoteAndTransform();

    Completable insertRentAmenities(List<RentAmenitiesEntity> rentAmenitiesEntityList);

}
