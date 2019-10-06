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
    private double price;
    private GeoPoint geoPoint;
    private String rentMode;
    private int wished;
    private Map<PoiCategory,List<RentPoiItem>> poiItemMap;
    private String referenceZone;

    public GeoRent(String id, String name, String imagePath) {
        super(id, name, imagePath);
    }

    public GeoRent(String id, String name, String imagePath, float rating, int ratingCount, String address, double price, GeoPoint geoPoint, String rentMode, int wished, Map<PoiCategory, List<RentPoiItem>> poiItemMap, String referenceZone) {
        super(id, name, imagePath);
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.address = address;
        this.price = price;
        this.geoPoint = geoPoint;
        this.rentMode = rentMode;
        this.wished = wished;
        this.poiItemMap = poiItemMap;
        this.referenceZone = referenceZone;
    }

    public double getDistanceBetween(Location userLocation) {
        Location rentLocation = new Location("");
        rentLocation.setLatitude(geoPoint.getLatitude());
        rentLocation.setLongitude(geoPoint.getLongitude());
        return userLocation.distanceTo(rentLocation);
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

    public Map<PoiCategory, List<RentPoiItem>> getPoiItemMap() {
        return poiItemMap;
    }

    public void setPoiItemMap(Map<PoiCategory, List<RentPoiItem>> poiItemMap) {
        this.poiItemMap = poiItemMap;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWished() {
        return wished;
    }

    public void setWished(int wished) {
        this.wished = wished;
    }

    public String getReferenceZone() {
        return referenceZone;
    }

    public void setReferenceZone(String referenceZone) {
        this.referenceZone = referenceZone;
    }

    public String humanRatingCount() {
        if (ratingCount > 0) {
            return String.format("(%s voto%s)", ratingCount, ratingCount > 1 ? "s" : "");
        } else {
            return "(sin votos)";
        }
    }

    public String humanPrice(){
        return String.format("%.2f cuc",price);
    }

    public String humanRating() {
        return String.format("%.1f", rating);
    }

    public String humanRentMode() {
        return "/"+rentMode;
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
        dest.writeDouble(this.price);
        dest.writeSerializable(this.geoPoint);
        dest.writeString(this.rentMode);
        dest.writeInt(this.wished);
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
        this.price = in.readDouble();
        this.geoPoint = (GeoPoint) in.readSerializable();
        this.rentMode = in.readString();
        this.wished = in.readInt();
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
