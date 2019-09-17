package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class RentPoiItem implements Parcelable {

    private String name;

    public RentPoiItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    protected RentPoiItem(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<RentPoiItem> CREATOR = new Creator<RentPoiItem>() {
        @Override
        public RentPoiItem createFromParcel(Parcel source) {
            return new RentPoiItem(source);
        }

        @Override
        public RentPoiItem[] newArray(int size) {
            return new RentPoiItem[size];
        }
    };
}
