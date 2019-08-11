package com.infinitum.bookingqba.view.adapters.items.map;

import android.os.Parcel;
import android.os.Parcelable;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class GeoRent extends BaseItem {

    private float rating;
    private int ratingCount;
    private double price;
    private GeoPoint geoPoint;
    private String rentMode;
    private List<PoiItem> poiItems;

    public GeoRent(String id, String name, String imagePath, int wished) {
        super(id, name, imagePath, wished);
    }

    public GeoRent(String id, String name, String imagePath, int wished, float rating, int ratingCount, double price, GeoPoint geoPoint, String rentMode, List<PoiItem> poiItems) {
        super(id, name, imagePath, wished);
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.price = price;
        this.geoPoint = geoPoint;
        this.rentMode = rentMode;
        this.poiItems = poiItems;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
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

    public List<PoiItem> getPoiItems() {
        return poiItems;
    }

    public void setPoiItems(List<PoiItem> poiItems) {
        this.poiItems = poiItems;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(this.rating);
        dest.writeInt(this.ratingCount);
        dest.writeDouble(this.price);
        dest.writeSerializable(this.geoPoint);
        dest.writeString(this.rentMode);
        dest.writeList(this.poiItems);
    }

    protected GeoRent(Parcel in) {
        super(in);
        this.rating = in.readFloat();
        this.ratingCount = in.readInt();
        this.price = in.readDouble();
        this.geoPoint = (GeoPoint) in.readSerializable();
        this.rentMode = in.readString();
        this.poiItems = new ArrayList<PoiItem>();
        in.readList(this.poiItems, PoiItem.class.getClassLoader());
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
