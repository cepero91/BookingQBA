package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.RentAmenitiesEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;
import com.infinitum.bookingqba.model.remote.pojo.RentMode;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class RentDetail {

    private String id;
    private String name;
    private String address;
    private String slogan;
    private String description;
    private String email;
    private String phoneNumber;
    private String phoneHomeNumber;
    private double latitude;
    private double longitude;
    private float rating;
    private int maxRooms;
    private int capability;
    private int maxBeds;
    private int maxBath;
    private double price;
    private Date created;
    private Date updated;
    private String rules;

    private String rentMode;
    private String municipality;
    private String referenceZone;

    @Relation(parentColumn = "rentMode", entityColumn = "id",entity = RentModeEntity.class,projection = {"name"})
    private Set<String> rentModeName;

    @Relation(parentColumn = "id", entityColumn = "rentId",entity = RentAmenitiesEntity.class)
    private List<RentAmenitieName> amenitieNames;

    @Relation(parentColumn = "id", entityColumn = "rent")
    private List<GalerieEntity> galeries;

    public RentDetail(String id, String name, String address, String slogan, String description, String email, String phoneNumber, String phoneHomeNumber, double latitude, double longitude, float rating, int maxRooms, int capability, int maxBeds, int maxBath, double price, Date created, Date updated, String rules, String rentMode, String municipality, String referenceZone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.slogan = slogan;
        this.description = description;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.phoneHomeNumber = phoneHomeNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.maxRooms = maxRooms;
        this.capability = capability;
        this.maxBeds = maxBeds;
        this.maxBath = maxBath;
        this.price = price;
        this.created = created;
        this.updated = updated;
        this.rules = rules;
        this.rentMode = rentMode;
        this.municipality = municipality;
        this.referenceZone = referenceZone;
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

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneHomeNumber() {
        return phoneHomeNumber;
    }

    public void setPhoneHomeNumber(String phoneHomeNumber) {
        this.phoneHomeNumber = phoneHomeNumber;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getMaxRooms() {
        return maxRooms;
    }

    public void setMaxRooms(int maxRooms) {
        this.maxRooms = maxRooms;
    }

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }

    public int getMaxBeds() {
        return maxBeds;
    }

    public void setMaxBeds(int maxBeds) {
        this.maxBeds = maxBeds;
    }

    public int getMaxBath() {
        return maxBath;
    }

    public void setMaxBath(int maxBath) {
        this.maxBath = maxBath;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
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

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getReferenceZone() {
        return referenceZone;
    }

    public void setReferenceZone(String referenceZone) {
        this.referenceZone = referenceZone;
    }

    public Set<String> getRentModeName() {
        return rentModeName;
    }

    public void setRentModeName(Set<String> rentModeName) {
        this.rentModeName = rentModeName;
    }

    public List<RentAmenitieName> getAmenitieNames() {
        return amenitieNames;
    }

    public void setAmenitieNames(List<RentAmenitieName> amenitieNames) {
        this.amenitieNames = amenitieNames;
    }

    public List<GalerieEntity> getGaleries() {
        return galeries;
    }

    public void setGaleries(List<GalerieEntity> galeries) {
        this.galeries = galeries;
    }
}
