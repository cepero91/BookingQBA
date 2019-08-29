package com.infinitum.bookingqba.view.adapters.items.reservation;

public class ReservationItem {

    private String startDate;
    private String endDate;
    private String userName;
    private String capability;
    private String userAvatar;

    public ReservationItem() {
    }

    public ReservationItem(String startDate, String endDate, String userName, String capability, String userAvatar) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.userName = userName;
        this.capability = capability;
        this.userAvatar = userAvatar;
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
}
