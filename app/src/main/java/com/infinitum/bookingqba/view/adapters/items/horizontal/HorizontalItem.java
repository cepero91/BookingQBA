package com.infinitum.bookingqba.view.adapters.items.horizontal;

public class HorizontalItem {

    private String uuid;
    private String rentName;
    private String portrait;
    private String rentMode;

    public HorizontalItem(String uuid, String rentName, String portrait, String rentMode) {
        this.uuid = uuid;
        this.rentName = rentName;
        this.portrait = portrait;
        this.rentMode = rentMode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRentName() {
        return rentName;
    }

    public void setRentName(String rentName) {
        this.rentName = rentName;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }
}
