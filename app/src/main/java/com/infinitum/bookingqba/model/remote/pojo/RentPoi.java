package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentPoi {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("pois")
    @Expose
    private List<Poi> pois;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Poi> getPois() {
        return pois;
    }

    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }
}
