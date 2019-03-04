package com.infinitum.bookingqba.model.repository.poi;

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
    private Single<List<Poi>> fetchPois() {
        return retrofit.create(ApiInterface.class).getPois();
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
            entity = new PoiEntity(item.getId(), item.getNombre(), item.getTipoLugarInteres());
            listEntity.add(entity);
        }
        return listEntity;
    }

    @Override
    public Single<List<PoiEntity>> fetchRemoteAndTransform() {
        return fetchPois().flatMap((Function<List<Poi>, SingleSource<? extends List<PoiEntity>>>) pois -> {
            ArrayList<PoiEntity> listEntity = new ArrayList<>(parseGsonToEntity(pois));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertPois(List<PoiEntity> poiEntityList) {
        return Completable.fromAction(() -> qbaDao.upsertPoi(poiEntityList))
                .subscribeOn(Schedulers.io());
    }
}
