package com.infinitum.bookingqba.model.repository.dbcommonsop;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteStatement;

import com.infinitum.bookingqba.model.OperationResult;
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
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import timber.log.Timber;

public class DBCommonOperationRepoImpl implements DBCommonOperationRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;
    public static final String SEPARATOR = ",";

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
        Timber.e("Sync fetchDatabaseUpdate");
        return retrofit.create(ApiInterface.class).getDatabaseUpdate();
    }

    private Flowable<List<RemovedList>> fetchRemoved(String dateValue) {
        return retrofit.create(ApiInterface.class).getRemoveds(dateValue);
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
    public Completable insert(DatabaseUpdateEntity entity) {
        return Completable.fromAction(()->qbaDao.addDatabaseUpdate(entity));
    }

    @Override
    public Single<OperationResult> insertFromRemote() {
        return fetchRemoteAndTransform()
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    @Override
    public Flowable<Resource<DatabaseUpdateEntity>> lastDatabaseUpdateLocal() {
        return qbaDao.getLastDatabaseUpdate()
                .subscribeOn(Schedulers.io())
                .map((Function<List<DatabaseUpdateEntity>, Resource<DatabaseUpdateEntity>>) entities -> {
                    if (entities.size() > 0) {
                        return Resource.success(entities.get(0));
                    } else {
                        return Resource.empty(null);
                    }
                })
                .onErrorReturn(Resource::error);
    }

    @Override
    public Single<OperationResult> deleteAll(String dateValue) {
        return fetchRemoved(dateValue)
                .subscribeOn(Schedulers.io())
                .map(this::getCompletableToDelete)
                .flatMapCompletable(simpleSQLiteQueries -> Observable.fromIterable(simpleSQLiteQueries)
                        .flatMapCompletable(simpleSQLiteQuery -> Completable.fromAction(() -> qbaDao.deleteAll(simpleSQLiteQuery))))
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    private List<SimpleSQLiteQuery> getCompletableToDelete(List<RemovedList> removedLists) {
        List<SimpleSQLiteQuery> simpleSQLiteQueries = new ArrayList<>();
        for (RemovedList removedList : removedLists) {
            if(removedList.getEntity().equals("Rent")){
              simpleSQLiteQueries.addAll(sqLiteQueriesForRentManyToMany(removedList.getItems()));
            }
            String stringQuery = String.format("DELETE FROM %s WHERE id IN (%s)", removedList.getEntity()
                    , convertListToCommaSeparated(removedList.getItems()));
            simpleSQLiteQueries.add(new SimpleSQLiteQuery(stringQuery));
        }
        return simpleSQLiteQueries;
    }

    private List<SimpleSQLiteQuery> sqLiteQueriesForRentManyToMany(List<String> ids){
        List<SimpleSQLiteQuery> simpleSQLiteQueries = new ArrayList<>();
        String stringQueryRentAmenities = String.format("DELETE FROM %s WHERE rentId IN (%s)", "RentAmenities"
                , convertListToCommaSeparated(ids));
        simpleSQLiteQueries.add(new SimpleSQLiteQuery(stringQueryRentAmenities));
        String stringQueryRentPoi = String.format("DELETE FROM %s WHERE rentId IN (%s)", "RentPoi"
                , convertListToCommaSeparated(ids));
        simpleSQLiteQueries.add(new SimpleSQLiteQuery(stringQueryRentPoi));
        String stringQueryRentDrawType = String.format("DELETE FROM %s WHERE rentId IN (%s)", "RentDrawType"
                , convertListToCommaSeparated(ids));
        simpleSQLiteQueries.add(new SimpleSQLiteQuery(stringQueryRentDrawType));
        return simpleSQLiteQueries;
    }

    private String convertListToCommaSeparated(List<String> ids) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            stringBuilder.append("'").append(ids.get(i)).append("'");
            if (i < ids.size() - 1)
                stringBuilder.append(SEPARATOR);
        }
        return stringBuilder.toString();
    }


}
