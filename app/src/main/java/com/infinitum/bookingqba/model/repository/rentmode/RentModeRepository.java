package com.infinitum.bookingqba.model.repository.rentmode;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface RentModeRepository {

    Completable insert(List<RentModeEntity> entities);

    Single<OperationResult> syncronizeRentMode(String token, String dateValue);
}
