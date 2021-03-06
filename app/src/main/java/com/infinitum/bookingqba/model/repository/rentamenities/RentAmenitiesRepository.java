package com.infinitum.bookingqba.model.repository.rentamenities;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface RentAmenitiesRepository {
    Completable insert(List<RentAmenitiesEntity> entities);

    Single<OperationResult> syncronizeRentAmenities(String token, String dateValue);
}
