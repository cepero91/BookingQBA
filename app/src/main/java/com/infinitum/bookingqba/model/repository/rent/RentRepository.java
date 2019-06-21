package com.infinitum.bookingqba.model.repository.rent;

import android.arch.paging.DataSource;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;

import java.util.List;
import java.util.Map;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface RentRepository {

    Completable insert(List<RentEntity> entities);

    Flowable<Resource<List<RentAndGalery>>> allRent();

    DataSource.Factory<Integer,RentAndGalery> allRentByOrderType(char orderType, String province);

    DataSource.Factory<Integer,RentAndGalery> allRentByZone(String province, String zone);

    Flowable<Resource<List<RentAndGalery>>> fivePopRentByProvince(String province);

    Flowable<Resource<List<RentAndGalery>>> fiveNewRentByProvince(String province);

    Flowable<Resource<RentDetail>> getRentDetailById(String uuid);

    Completable addOrUpdateRentVisitCount(String id, String rent);

    Completable addOrUpdateRating(RatingEntity entity);

    Single<RatingEntity> getLastRentVote(String rent);

    Flowable<Resource<List<RentAndGalery>>> allWishedRent(String province);

    Completable updateIsWishedRent(String uuid, int isWished);

    Completable update(RentEntity entity);

    Single<OperationResult> syncronizeRents(String token, String dateValue);

    Flowable<Resource<List<RentModeEntity>>> allRentMode();

    DataSource.Factory<Integer,RentAndGalery> filterRents(Map<String,List<String>> filterParams, String province);

}
