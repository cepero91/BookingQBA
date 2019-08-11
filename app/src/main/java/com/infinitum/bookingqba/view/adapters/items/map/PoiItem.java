package com.infinitum.bookingqba.view.adapters.items.map;

import android.os.Parcel;
import android.os.Parcelable;

public class PoiItem implements Parcelable {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String category;

    public PoiItem() {
    }

    public PoiItem(String id, String name, double latitude, double longitude, String category) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.category);
    }

    protected PoiItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.category = in.readString();
    }

    public static final Parcelable.Creator<PoiItem> CREATOR = new Parcelable.Creator<PoiItem>() {
        @Override
        public PoiItem createFromParcel(Parcel source) {
            return new PoiItem(source);
        }

        @Override
        public PoiItem[] newArray(int size) {
            return new PoiItem[size];
        }
    };
}
