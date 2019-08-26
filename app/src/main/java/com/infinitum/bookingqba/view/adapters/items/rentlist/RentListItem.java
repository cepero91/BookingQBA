package com.infinitum.bookingqba.view.adapters.items.rentlist;


import android.os.Parcel;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentListItem extends BaseItem {

    private double price;
    private String rentMode;
    private String address;
    private float rating;
    private int wished;

    public RentListItem(String id, String name, String imagePath) {
        super(id, name, imagePath);
    }

    public RentListItem(String id, String name, String imagePath, double price, String rentMode, String address, float rating, int wished) {
        super(id, name, imagePath);
        this.price = price;
        this.rentMode = rentMode;
        this.address = address;
        this.rating = rating;
        this.wished = wished;
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

    public int getWished() {
        return wished;
    }

    public void setWished(int wished) {
        this.wished = wished;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(this.price);
        dest.writeString(this.rentMode);
        dest.writeString(this.address);
        dest.writeFloat(this.rating);
        dest.writeInt(this.wished);
    }

    protected RentListItem(Parcel in) {
        super(in);
        this.price = in.readDouble();
        this.rentMode = in.readString();
        this.address = in.readString();
        this.rating = in.readFloat();
        this.wished = in.readInt();
    }

    public static final Creator<RentListItem> CREATOR = new Creator<RentListItem>() {
        @Override
        public RentListItem createFromParcel(Parcel source) {
            return new RentListItem(source);
        }

        @Override
        public RentListItem[] newArray(int size) {
            return new RentListItem[size];
        }
    };
}
