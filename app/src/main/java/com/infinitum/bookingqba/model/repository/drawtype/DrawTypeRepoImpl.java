package com.infinitum.bookingqba.model.repository.drawtype;

import com.infinitum.bookingqba.model.OperationResult;
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
    private Single<List<DrawType>> fetchDrawsType(String dateValue) {
        return retrofit.create(ApiInterface.class).getDrawsType(dateValue);
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
    public Single<List<DrawTypeEntity>> fetchRemoteAndTransform(String dateValue) {
        return fetchDrawsType(dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<DrawTypeEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertDrawsType(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeDrawType(String dateValue) {
        return fetchRemoteAndTransform(dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
