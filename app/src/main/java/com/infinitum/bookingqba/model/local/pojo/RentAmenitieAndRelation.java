package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;

import java.util.List;
import java.util.Set;

public class RentAmenitieAndRelation {

    @Embedded
    RentAmenitiesEntity rentAmenitiesEntity;

    @Relation(parentColumn = "amenityId", entityColumn = "id", entity = AmenitiesEntity.class ,projection = {"name"})
    private Set<String>amenityName;

    public RentAmenitieAndRelation(RentAmenitiesEntity rentAmenitiesEntity) {
        this.rentAmenitiesEntity = rentAmenitiesEntity;
    }

    public RentAmenitiesEntity getRentAmenitiesEntity() {
        return rentAmenitiesEntity;
    }

    public void setRentAmenitiesEntity(RentAmenitiesEntity rentAmenitiesEntity) {
        this.rentAmenitiesEntity = rentAmenitiesEntity;
    }

    public Set<String> getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(Set<String> amenityName) {
        this.amenityName = amenityName;
    }

    public String getAmenitieNameobject(){
        return (String) amenityName.toArray()[0];
    }
}
