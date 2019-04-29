package com.infinitum.bookingqba.model.repository.poitype;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface PoiTypeRepository {

    Single<List<PoiTypeEntity>> fetchRemoteAndTransform(String dateValue);

    Completable insert(List<PoiTypeEntity> entities);

    Single<OperationResult> syncronizePoiTypes(String dateValue);
}
