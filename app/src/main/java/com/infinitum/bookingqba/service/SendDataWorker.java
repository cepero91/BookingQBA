package com.infinitum.bookingqba.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;
import com.infinitum.bookingqba.model.repository.usertrace.UserTraceRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import androidx.work.RxWorker;
import androidx.work.WorkerParameters;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SendDataWorker extends RxWorker {

    private UserTraceRepository repository;

    @Inject
    public SendDataWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams, UserTraceRepository repository) {
        super(appContext, workerParams);
        this.repository = repository;
    }

    public Single<OperationResult> visitWork() {
        return repository.traceVisitCountToServer();
    }

    public Single<OperationResult> wishedWork() {
        return repository.traceRentWishedToServer();
    }

    public Single<OperationResult> ratingWork() {
        return repository.traceRatingToServer();
    }

    public Single<OperationResult> commentWork() {
        return repository.traceCommentToServer();
    }

    public Single<List<OperationResult>> doAllWork() {
        return Single.zip(visitWork(), wishedWork(), ratingWork(), commentWork(), (visit, wished, rating, comment) -> {
            List<OperationResult> list = new ArrayList<>();
            list.add(visit);
            list.add(wished);
            list.add(rating);
            list.add(comment);
            return list;
        }).subscribeOn(Schedulers.io());
    }


    @Override
    public Single<Result> createWork() {
        return doAllWork()
                .subscribeOn(Schedulers.io())
                .flatMap(operationResults -> {
                    for(OperationResult operationResult: operationResults){
                        if(operationResult.result == OperationResult.Result.SUCCESS){
                            Timber.e("WORK SUCCESS");
                        }else{
                            Timber.e("WORK ERROR");
                        }
                    }
                    return Single.just(Result.success());
                })
                .onErrorReturn(throwable -> {
                    Timber.e("WORKER FAILED");
                    Timber.e(throwable);
                    return Result.success();
                });
    }

}
