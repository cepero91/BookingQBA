package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RentInnerDetail implements Parcelable {

    private String id;
    private String name;
    private String description;
    private String address;
    private double price;
    private float rating;
    private int votes;
    private String rules;
    private String rentMode;
    private int maxBeds;
    private int maxBaths;
    private int capability;
    private int maxRooms;
    private double latitude;
    private double longitude;
    private int wished;
    private ArrayList<RentAmenitieItem> amenitieItems;
    private ArrayList<RentGalerieItem> galerieItems;
    private ArrayList<RentPoiItem> rentPoiItems;

    public RentInnerDetail() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
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

    public ArrayList<RentAmenitieItem> getAmenitieItems() {
        return amenitieItems;
    }

    public void setAmenitieItems(ArrayList<RentAmenitieItem> amenitieItems) {
        this.amenitieItems = amenitieItems;
    }

    public ArrayList<RentGalerieItem> getGalerieItems() {
        return galerieItems;
    }

    public void setGalerieItems(ArrayList<RentGalerieItem> galerieItems) {
        this.galerieItems = galerieItems;
    }

    public ArrayList<RentPoiItem> getRentPoiItems() {
        return rentPoiItems;
    }

    public void setRentPoiItems(ArrayList<RentPoiItem> rentPoiItems) {
        this.rentPoiItems = rentPoiItems;
    }

    public int galerieSize(){
        return galerieItems.size();
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

    public String firstImage(){
        return galerieItems.get(0).getImage();
    }

    public int getWished() {
        return wished;
    }

    public void setWished(int wished) {
        this.wished = wished;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.address);
        dest.writeDouble(this.price);
        dest.writeFloat(this.rating);
        dest.writeInt(this.votes);
        dest.writeString(this.rules);
        dest.writeString(this.rentMode);
        dest.writeInt(this.maxBeds);
        dest.writeInt(this.maxBaths);
        dest.writeInt(this.capability);
        dest.writeInt(this.maxRooms);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.wished);
        dest.writeTypedList(this.amenitieItems);
        dest.writeTypedList(this.galerieItems);
        dest.writeTypedList(this.rentPoiItems);
    }

    protected RentInnerDetail(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.address = in.readString();
        this.price = in.readDouble();
        this.rating = in.readFloat();
        this.votes = in.readInt();
        this.rules = in.readString();
        this.rentMode = in.readString();
        this.maxBeds = in.readInt();
        this.maxBaths = in.readInt();
        this.capability = in.readInt();
        this.maxRooms = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.wished = in.readInt();
        this.amenitieItems = in.createTypedArrayList(RentAmenitieItem.CREATOR);
        this.galerieItems = in.createTypedArrayList(RentGalerieItem.CREATOR);
        this.rentPoiItems = in.createTypedArrayList(RentPoiItem.CREATOR);
    }

    public static final Creator<RentInnerDetail> CREATOR = new Creator<RentInnerDetail>() {
        @Override
        public RentInnerDetail createFromParcel(Parcel source) {
            return new RentInnerDetail(source);
        }

        @Override
        public RentInnerDetail[] newArray(int size) {
            return new RentInnerDetail[size];
        }
    };
}