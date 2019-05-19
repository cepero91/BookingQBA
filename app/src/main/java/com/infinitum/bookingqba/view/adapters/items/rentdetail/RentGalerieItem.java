package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class RentGalerieItem implements ViewModel, Parcelable {

    private String id;
    private String image;

    public RentGalerieItem(String id, String image) {
        this.id = id;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        dest.writeString(this.id);
        dest.writeString(this.image);
    }

    protected RentGalerieItem(Parcel in) {
        this.id = in.readString();
        this.image = in.readString();
    }

    public static final Creator<RentGalerieItem> CREATOR = new Creator<RentGalerieItem>() {
        @Override
        public RentGalerieItem createFromParcel(Parcel source) {
            return new RentGalerieItem(source);
        }

        @Override
        public RentGalerieItem[] newArray(int size) {
            return new RentGalerieItem[size];
        }
    };
}
