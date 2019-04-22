package com.infinitum.bookingqba.model.local.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.ChangeRateEntity;
import com.infinitum.bookingqba.model.local.entity.DrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.MunicipalityEntity;
import com.infinitum.bookingqba.model.local.entity.OfferEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;
import com.infinitum.bookingqba.model.local.entity.ProvinceEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentDrawTypeEntity;
import com.infinitum.bookingqba.model.local.entity.RentEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;
import com.infinitum.bookingqba.model.local.entity.RentVisitCountEntity;


@Database(entities = {ProvinceEntity.class, MunicipalityEntity.class, AmenitiesEntity.class, DrawTypeEntity.class,
        PoiTypeEntity.class, PoiEntity.class, ChangeRateEntity.class, RentModeEntity.class,
        ReferenceZoneEntity.class, RentEntity.class, GalerieEntity.class, OfferEntity.class,
        RentAmenitiesEntity.class, RentPoiEntity.class, RentDrawTypeEntity.class, RentVisitCountEntity.class}, version = 1)
public abstract class BookingQBADatabase extends RoomDatabase {

    public abstract BookingQBADao dao();

}

