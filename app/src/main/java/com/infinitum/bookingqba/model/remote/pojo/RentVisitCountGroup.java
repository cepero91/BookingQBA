package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RentVisitCountGroup {

    @SerializedName("imei")
    @Expose
    private String imei;

    @SerializedName("rentVisitCounts")
    @Expose
    private List<RentVisitCount> rentVisitCounts;

    public RentVisitCountGroup(String imei) {
        this.imei = imei;
        this.rentVisitCounts = new ArrayList<>();
    }

    public RentVisitCountGroup(String imei, List<RentVisitCount> rentVisitCounts) {
        this.imei = imei;
        this.rentVisitCounts = rentVisitCounts;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<RentVisitCount> getRentVisitCounts() {
        return rentVisitCounts;
    }

    public void setRentVisitCounts(List<RentVisitCount> rentVisitCounts) {
        this.rentVisitCounts = rentVisitCounts;
    }

    public void addRentVisitCount(RentVisitCount rentVisitCount){
        rentVisitCounts.add(rentVisitCount);
    }
}
