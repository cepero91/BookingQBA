package com.infinitum.bookingqba.model.repository.rentamenities;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.util.Pair;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Amenities;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.util.StringUtils;

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
     *
     * @return
     */
    private Single<List<RentAmenities>> fetchRentAmenities(String token, String dateValue) {
        return retrofit.create(ApiInterface.class).getRentsAmenities(token, dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     *
     * @param gsonList
     * @return
     */
    private List<RentAmenitiesEntity> parseGsonToEntity(List<RentAmenities> gsonList) {
        ArrayList<RentAmenitiesEntity> listEntity = new ArrayList<>();
        for (RentAmenities rentAmenities : gsonList) {
            for (String amenitiesId : rentAmenities.getAmenities()) {
                listEntity.add(new RentAmenitiesEntity(amenitiesId, rentAmenities.getId()));
            }
        }
        return listEntity;
    }

    public Single<Pair<String, List<RentAmenitiesEntity>>> fetchRemoteAndTransform(String token, String dateValue) {
        return fetchRentAmenities(token, dateValue)
                .map(new Function<List<RentAmenities>, Pair<String, List<RentAmenitiesEntity>>>() {
                    @Override
                    public Pair<String, List<RentAmenitiesEntity>> apply(List<RentAmenities> rentAmenities) throws Exception {
                        List<String> uuids = new ArrayList<>();
                        ArrayList<RentAmenitiesEntity> listEntity = new ArrayList<>();
                        for (RentAmenities item : rentAmenities) {
                            uuids.add(item.getId());
                            for (String amenitiesId : item.getAmenities()) {
                                listEntity.add(new RentAmenitiesEntity(amenitiesId, item.getId()));
                            }
                        }
                        return new Pair<>(StringUtils.convertListToCommaSeparated(uuids),listEntity);
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public Completable deleteAllRentAmenities(String uuids) {
        String querySql = "DELETE FROM RentAmenities WHERE rentId IN(" + uuids + ")";
        SupportSQLiteQuery supportSQLiteQuery = new SimpleSQLiteQuery(querySql);
        return Completable.fromAction(() -> qbaDao.deleteAllRentAmenities(supportSQLiteQuery))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<RentAmenitiesEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertRentAmenities(entities)).subscribeOn(Schedulers.io());
    }

    public Completable deleteAndCreate(Pair<String, List<RentAmenitiesEntity>> pair) {
        return Completable.fromAction(() -> qbaDao.deleteAndCreateRentAmenities(pair)).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeRentAmenities(String token, String dateValue) {
        return fetchRemoteAndTransform(token, dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::deleteAndCreate)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
