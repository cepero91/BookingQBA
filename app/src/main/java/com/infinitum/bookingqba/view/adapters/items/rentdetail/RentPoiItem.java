package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class RentPoiItem implements ViewModel, Parcelable {

    private String mName;

    public RentPoiItem(String mName) {
        this.mName = mName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final RentPoiItem that = (RentPoiItem) o;
        return !mName.equals(that.mName);
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
    }

    protected RentPoiItem(Parcel in) {
        this.mName = in.readString();
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
