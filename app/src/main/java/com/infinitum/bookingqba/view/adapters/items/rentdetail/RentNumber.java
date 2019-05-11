package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

public class RentNumber implements Parcelable {

    private int maxBeds;
    private int maxBaths;
    private int capability;
    private int maxRooms;

    public RentNumber(int maxBeds, int maxBaths, int capability, int maxRooms) {
        this.maxBeds = maxBeds;
        this.maxBaths = maxBaths;
        this.capability = capability;
        this.maxRooms = maxRooms;
    }

    public int getMaxBeds() {
        return maxBeds;
    }

    public void setMaxBeds(int maxBeds) {
        this.maxBeds = maxBeds;
    }

    public int getMaxBaths() {
        return maxBaths;
    }

    public void setMaxBaths(int maxBaths) {
        this.maxBaths = maxBaths;
    }

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }

    public int getMaxRooms() {
        return maxRooms;
    }

    public void setMaxRooms(int maxRooms) {
        this.maxRooms = maxRooms;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxBeds);
        dest.writeInt(this.maxBaths);
        dest.writeInt(this.capability);
        dest.writeInt(this.maxRooms);
    }

    protected RentNumber(Parcel in) {
        this.maxBeds = in.readInt();
        this.maxBaths = in.readInt();
        this.capability = in.readInt();
        this.maxRooms = in.readInt();
    }

    public static final Parcelable.Creator<RentNumber> CREATOR = new Parcelable.Creator<RentNumber>() {
        @Override
        public RentNumber createFromParcel(Parcel source) {
            return new RentNumber(source);
        }

        @Override
        public RentNumber[] newArray(int size) {
            return new RentNumber[size];
        }
    };
}
