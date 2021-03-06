package com.infinitum.bookingqba.model.repository.rent;

import android.arch.paging.DataSource;
import android.util.Pair;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.entity.WishedRentEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndDependencies;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.local.pojo.RentDetailEsential;
import com.infinitum.bookingqba.model.local.pojo.RentMostComment;
import com.infinitum.bookingqba.model.local.pojo.RentMostRating;
import com.infinitum.bookingqba.model.local.pojo.RentPoiAndRelation;
import com.infinitum.bookingqba.model.remote.pojo.AddressResponse;
import com.infinitum.bookingqba.model.remote.pojo.BlockDay;
import com.infinitum.bookingqba.model.remote.pojo.BookRequest;
import com.infinitum.bookingqba.model.remote.pojo.DisabledDays;
import com.infinitum.bookingqba.model.remote.pojo.DrawChange;
import com.infinitum.bookingqba.model.remote.pojo.RatingVote;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentEdit;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.remote.pojo.RentPoiReferenceZone;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.view.adapters.items.home.RentMostCommentItem;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.Response;

public interface RentRepository {

    Completable insert(List<RentEntity> entities);

    Flowable<Resource<List<RentAndDependencies>>> allRent();

    Flowable<Resource<List<RentAndDependencies>>> allRentByOrderType(char orderType, String province);

    DataSource.Factory<Integer, RentAndGalery> allRentByZone(String province, String zone);

    Flowable<Resource<List<RentMostRating>>> fiveMostRatingRents(String province);

    Flowable<Resource<List<RentMostComment>>> fiveMostCommentRents(String province);

    Flowable<Resource<List<RentAndGalery>>> fiveNewRentByProvince(String province);

    Flowable<Resource<RentDetail>> getRentDetailById(String uuid);

    Flowable<Resource<List<RentPoiAndRelation>>> getRentPoiById(String uuid);

    Flowable<Resource<WishedRentEntity>> rentIsWished(String rentUudi, String userId);

    Completable addOrUpdateRentVisitCount(String id, String rent);

    Completable addOrUpdateRating(RatingEntity entity);

    Single<RatingEntity> getLastRentVote(String rent);

    Flowable<Resource<List<RentAndDependencies>>> allWishedRent(String province, String userId);

    Completable addOrUpdateWishedRent(String uuid, String userId, int isWished);

    Completable update(RentEntity entity);

    Single<OperationResult> syncronizeRents(String token, String dateValue);

    Flowable<Resource<List<RentModeEntity>>> allRentMode();

    Single<Resource<List<RentMode>>> allRemoteRentMode(String token);

    Flowable<Resource<List<RentAndDependencies>>> filterRents(Map<String, List<String>> filterParams, String province);

    Single<List<ResponseResult>> addRent(String token, Map<String, Object> params);

    Flowable<Resource<List<RentEsential>>> allRentByUserId(String token, String userid);

    Single<Response<AddressResponse>> addressByLocation(double lat, double lon);

    Single<Resource<List<RentEdit>>> rentById(String token, String uuid);

    Flowable<Resource<List<RentAndDependencies>>> rentNearLocation(LatLong latLong, Map<String,Object> filterParams);

    Flowable<Double> maxRentPrice();

    Flowable<Integer> maxRentCapability();

    Single<Resource<ResponseResult>> sendBookRequest(String token, BookRequest bookRequest);

    Single<Resource<ResponseResult>> sendRatingVote(String token, RatingVote ratingVote);

    Single<Resource<List<DrawChange>>> drawChangeByFinalPrice(String token, double finalPrice);

    Single<Resource<DisabledDays>> disabledDaysByRent(String token, String uuid);

    Single<Resource<ResponseResult>> blockDates(String token, BlockDay blockDay);

    Single<Resource<RentPoiReferenceZone>> referenceZoneAndPoiByLocation(String token, double lat, double lon);

    Single<Resource<ResponseResult>> deleteImage(String token, String uuid);

    Single<Resource<ResponseResult>> deleteOffer(String token, String uuid);

}
