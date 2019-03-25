package com.infinitum.bookingqba.model.repository.rent;

import android.arch.paging.DataSource;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;

import java.util.List;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface RentRepository {

    Single<List<RentEntity>> fetchRemoteAndTransform();

    Completable insertRents(List<RentEntity> rentEntityList);

    Flowable<Resource<List<RentAndGalery>>> allRent();

    Flowable<Resource<List<RentAndGalery>>> allRentWithFirstImage(char orderType);

    Flowable<Resource<List<RentAndGalery>>> fivePopRent();

    Flowable<Resource<List<RentAndGalery>>> fiveNewRent();

    DataSource.Factory<Integer,RentAndGalery> getRentPaged();

    Flowable<Resource<RentDetail>> getRentDetailById(String uuid);
}
