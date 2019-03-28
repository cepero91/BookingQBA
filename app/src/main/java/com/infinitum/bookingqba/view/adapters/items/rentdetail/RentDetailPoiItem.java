package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class RentDetailPoiItem implements ViewModel, Parcelable {

    private String mName;
    private byte[] iconByte;

    public RentDetailPoiItem(String mName) {
        this.mName = mName;
    }

    public RentDetailPoiItem(String mName, byte[] iconByte) {
        this.mName = mName;
        this.iconByte = iconByte;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final RentDetailPoiItem that = (RentDetailPoiItem) o;
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

    public byte[] getIconByte() {
        return iconByte;
    }

    public void setIconByte(byte[] iconByte) {
        this.iconByte = iconByte;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeByteArray(this.iconByte);
    }

    protected RentDetailPoiItem(Parcel in) {
        this.mName = in.readString();
        this.iconByte = in.createByteArray();
    }

    public static final Parcelable.Creator<RentDetailPoiItem> CREATOR = new Parcelable.Creator<RentDetailPoiItem>() {
        @Override
        public RentDetailPoiItem createFromParcel(Parcel source) {
            return new RentDetailPoiItem(source);
        }

        @Override
        public RentDetailPoiItem[] newArray(int size) {
            return new RentDetailPoiItem[size];
        }
    };
}
