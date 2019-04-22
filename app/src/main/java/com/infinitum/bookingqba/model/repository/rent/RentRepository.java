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

    DataSource.Factory<Integer,RentAndGalery> allRentByOrderType(char orderType, String province);

    Flowable<Resource<List<RentAndGalery>>> fivePopRentByProvince(String province);

    Flowable<Resource<List<RentAndGalery>>> fiveNewRentByProvince(String province);

    Flowable<Resource<RentDetail>> getRentDetailById(String uuid);

    Completable addOrUpdateRentVisitCount(String id, String rent);

    Flowable<Resource<List<RentAndGalery>>> allWishedRent(String province);

    Completable updateIsWishedRent(String uuid, int isWished);

}
