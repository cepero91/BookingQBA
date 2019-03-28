package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.PoiEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;

import java.util.Set;

public class RentPoiAndRelation {

    @Embedded
    RentPoiEntity rentPoiEntity;

    @Relation(parentColumn = "poiId", entityColumn = "id", entity = PoiEntity.class)
    Set<PoiAndRelations>poiAndRelations;

    public RentPoiAndRelation(RentPoiEntity rentPoiEntity) {
        this.rentPoiEntity = rentPoiEntity;
    }

    public RentPoiEntity getRentPoiEntity() {
        return rentPoiEntity;
    }

    public void setRentPoiEntity(RentPoiEntity rentPoiEntity) {
        this.rentPoiEntity = rentPoiEntity;
    }

    public Set<PoiAndRelations> getPoiAndRelations() {
        return poiAndRelations;
    }

    public void setPoiAndRelations(Set<PoiAndRelations> poiAndRelations) {
        this.poiAndRelations = poiAndRelations;
    }

    public PoiAndRelations getPoiAndRelationsObject(){
        return (PoiAndRelations) poiAndRelations.toArray()[0];
    }
}
