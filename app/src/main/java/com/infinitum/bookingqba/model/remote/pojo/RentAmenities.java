package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentAmenities {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("amenities")
    @Expose
    private List<Amenities> amenities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Amenities> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenities> amenities) {
        this.amenities = amenities;
    }
}
