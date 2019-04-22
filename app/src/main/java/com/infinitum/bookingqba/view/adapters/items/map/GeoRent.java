package com.infinitum.bookingqba.view.adapters.items.map;

import android.os.Parcel;
import android.os.Parcelable;

import org.oscim.core.GeoPoint;

public class GeoRent implements Parcelable {

    private String id;
    private String name;
    private float rating;
    private double price;
    private GeoPoint geoPoint;
    private String imagePath;
    private String rentMode;

    public GeoRent(String id, String name, float rating, double price, GeoPoint geoPoint) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.geoPoint = geoPoint;
    }

    public GeoRent(String id, String name, float rating, double price, GeoPoint geoPoint, String imagePath, String rentMode) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.geoPoint = geoPoint;
        this.imagePath = imagePath;
        this.rentMode = rentMode;
    }

    public GeoRent(String id) {
        this.id = id;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeFloat(this.rating);
        dest.writeDouble(this.price);
        dest.writeSerializable(this.geoPoint);
        dest.writeString(this.imagePath);
        dest.writeString(this.rentMode);
    }

    protected GeoRent(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.rating = in.readFloat();
        this.price = in.readDouble();
        this.geoPoint = (GeoPoint) in.readSerializable();
        this.imagePath = in.readString();
        this.rentMode = in.readString();
    }

    public static final Parcelable.Creator<GeoRent> CREATOR = new Parcelable.Creator<GeoRent>() {
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
