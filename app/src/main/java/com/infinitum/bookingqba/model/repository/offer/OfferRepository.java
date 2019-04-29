package com.infinitum.bookingqba.model.repository.offer;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.entity.OfferEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface OfferRepository {

    Single<List<OfferEntity>> fetchRemoteAndTransform(String dateValue);

    Completable insert(List<OfferEntity> entities);

    Single<OperationResult> syncronizeOffers(String dateValue);

}
