package com.infinitum.bookingqba.view.adapters.items.horizontal;

public class HorizontalItem {

    private String uuid;
    private String rentName;
    private String rating;

    public HorizontalItem(String uuid, String rentName, String rating) {
        this.uuid = uuid;
        this.rentName = rentName;
        this.rating = rating;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
