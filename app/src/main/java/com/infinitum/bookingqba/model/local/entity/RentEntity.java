package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.infinitum.bookingqba.model.local.tconverter.DateTypeConverter;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static com.infinitum.bookingqba.util.Constants.RENT_TABLE_NAME;

@Entity(tableName = RENT_TABLE_NAME, indices = {@Index("municipality"),@Index("rentMode"),@Index("referenceZone")}, foreignKeys = {
        @ForeignKey(
                entity = MunicipalityEntity.class,
                parentColumns = "id",
                childColumns = "municipality",
                onDelete = RESTRICT),
        @ForeignKey(
                entity = RentModeEntity.class,
                parentColumns = "id",
                childColumns = "rentMode",
                onDelete = RESTRICT),
        @ForeignKey(
                entity = ReferenceZoneEntity.class,
                parentColumns = "id",
                childColumns = "referenceZone",
                onDelete = RESTRICT)})
@TypeConverters({DateTypeConverter.class})
public class RentEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @NonNull
    private String name;

    @NonNull
    private String address;

    @NonNull
    private String description;

    @NonNull
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

    @NonNull
    private Date created;

    @NonNull
    private String rentMode;

    @NonNull
    private String rules;

    @NonNull
    private String municipality;

    @NonNull
    private String referenceZone;

    private int isWished;

    @Ignore
    public RentEntity(@NonNull String id) {
        this.id = id;
    }

    public RentEntity(@NonNull String id, @NonNull String name, @NonNull String address, @NonNull String description, @NonNull String email, String phoneNumber, String phoneHomeNumber, double latitude, double longitude, float rating, int maxRooms, int capability, int maxBeds, int maxBath, double price, @NonNull Date created, @NonNull String rentMode, @NonNull String rules, @NonNull String municipality, @NonNull String referenceZone, int isWished) {
        this.id = id;
        this.name = name;
        this.address = address;
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
        this.rentMode = rentMode;
        this.rules = rules;
        this.municipality = municipality;
        this.referenceZone = referenceZone;
        this.isWished = isWished;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
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

    @NonNull
    public Date getCreated() {
        return created;
    }

    public void setCreated(@NonNull Date created) {
        this.created = created;
    }

    @NonNull
    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(@NonNull String rentMode) {
        this.rentMode = rentMode;
    }

    @NonNull
    public String getRules() {
        return rules;
    }

    public void setRules(@NonNull String rules) {
        this.rules = rules;
    }

    @NonNull
    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(@NonNull String municipality) {
        this.municipality = municipality;
    }

    @NonNull
    public String getReferenceZone() {
        return referenceZone;
    }

    public void setReferenceZone(@NonNull String referenceZone) {
        this.referenceZone = referenceZone;
    }

    public int getIsWished() {
        return isWished;
    }

    public void setIsWished(int isWished) {
        this.isWished = isWished;
    }
}
