package com.infinitum.bookingqba.model.repository.referencezone;

import android.util.Base64;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;

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
    private Single<List<ReferenceZone>> fetchReferencesZone() {
        return retrofit.create(ApiInterface.class).getReferencesZone();
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
            entity = new ReferenceZoneEntity(item.getId(), item.getNombre(), Base64.decode(item.getImagen(),Base64.DEFAULT));
            listEntity.add(entity);
        }
        return listEntity;
    }

    @Override
    public Single<List<ReferenceZoneEntity>> fetchRemoteAndTransform() {
        return fetchReferencesZone().flatMap((Function<List<ReferenceZone>, SingleSource<? extends List<ReferenceZoneEntity>>>) referenceZones -> {
            ArrayList<ReferenceZoneEntity> listEntity = new ArrayList<>(parseGsonToEntity(referenceZones));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertReferencesMode(List<ReferenceZoneEntity> referenceZoneEntities) {
        return Completable.fromAction(() -> qbaDao.upsertReferencesZone(referenceZoneEntities)).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<Resource<List<ReferenceZoneEntity>>> allReferencesZone() {
        return qbaDao.getAllReferencesZone()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }
}
