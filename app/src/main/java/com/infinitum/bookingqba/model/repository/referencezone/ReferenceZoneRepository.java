package com.infinitum.bookingqba.model.repository.referencezone;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.remote.pojo.ReferenceZone;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface ReferenceZoneRepository {

    Completable insert(List<ReferenceZoneEntity> entities);

    Flowable<Resource<List<ReferenceZoneEntity>>> allLocalReferencesZone(String province);

    Single<Resource<List<ReferenceZone>>> allRemoteReferencesZone(String token);

    Single<OperationResult> syncronizeReferenceZone(String token, String dateValue);

}
