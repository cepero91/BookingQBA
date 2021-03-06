package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static com.infinitum.bookingqba.util.Constants.RENT_DRAW_TYPE_TABLE_NAME;

@Entity(tableName = RENT_DRAW_TYPE_TABLE_NAME,
        primaryKeys = {"drawTypeId", "rentId"},
        indices = {@Index("drawTypeId"),@Index("rentId")},
        foreignKeys = {
        @ForeignKey(
                entity = DrawTypeEntity.class,
                parentColumns = "id",
                childColumns = "drawTypeId",
                onDelete = RESTRICT),
        @ForeignKey(
                entity = RentEntity.class,
                parentColumns = "id",
                childColumns = "rentId",
                onDelete = RESTRICT
        )})
public class RentDrawTypeEntity {

    @NonNull
    private String drawTypeId;

    @NonNull
    private String rentId;

    public RentDrawTypeEntity(@NonNull String drawTypeId, @NonNull String rentId) {
        this.drawTypeId = drawTypeId;
        this.rentId = rentId;
    }

    @NonNull
    public String getDrawTypeId() {
        return drawTypeId;
    }

    public void setDrawTypeId(@NonNull String drawTypeId) {
        this.drawTypeId = drawTypeId;
    }

    @NonNull
    public String getRentId() {
        return rentId;
    }

    public void setRentId(@NonNull String rentId) {
        this.rentId = rentId;
    }
}
