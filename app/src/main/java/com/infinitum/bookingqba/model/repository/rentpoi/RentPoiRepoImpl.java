package com.infinitum.bookingqba.model.repository.rentpoi;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.model.remote.pojo.RentPoi;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RentPoiRepoImpl implements RentPoiRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public RentPoiRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     * @return
     */
    private Single<List<RentPoi>> fetchRentPoi(String dateValue) {
        return retrofit.create(ApiInterface.class).getRentsPoi(dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<RentPoiEntity> parseGsonToEntity(List<RentPoi> gsonList) {
        List<RentPoiEntity> rentPoiEntityList = new ArrayList<>();
        for(RentPoi rentPoi: gsonList){
            for(Poi poi: rentPoi.getPois()){
                rentPoiEntityList.add(new RentPoiEntity(poi.getId(),rentPoi.getId()));
            }
        }
        return rentPoiEntityList;
    }

    @Override
    public Single<List<RentPoiEntity>> fetchRemoteAndTransform(String dateValue) {
        return fetchRentPoi(dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<RentPoiEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertRentsPoi(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeRentPoi(String dateValue) {
        return fetchRemoteAndTransform(dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
