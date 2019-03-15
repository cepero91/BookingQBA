package com.infinitum.bookingqba.model.repository.amenities;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface AmenitiesRepository {

    Single<List<AmenitiesEntity>> fetchRemoteAndTransform();

    Completable insertAmenities(List<AmenitiesEntity> amenitiesEntityList);

    Flowable<Resource<List<AmenitiesEntity>>> fetchLocal();

}
