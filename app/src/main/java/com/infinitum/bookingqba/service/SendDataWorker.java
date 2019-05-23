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

import androidx.work.ListenableWorker;
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

    @Override
    public Single<Result> createWork() {
        return doAllWork()
                .subscribeOn(Schedulers.io())
                .flatMap(this::checkWorkResult)
                .onErrorReturn(throwable -> {
                    Timber.e("ALL WORK FAILED == reason ==> %s", throwable.getMessage());
                    return Result.success();
                });
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

    private Single<Result> checkWorkResult(List<OperationResult> operationResults) {
        for (int i = 0; i < operationResults.size(); i++) {
            if (operationResults.get(i).result == OperationResult.Result.SUCCESS) {
                Timber.e("WORK %s SUCCESS", getNameOfWorkByIndex(i));
            } else {
                Timber.e("WORK %s ERROR, reason ==> %s", getNameOfWorkByIndex(i), operationResults.get(i).message);
            }
        }
        return Single.just(Result.success());
    }


    public String getNameOfWorkByIndex(int index) {
        String name = "";
        switch (index) {
            case 0:
                name = "VISIT COUNT";
                break;
            case 1:
                name = "WISHED RENT";
                break;
            case 2:
                name = "RATING VOTES";
                break;
            case 3:
                name = "RENT COMMENTS";
                break;
        }
        return name;
    }

}
