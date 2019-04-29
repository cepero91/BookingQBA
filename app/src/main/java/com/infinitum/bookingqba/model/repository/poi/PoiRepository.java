package com.infinitum.bookingqba.model.repository.poi;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface PoiRepository {

    Single<List<PoiEntity>> fetchRemoteAndTransform(String dateValue);

    Completable insert(List<PoiEntity> entities);

    Single<OperationResult> syncronizePois(String dateValue);
}
