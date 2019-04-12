package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.infinitum.bookingqba.util.Constants.USER_TRACKING_TABLE_NAME;

@Entity(tableName = USER_TRACKING_TABLE_NAME, indices = {@Index("rentId")},
        foreignKeys = {@ForeignKey(entity = RentEntity.class,
        parentColumns = "id",
        childColumns = "rentId",
        onDelete = CASCADE)})
public class UserTrackingEntity {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String imei;

    @NonNull
    private String rentId;

    private int viewCount;

    public UserTrackingEntity(@NonNull String imei, @NonNull String rentId, int viewCount) {
        this.id = UUID.randomUUID().toString();
        this.imei = imei;
        this.rentId = rentId;
        this.viewCount = viewCount;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getImei() {
        return imei;
    }

    public void setImei(@NonNull String imei) {
        this.imei = imei;
    }

    @NonNull
    public String getRentId() {
        return rentId;
    }

    public void setRentId(@NonNull String rentId) {
        this.rentId = rentId;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
