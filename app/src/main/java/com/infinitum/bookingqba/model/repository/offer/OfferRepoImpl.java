package com.infinitum.bookingqba.model.repository.offer;

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
     * @return
     */
    private Single<List<Offer>> fetchOffers() {
        return retrofit.create(ApiInterface.class).getOffers();
    }

    /**
     * Transforma entidad JSON a entidad de Base de Datos
     * @param gsonList
     * @return
     */
    private List<OfferEntity> parseGsonToEntity(List<Offer> gsonList) {
        ArrayList<OfferEntity> listEntity = new ArrayList<>();
        OfferEntity entity;
        for (Offer item : gsonList) {
            entity = new OfferEntity(item.getId(), item.getNombre(), item.getDescripcion(), item.getPrecio(),item.getHospedaje());
            listEntity.add(entity);
        }
        return listEntity;
    }

    @Override
    public Single<List<OfferEntity>> fetchRemoteAndTransform() {
        return fetchOffers().flatMap((Function<List<Offer>, SingleSource<? extends List<OfferEntity>>>) offers -> {
            ArrayList<OfferEntity> listEntity = new ArrayList<>(parseGsonToEntity(offers));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertOffers(List<OfferEntity> offerEntityList) {
        return Completable.fromAction(() -> qbaDao.upsertOffers(offerEntityList))
                .subscribeOn(Schedulers.io());
    }
}
