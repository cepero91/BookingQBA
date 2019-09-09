package com.infinitum.bookingqba.model.repository.rent;

import android.arch.paging.DataSource;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.graphics.PointF;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RatingEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.pojo.RentAndDependencies;
import com.infinitum.bookingqba.model.local.pojo.RentAndGalery;
import com.infinitum.bookingqba.model.local.pojo.RentDetail;
import com.infinitum.bookingqba.model.local.pojo.RentMostComment;
import com.infinitum.bookingqba.model.local.pojo.RentMostRating;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.AddressResponse;
import com.infinitum.bookingqba.model.remote.pojo.BookRequest;
import com.infinitum.bookingqba.model.remote.pojo.DisabledDays;
import com.infinitum.bookingqba.model.remote.pojo.DrawChange;
import com.infinitum.bookingqba.model.remote.pojo.Offer;
import com.infinitum.bookingqba.model.remote.pojo.RatingVote;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentEdit;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.remote.pojo.RentPoiAdd;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.util.DateUtils;

import org.mapsforge.core.model.LatLong;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_MOST_COMMENTED;
import static com.infinitum.bookingqba.util.Constants.ORDER_TYPE_MOST_RATING;

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
    private Single<List<Rent>> fetchRents(String token, String dateValue) {
        return retrofit.create(ApiInterface.class).getRents(token, dateValue);
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
            entity.setRating(item.getRatingEmbeded().getAverage());
            entity.setRatingCount(item.getRatingEmbeded().getCount());
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
            entity.setUserId(item.getUserid());
            if (item.getCheckin() != null)
                entity.setCheckin(item.getCheckin());
            if (item.getCheckout() != null)
                entity.setCheckout(item.getCheckout());
            listEntity.add(entity);
        }
        return listEntity;
    }

    private Single<List<RentEntity>> fetchRemoteAndTransform(String token, String dateValue) {
        return fetchRents(token, dateValue)
                .map(this::parseGsonToEntity)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable insert(List<RentEntity> entities) {
        return Completable.fromAction(() -> qbaDao.upsertRents(entities))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<Resource<List<RentAndDependencies>>> allRent() {
        return qbaDao.getAllRents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Flowable<Resource<List<RentAndDependencies>>> allRentByOrderType(char orderType, String province) {
        if (orderType == ORDER_TYPE_MOST_RATING) {
            return qbaDao.getAllMostRatingRent(province).map(Resource::success).onErrorReturn(Resource::error).subscribeOn(Schedulers.io());
        } else if (orderType == ORDER_TYPE_MOST_COMMENTED) {
            String query = "SELECT Rent.id,Rent.name,Rent.address,Rent.price, Rent.rating, Rent.ratingCount, Rent.latitude, Rent.longitude, Rent.rentMode, Rent.isWished, AVG(Comment.emotion) as emotionAvg, COUNT(Comment.id) as totalComment FROM Rent " +
                    "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) " +
                    "LEFT JOIN Municipality ON Rent.municipality = Municipality.id " +
                    "LEFT JOIN Comment ON Comment.rent = Rent.id " +
                    "LEFT JOIN Province ON Municipality.province = Province.id " +
                    "WHERE Province.id = '" + province + "'" +
                    "GROUP BY Rent.id " +
                    "ORDER BY emotionAvg DESC, totalComment DESC";
            SupportSQLiteQuery supportSQLiteQuery = new SimpleSQLiteQuery(query);
            return qbaDao.getAllMostCommentedRent(supportSQLiteQuery).map(Resource::success).onErrorReturn(Resource::error).subscribeOn(Schedulers.io());
        }
        return qbaDao.getAllMostRatingRent(province).map(Resource::success).onErrorReturn(Resource::error).subscribeOn(Schedulers.io());
    }

    @Override
    public DataSource.Factory<Integer, RentAndGalery> allRentByZone(String province, String zone) {
        return qbaDao.getAllRentByZone(province, zone);
    }

    @Override
    public Flowable<Resource<List<RentMostRating>>> fiveMostRatingRents(String province) {
        return qbaDao.getFiveMostRatingRents(province)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Flowable<Resource<List<RentMostComment>>> fiveMostCommentRents(String province) {
        String query = "SELECT Rent.id,Rent.name,Rent.price, Rent.rentMode, Rent.rating, Rent.ratingCount, AVG(Comment.emotion) as emotionAvg, COUNT(Comment.id) as totalComment FROM Rent " +
                "LEFT JOIN Galerie ON Galerie.id = (SELECT Galerie.id FROM Galerie WHERE rent = Rent.id LIMIT 1) " +
                "LEFT JOIN Municipality ON Rent.municipality = Municipality.id " +
                "LEFT JOIN Comment ON Comment.rent = Rent.id " +
                "LEFT JOIN Province ON Municipality.province = Province.id " +
                "WHERE Province.id = '" + province + "'" +
                "GROUP BY Rent.id " +
                "ORDER BY emotionAvg DESC, totalComment DESC LIMIT 5";
        SupportSQLiteQuery supportSQLiteQuery = new SimpleSQLiteQuery(query);
        return qbaDao.getFiveMostCommentRent(supportSQLiteQuery)
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
        SupportSQLiteQuery query = new SimpleSQLiteQuery("INSERT OR REPLACE INTO Rating VALUES ('" + entity.getId() + "','" + entity.getRating() + "', '" + entity.getComment() + "', '" + entity.getRent() + "','" + entity.getVersion() + "')");
        return Completable.fromAction(() -> qbaDao.addOrUpdateRating(query))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<RatingEntity> getLastRentVote(String rent) {
        return qbaDao.getLastRentVote(rent).subscribeOn(Schedulers.io());
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
    public Single<OperationResult> syncronizeRents(String token, String dateValue) {
        return fetchRemoteAndTransform(token, dateValue)
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
    public Single<Resource<List<RentMode>>> allRemoteRentMode(String token) {
        return retrofit.create(ApiInterface.class)
                .getAllRentsMode(token)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Flowable<Resource<List<RentAndDependencies>>> filterRents(Map<String, List<String>> filterParams, String province) {
//        String query = FilterRepositoryUtil.buildFilterQuery(filterParams, province);
//        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(query);
        return Flowable.just(FilterRepositoryUtil.buildFilterQuery(filterParams, province)).flatMap(result->{
            SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(result);
            return qbaDao.filterRents(simpleSQLiteQuery).subscribeOn(Schedulers.io());
        }).map(Resource::success).subscribeOn(Schedulers.io()).onErrorReturn(Resource::error);
    }

    @Override
    public Single<List<ResponseResult>> addRent(String token, Map<String, Object> params) {
        Collection<Single<ResponseResult>> collection = new ArrayList<>();
        Rent rentParams = (Rent) params.get("rent");
        Single<ResponseResult> newRent = retrofit.create(ApiInterface.class)
                .addRent(token, rentParams).subscribeOn(Schedulers.io());
        Single<ResponseResult> newAmenitiesRent = retrofit.create(ApiInterface.class)
                .addRentAmenities(token, (RentAmenities) params.get("amenities")).subscribeOn(Schedulers.io());

        collection.add(newRent);
        collection.add(newAmenitiesRent);
        if (params.containsKey("galery")) {
            MultipartBody imagesRequestBody = getMultipartImagesBody(rentParams.getId(), (ArrayList<String>) params.get("galery"));
            Single<ResponseResult> newRentGalery = retrofit.create(ApiInterface.class)
                    .addRentGalery(token, imagesRequestBody).subscribeOn(Schedulers.io());
            collection.add(newRentGalery);
        }
        if (params.containsKey("poi")) {
            Single<ResponseResult> newRentPoi = retrofit.create(ApiInterface.class)
                    .addRentPoi(token, (RentPoiAdd) params.get("poi")).subscribeOn(Schedulers.io());
            collection.add(newRentPoi);
        }
        if (params.containsKey("offer")) {
            Single<ResponseResult> newRentOffer = retrofit.create(ApiInterface.class)
                    .addRentOffer(token, (List<Offer>) params.get("offer")).subscribeOn(Schedulers.io());
            collection.add(newRentOffer);
        }

        return Single.concat(collection).onErrorReturn(throwable -> new ResponseResult(500, throwable.getMessage())).toList();
    }

    @Override
    public Flowable<Resource<List<RentEsential>>> allRentByUserId(String token, String userid) {
        return retrofit.create(ApiInterface.class)
                .allRentByUserId(token, userid)
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Single<Response<AddressResponse>> addressByLocation(double lat, double lon) {
        String url = "http://nominatim.openstreetmap.org/"
                + "reverse?"
                + "format=json"
                + "&accept-language=" + "es"
                + "&lat=" + lat
                + "&lon=" + lon;
        return retrofit.create(ApiInterface.class)
                .addressByLocationOSM(url);
    }

    @Override
    public Single<Resource<List<RentEdit>>> rentById(String token, String uuid) {
        return retrofit.create(ApiInterface.class)
                .rentById(token, uuid)
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    private MultipartBody getMultipartImagesBody(String id, ArrayList<String> imagesPath) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("rent", id);
        for (int i = 0; i < imagesPath.size(); i++) {
            File file = new File(imagesPath.get(i));
            builder.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }
        return builder.build();
    }

    @Override
    public Flowable<Resource<List<RentAndDependencies>>> rentNearLocation(LatLong latLong, double range) {
        PointF center = new PointF((float) latLong.latitude, (float) latLong.longitude);
        final double mult = 1; // mult = 1.1; is more reliable
        PointF p1 = calculateDerivedPosition(center, mult * range, 0);
        PointF p2 = calculateDerivedPosition(center, mult * range, 90);
        PointF p3 = calculateDerivedPosition(center, mult * range, 180);
        PointF p4 = calculateDerivedPosition(center, mult * range, 270);

        String strWhere = "SELECT * FROM Rent WHERE "
                + "Rent.latitude > " + String.valueOf(p3.x) + " AND "
                + "Rent.latitude < " + String.valueOf(p1.x) + " AND "
                + "Rent.longitude < " + String.valueOf(p2.y) + " AND "
                + "Rent.longitude > " + String.valueOf(p4.y);
        SupportSQLiteQuery simpleQuery = new SimpleSQLiteQuery(strWhere);
        return qbaDao.getRentNearLatLon(simpleQuery)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Flowable<Double> maxRentPrice() {
        return qbaDao.getAllRentOrderPrice()
                .map(rentEntityList -> {
                    if (rentEntityList.size() > 0) {
                        return rentEntityList.get(0).getPrice();
                    } else {
                        return 0d;
                    }
                })
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> 0d);
    }

    @Override
    public Flowable<Integer> maxRentCapability() {
        return qbaDao.getMaxRentCapability()
                .map(rentEntityList -> {
                    if (rentEntityList.size() > 0) {
                        return rentEntityList.get(0).getCapability();
                    } else {
                        return 0;
                    }
                })
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    Timber.e(throwable);
                    return 0;
                });
    }

    @Override
    public Single<Resource<ResponseResult>> sendBookRequest(String token, BookRequest bookRequest) {
        return retrofit.create(ApiInterface.class)
                .sendBookRequest(token, bookRequest)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Single<Resource<ResponseResult>> sendRatingVote(String token, RatingVote ratingVote) {
        return retrofit.create(ApiInterface.class)
                .sendRating(token, ratingVote)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Single<Resource<List<DrawChange>>> drawChangeByFinalPrice(String token, double finalPrice) {
        return retrofit.create(ApiInterface.class)
                .drawChangeByFinalPrice(token, finalPrice)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Single<Resource<DisabledDays>> disabledDaysByRent(String token, String uuid) {
        return retrofit.create(ApiInterface.class)
                .disabledDaysByRent(token, uuid)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    /**
     * Calculates the end-point from a given source at a given range (meters)
     * and bearing (degrees). This methods uses simple geometry equations to
     * calculate the end-point.
     *
     * @param point   Point of origin
     * @param range   Range in meters
     * @param bearing Bearing in degrees
     * @return End-point from the source given the desired range and bearing.
     */
    public static PointF calculateDerivedPosition(PointF point,
                                                  double range, double bearing) {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        return new PointF((float) lat, (float) lon);

    }


}
