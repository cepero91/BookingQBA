package com.infinitum.bookingqba.model.repository.rent;

import android.arch.paging.DataSource;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_POPULAR;

public class RentRepoImpl implements RentRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public RentRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     *
     * @return
     */
    private Single<List<Rent>> fetchRents(String dateValue) {
        return retrofit.create(ApiInterface.class).getRents(dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     *
     * @param gsonList
     * @return
     */
    private List<RentEntity> parseGsonToEntity(List<Rent> gsonList) {
        ArrayList<RentEntity> listEntity = new ArrayList<>();
        RentEntity entity;
        for (Rent item : gsonList) {
            entity = new RentEntity(item.getId());
            entity.setName(item.getName());
            entity.setAddress(item.getAddress());
            entity.setDescription(item.getDescription());
            entity.setEmail(item.getEmail());
            entity.setPhoneNumber(item.getPhoneNumber());
            entity.setPhoneHomeNumber(item.getPhoneHomeNumber());
            entity.setLatitude(Double.parseDouble(item.getLatitude()));
            entity.setLongitude(Double.parseDouble(item.getLongitude()));
            entity.setRating(item.getRating().getAverage());
            entity.setRatingCount(item.getRating().getCount());
            entity.setMaxRooms(item.getMaxRooms());
            entity.setMaxBeds(item.getMaxBeds());
            entity.setMaxBath(item.getMaxBath());
            entity.setCapability(item.getCapability());
            entity.setPrice(item.getPrice());
            entity.setRentMode(item.getRentMode());
            entity.setRules(item.getRules());
            entity.setMunicipality(item.getMunicipality());
            entity.setReferenceZone(item.getReferenceZone());
            entity.setCreated(DateUtils.dateStringToDate(item.getCreated()));
            listEntity.add(entity);
        }
        return listEntity;
    }


    @Override
    public Single<List<RentEntity>> fetchRemoteAndTransform(String dateValue) {
        return fetchRents(dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<RentEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertRents(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<Resource<List<RentAndGalery>>> allRent() {
        return qbaDao.getAllRents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public DataSource.Factory<Integer, RentAndGalery> allRentByOrderType(char orderType, String province) {
        if (orderType == ORDER_TYPE_POPULAR) {
            return qbaDao.getAllPopRent(province);
        } else {
            return qbaDao.getAllNewRent(province);
        }
    }

    @Override
    public Flowable<Resource<List<RentAndGalery>>> fivePopRentByProvince(String province) {
        return qbaDao.getFivePopRents(province)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Flowable<Resource<List<RentAndGalery>>> fiveNewRentByProvince(String province) {
        return qbaDao.getFiveNewRents(province)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Flowable<Resource<RentDetail>> getRentDetailById(String uuid) {
        return qbaDao.getRentDetailById(uuid)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable addOrUpdateRentVisitCount(String id, String rent) {
        SupportSQLiteQuery query = new SimpleSQLiteQuery("INSERT OR REPLACE INTO RentVisitCount VALUES ('" + rent + "', COALESCE(" +
                "(SELECT visitCount FROM RentVisitCount WHERE rentId = '" + rent + "'),0)+1)");
        return Completable.fromAction(() -> qbaDao.addOrUpdateRentVisit(query)).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable addOrUpdateRating(RatingEntity entity) {
        SupportSQLiteQuery query = new SimpleSQLiteQuery("INSERT OR REPLACE INTO Rating VALUES ('" + entity.getId() + "','" + entity.getRating() + "', '" + entity.getComment() + "', '" + entity.getRent() + "')");
        return Completable.fromAction(() -> qbaDao.addOrUpdateRentVisit(query)).subscribeOn(Schedulers.io());
//        return Completable.complete();
    }

    @Override
    public Flowable<Resource<List<RentAndGalery>>> allWishedRent(String province) {
        return qbaDao.getAllWishedRents(province)
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Completable updateIsWishedRent(String uuid, int isWished) {
        SupportSQLiteQuery query = new SimpleSQLiteQuery("UPDATE Rent SET isWished ='" + isWished + "' WHERE id='" + uuid + "'");
        return Completable.fromAction(() -> qbaDao.updateRentIsWished(query)).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable update(RentEntity entity) {
        return Completable.fromAction(() -> qbaDao.updateRent(entity)).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeRents(String dateValue) {
        return fetchRemoteAndTransform(dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
