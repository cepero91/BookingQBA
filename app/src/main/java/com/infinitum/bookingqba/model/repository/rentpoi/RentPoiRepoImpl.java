package com.infinitum.bookingqba.model.repository.rentpoi;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.model.remote.pojo.RentPoi;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RentPoiRepoImpl implements RentPoiRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public RentPoiRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     * @return
     */
    private Single<List<RentPoi>> fetchRentPoi() {
        return retrofit.create(ApiInterface.class).getRentsPoi();
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<RentPoiEntity> parseGsonToEntity(List<RentPoi> gsonList) {
        ArrayList<RentPoiEntity> listEntity = new ArrayList<>();
        RentPoiEntity entity;
        for (RentPoi item : gsonList) {
            entity = new RentPoiEntity(item.getId());
            for(Poi poi: item.getLugar_interes()){
                entity.setPoiId(poi.getId());
                listEntity.add(entity);
            }
        }
        return listEntity;
    }

    @Override
    public Single<List<RentPoiEntity>> fetchRemoteAndTransform() {
        return fetchRentPoi()
                .flatMap((Function<List<RentPoi>, SingleSource<? extends List<RentPoiEntity>>>) rentPoiList -> {
                    ArrayList<RentPoiEntity> listEntity = new ArrayList<>(parseGsonToEntity(rentPoiList));
                    return Single.just(listEntity);
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertRentPoi(List<RentPoiEntity> rentPoiEntityList) {
        return Completable.fromAction(() -> qbaDao.upsertRentsPoi(rentPoiEntityList))
                .subscribeOn(Schedulers.io());
    }
}
