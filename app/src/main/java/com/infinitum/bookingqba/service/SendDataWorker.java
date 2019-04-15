package com.infinitum.bookingqba.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.repository.rentvisitcount.RentVisitCountRepository;

import javax.inject.Inject;

import androidx.work.ListenableWorker;
import androidx.work.RxWorker;
import androidx.work.WorkerParameters;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

public class SendDataWorker extends RxWorker {

    private RentVisitCountRepository repository;

    @Inject
    public SendDataWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams, RentVisitCountRepository repository) {
        super(appContext, workerParams);
        this.repository = repository;
    }

    @Override
    public Single<Result> createWork() {
        return repository.sendVisitCountToServer()
                .flatMap(responseResultResource -> {
                    Timber.e(responseResultResource.message);
                    if(responseResultResource.data!=null){
                        Timber.e("response msg %s, code %s",responseResultResource.data.getMsg(),responseResultResource.data.getCode());
                    }
                    return Single.just(Result.success());
                })
                .onErrorReturn(throwable -> {
                    Timber.e(throwable);
                    return Result.success();
                });
    }

}
