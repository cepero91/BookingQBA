package com.infinitum.bookingqba.model.repository.rentamenities;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RentAmenitiesRepoImpl implements RentAmenitiesRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public RentAmenitiesRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     * @return
     */
    private Single<List<RentAmenities>> fetchRentAmenities(String dateValue) {
        return retrofit.create(ApiInterface.class).getRentsAmenities(dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<RentAmenitiesEntity> parseGsonToEntity(List<RentAmenities> gsonList) {
        ArrayList<RentAmenitiesEntity> listEntity = new ArrayList<>();
        RentAmenitiesEntity entity;
        for (RentAmenities item : gsonList) {
            for(Amenities amenities: item.getAmenities()){
                entity = new RentAmenitiesEntity(item.getId());
                entity.setAmenityId(amenities.getId());
                listEntity.add(entity);
            }
        }
        return listEntity;
    }

    @Override
    public Single<List<RentAmenitiesEntity>> fetchRemoteAndTransform(String dateValue) {
        return fetchRentAmenities(dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<RentAmenitiesEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertRentAmenities(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeRentAmenities(String dateValue) {
        return fetchRemoteAndTransform(dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
