package com.infinitum.bookingqba.model.repository.amenities;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface AmenitiesRepository {

    Completable insert(List<AmenitiesEntity> entities);

    Flowable<Resource<List<AmenitiesEntity>>> allLocalAmenities();

    Single<Resource<List<Amenities>>> allRemoteAmenities(String token);

    Single<OperationResult> syncronizeAmenities(String token, String dateValue);

}
