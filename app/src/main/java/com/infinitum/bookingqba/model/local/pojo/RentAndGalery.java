package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RentAndGalery {

    private String id;
    private String name;
    private String address;
    private double price;
    private float rating;
    private double latitude;
    private double longitude;
    private String rentMode;
    private int isWished;


    @Relation(parentColumn = "rentMode", entityColumn = "id",entity = RentModeEntity.class,projection = {"name"})
    private Set<String> rentModeSet;

    @Relation(parentColumn = "id", entityColumn = "rent")
    private List<GalerieEntity> galeries;

    public RentAndGalery(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public RentAndGalery(String id, String name, String address, double price, float rating, double latitude, double longitude, String rentMode, int isWished, Set<String> rentModeSet, List<GalerieEntity> galeries) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.price = price;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rentMode = rentMode;
        this.isWished = isWished;
        this.rentModeSet = rentModeSet;
        this.galeries = galeries;
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

    public String getRentMode() {
        return rentModeSet.toArray()[0].toString();
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public Set<String> getRentModeSet() {
        return rentModeSet;
    }

    public void setRentModeSet(Set<String> rentModeSet) {
        this.rentModeSet = rentModeSet;
    }

    public List<GalerieEntity> getGaleries() {
        return galeries;
    }

    public void setGaleries(List<GalerieEntity> galeries) {
        this.galeries = galeries;
    }

    public GalerieEntity getGalerieAtPos(int pos){
        return galeries.get(pos);
    }

    public int getIsWished() {
        return isWished;
    }

    public void setIsWished(int isWished) {
        this.isWished = isWished;
    }
}
