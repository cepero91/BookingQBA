package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.AmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.remote.pojo.RentAmenities;

import java.util.List;
import java.util.Set;

public class RentAmenitieName {

    private String rentId;
    private String amenityId;

    @Relation(parentColumn = "amenityId", entityColumn = "id", entity = AmenitiesEntity.class ,projection = {"name"})
    private Set<String>amenityName;

    public RentAmenitieName(String rentId, String amenityId) {
        this.rentId = rentId;
        this.amenityId = amenityId;
    }

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public String getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(String amenityId) {
        this.amenityId = amenityId;
    }

    public Set<String> getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(Set<String> amenityName) {
        this.amenityName = amenityName;
    }
}
