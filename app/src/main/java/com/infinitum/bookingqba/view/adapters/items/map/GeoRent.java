package com.infinitum.bookingqba.view.adapters.items.map;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.PoiCategory;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;

import org.mapsforge.core.model.LatLong;
import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoRent extends BaseItem {

    private float rating;
    private int ratingCount;
    private String address;
    private GeoPoint geoPoint;
    private Map<PoiCategory,List<RentPoiItem>> poiItemMap;
    private String referenceZone;

    public GeoRent(String id, String name, String imagePath, float price, String rentMode) {
        super(id, name, imagePath, price, rentMode);
    }

    public GeoRent(String id, String name, String imagePath, float price, String rentMode, float rating, int ratingCount, String address, GeoPoint geoPoint, Map<PoiCategory, List<RentPoiItem>> poiItemMap, String referenceZone) {
        super(id, name, imagePath, price, rentMode);
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.address = address;
        this.geoPoint = geoPoint;
        this.poiItemMap = poiItemMap;
        this.referenceZone = referenceZone;
    }

    public double getDistanceBetween(Location userLocation) {
        Location rentLocation = new Location("");
        rentLocation.setLatitude(geoPoint.getLatitude());
        rentLocation.setLongitude(geoPoint.getLongitude());
        return userLocation.distanceTo(rentLocation);
    }

    public String getHumanDistanceBetween(Location userLocation){
        double distance = getDistanceBetween(userLocation)/1000; //convert to km
        return distance < 1?String.format("%.1f m",distance * 1000):String.format("%.1f km",distance);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Map<PoiCategory, List<RentPoiItem>> getPoiItemMap() {
        return poiItemMap;
    }

    public void setPoiItemMap(Map<PoiCategory, List<RentPoiItem>> poiItemMap) {
        this.poiItemMap = poiItemMap;
    }

    public String getReferenceZone() {
        return referenceZone;
    }

    public void setReferenceZone(String referenceZone) {
        this.referenceZone = referenceZone;
    }

    public String humanRatingCount() {
        if (ratingCount > 0) {
            return String.format("(%s)", ratingCount);
        } else {
            return "(0)";
        }
    }

    public String humanRating() {
        return String.format("%.1f", rating);
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
        dest.writeString(this.address);
        dest.writeSerializable(this.geoPoint);
        dest.writeInt(this.poiItemMap.size());
        for (Map.Entry<PoiCategory, List<RentPoiItem>> entry : this.poiItemMap.entrySet()) {
            dest.writeParcelable(entry.getKey(), flags);
            dest.writeTypedList(entry.getValue());
        }
        dest.writeString(this.referenceZone);
    }

    protected GeoRent(Parcel in) {
        super(in);
        this.rating = in.readFloat();
        this.ratingCount = in.readInt();
        this.address = in.readString();
        this.geoPoint = (GeoPoint) in.readSerializable();
        int poiItemMapSize = in.readInt();
        this.poiItemMap = new HashMap<PoiCategory, List<RentPoiItem>>(poiItemMapSize);
        for (int i = 0; i < poiItemMapSize; i++) {
            PoiCategory key = in.readParcelable(PoiCategory.class.getClassLoader());
            List<RentPoiItem> value = in.createTypedArrayList(RentPoiItem.CREATOR);
            this.poiItemMap.put(key, value);
        }
        this.referenceZone = in.readString();
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
