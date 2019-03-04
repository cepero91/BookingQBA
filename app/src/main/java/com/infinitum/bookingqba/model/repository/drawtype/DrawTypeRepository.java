package com.infinitum.bookingqba.model.repository.drawtype;

import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface DrawTypeRepository {

    Single<List<DrawTypeEntity>> fetchRemoteAndTransform();

    Completable insertDrawType(List<DrawTypeEntity> drawTypeEntities);

}
