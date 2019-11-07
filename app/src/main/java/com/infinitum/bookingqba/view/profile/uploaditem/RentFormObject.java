package com.infinitum.bookingqba.view.profile.uploaditem;

import com.infinitum.bookingqba.util.Constants;

import java.util.List;

public class RentFormObject implements Cloneable{
    private String uuid;
    private String rentName;
    private String address;
    private String description;
    private String email;
    private String phoneNumber;
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
    private String checkin;
    private String checkout;
    private String userid;
    private List<String> amenities;

    public RentFormObject(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    private boolean sameAmenities(List<String> amenitiesUuid) {
        if (this.amenities.size() == amenitiesUuid.size()) {
            for (int i = 0; i < amenitiesUuid.size(); i++) {
                if (!this.amenities.get(i).equals(amenitiesUuid.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass() != obj.getClass())
            return false;
        RentFormObject other = (RentFormObject) obj;
        return sameRentObject(other);
    }

    private boolean sameRentObject(RentFormObject copyRentObject) {
        boolean equals = this.uuid.equals(copyRentObject.getUuid()) &&
                this.rentName.equals(copyRentObject.getRentName()) &&
                this.address.equals(copyRentObject.getAddress()) &&
                this.description.equals(copyRentObject.getDescription()) &&
                this.email.equals(copyRentObject.getEmail()) &&
                this.phoneNumber.equals(copyRentObject.getPhoneNumber()) &&
                this.maxRooms.equals(copyRentObject.getMaxRooms()) &&
                this.maxBaths.equals(copyRentObject.getMaxBaths()) &&
                this.maxBeds.equals(copyRentObject.getMaxBeds()) &&
                this.capability.equals(copyRentObject.getCapability()) &&
                this.rentMode.equals(copyRentObject.getRentMode()) &&
                this.rules.equals(copyRentObject.getRules()) &&
                this.price == copyRentObject.getPrice() &&
                this.latitude.equals(copyRentObject.getLatitude()) &&
                this.longitude.equals(copyRentObject.getLongitude()) &&
                this.municipality.equals(copyRentObject.getMunicipality()) &&
                this.referenceZone.equals(copyRentObject.getReferenceZone()) &&
                this.checkin.equals(copyRentObject.getCheckin()) &&
                this.checkout.equals(copyRentObject.getCheckout()) &&
                this.userid.equals(copyRentObject.getUserid()) &&
                sameAmenities(copyRentObject.getAmenities());
        return equals;
    }

    public boolean isAValidObject(){
        boolean validRentMode = true;
        boolean validEsential = !this.rentName.isEmpty() &&
         !this.address.isEmpty() &&
         !this.description.isEmpty() &&
         !this.maxBeds.isEmpty() &&
         !this.maxRooms.isEmpty() &&
         !this.maxBaths.isEmpty() &&
         !this.capability.isEmpty() &&
         this.price > 0 &&
         !this.latitude.isEmpty() &&
         !this.longitude.isEmpty() &&
         !this.municipality.isEmpty() &&
         !this.referenceZone.isEmpty() &&
         !this.rentMode.isEmpty() &&
         !this.userid.isEmpty();
        if(rentMode.equals(Constants.BY_NIGHT_UUID)){
            validRentMode = !this.email.isEmpty();
        }else if(rentMode.equals(Constants.BY_HOURS_UUID)){
            validRentMode = !this.phoneNumber.isEmpty();
        }
        return validEsential &&  validRentMode;
    }


}
