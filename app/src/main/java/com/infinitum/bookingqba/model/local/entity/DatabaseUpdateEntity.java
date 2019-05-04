package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.infinitum.bookingqba.model.local.tconverter.DateTypeConverter;

import java.util.Date;
import java.util.UUID;

import static com.infinitum.bookingqba.util.Constants.DATABASE_UPDATE_TABLE_NAME;

@Entity(tableName = DATABASE_UPDATE_TABLE_NAME)
@TypeConverters({DateTypeConverter.class})
public class DatabaseUpdateEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "lastDateUpdateEntity")
    @NonNull
    private Date lastDateUpdateEntity;

    private int totalRent;

    public DatabaseUpdateEntity(@NonNull Date lastDateUpdateEntity, int totalRent) {
        this.id = UUID.randomUUID().toString();
        this.lastDateUpdateEntity = lastDateUpdateEntity;
        this.totalRent = totalRent;
    }

    @Ignore
    public DatabaseUpdateEntity(@NonNull String id, @NonNull Date lastDateUpdateEntity, int totalRent) {
        this.id = id;
        this.lastDateUpdateEntity = lastDateUpdateEntity;
        this.totalRent = totalRent;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public Date getLastDateUpdateEntity() {
        return lastDateUpdateEntity;
    }

    public void setLastDateUpdateEntity(@NonNull Date lastDateUpdateEntity) {
        this.lastDateUpdateEntity = lastDateUpdateEntity;
    }

    public int getTotalRent() {
        return totalRent;
    }

    public void setTotalRent(int totalRent) {
        this.totalRent = totalRent;
    }
}
