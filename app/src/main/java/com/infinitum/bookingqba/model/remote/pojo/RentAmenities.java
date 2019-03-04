package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentAmenities {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("facilidades")
    @Expose
    private List<Amenities> facilidades;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Amenities> getFacilidades() {
        return facilidades;
    }

    public void setFacilidades(List<Amenities> facilidades) {
        this.facilidades = facilidades;
    }
}
