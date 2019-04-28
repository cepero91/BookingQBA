package com.infinitum.bookingqba.view.adapters.items.rentlist;


import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentListItem extends BaseItem {

    private double mPrice;
    private String rentMode;
    private String mAddress;
    private String imagePath;
    private float rating;
    private int isWished;

    public RentListItem(String id, String mName, double mPrice, String rentMode, String mAddress, String imagePath, float rating, int isWished) {
        super(id, mName);
        this.mPrice = mPrice;
        this.rentMode = rentMode;
        this.mAddress = mAddress;
        this.imagePath = imagePath;
        this.rating = rating;
        this.isWished = isWished;
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

    public int getIsWished() {
        return isWished;
    }

    public void setIsWished(int isWished) {
        this.isWished = isWished;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }
}
