package com.infinitum.bookingqba.model.repository.galerie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import timber.log.Timber;

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
    private Single<List<Galerie>> fetchGaleries() {
        return retrofit.create(ApiInterface.class).getGaleries();
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
            entity = new GalerieEntity(item.getId(), item.getImagen(), item.getHospedaje());
            listEntity.add(entity);
        }
        return listEntity;
    }

    @Override
    public Single<List<GalerieEntity>> fetchRemoteAndTransform() {
        return fetchGaleries()
                .flatMap((Function<List<Galerie>, SingleSource<? extends List<GalerieEntity>>>) galeries -> {
                    ArrayList<GalerieEntity> listEntity = new ArrayList<>(parseGsonToEntity(galeries));
                    return Single.just(listEntity);
                })
                .flatMap(v -> downloadImages(v).andThen(Single.just(v)))
                .subscribeOn(Schedulers.io());
    }


    private Completable downloadImages(List<GalerieEntity>entities){
        ArrayList<Completable>completables = new ArrayList<>();
        for(GalerieEntity item: entities){
            Single<ResponseBody> singleImage = retrofit.create(ApiInterface.class)
                    .getSingleImages(item.getImageUrl())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess(responseBody -> {
                        Timber.e("onSuccess segundo flatmap");
                        byte[] bytes = responseBody.bytes();
                        item.setImageByte(bytes);
                    });
            completables.add(Completable.fromSingle(singleImage));
        }
        return Completable.concat(completables);
    }



    @Override
    public Completable insertGalerie(List<GalerieEntity> galerieEntities) {
        return Completable.fromAction(() -> qbaDao.upsertGaleries(galerieEntities))
                .subscribeOn(Schedulers.io());
    }
}
