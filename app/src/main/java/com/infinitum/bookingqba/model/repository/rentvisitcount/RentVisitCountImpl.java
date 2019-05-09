package com.infinitum.bookingqba.model.repository.rentvisitcount;

import android.content.SharedPreferences;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentVisitCountEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.RentVisitCount;
import com.infinitum.bookingqba.model.remote.pojo.RentVisitCountGroup;
import com.infinitum.bookingqba.model.remote.pojo.RentWished;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;

import org.reactivestreams.Publisher;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

import static com.infinitum.bookingqba.util.Constants.IMEI;

public class RentVisitCountImpl implements RentVisitCountRepository {

    private BookingQBADao qbaDao;
    private Retrofit retrofit;
    private SharedPreferences sharedPreferences;

    @Inject
    public RentVisitCountImpl(BookingQBADao qbaDao, Retrofit retrofit, SharedPreferences sharedPreferences) {
        this.qbaDao = qbaDao;
        this.retrofit = retrofit;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Single<Resource<ResponseResult>> sendRentWishedToServer() {
        return qbaDao.getAllWishedRents()
                .subscribeOn(Schedulers.io())
                .map(this::tranformEntityToRentWished)
                .flatMap(this::sendWishedToServer)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Single<OperationResult> removeHistory() {
        return Completable.fromAction(()-> qbaDao.deleteAllVisitoCount())
                .subscribeOn(Schedulers.io())
                .toSingle(OperationResult::success)
                .onErrorReturn(OperationResult::error);
    }

    private Single<Resource<ResponseResult>> sendWishedToServer(Resource<RentWished> rentWishedResource){
        if(rentWishedResource.data!=null) {
            return retrofit.create(ApiInterface.class)
                    .updateRentWished(rentWishedResource.data)
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        }else {
            return Single.just(Resource.error("No hay nada que enviar"));
        }
    }

    @Override
    public Single<Resource<ResponseResult>> sendVisitCountToServer() {
        return qbaDao.getAllRentVisitCount()
                .subscribeOn(Schedulers.io())
                .map(this::transformToPojo)
                .flatMap(this::sendVisitToServer);
    }

    private Single<Resource<ResponseResult>> sendVisitToServer(Resource<RentVisitCountGroup> rentVisitCountGroupResource) {
        if(rentVisitCountGroupResource.data!=null) {
            return retrofit.create(ApiInterface.class)
                    .updateRentVisitCount(rentVisitCountGroupResource.data)
                    .subscribeOn(Schedulers.io())
                    .map(Resource::success)
                    .onErrorReturn(Resource::error);
        }else{
            return Single.just(Resource.error("No hay nada que enviar"));
        }
    }

    private Resource<RentVisitCountGroup> transformToPojo(List<RentVisitCountEntity> rentVisitCountEntities) {
        String uniqueDeviceID = sharedPreferences.getString(IMEI, "");
        if (uniqueDeviceID.equals("")) {
            return Resource.error("No exite IMEI en este dispositivo");
        } else {
            RentVisitCountGroup rentVisitCountGroup = new RentVisitCountGroup(uniqueDeviceID);
            if(rentVisitCountEntities.size() > 0) {
                for (RentVisitCountEntity entity : rentVisitCountEntities) {
                    rentVisitCountGroup.addRentVisitCount(new RentVisitCount(entity.getRentId(), entity.getVisitCount()));
                    Timber.e("VISIT COUNT to send ====> rent %s, count %s",entity.getRentId(),entity.getVisitCount());
                }
                return Resource.success(rentVisitCountGroup);
            }else{
                return Resource.error("Lista vacia");
            }
        }

    }

    private Resource<RentWished> tranformEntityToRentWished(List<RentEntity> entities) {
        String uniqueDeviceID = sharedPreferences.getString(IMEI, "");
        if (uniqueDeviceID.equals("")) {
            return Resource.error("No exite IMEI en este dispositivo");
        } else {
            RentWished rentWished = new RentWished(uniqueDeviceID);
            if(entities.size() > 0) {
                for (RentEntity entity : entities) {
                    rentWished.addRentId(entity.getId());
                    Timber.e("WHISHED to send ====> rent %s, name %s",entity.getId(),entity.getName());
                }
                return Resource.success(rentWished);
            }else{
                return Resource.error("Lista vacia");
            }
        }

    }


}
