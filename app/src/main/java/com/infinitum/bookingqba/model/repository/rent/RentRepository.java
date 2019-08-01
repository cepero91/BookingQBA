package com.infinitum.bookingqba.model.repository.rent;

import android.arch.paging.DataSource;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.remote.pojo.AddressResponse;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.Response;

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

    Single<Resource<List<RentMode>>> allRemoteRentMode(String token);

    DataSource.Factory<Integer,RentAndGalery> filterRents(Map<String,List<String>> filterParams, String province);

    Single<OperationResult> addRent(String token, Rent rent, RentAmenities rentAmenities, ArrayList<String> imagesPath);

    Flowable<Resource<List<RentEsential>>> allRentByUserId(String token, String userid);

    Single<Response<AddressResponse>> addressByLocation(double lat, double lon);

}
