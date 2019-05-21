package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RentWished {

    @SerializedName("imei")
    @Expose
    private String imei;

    @SerializedName("items")
    @Expose
    private List<String> uuids;

    public RentWished(String imei) {
        this.imei = imei;
        this.uuids = new ArrayList<>();
    }

    public RentWished(String imei, List<String> uuids) {
        this.imei = imei;
        this.uuids = uuids;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public void addRentId(String id){
        uuids.add(id);
    }
}
