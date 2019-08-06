package com.infinitum.bookingqba.model.repository.poi;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Poi;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PoiRepoImpl implements PoiRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public PoiRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     * @return
     */
    private Single<List<Poi>> fetchPois(String token, String dateValue) {
        return retrofit.create(ApiInterface.class).getPois(token, dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<PoiEntity> parseGsonToEntity(List<Poi> gsonList) {
        ArrayList<PoiEntity> listEntity = new ArrayList<>();
        PoiEntity entity;
        for (Poi item : gsonList) {
            entity = new PoiEntity(item.getId(), item.getName(), item.getCategory(),
                    item.getMinLat(),item.getMinLon(),item.getMaxLat(),item.getMaxLon());
            listEntity.add(entity);
        }
        return listEntity;
    }

    public Single<List<PoiEntity>> fetchRemoteAndTransform(String token, String dateValue) {
        return fetchPois(token, dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<PoiEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertPoi(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizePois(String token, String dateValue) {
        return fetchRemoteAndTransform(token, dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
