package com.infinitum.bookingqba.model.repository.galerie;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.pojo.GaleryUpdateUtil;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

public interface GalerieRepository {

    Single<ResponseBody> fetchImage(String url);

    Flowable<List<GalerieEntity>> allGaleries();

    Flowable<List<GalerieEntity>> allGaleriesVersionOne();

    Completable updateGalery(GalerieEntity galerieEntity);

    Completable updateListGaleryUtil(List<GaleryUpdateUtil> list);

    Completable insert(List<GalerieEntity> entities);

    Single<OperationResult> syncronizeGaleries(String token, String dateValue);

}
