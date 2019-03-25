package com.infinitum.bookingqba.view.adapters.rendered.home;

import com.infinitum.bookingqba.view.adapters.rendered.baseitem.BaseItem;

public class RentPopItem extends BaseItem {

    private byte[] mImage;
    private float rating;
    private double price;

    public RentPopItem(String id, String mName, byte[] mImage, float rating) {
        super(id, mName);
        this.mImage = mImage;
        this.rating = rating;
    }

    public RentPopItem(String id, String mName, byte[] mImage, float rating, double price) {
        super(id, mName);
        this.mImage = mImage;
        this.rating = rating;
        this.price = price;
    }

    public byte[] getmImage() {
        return mImage;
    }

    public void setmImage(byte[] mImage) {
        this.mImage = mImage;
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
