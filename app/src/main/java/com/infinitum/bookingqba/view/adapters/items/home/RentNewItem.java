package com.infinitum.bookingqba.view.adapters.items.home;

import android.os.Parcel;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentNewItem extends BaseItem {
    private float rating;

    public RentNewItem(String id, String name, String imagePath, int wished) {
        super(id, name, imagePath, wished);
    }

    public RentNewItem(String id, String name, String imagePath, int wished, float rating) {
        super(id, name, imagePath, wished);
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(this.rating);
    }

    protected RentNewItem(Parcel in) {
        super(in);
        this.rating = in.readFloat();
    }

    public static final Creator<RentNewItem> CREATOR = new Creator<RentNewItem>() {
        @Override
        public RentNewItem createFromParcel(Parcel source) {
            return new RentNewItem(source);
        }

        @Override
        public RentNewItem[] newArray(int size) {
            return new RentNewItem[size];
        }
    };
}
