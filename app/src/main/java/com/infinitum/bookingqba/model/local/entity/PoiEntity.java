package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static com.infinitum.bookingqba.util.Constants.POI_TABLE_NAME;

@Entity(tableName = POI_TABLE_NAME, indices = {@Index("category")},foreignKeys = {@ForeignKey(
        entity = PoiTypeEntity.class,
        parentColumns = "id",
        childColumns = "category",
        onDelete = RESTRICT
)})
public class PoiEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "category")
    private int category;

    @ColumnInfo(name = "minLat")
    private double minLat;

    @ColumnInfo(name = "minLon")
    private double minLon;

    @ColumnInfo(name = "maxLat")
    private double maxLat;

    @ColumnInfo(name = "maxLon")
    private double maxLon;

    public PoiEntity(@NonNull String id, @NonNull String name, int category, double minLat, double minLon, double maxLat, double maxLon) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public double getMinLat() {
        return minLat;
    }

    public void setMinLat(double minLat) {
        this.minLat = minLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public void setMinLon(double minLon) {
        this.minLon = minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(double maxLat) {
        this.maxLat = maxLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(double maxLon) {
        this.maxLon = maxLon;
    }
}
