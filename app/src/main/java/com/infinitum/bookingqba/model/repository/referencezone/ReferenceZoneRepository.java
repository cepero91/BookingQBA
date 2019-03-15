package com.infinitum.bookingqba.model.repository.referencezone;

import com.infinitum.bookingqba.model.Resource;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface ReferenceZoneRepository {

    Single<List<ReferenceZoneEntity>> fetchRemoteAndTransform();

    Completable insertReferencesMode(List<ReferenceZoneEntity> referenceZoneEntities);

    Flowable<Resource<List<ReferenceZoneEntity>>> allReferencesZone();

}
