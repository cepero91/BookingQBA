package com.infinitum.bookingqba.model.repository.poitype;

import android.util.Base64;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.PoiType;

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

public class PoiTypeRepoImpl implements PoiTypeRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public PoiTypeRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     * @return
     */
    private Single<List<PoiType>> fetchPoiTypes(String token, String dateValue) {
        return retrofit.create(ApiInterface.class).getPoiTypes(token, dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<PoiTypeEntity> parseGsonToEntity(List<PoiType> gsonList) {
        ArrayList<PoiTypeEntity> listEntity = new ArrayList<>();
        PoiTypeEntity entity;
        for (PoiType item : gsonList) {
            entity = new PoiTypeEntity(item.getId(), item.getName());
            listEntity.add(entity);
        }
        return listEntity;
    }

    public Single<List<PoiTypeEntity>> fetchRemoteAndTransform(String token, String dateValue) {
        return fetchPoiTypes(token, dateValue).map(this::parseGsonToEntity).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<PoiTypeEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertPoiTypes(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizePoiTypes(String token, String dateValue) {
        return fetchRemoteAndTransform(token, dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    @Override
    public Flowable<Resource<List<PoiTypeEntity>>> allLocalPoiType() {
        return qbaDao.getAllPoiTypes()
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }
}
