package com.infinitum.bookingqba.model.repository.databaseupdate;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.DatabaseUpdateEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.DatabaseUpdate;
import com.infinitum.bookingqba.util.DateUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DatabaseUpdateRepoImpl implements DatabaseUpdateRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public DatabaseUpdateRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
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
        return fetchDatabaseUpdate().map(this::transformToEntity).subscribeOn(Schedulers.io());
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
    public Flowable<Resource<DatabaseUpdateEntity>> getLastDatabaseUpdate() {
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

    private DatabaseUpdateEntity transformToEntity(DatabaseUpdate databaseUpdate) {
        return new DatabaseUpdateEntity(DateUtils.dateStringToDate(databaseUpdate.getLastDatabaseUpdate()), databaseUpdate.getTotalRents());
    }




}
