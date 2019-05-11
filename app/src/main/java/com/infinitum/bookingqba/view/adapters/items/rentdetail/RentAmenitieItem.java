package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class RentAmenitieItem implements ViewModel, Parcelable {

    private String mName;

    public RentAmenitieItem(String mName) {
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

        final RentAmenitieItem that = (RentAmenitieItem) o;
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

    protected RentAmenitieItem(Parcel in) {
        this.mName = in.readString();
    }

    public static final Parcelable.Creator<RentAmenitieItem> CREATOR = new Parcelable.Creator<RentAmenitieItem>() {
        @Override
        public RentAmenitieItem createFromParcel(Parcel source) {
            return new RentAmenitieItem(source);
        }

        @Override
        public RentAmenitieItem[] newArray(int size) {
            return new RentAmenitieItem[size];
        }
    };
}
