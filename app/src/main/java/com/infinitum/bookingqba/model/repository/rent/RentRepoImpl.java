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
import com.infinitum.bookingqba.model.remote.pojo.AddressResponse;
import com.infinitum.bookingqba.model.remote.pojo.Offer;
import com.infinitum.bookingqba.model.remote.pojo.Rent;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;
import com.infinitum.bookingqba.model.remote.pojo.RentEdit;
import com.infinitum.bookingqba.model.remote.pojo.RentEsential;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;
import com.infinitum.bookingqba.model.remote.pojo.RentPoi;
import com.infinitum.bookingqba.model.remote.pojo.RentPoiAdd;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.util.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

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
    public DataSource.Factory<Integer, RentAndGalery> allRentByZone(String province, String zone) {
        return qbaDao.getAllRentByZone(province, zone);
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
    public DataSource.Factory<Integer, RentAndGalery> filterRents(Map<String, List<String>> filterParams, String province) {
        String query = FilterRepositoryUtil.generalQuery(filterParams, province);
        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(query);
        return qbaDao.filterRents(simpleSQLiteQuery);
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
        if(params.containsKey("galery")){
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

        return Single.concat(collection).onErrorReturn(throwable -> new ResponseResult(500,throwable.getMessage())).toList();
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


}
