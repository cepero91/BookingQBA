package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class RentDetailGalerieItem implements ViewModel, Parcelable {

    private byte[] image;

    public RentDetailGalerieItem(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.image);
    }

    protected RentDetailGalerieItem(Parcel in) {
        this.image = in.createByteArray();
    }

    public static final Creator<RentDetailGalerieItem> CREATOR = new Creator<RentDetailGalerieItem>() {
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
