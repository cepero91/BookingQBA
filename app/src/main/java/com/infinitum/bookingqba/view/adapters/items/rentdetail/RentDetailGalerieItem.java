package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class RentDetailGalerieItem implements ViewModel, Parcelable {

    private String image;

    public RentDetailGalerieItem(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
    }

    protected RentDetailGalerieItem(Parcel in) {
        this.image = in.readString();
    }

    public static final Parcelable.Creator<RentDetailGalerieItem> CREATOR = new Parcelable.Creator<RentDetailGalerieItem>() {
        @Override
        public RentDetailGalerieItem createFromParcel(Parcel source) {
            return new RentDetailGalerieItem(source);
        }

        @Override
        public RentDetailGalerieItem[] newArray(int size) {
            return new RentDetailGalerieItem[size];
        }
    };
}
