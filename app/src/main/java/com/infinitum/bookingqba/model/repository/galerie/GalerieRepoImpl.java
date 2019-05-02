package com.infinitum.bookingqba.model.repository.galerie;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.pojo.GaleryUpdateUtil;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import static com.infinitum.bookingqba.util.Constants.BASIC_URL_API;

public class GalerieRepoImpl implements GalerieRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public GalerieRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     *
     * @return
     */
    private Single<List<Galerie>> fetchGaleries(String dateValue) {
        return retrofit.create(ApiInterface.class).getGaleries(dateValue);
    }

    @Override
    public Single<ResponseBody> fetchImage(String url) {
        return retrofit
                .create(ApiInterface.class)
                .getSingleImage(url)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     *
     * @param gsonList List<Galerie>
     * @return List<GalerieEntity>
     */
    private List<GalerieEntity> parseGsonToEntity(List<Galerie> gsonList) {
        ArrayList<GalerieEntity> listEntity = new ArrayList<>();
        GalerieEntity entity;
        for (Galerie item : gsonList) {
            entity = new GalerieEntity(item.getId(), BASIC_URL_API + "/" + item.getImage(), null, item.getRent());
            listEntity.add(entity);
        }
        return listEntity;
    }

    @Override
    public Single<List<GalerieEntity>> fetchRemoteAndTransform(String dateValue) {
        return fetchGaleries(dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<List<GalerieEntity>> allGaleries() {
        return qbaDao.getAllGaleries().subscribeOn(Schedulers.io());
    }

    @Override
    public Completable updateGalery(GalerieEntity galerieEntity) {
        return Completable
                .fromAction(() -> qbaDao.updateGalerie(galerieEntity))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable updateListGaleryUtil(List<GaleryUpdateUtil> list) {
        return Completable
                .fromAction(() -> qbaDao.updateListGaleryUpdateUtil(list))
                .subscribeOn(Schedulers.io());
    }


    @Override
    public Completable insert(List<GalerieEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertGaleries(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeGaleries(String dateValue) {
        return fetchRemoteAndTransform(dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
