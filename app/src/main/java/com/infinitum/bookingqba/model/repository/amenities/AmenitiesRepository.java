package com.infinitum.bookingqba.model.repository.amenities;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface AmenitiesRepository {

    Single<List<AmenitiesEntity>> fetchRemoteAndTransform(String dateValue);

    Completable insert(List<AmenitiesEntity> entities);

    Flowable<Resource<List<AmenitiesEntity>>> allAmenities();

    Single<OperationResult> syncronizeAmenities(String dateValue);

}
