package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentPoi {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("poi")
    @Expose
    private List<String> pois;

    public RentPoi(String id, List<String> pois) {
        this.id = id;
        this.pois = pois;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPois() {
        return pois;
    }

    public void setPois(List<String> pois) {
        this.pois = pois;
    }
}
