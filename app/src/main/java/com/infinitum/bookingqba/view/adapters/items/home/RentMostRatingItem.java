package com.infinitum.bookingqba.view.adapters.items.home;

import android.os.Parcel;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentMostRatingItem extends BaseItem {

    private float rating;
    private int ratingCount;

    public RentMostRatingItem(String id, String name, String imagePath, float price, String rentMode) {
        super(id, name, imagePath, price, rentMode);
    }

    public RentMostRatingItem(String id, String name, String imagePath, float price, String rentMode, float rating, int ratingCount) {
        super(id, name, imagePath, price, rentMode);
        this.rating = rating;
        this.ratingCount = ratingCount;
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
        dest.writeFloat(this.rating);
        dest.writeInt(this.ratingCount);
    }

    protected RentMostRatingItem(Parcel in) {
        super(in);
        this.rating = in.readFloat();
        this.ratingCount = in.readInt();
    }

    public static final Creator<RentMostRatingItem> CREATOR = new Creator<RentMostRatingItem>() {
        @Override
        public RentMostRatingItem createFromParcel(Parcel source) {
            return new RentMostRatingItem(source);
        }

        @Override
        public RentMostRatingItem[] newArray(int size) {
            return new RentMostRatingItem[size];
        }
    };
}
