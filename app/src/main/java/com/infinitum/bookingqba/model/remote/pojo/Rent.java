package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rent {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phoneHomeNumber")
    @Expose
    private String phoneHomeNumber;

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("rating")
    @Expose
    private RatingEmbeded ratingEmbeded;

    @SerializedName("maxRooms")
    @Expose
    private Integer maxRooms;

    @SerializedName("capability")
    @Expose
    private Integer capability;

    @SerializedName("maxBeds")
    @Expose
    private Integer maxBeds;

    @SerializedName("maxBath")
    @Expose
    private Integer maxBath;

    @SerializedName("price")
    @Expose
    private Double price;

    @SerializedName("rentMode")
    @Expose
    private String rentMode;

    @SerializedName("rules")
    @Expose
    private String rules;

    @SerializedName("municipality")
    @Expose
    private String municipality;

    @SerializedName("referenceZone")
    @Expose
    private String referenceZone;

    @SerializedName("created")
    @Expose
    private String created;



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

    public String getPhoneHomeNumber() {
        return phoneHomeNumber;
    }

    public void setPhoneHomeNumber(String phoneHomeNumber) {
        this.phoneHomeNumber = phoneHomeNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getMaxRooms() {
        return maxRooms;
    }

    public void setMaxRooms(Integer maxRooms) {
        this.maxRooms = maxRooms;
    }

    public Integer getCapability() {
        return capability;
    }

    public void setCapability(Integer capability) {
        this.capability = capability;
    }

    public Integer getMaxBeds() {
        return maxBeds;
    }

    public void setMaxBeds(Integer maxBeds) {
        this.maxBeds = maxBeds;
    }

    public Integer getMaxBath() {
        return maxBath;
    }

    public void setMaxBath(Integer maxBath) {
        this.maxBath = maxBath;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public RatingEmbeded getRatingEmbeded() {
        return ratingEmbeded;
    }

    public void setRatingEmbeded(RatingEmbeded ratingEmbeded) {
        this.ratingEmbeded = ratingEmbeded;
    }
}
