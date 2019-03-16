package com.infinitum.bookingqba.model.repository.rent;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.tconverter.DateTypeConverter;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Poi;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_NEW;
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
    private Single<List<Rent>> fetchRents() {
        return retrofit.create(ApiInterface.class).getRents();
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
            entity.setName(item.getNombre());
            entity.setAddress(item.getDireccion());
            entity.setSlogan(item.getEslogan());
            entity.setDescription(item.getDescripcion());
            entity.setEmail(item.getCorreo());
            entity.setPhoneNumber(item.getTelmovil());
            entity.setPhoneHomeNumber(item.getTelcasa());
            entity.setLatitude(Double.parseDouble(item.getLatitud()));
            entity.setLongitude(Double.parseDouble(item.getLongitud()));
            entity.setRating(item.getRating());
            entity.setMaxRooms(item.getCantHabitaciones());
            entity.setMaxBeds(item.getCantCamas());
            entity.setMaxBath(item.getCantBannos());
            entity.setCapability(item.getCapacidad());
            entity.setPrice(item.getPrecio());
            entity.setRentMode(item.getModalidadRenta());
            entity.setRules(item.getNormas());
            entity.setMunicipality(item.getMunicipio());
            entity.setReferenceZone(item.getZonaReferencia());
            entity.setCreated(DateUtils.dateStringToDate(item.getFechaCreado()));
            entity.setUpdated(DateUtils.dateStringToDate(item.getFechaModificado()));
            listEntity.add(entity);
        }
        return listEntity;
    }


    @Override
    public Single<List<RentEntity>> fetchRemoteAndTransform() {
        return fetchRents().flatMap((Function<List<Rent>, SingleSource<? extends List<RentEntity>>>) rents -> {
            ArrayList<RentEntity> listEntity = new ArrayList<>(parseGsonToEntity(rents));
            return Single.just(listEntity);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insertRents(List<RentEntity> rentEntityList) {
        return Completable.fromAction(() -> qbaDao.upsertRents(rentEntityList))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<Resource<List<RentAndGalery>>> allRent() {
        return qbaDao.getAllRents()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Flowable<Resource<List<RentAndGalery>>> allRentWithFirstImage(char orderType) {
        if (orderType == ORDER_TYPE_POPULAR) {
            return qbaDao.getAllPopRent()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        } else {
            return qbaDao.getAllNewRent()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        }
    }

    @Override
    public Flowable<Resource<List<RentAndGalery>>> fivePopRent() {
        return qbaDao.getFivePopRents()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Flowable<Resource<List<RentAndGalery>>> fiveNewRent() {
        return qbaDao.getFiveNewRents()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }
}
