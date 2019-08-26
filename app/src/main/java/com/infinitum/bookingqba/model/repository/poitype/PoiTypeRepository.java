package com.infinitum.bookingqba.model.repository.poitype;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface PoiTypeRepository {

    Completable insert(List<PoiTypeEntity> entities);

    Single<OperationResult> syncronizePoiTypes(String token, String dateValue);

    Flowable<Resource<List<PoiTypeEntity>>> allLocalPoiType();

}
