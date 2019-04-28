package com.infinitum.bookingqba.model.repository.galerie;

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
    private Single<List<Galerie>> fetchGaleries() {
        return retrofit.create(ApiInterface.class).getGaleries();
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
    public Single<List<GalerieEntity>> fetchRemoteAndTransform() {
        return fetchGaleries()
                .flatMap((Function<List<Galerie>, SingleSource<? extends List<GalerieEntity>>>) galeries -> {
                    ArrayList<GalerieEntity> listEntity = new ArrayList<>(parseGsonToEntity(galeries));
                    return Single.just(listEntity);
                })
                .subscribeOn(Schedulers.io());
    }


//    private Completable downloadImages(List<GalerieEntity>entities){
//        ArrayList<Completable>completables = new ArrayList<>();
//        for(GalerieEntity item: entities){
//            Single<ResponseBody> singleImage = retrofit.create(ApiInterface.class)
//                    .getSingleImages(item.getImageUrl())
//                    .subscribeOn(Schedulers.io())
//                    .doOnSuccess(responseBody -> {
//                        byte[] bytes = responseBody.bytes();
//                        item.setImageByte(bytes);
//                    });
//            completables.add(Completable.fromSingle(singleImage));
//        }
//        return Completable.concat(completables);
//    }
//


//    @Override
//    public Single<List<GalerieEntity>> fetchRemoteAndTransform2() {
//        return fetchGaleries()
//                .flatMap((Function<List<Galerie>, SingleSource<? extends List<GalerieEntity>>>) galeries -> {
//                    ArrayList<GalerieEntity> listEntity = new ArrayList<>(parseGsonToEntity(galeries));
//                    return Single.just(listEntity);
//                })
//                .subscribeOn(Schedulers.io());
//
////        .flatMap(v -> downloadImages(v).andThen(Single.just(v)))
//    }

    @Override
    public Flowable<List<GalerieEntity>> allGalerieEntities() {
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
    public Completable insertGalerie(List<GalerieEntity> galerieEntities) {
        return Completable.fromAction(() -> qbaDao.upsertGaleries(galerieEntities))
                .subscribeOn(Schedulers.io());
    }
}
