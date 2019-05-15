package com.infinitum.bookingqba.model.repository.rent;

import android.arch.paging.DataSource;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import timber.log.Timber;

import static com.infinitum.bookingqba.model.repository.dbcommonsop.DBCommonOperationRepoImpl.SEPARATOR;
import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_POPULAR;

public class RentRepoImpl implements RentRepository {

    public static final String RENTMODE = "RentMode";
    public static final String MUNICIPALITY = "Municipality";
    public static final String AMENITIES = "Amenities";
    public static final String PRICE = "Price";
    public static final String RATING = "Rating";

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

    @Override
    public Flowable<Resource<List<RentModeEntity>>> allRentMode() {
        return qbaDao.getAllRentsMode().subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Flowable<Resource<List<RentAndGalery>>> filterRents(Map<String, List<String>> filterParams) {
        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(generalQuery(filterParams));
        if (true) {
            Timber.e("Hi");
        }
        return qbaDao.filterRents(simpleSQLiteQuery)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    private String generalQuery(Map<String, List<String>> filterParams) {
        String generalQuery = "";
        if (filterParams.containsKey(RENTMODE) && filterParams.size() == 1) {
            generalQuery = filterWithRentMode(filterParams.get(RENTMODE), "Rent","q1");
        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.size() == 1) {
//            generalQuery = filterWithMunicipality(filterParams.get(MUNICIPALITY), "Rent");
        } else if (filterParams.containsKey(AMENITIES) && filterParams.size() == 1) {
            generalQuery = filterWithAmenities(filterParams.get(AMENITIES), "Rent");
        } else if (filterParams.containsKey(PRICE) && filterParams.size() == 1) {
            float min = Float.parseFloat(filterParams.get(PRICE).get(0));
            float max = Float.parseFloat(filterParams.get(PRICE).get(1));
            generalQuery = filterWithPriceRange(min, max, "Rent");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.size() == 2) {
            String subquery = String.format("(%s)",filterWithRentMode(filterParams.get(RENTMODE),"Rent","q1"));
            generalQuery = filterWithMunicipality(filterParams.get(MUNICIPALITY),subquery,"q2");
        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(AMENITIES)
                && filterParams.size() == 2) {

        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(PRICE)
                && filterParams.size() == 2) {

        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.containsKey(AMENITIES)
                && filterParams.size() == 2) {

        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.containsKey(PRICE)
                && filterParams.size() == 2) {

        } else if (filterParams.containsKey(AMENITIES) && filterParams.containsKey(PRICE)
                && filterParams.size() == 2) {

        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.containsKey(AMENITIES) && filterParams.size() == 3) {

        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.containsKey(AMENITIES) && filterParams.containsKey(PRICE)
                && filterParams.size() == 4) {

        } else if (filterParams.containsKey(MUNICIPALITY) && filterParams.containsKey(AMENITIES)
                && filterParams.containsKey(PRICE) && filterParams.size() == 3) {

        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(AMENITIES)
                && filterParams.containsKey(PRICE) && filterParams.size() == 3) {

        } else if (filterParams.containsKey(RENTMODE) && filterParams.containsKey(MUNICIPALITY)
                && filterParams.containsKey(PRICE) && filterParams.size() == 3) {

        }
        return generalQuery;
    }

    private String filterWithRentMode(List<String> rentModes, String subquery, String alias) {
        String stringQuery = "SELECT * FROM "+subquery+" AS "+alias+" JOIN RentMode On "+alias+".rentMode = RentMode.id " +
                "WHERE RentMode.id IN ("+convertListToCommaSeparated(rentModes)+") GROUP BY "+alias+".id";
        return stringQuery;
    }

    private String filterWithAmenities(List<String> amenities, String subquery) {
        String stringQuery = String.format("SELECT * FROM %s JOIN RentAmenities on Rent.id = RentAmenities.rentId " +
                "JOIN Amenities on Amenities.id = RentAmenities.amenityId WHERE RentAmenities.amenityId " +
                "IN (%s) GROUP BY rentId having count (*) = '" + amenities.size() + "'", subquery, convertListToCommaSeparated(amenities));
        return stringQuery;
    }

    private String filterWithMunicipality(List<String> municipality, String subquery, String alias) {
        String stringQuery = "SELECT * FROM "+subquery+" AS "+alias+" JOIN Municipality on Municipality.id = "+alias+".municipality " +
                "WHERE "+alias+".municipality IN("+convertListToCommaSeparated(municipality)+") " +
                "GROUP BY "+alias+".id";
        return stringQuery;
    }

    private String filterWithPriceRange(float min, float max, String subquery) {
        String stringQuery = String.format("SELECT * FROM %s WHERE Rent.price " +
                "BETWEEN %2.0f AND %2.0f", subquery, min, max);
        return stringQuery;
    }

    private String filterWithRating(float rating, String subquery) {
        String stringQuery = String.format("SELECT * FROM %s WHERE Rent.rating = %.1f", subquery, rating);
        return stringQuery;
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
