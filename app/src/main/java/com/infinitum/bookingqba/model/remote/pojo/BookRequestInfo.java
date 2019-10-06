package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookRequestInfo {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("rentId")
    @Expose
    private String rentId;

    @SerializedName("rentAvatar")
    @Expose
    private String rentAvatar;

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

    @SerializedName("state")
    @Expose
    private int state;

    public BookRequestInfo() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public String getRentAvatar() {
        return rentAvatar;
    }

    public void setRentAvatar(String rentAvatar) {
        this.rentAvatar = rentAvatar;
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

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }
}
