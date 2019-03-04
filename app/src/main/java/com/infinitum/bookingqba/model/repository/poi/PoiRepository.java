package com.infinitum.bookingqba.model.repository.poi;

import com.infinitum.bookingqba.model.local.entity.PoiEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface PoiRepository {

    Single<List<PoiEntity>> fetchRemoteAndTransform();

    Completable insertPois(List<PoiEntity> poiEntityList);
}
