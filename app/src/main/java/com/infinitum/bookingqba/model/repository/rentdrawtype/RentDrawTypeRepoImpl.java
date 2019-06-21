package com.infinitum.bookingqba.model.repository.rentdrawtype;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentDrawTypeEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.DrawType;
import com.infinitum.bookingqba.model.remote.pojo.RentDrawType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RentDrawTypeRepoImpl implements RentDrawTypeRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public RentDrawTypeRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     *
     * @return
     */
    private Single<List<RentDrawType>> fetchRentDrawTypes(String token, String dateValue) {
        return retrofit.create(ApiInterface.class).getRentsDrawType(token, dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     *
     * @param gsonList
     * @return
     */
    private List<RentDrawTypeEntity> parseGsonToEntity(List<RentDrawType> gsonList) {
        ArrayList<RentDrawTypeEntity> listEntity = new ArrayList<>();
        for (RentDrawType rentDrawType : gsonList) {
            for (String drawType : rentDrawType.getDrawTypes()) {
                listEntity.add(new RentDrawTypeEntity(drawType, rentDrawType.getId()));
            }
        }
        return listEntity;
    }


    public Single<List<RentDrawTypeEntity>> fetchRemoteAndTransform(String token, String dateValue) {
        return fetchRentDrawTypes(token, dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<RentDrawTypeEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertRentsDrawType(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeRentDrawType(String token, String dateValue) {
        return fetchRemoteAndTransform(token, dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
