package com.infinitum.bookingqba.model.repository.dbcommonsop;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.remote.pojo.RemovedList;
import com.infinitum.bookingqba.model.remote.pojo.SyncData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface DBCommonOperationRepository {

    Flowable<DatabaseUpdateEntity> fetchRemoteAndTransform();

    Flowable<Resource<DatabaseUpdateEntity>> lastDatabaseUpdateRemote();

    Single<OperationResult> insertFromRemote();

    Completable insert(DatabaseUpdateEntity entity);

    Flowable<Resource<DatabaseUpdateEntity>> lastDatabaseUpdateLocal();

    Single<OperationResult> deleteAll(String dateValue);

    Single<SyncData> getSyncData(String dateValue);
}
