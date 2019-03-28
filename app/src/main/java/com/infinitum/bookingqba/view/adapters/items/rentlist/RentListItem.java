package com.infinitum.bookingqba.view.adapters.items.rentlist;


import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentListItem extends BaseItem {

    private double mPrice;
    private String mAddress;
    private byte[] imageByte;
    private float rating;

    public RentListItem(String id, String mName, double mPrice, String mAddress, byte[] imageByte, float rating) {
        super(id, mName);
        this.mPrice = mPrice;
        this.mAddress = mAddress;
        this.imageByte = imageByte;
        this.rating = rating;
    }

    public double getmPrice() {
        return mPrice;
    }

    public void setmPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public byte[] getImageByte() {
        return imageByte;
    }

    public void setImageByte(byte[] imageByte) {
        this.imageByte = imageByte;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
