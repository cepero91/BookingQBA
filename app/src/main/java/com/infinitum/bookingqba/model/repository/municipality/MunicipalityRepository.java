package com.infinitum.bookingqba.model.repository.municipality;

import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.remote.pojo.Municipality;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface MunicipalityRepository {

    Single<List<MunicipalityEntity>> fetchRemoteAndTransform();

    Completable insertMunicipalities(List<MunicipalityEntity> municipalityEntityList);

}
