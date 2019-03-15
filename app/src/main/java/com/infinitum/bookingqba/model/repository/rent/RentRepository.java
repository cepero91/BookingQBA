package com.infinitum.bookingqba.model.repository.rent;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface RentRepository {

    Single<List<RentEntity>> fetchRemoteAndTransform();

    Completable insertRents(List<RentEntity> rentEntityList);

    Flowable<Resource<List<RentAndGalery>>> allRent();

    Flowable<Resource<List<RentAndGalery>>> allRentWithFirstImage();
}
