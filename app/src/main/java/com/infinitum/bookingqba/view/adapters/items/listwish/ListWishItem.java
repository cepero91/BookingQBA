package com.infinitum.bookingqba.view.adapters.items.listwish;

import android.os.Parcel;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class ListWishItem extends BaseItem{

    private String address;
    private float rating;
    private double price;
    private String rentMode;

    public ListWishItem(String id, String name, String imagePath) {
        super(id, name, imagePath);
    }

    public ListWishItem(String id, String name, String imagePath, String address, float rating, double price, String rentMode) {
        super(id, name, imagePath);
        this.address = address;
        this.rating = rating;
        this.price = price;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.address);
        dest.writeFloat(this.rating);
        dest.writeDouble(this.price);
        dest.writeString(this.rentMode);
    }

    protected ListWishItem(Parcel in) {
        super(in);
        this.address = in.readString();
        this.rating = in.readFloat();
        this.price = in.readDouble();
        this.rentMode = in.readString();
    }

    public static final Creator<ListWishItem> CREATOR = new Creator<ListWishItem>() {
        @Override
        public ListWishItem createFromParcel(Parcel source) {
            return new ListWishItem(source);
        }

        @Override
        public ListWishItem[] newArray(int size) {
            return new ListWishItem[size];
        }
    };
}
