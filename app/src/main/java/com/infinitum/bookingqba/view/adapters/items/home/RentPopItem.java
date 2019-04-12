package com.infinitum.bookingqba.view.adapters.items.home;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentPopItem extends BaseItem {

    private String imagePath;
    private float rating;
    private double price;

    public RentPopItem(String id, String mName, String imagePath, float rating, double price) {
        super(id, mName);
        this.imagePath = imagePath;
        this.rating = rating;
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
}
