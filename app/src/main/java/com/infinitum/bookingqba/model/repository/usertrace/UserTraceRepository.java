package com.infinitum.bookingqba.model.repository.usertrace;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.remote.pojo.ResponseResult;

import io.reactivex.Single;

public interface UserTraceRepository {

    Single<OperationResult> traceVisitCountToServer(String token);

    Single<OperationResult> traceRentWishedToServer(String token);

    Single<OperationResult> traceCommentToServer(String token);

    Single<OperationResult> traceRatingToServer(String token);

    Single<OperationResult> removeHistory();

}
