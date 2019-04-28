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

    @ColumnInfo(name = "lastDatabaseUpdate")
    @NonNull
    private Date lastDatabaseUpdate;

    private int totalRent;

    public DatabaseUpdateEntity(@NonNull Date lastDatabaseUpdate, int totalRent) {
        this.id = UUID.randomUUID().toString();
        this.lastDatabaseUpdate = lastDatabaseUpdate;
        this.totalRent = totalRent;
    }

    @Ignore
    public DatabaseUpdateEntity(@NonNull String id, @NonNull Date lastDatabaseUpdate, int totalRent) {
        this.id = id;
        this.lastDatabaseUpdate = lastDatabaseUpdate;
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
    public Date getLastDatabaseUpdate() {
        return lastDatabaseUpdate;
    }

    public void setLastDatabaseUpdate(@NonNull Date lastDatabaseUpdate) {
        this.lastDatabaseUpdate = lastDatabaseUpdate;
    }

    public int getTotalRent() {
        return totalRent;
    }

    public void setTotalRent(int totalRent) {
        this.totalRent = totalRent;
    }
}
