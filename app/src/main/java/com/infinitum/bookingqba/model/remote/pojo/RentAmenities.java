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
    private List<String> amenities;

    public RentAmenities(String id, List<String> amenities) {
        this.id = id;
        this.amenities = amenities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
}
