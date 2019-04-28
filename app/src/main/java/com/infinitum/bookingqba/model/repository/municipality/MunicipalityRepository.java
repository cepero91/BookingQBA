package com.infinitum.bookingqba.model.repository.municipality;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface MunicipalityRepository {

    Single<List<MunicipalityEntity>> fetchRemoteAndTransform(String dateValue);

    Completable insert(List<MunicipalityEntity> entities);

    Single<OperationResult> syncronizeMunicipalities(String dateValue);

}
