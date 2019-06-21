package com.infinitum.bookingqba.model.repository.referencezone;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface ReferenceZoneRepository {

    Completable insert(List<ReferenceZoneEntity> entities);

    Flowable<Resource<List<ReferenceZoneEntity>>> allReferencesZone(String province);

    Single<OperationResult> syncronizeReferenceZone(String token, String dateValue);

}
