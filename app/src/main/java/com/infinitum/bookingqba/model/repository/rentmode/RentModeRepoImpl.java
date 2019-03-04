package com.infinitum.bookingqba.model.repository.rentmode;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RentModeRepoImpl implements RentModeRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public RentModeRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion al API
     * @return
     */
    private Single<List<RentMode>> fetchRentsMode() {
        return retrofit.create(ApiInterface.class).getRentsMode();
    }

    /**
     * Tranforma entidad JSON en entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<RentModeEntity> parseGsonToEntity(List<RentMode> gsonList) {
        ArrayList<RentModeEntity> listEntity = new ArrayList<>();
        RentModeEntity entity;
        for (RentMode item : gsonList) {
            entity = new RentModeEntity(item.getId(), item.getNombre());
            listEntity.add(entity);
        }
        return listEntity;
    }

    @Override
    public Single<List<RentModeEntity>> fetchRemoteAndTransform() {
        return fetchRentsMode().flatMap((Function<List<RentMode>, SingleSource<? extends List<RentModeEntity>>>) rentModes -> {
            ArrayList<RentModeEntity> listEntity = new ArrayList<>(parseGsonToEntity(rentModes));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertRentsMode(List<RentModeEntity> rentModeEntities) {
        return Completable.fromAction(() -> qbaDao.upsertRentsMode(rentModeEntities)).subscribeOn(Schedulers.io());
    }
}
