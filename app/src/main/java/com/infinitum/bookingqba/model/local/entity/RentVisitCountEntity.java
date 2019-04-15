package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.infinitum.bookingqba.util.Constants.RENT_VISIT_COUNT_TABLE_NAME;

@Entity(tableName = RENT_VISIT_COUNT_TABLE_NAME)
public class RentVisitCountEntity {

    @PrimaryKey
    @NonNull
    private String rentId;

    private int visitCount;

    public RentVisitCountEntity(@NonNull String rentId, int visitCount) {
        this.rentId = rentId;
        this.visitCount = visitCount;
    }

    @NonNull
    public String getRentId() {
        return rentId;
    }

    public void setRentId(@NonNull String rentId) {
        this.rentId = rentId;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
}
