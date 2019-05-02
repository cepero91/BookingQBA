package com.infinitum.bookingqba.model.repository.dbcommonsop;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.remote.pojo.RemovedList;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface DBCommonOperationRepository {

    Flowable<DatabaseUpdateEntity> fetchRemoteAndTransform();

    Flowable<Resource<DatabaseUpdateEntity>> lastDatabaseUpdateRemote();

    Completable insert(DatabaseUpdateEntity entitie);

    Flowable<Resource<DatabaseUpdateEntity>> lastDatabaseUpdateLocal();

    Completable deleteAll(List<RemovedList> removedLists);
}
