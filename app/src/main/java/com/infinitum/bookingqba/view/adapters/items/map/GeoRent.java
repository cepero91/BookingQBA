package com.infinitum.bookingqba.view.adapters.items.map;

import android.os.Parcel;
import android.os.Parcelable;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

import org.oscim.core.GeoPoint;

public class GeoRent extends BaseItem {

    private float rating;
    private double price;
    private GeoPoint geoPoint;
    private String rentMode;

    public GeoRent(String id, String name, String imagePath, int wished) {
        super(id, name, imagePath, wished);
    }

    public GeoRent(Parcel in, float rating, double price, GeoPoint geoPoint, String rentMode) {
        super(in);
        this.rating = rating;
        this.price = price;
        this.geoPoint = geoPoint;
        this.rentMode = rentMode;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(this.rating);
        dest.writeDouble(this.price);
        dest.writeSerializable(this.geoPoint);
        dest.writeString(this.rentMode);
    }

    protected GeoRent(Parcel in) {
        super(in);
        this.rating = in.readFloat();
        this.price = in.readDouble();
        this.geoPoint = (GeoPoint) in.readSerializable();
        this.rentMode = in.readString();
    }

    public static final Creator<GeoRent> CREATOR = new Creator<GeoRent>() {
        @Override
        public GeoRent createFromParcel(Parcel source) {
            return new GeoRent(source);
        }

        @Override
        public GeoRent[] newArray(int size) {
            return new GeoRent[size];
        }
    };
}
