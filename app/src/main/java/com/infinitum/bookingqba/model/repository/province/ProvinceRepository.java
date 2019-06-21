package com.infinitum.bookingqba.model.repository.province;

import com.infinitum.bookingqba.model.OperationResult;
import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface ProvinceRepository {

    Completable insert(List<ProvinceEntity> entities);

    Flowable<Resource<List<ProvinceEntity>>> allProvinces();

    Single<OperationResult> syncronizeProvinces(String token,String dateValue);

}
