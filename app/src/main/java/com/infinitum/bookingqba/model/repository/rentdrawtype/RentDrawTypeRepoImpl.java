package com.infinitum.bookingqba.model.repository.rentdrawtype;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentDrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.DrawType;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.model.remote.pojo.RentDrawType;
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

public class RentDrawTypeRepoImpl implements RentDrawTypeRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public RentDrawTypeRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     *
     * @return
     */
    private Single<List<RentDrawType>> fetchRentDrawTypes() {
        return retrofit.create(ApiInterface.class).getRentsDrawType();
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     *
     * @param gsonList
     * @return
     */
    private List<RentDrawTypeEntity> parseGsonToEntity(List<RentDrawType> gsonList) {
        ArrayList<RentDrawTypeEntity> listEntity = new ArrayList<>();
        RentDrawTypeEntity entity;
        for (RentDrawType item : gsonList) {
            entity = new RentDrawTypeEntity(item.getId());
            for (DrawType drawType : item.getTipo_monedas()) {
                entity.setDrawTypeId(drawType.getId());
                listEntity.add(entity);
            }
        }
        return listEntity;
    }

    @Override
    public Single<List<RentDrawTypeEntity>> fetchRemoteAndTransform() {
        return fetchRentDrawTypes()
                .flatMap((Function<List<RentDrawType>, SingleSource<? extends List<RentDrawTypeEntity>>>) rentDrawTypes -> {
                    ArrayList<RentDrawTypeEntity> listEntity = new ArrayList<>(parseGsonToEntity(rentDrawTypes));
                    return Single.just(listEntity);
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertRentDrawType(List<RentDrawTypeEntity> rentDrawTypeEntityList) {
        return Completable.fromAction(() -> qbaDao.upsertRentsDrawType(rentDrawTypeEntityList))
                .subscribeOn(Schedulers.io());
    }
}
