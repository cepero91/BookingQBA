package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

public class PoiCategory implements Parcelable {

    private int id;
    private String name;

    public PoiCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        PoiCategory poiCategory = (PoiCategory) obj;
        return poiCategory.getName().equals(this.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    protected PoiCategory(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<PoiCategory> CREATOR = new Parcelable.Creator<PoiCategory>() {
        @Override
        public PoiCategory createFromParcel(Parcel source) {
            return new PoiCategory(source);
        }

        @Override
        public PoiCategory[] newArray(int size) {
            return new PoiCategory[size];
        }
    };
}
