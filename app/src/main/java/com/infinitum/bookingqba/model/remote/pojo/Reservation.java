package com.infinitum.bookingqba.model.remote.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reservation {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("startDate")
    @Expose
    private String startDate;

    @SerializedName("endDate")
    @Expose
    private String endDate;

    @SerializedName("created")
    @Expose
    private String created;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("capability")
    @Expose
    private int capability;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("rentId")
    @Expose
    private String rentId;

    @SerializedName("aditional")
    @Expose
    private String aditional;

    @SerializedName("rentName")
    @Expose
    private String rentName;

    @SerializedName("hostCount")
    @Expose
    private int hostCount;

    @SerializedName("state")
    @Expose
    private int state;

    public Reservation(String id, String username, String startDate, String endDate, String created, String avatar, int capability, String userId, String rentId, String aditional, String rentName, int hostCount) {
        this.id = id;
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
        this.created = created;
        this.avatar = avatar;
        this.capability = capability;
        this.userId = userId;
        this.rentId = rentId;
        this.aditional = aditional;
        this.rentName = rentName;
        this.hostCount = hostCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public String getAditional() {
        return aditional;
    }

    public void setAditional(String aditional) {
        this.aditional = aditional;
    }

    public String getRentName() {
        return rentName;
    }

    public void setRentName(String rentName) {
        this.rentName = rentName;
    }

    public int getHostCount() {
        return hostCount;
    }

    public void setHostCount(int hostCount) {
        this.hostCount = hostCount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
