package com.infinitum.bookingqba.model.repository.galerie;

import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface GalerieRepository {

    Single<List<GalerieEntity>> fetchRemoteAndTransform();

    Completable insertGalerie(List<GalerieEntity> galerieEntities);

}
