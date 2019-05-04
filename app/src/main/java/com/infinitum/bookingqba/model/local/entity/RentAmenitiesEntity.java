package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static com.infinitum.bookingqba.util.Constants.RENT_AMENITIES_TABLE_NAME;

@Entity(tableName = RENT_AMENITIES_TABLE_NAME,
        primaryKeys = {"amenityId", "rentId"},
        indices = {@Index("amenityId"), @Index("rentId")},
        foreignKeys = {
                @ForeignKey(
                        entity = AmenitiesEntity.class,
                        parentColumns = "id",
                        childColumns = "amenityId"
                        , onDelete = RESTRICT),
                @ForeignKey(
                        entity = RentEntity.class,
                        parentColumns = "id",
                        childColumns = "rentId",
                        onDelete = RESTRICT
                )})
public class RentAmenitiesEntity {

    @ColumnInfo(name = "amenityId")
    @NonNull
    private String amenityId;

    @ColumnInfo(name = "rentId")
    @NonNull
    private String rentId;

    public RentAmenitiesEntity(@NonNull String amenityId, @NonNull String rentId) {
        this.amenityId = amenityId;
        this.rentId = rentId;
    }

    @Ignore
    public RentAmenitiesEntity(@NonNull String rentId) {
        this.rentId = rentId;
    }

    @NonNull
    public String getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(@NonNull String amenityId) {
        this.amenityId = amenityId;
    }

    @NonNull
    public String getRentId() {
        return rentId;
    }

    public void setRentId(@NonNull String rentId) {
        this.rentId = rentId;
    }
}
