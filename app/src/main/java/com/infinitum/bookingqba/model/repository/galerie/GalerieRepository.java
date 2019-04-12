package com.infinitum.bookingqba.model.repository.galerie;

import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.pojo.GaleryUpdateUtil;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

public interface GalerieRepository {

    Single<List<GalerieEntity>> fetchRemoteAndTransform();

    Single<ResponseBody> fetchImage(String url);

    Flowable<List<GalerieEntity>> allGalerieEntities();

    Completable updateGalery(GalerieEntity galerieEntity);

    Completable updateListGaleryUtil(List<GaleryUpdateUtil> list);

    Completable insertGalerie(List<GalerieEntity> galerieEntities);

}
