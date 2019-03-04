package com.infinitum.bookingqba.model.repository.municipality;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;
import com.infinitum.bookingqba.model.remote.pojo.Province;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import timber.log.Timber;

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
    private Single<List<Municipality>> fetchMunicipalities() {
        return retrofit.create(ApiInterface.class).getMunicipality();
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
            entity = new MunicipalityEntity(item.getId(), item.getNombre(), item.getProvincia());
            listEntity.add(entity);
        }
        return listEntity;
    }

    /**
     * Ejecuta la peticion y devuelve resultado transformado
     * @return
     */
    @Override
    public Single<List<MunicipalityEntity>> fetchRemoteAndTransform() {
        return fetchMunicipalities().flatMap((Function<List<Municipality>, SingleSource<? extends List<MunicipalityEntity>>>) municipalities -> {
            ArrayList<MunicipalityEntity> listEntity = new ArrayList<>(parseGsonToEntity(municipalities));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }

    /**
     * Insera coleccion de Municipios
     * @param municipalityEntityList
     * @return
     */
    @Override
    public Completable insertMunicipalities(List<MunicipalityEntity> municipalityEntityList) {
        return Completable.fromAction(() -> qbaDao.upsertMunicipalities(municipalityEntityList))
                .subscribeOn(Schedulers.io());
    }

}
