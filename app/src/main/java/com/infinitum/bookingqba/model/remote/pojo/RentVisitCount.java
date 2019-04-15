package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RentVisitCount {

    @SerializedName("rentId")
    @Expose
    private String rentId;

    @SerializedName("count")
    @Expose
    private int count;

    public RentVisitCount(String rentId, int count) {
        this.rentId = rentId;
        this.count = count;
    }

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
