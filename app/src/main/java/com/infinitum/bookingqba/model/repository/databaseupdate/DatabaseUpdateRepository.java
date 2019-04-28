package com.infinitum.bookingqba.model.repository.databaseupdate;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface DatabaseUpdateRepository {

    Flowable<DatabaseUpdateEntity> fetchRemoteAndTransform();

    Flowable<Resource<DatabaseUpdateEntity>> lastDatabaseUpdateRemote();

    Completable insert(DatabaseUpdateEntity entitie);

    Flowable<Resource<DatabaseUpdateEntity>> getLastDatabaseUpdate();
}
