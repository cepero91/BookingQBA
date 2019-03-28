package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static com.infinitum.bookingqba.util.Constants.POI_TABLE_NAME;

@Entity(tableName = POI_TABLE_NAME, indices = {@Index("poiType")},foreignKeys = {@ForeignKey(
        entity = PoiTypeEntity.class,
        parentColumns = "id",
        childColumns = "poiType",
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

    @ColumnInfo(name = "poiType")
    @NonNull
    private String poiType;

    public PoiEntity(@NonNull String id, @NonNull String name, @NonNull String poiType) {
        this.id = id;
        this.name = name;
        this.poiType = poiType;
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
    public String getPoiType() {
        return poiType;
    }

    public void setPoiType(@NonNull String poiType) {
        this.poiType = poiType;
    }
}
