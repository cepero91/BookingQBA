package com.infinitum.bookingqba.model.repository.rentvisitcount;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.RentVisitCountEntity;
import com.infinitum.bookingqba.model.remote.pojo.RentVisitCount;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;

public interface RentVisitCountRepository {

    Single<Resource<ResponseResult>> sendVisitCountToServer();

    Single<Resource<ResponseResult>> sendRentWishedToServer();

    Single<OperationResult> removeHistory();

}
