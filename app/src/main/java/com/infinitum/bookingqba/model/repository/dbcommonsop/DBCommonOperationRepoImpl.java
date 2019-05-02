package com.infinitum.bookingqba.model.repository.dbcommonsop;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteStatement;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.DatabaseUpdate;
import com.infinitum.bookingqba.model.remote.pojo.RemovedList;
import com.infinitum.bookingqba.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DBCommonOperationRepoImpl implements DBCommonOperationRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public DBCommonOperationRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     *
     * @return
     */
    private Flowable<DatabaseUpdate> fetchDatabaseUpdate() {
        return retrofit.create(ApiInterface.class).getDatabaseUpdate();
    }

    @Override
    public Flowable<DatabaseUpdateEntity> fetchRemoteAndTransform() {
        return fetchDatabaseUpdate().map(this::parseJsonToEntity).subscribeOn(Schedulers.io());
    }

    private DatabaseUpdateEntity parseJsonToEntity(DatabaseUpdate databaseUpdate) {
        return new DatabaseUpdateEntity(DateUtils.dateStringToDate(databaseUpdate.getLastDatabaseUpdate()), databaseUpdate.getTotalRents());
    }

    @Override
    public Flowable<Resource<DatabaseUpdateEntity>> lastDatabaseUpdateRemote() {
        return fetchRemoteAndTransform().map(Resource::success).onErrorReturn(Resource::error);
    }

    @Override
    public Completable insert(DatabaseUpdateEntity entitie) {
        return Completable.fromAction(() -> qbaDao.upsertDatabaseUpdate(entitie)).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<Resource<DatabaseUpdateEntity>> lastDatabaseUpdateLocal() {
        return qbaDao.getLastDatabaseUpdate()
                .subscribeOn(Schedulers.io())
                .map((Function<List<DatabaseUpdateEntity>, Resource<DatabaseUpdateEntity>>) entities -> {
                    if(entities.size()>0){
                        return Resource.success(entities.get(0));
                    }else{
                        return Resource.empty(null);
                    }
                })
                .onErrorReturn(Resource::error);
    }

    @Override
    public Completable deleteAll(List<RemovedList> removedLists) {
//        List<Comparable> deleteCompletable = new ArrayList<>();
//        SupportSQLiteQuery sqLiteQuery;
//        for(RemovedList removedList: removedLists){
//            sqLiteQuery = new SimpleSQLiteQuery("DELETE FROM "+removedList.getEntity())
//        }
        return null;
    }






}
