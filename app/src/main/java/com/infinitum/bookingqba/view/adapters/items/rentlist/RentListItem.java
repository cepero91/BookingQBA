package com.infinitum.bookingqba.view.adapters.items.rentlist;


import android.os.Parcel;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentListItem extends BaseItem {

    private String address;
    private float rating;
    private int ratingCount;

    public RentListItem(String id, String name, String imagePath, float price, String rentMode) {
        super(id, name, imagePath, price, rentMode);
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

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
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
        dest.writeInt(this.ratingCount);
    }

    protected RentListItem(Parcel in) {
        super(in);
        this.address = in.readString();
        this.rating = in.readFloat();
        this.ratingCount = in.readInt();
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
