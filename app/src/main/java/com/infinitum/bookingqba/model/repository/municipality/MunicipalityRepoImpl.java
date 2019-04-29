package com.infinitum.bookingqba.model.repository.municipality;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MunicipalityRepoImpl implements MunicipalityRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public MunicipalityRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     * @return
     */
    private Single<List<Municipality>> fetchMunicipalities(String dateValue) {
        return retrofit.create(ApiInterface.class).getMunicipality(dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<MunicipalityEntity> parseGsonToEntity(List<Municipality> gsonList) {
        ArrayList<MunicipalityEntity> listEntity = new ArrayList<>();
        MunicipalityEntity entity;
        for (Municipality item : gsonList) {
            entity = new MunicipalityEntity(item.getId(), item.getName(), item.getProvince());
            listEntity.add(entity);
        }
        return listEntity;
    }

    /**
     * Ejecuta la peticion y devuelve resultado transformado
     * @return
     */
    @Override
    public Single<List<MunicipalityEntity>> fetchRemoteAndTransform(String dateValue) {
        return fetchMunicipalities(dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Insera coleccion de Municipios
     * @param entities
     * @return
     */
    @Override
    public Completable insert(List<MunicipalityEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertMunicipalities(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeMunicipalities(String dateValue) {
        return fetchRemoteAndTransform(dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

}
