package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.ReferenceZoneEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.local.entity.RentPoiEntity;

import java.util.List;
import java.util.Set;

public class RentAndDependencies {

    private String id;
    private String name;
    private String address;
    private double price;
    private float rating;
    private int ratingCount;
    private double latitude;
    private double longitude;
    private int isWished;
    private String rentMode;
    private String referenceZone;

    @Relation(parentColumn = "referenceZone", entityColumn = "id",entity = ReferenceZoneEntity.class,projection = {"name"})
    private Set<String> referenceZoneSet;

    @Relation(parentColumn = "rentMode", entityColumn = "id",entity = RentModeEntity.class,projection = {"name"})
    private Set<String> rentModeSet;

    @Relation(parentColumn = "id", entityColumn = "rent")
    private List<GalerieEntity> galeries;

    @Relation(parentColumn = "id", entityColumn = "rentId",entity = RentPoiEntity.class)
    private List<RentPoiAndRelation> rentPoiAndRelations;

    public RentAndDependencies(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public RentAndDependencies(String id, String name, String address, double price, float rating, int ratingCount, double latitude, double longitude, int isWished, String rentMode, String referenceZone, Set<String> referenceZoneSet, Set<String> rentModeSet, List<GalerieEntity> galeries, List<RentPoiAndRelation> rentPoiAndRelations) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.price = price;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isWished = isWished;
        this.rentMode = rentMode;
        this.referenceZone = referenceZone;
        this.referenceZoneSet = referenceZoneSet;
        this.rentModeSet = rentModeSet;
        this.galeries = galeries;
        this.rentPoiAndRelations = rentPoiAndRelations;
    }

    public int getIsWished() {
        return isWished;
    }

    public void setIsWished(int isWished) {
        this.isWished = isWished;
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

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
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

    public List<RentPoiAndRelation> getRentPoiAndRelations() {
        return rentPoiAndRelations;
    }

    public void setRentPoiAndRelations(List<RentPoiAndRelation> rentPoiAndRelations) {
        this.rentPoiAndRelations = rentPoiAndRelations;
    }

    public String getRentMode() {
        return rentModeSet.toArray()[0].toString();
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public String getReferenceZone() {
        return referenceZoneSet.toArray()[0].toString();
    }

    public void setReferenceZone(String referenceZone) {
        this.referenceZone = referenceZone;
    }

    public Set<String> getReferenceZoneSet() {
        return referenceZoneSet;
    }

    public void setReferenceZoneSet(Set<String> referenceZoneSet) {
        this.referenceZoneSet = referenceZoneSet;
    }

    public String getImageAtPos(int pos){
        String url = "";
        if(galeries.size() > 0) {
            url = galeries.get(pos).getImageLocalPath()!=null?galeries.get(pos).getImageLocalPath():galeries.get(pos).getImageUrl();
        }
        return url;
    }
}
