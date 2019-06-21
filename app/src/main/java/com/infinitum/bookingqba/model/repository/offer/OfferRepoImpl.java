package com.infinitum.bookingqba.model.repository.offer;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.OfferEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Offer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class OfferRepoImpl implements OfferRepository {

    private Retrofit retrofit;
    private BookingQBADao qbaDao;

    @Inject
    public OfferRepoImpl(Retrofit retrofit, BookingQBADao qbaDao) {
        this.retrofit = retrofit;
        this.qbaDao = qbaDao;
    }

    /**
     * Prepara la peticion del API
     *
     * @return
     */
    private Single<List<Offer>> fetchOffers(String token, String dateValue) {
        return retrofit.create(ApiInterface.class).getOffers(token, dateValue);
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     *
     * @param gsonList
     * @return
     */
    private List<OfferEntity> parseGsonToEntity(List<Offer> gsonList) {
        ArrayList<OfferEntity> listEntity = new ArrayList<>();
        OfferEntity entity;
        for (Offer item : gsonList) {
            entity = new OfferEntity(item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getRent());
            listEntity.add(entity);
        }
        return listEntity;
    }

    public Single<List<OfferEntity>> fetchRemoteAndTransform(String token, String dateValue) {
        return fetchOffers(token, dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<OfferEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertOffers(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<OperationResult> syncronizeOffers(String token, String dateValue) {
        return fetchRemoteAndTransform(token, dateValue)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::insert)
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }
}
