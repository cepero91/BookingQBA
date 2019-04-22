package com.infinitum.bookingqba.view.adapters.items.listwish;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class ListWishItem extends BaseItem{

    private String address;
    private float rating;
    private double price;
    private String rentMode;
    private String imagePath;

    public ListWishItem(String id, String mName) {
        super(id, mName);
    }

    public ListWishItem(String id, String mName, String address, float rating, double price, String rentMode, String imagePath) {
        super(id, mName);
        this.address = address;
        this.rating = rating;
        this.price = price;
        this.rentMode = rentMode;
        this.imagePath = imagePath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
