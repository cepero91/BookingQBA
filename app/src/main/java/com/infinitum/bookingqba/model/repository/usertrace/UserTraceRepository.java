package com.infinitum.bookingqba.model.repository.usertrace;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;

import io.reactivex.Single;

public interface UserTraceRepository {

    Single<OperationResult> traceVisitCountToServer();

    Single<OperationResult> traceRentWishedToServer();

    Single<OperationResult> traceCommentToServer();

    Single<OperationResult> traceRatingToServer();

    Single<OperationResult> removeHistory();

}
