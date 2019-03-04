package com.infinitum.bookingqba.model.repository.province;

import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface ProvinceRepository {

    Single<List<ProvinceEntity>> fetchRemoteAndTransform();

    Completable insertProvinces(List<ProvinceEntity> provinceEntityList);

}
