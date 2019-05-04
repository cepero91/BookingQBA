package com.infinitum.bookingqba.view.adapters.items.home;

import android.os.Parcel;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentPopItem extends BaseItem {
    private float rating;
    private double price;

    public RentPopItem(String id, String name, String imagePath, int wished) {
        super(id, name, imagePath, wished);
    }

    public RentPopItem(String id, String name, String imagePath, int wished, float rating, double price) {
        super(id, name, imagePath, wished);
        this.rating = rating;
        this.price = price;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(this.rating);
        dest.writeDouble(this.price);
    }

    protected RentPopItem(Parcel in) {
        super(in);
        this.rating = in.readFloat();
        this.price = in.readDouble();
    }

    public static final Creator<RentPopItem> CREATOR = new Creator<RentPopItem>() {
        @Override
        public RentPopItem createFromParcel(Parcel source) {
            return new RentPopItem(source);
        }

        @Override
        public RentPopItem[] newArray(int size) {
            return new RentPopItem[size];
        }
    };
}
