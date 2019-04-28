package com.infinitum.bookingqba.model.repository.amenities;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AmenitiesRepoImpl implements AmenitiesRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public AmenitiesRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     * @return
     */
    private Single<List<Amenities>> fetchAmenities(String dateValue) {
        return retrofit.create(ApiInterface.class).getAmenities(dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<AmenitiesEntity> parseGsonToEntity(List<Amenities> gsonList) {
        ArrayList<AmenitiesEntity> listEntity = new ArrayList<>();
        AmenitiesEntity entity;
        for (Amenities item : gsonList) {
            entity = new AmenitiesEntity(item.getId(), item.getName());
            listEntity.add(entity);
        }
        return listEntity;
    }

    @Override
    public Single<List<AmenitiesEntity>> fetchRemoteAndTransform(String dateValue) {
        return fetchAmenities(dateValue).flatMap((Function<List<Amenities>, SingleSource<? extends List<AmenitiesEntity>>>) amenities -> {
            ArrayList<AmenitiesEntity> listEntity = new ArrayList<>(parseGsonToEntity(amenities));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<AmenitiesEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertAmenities(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<Resource<List<AmenitiesEntity>>> allAmenities() {
        return qbaDao.getAllAmenities()
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeAmenities(String dateValue) {
        return fetchRemoteAndTransform(dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
