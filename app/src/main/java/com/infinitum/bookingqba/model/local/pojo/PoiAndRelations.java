package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.PoiTypeEntity;

import java.util.Set;

public class PoiAndRelations {

    @Embedded
    public PoiEntity poiEntity;

    @Relation(parentColumn = "poiType", entityColumn = "id", entity = PoiTypeEntity.class)
    public Set<PoiTypeEntity>poiTypeEntitySet;

    public PoiEntity getPoiEntity() {
        return poiEntity;
    }

    public void setPoiEntity(PoiEntity poiEntity) {
        this.poiEntity = poiEntity;
    }

    public Set<PoiTypeEntity> getPoiTypeEntitySet() {
        return poiTypeEntitySet;
    }

    public void setPoiTypeEntitySet(Set<PoiTypeEntity> poiTypeEntitySet) {
        this.poiTypeEntitySet = poiTypeEntitySet;
    }

    public PoiTypeEntity getPoiTypeEntitySetObject(){
        return (PoiTypeEntity) poiTypeEntitySet.toArray()[0];
    }
}
