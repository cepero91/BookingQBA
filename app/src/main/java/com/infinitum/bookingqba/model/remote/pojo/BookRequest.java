package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookRequest {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("userId")
    @Expose
    private String userid;

    @SerializedName("rentId")
    @Expose
    private String rentId;

    @SerializedName("startDate")
    @Expose
    private String startDate;

    @SerializedName("endDate")
    @Expose
    private String endDate;

    @SerializedName("aditional")
    @Expose
    private String aditional;

    @SerializedName("capability")
    @Expose
    private String capability;

    public BookRequest() {
    }

    public BookRequest(String id, String userid, String rentId, String startDate, String endDate, String aditional, String capability) {
        this.id = id;
        this.userid = userid;
        this.rentId = rentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.aditional = aditional;
        this.capability = capability;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAditional() {
        return aditional;
    }

    public void setAditional(String aditional) {
        this.aditional = aditional;
    }

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }
}
