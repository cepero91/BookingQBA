package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class RentDetailAmenitieItem implements ViewModel, Parcelable {

    private String mName;

    public RentDetailAmenitieItem(String mName) {
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

        final RentDetailAmenitieItem that = (RentDetailAmenitieItem) o;
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

    protected RentDetailAmenitieItem(Parcel in) {
        this.mName = in.readString();
    }

    public static final Parcelable.Creator<RentDetailAmenitieItem> CREATOR = new Parcelable.Creator<RentDetailAmenitieItem>() {
        @Override
        public RentDetailAmenitieItem createFromParcel(Parcel source) {
            return new RentDetailAmenitieItem(source);
        }

        @Override
        public RentDetailAmenitieItem[] newArray(int size) {
            return new RentDetailAmenitieItem[size];
        }
    };
}
