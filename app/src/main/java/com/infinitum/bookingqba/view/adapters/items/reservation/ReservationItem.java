package com.infinitum.bookingqba.view.adapters.items.reservation;

import android.os.Parcel;
import android.os.Parcelable;

public class ReservationItem implements Parcelable {

    private String id;
    private String startDate;
    private String endDate;
    private String userName;
    private String capability;
    private String userAvatar;
    private String userId;
    private String rentId;
    private String aditionalNote;

    public ReservationItem() {
    }

    public ReservationItem(String id, String startDate, String endDate, String userName, String capability, String userAvatar, String userId, String rentId, String aditionalNote) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userName = userName;
        this.capability = capability;
        this.userAvatar = userAvatar;
        this.userId = userId;
        this.rentId = rentId;
        this.aditionalNote = aditionalNote;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
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

    public String getAditionalNote() {
        return aditionalNote;
    }

    public void setAditionalNote(String aditionalNote) {
        this.aditionalNote = aditionalNote;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeString(this.userName);
        dest.writeString(this.capability);
        dest.writeString(this.userAvatar);
        dest.writeString(this.userId);
        dest.writeString(this.rentId);
        dest.writeString(this.aditionalNote);
    }

    protected ReservationItem(Parcel in) {
        this.id = in.readString();
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.userName = in.readString();
        this.capability = in.readString();
        this.userAvatar = in.readString();
        this.userId = in.readString();
        this.rentId = in.readString();
        this.aditionalNote = in.readString();
    }

    public static final Creator<ReservationItem> CREATOR = new Creator<ReservationItem>() {
        @Override
        public ReservationItem createFromParcel(Parcel source) {
            return new ReservationItem(source);
        }

        @Override
        public ReservationItem[] newArray(int size) {
            return new ReservationItem[size];
        }
    };
}
