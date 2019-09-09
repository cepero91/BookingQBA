package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RentWished {

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("items")
    @Expose
    private List<String> uuids;

    public RentWished(String userId, List<String> uuids) {
        this.userId = userId;
        this.uuids = uuids;
    }

    public RentWished(String userId) {
        this.userId = userId;
        uuids = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public void addRentId(String rentId){
        uuids.add(rentId);
    }
}
