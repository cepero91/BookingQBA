package com.infinitum.bookingqba.view.profile.uploaditem;

public class RentFormObject {
    private String uuid;
    private String rentName;
    private String address;
    private String description;
    private String email;
    private String phoneNumber;
    private String phoneHomeNumber;
    private String maxRooms;
    private String maxBeds;
    private String maxBaths;
    private String capability;
    private String rentMode;
    private String rules;
    private double price;
    private String latitude;
    private String longitude;
    private String municipality;
    private String referenceZone;
    private String userid;

    public RentFormObject(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRentName() {
        return rentName;
    }

    public void setRentName(String rentName) {
        this.rentName = rentName;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getMaxRooms() {
        return maxRooms;
    }

    public void setMaxRooms(String maxRooms) {
        this.maxRooms = maxRooms;
    }

    public String getMaxBeds() {
        return maxBeds;
    }

    public void setMaxBeds(String maxBeds) {
        this.maxBeds = maxBeds;
    }

    public String getMaxBaths() {
        return maxBaths;
    }

    public void setMaxBaths(String maxBaths) {
        this.maxBaths = maxBaths;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
