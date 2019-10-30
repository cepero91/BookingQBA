package com.infinitum.bookingqba.view.adapters.items.listwish;

import android.os.Parcel;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

import java.util.Locale;

public class ListWishItem extends BaseItem {

    private String address;
    private float rating;
    private int ratingCount;

    public ListWishItem(String id, String name, String imagePath, float price, String rentMode) {
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

    public String humanRatingCount(){
        return String.format("(%s)",ratingCount);
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

    protected ListWishItem(Parcel in) {
        super(in);
        this.address = in.readString();
        this.rating = in.readFloat();
        this.ratingCount = in.readInt();
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
