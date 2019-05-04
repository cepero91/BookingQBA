package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static com.infinitum.bookingqba.util.Constants.RENT_POI_TABLE_NAME;

@Entity(tableName = RENT_POI_TABLE_NAME,
        primaryKeys = {"poiId", "rentId"},
        indices = {@Index("poiId"),@Index("rentId")},
        foreignKeys = {
        @ForeignKey(
                entity = PoiEntity.class,
                parentColumns = "id",
                childColumns = "poiId",
                onDelete = RESTRICT),
        @ForeignKey(
                entity = RentEntity.class,
                parentColumns = "id",
                childColumns = "rentId",
                onDelete = RESTRICT
        )})
public class RentPoiEntity {

    @ColumnInfo(name = "poiId")
    @NonNull
    private String poiId;

    @ColumnInfo(name = "rentId")
    @NonNull
    private String rentId;

    public RentPoiEntity(@NonNull String poiId, @NonNull String rentId) {
        this.poiId = poiId;
        this.rentId = rentId;
    }

    @Ignore
    public RentPoiEntity(@NonNull String rentId) {
        this.rentId = rentId;
    }

    @NonNull
    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(@NonNull String poiId) {
        this.poiId = poiId;
    }

    @NonNull
    public String getRentId() {
        return rentId;
    }

    public void setRentId(@NonNull String rentId) {
        this.rentId = rentId;
    }
}
