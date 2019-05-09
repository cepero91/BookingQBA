package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RentWished {

    @SerializedName("imei")
    @Expose
    private String rentId;

    @SerializedName("items")
    @Expose
    private List<String> uuids;

    public RentWished(String rentId) {
        this.rentId = rentId;
        this.uuids = new ArrayList<>();
    }

    public RentWished(String rentId, List<String> uuids) {
        this.rentId = rentId;
        this.uuids = uuids;
    }

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
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
