package com.infinitum.bookingqba.model.repository.drawtype;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.DrawType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DrawTypeRepoImpl implements DrawTypeRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public DrawTypeRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     * @return
     */
    private Single<List<DrawType>> fetchDrawsType() {
        return retrofit.create(ApiInterface.class).getDrawsType();
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     * @param gsonList List<DrawType>
     * @return List<DrawTypeEntity>
     */
    private List<DrawTypeEntity> parseGsonToEntity(List<DrawType> gsonList) {
        ArrayList<DrawTypeEntity> listEntity = new ArrayList<>();
        DrawTypeEntity entity;
        for (DrawType item : gsonList) {
            entity = new DrawTypeEntity(item.getId(), item.getName());
            listEntity.add(entity);
        }
        return listEntity;
    }

    @Override
    public Single<List<DrawTypeEntity>> fetchRemoteAndTransform() {
        return fetchDrawsType().flatMap((Function<List<DrawType>, SingleSource<? extends List<DrawTypeEntity>>>) drawTypes -> {
            ArrayList<DrawTypeEntity> listEntity = new ArrayList<>(parseGsonToEntity(drawTypes));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertDrawType(List<DrawTypeEntity> drawTypeEntities) {
        return Completable.fromAction(() -> qbaDao.upsertDrawsType(drawTypeEntities))
                .subscribeOn(Schedulers.io());
    }
}
