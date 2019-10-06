package com.infinitum.bookingqba.model.repository.referencezone;

import android.util.Base64;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import timber.log.Timber;

public class ReferenceZoneRepoImpl implements ReferenceZoneRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public ReferenceZoneRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion al API
     * @return
     */
    private Single<List<ReferenceZone>> fetchReferencesZone(String token, String value) {
        return retrofit.create(ApiInterface.class).getReferencesZone(token, value);
    }

    /**
     * Tranforma entidad JSON en entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<ReferenceZoneEntity> parseGsonToEntity(List<ReferenceZone> gsonList) {
        ArrayList<ReferenceZoneEntity> listEntity = new ArrayList<>();
        ReferenceZoneEntity entity;
        for (ReferenceZone item : gsonList) {
            entity = new ReferenceZoneEntity(item.getId(), item.getName());
            listEntity.add(entity);
        }
        return listEntity;
    }


    public Single<List<ReferenceZoneEntity>> fetchRemoteAndTransform(String token, String value) {
        return fetchReferencesZone(token, value)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<ReferenceZoneEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertReferencesZone(entities)).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<Resource<List<ReferenceZoneEntity>>> allLocalReferencesZone(String province) {
        return qbaDao.getAllReferencesZone(province)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Single<Resource<List<ReferenceZone>>> allRemoteReferencesZone(String token) {
        return retrofit.create(ApiInterface.class).getAllReferencesZone(token)
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Single<OperationResult> syncronizeReferenceZone(String token,String dateValue) {
        return fetchRemoteAndTransform(token, dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
