package com.infinitum.bookingqba.model.repository.rentamenities;

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
    private Single<List<RentAmenities>> fetchRentAmenities() {
        return retrofit.create(ApiInterface.class).getRentsAmenities();
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
            entity = new RentAmenitiesEntity(item.getId());
            for(Amenities amenities: item.getFacilidades()){
                entity.setAmenityId(amenities.getId());
                listEntity.add(entity);
            }
        }
        return listEntity;
    }

    @Override
    public Single<List<RentAmenitiesEntity>> fetchRemoteAndTransform() {
        return fetchRentAmenities()
                .flatMap((Function<List<RentAmenities>, SingleSource<? extends List<RentAmenitiesEntity>>>) rentAmenities -> {
            ArrayList<RentAmenitiesEntity> listEntity = new ArrayList<>(parseGsonToEntity(rentAmenities));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertRentAmenities(List<RentAmenitiesEntity> rentAmenitiesEntityList) {
        return Completable.fromAction(() -> qbaDao.upsertRentAmenities(rentAmenitiesEntityList))
                .subscribeOn(Schedulers.io());
    }
}
