package com.infinitum.bookingqba.model.remote.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reservation {

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("startDate")
    @Expose
    private String startDate;

    @SerializedName("endDate")
    @Expose
    private String endDate;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("capability")
    @Expose
    private int capability;

    @SerializedName("status")
    @Expose
    private ReservationStatus reservationStatus;

    public Reservation(String username, String startDate, String endDate, String avatar, int capability, ReservationStatus reservationStatus) {
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
        this.avatar = avatar;
        this.capability = capability;
        this.reservationStatus = reservationStatus;
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

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public enum ReservationStatus{
        PENDING, ACCEPTED, REJECTED, CANCELED
    }

}
