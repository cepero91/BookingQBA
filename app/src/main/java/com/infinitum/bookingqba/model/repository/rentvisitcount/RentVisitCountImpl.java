package com.infinitum.bookingqba.model.repository.rentvisitcount;

import android.content.SharedPreferences;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.database.BookingQBADao;
import com.infinitum.bookingqba.model.local.entity.RentVisitCountEntity;
import com.infinitum.bookingqba.model.remote.ApiInterface;
import com.infinitum.bookingqba.model.remote.pojo.RentVisitCount;
import com.infinitum.bookingqba.model.remote.pojo.RentVisitCountGroup;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;

import org.reactivestreams.Publisher;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    public Single<Resource<List<RentVisitCountEntity>>> allRentVisitCount() {
        return qbaDao.getAllRentVisitCount().subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
    }

    @Override
    public Single<Resource<ResponseResult>> sendVisitCountToServer() {
        return qbaDao.getAllRentVisitCount()
                .subscribeOn(Schedulers.io())
                .map(this::transformToPojo)
                .flatMap(this::sendUpdateRequest);
    }

    private Single<Resource<ResponseResult>> sendUpdateRequest(Resource<RentVisitCountGroup> rentVisitCountGroupResource) {
        return retrofit.create(ApiInterface.class)
                .updateRentVisitCount(rentVisitCountGroupResource.data)
                .subscribeOn(Schedulers.io())
                .map(Resource::success)
                .onErrorReturn(Resource::error);
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
                }
                return Resource.success(rentVisitCountGroup);
            }else{
                return Resource.error("Lista vacia");
            }
        }

    }


}
