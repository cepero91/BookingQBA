package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static com.infinitum.bookingqba.util.Constants.DRAW_TYPE_TABLE_NAME;

@Entity(tableName = DRAW_TYPE_TABLE_NAME)
public class DrawTypeEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    public DrawTypeEntity(@NonNull String id, @NonNull String name) {
        this.id = id;
        this.name = name;
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
}
