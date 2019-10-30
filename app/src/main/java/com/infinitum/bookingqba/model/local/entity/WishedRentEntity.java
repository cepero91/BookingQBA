package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.infinitum.bookingqba.util.Constants.OFFER_TABLE_NAME;
import static com.infinitum.bookingqba.util.Constants.WISHED_TABLE_NAME;

@Entity(tableName = WISHED_TABLE_NAME, indices = {@Index("rent")},
        foreignKeys = {@ForeignKey(entity = RentEntity.class,parentColumns = "id",
        childColumns = "rent",onDelete = CASCADE)})
public class WishedRentEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    private int value;

    @NonNull
    private String userId;

    @NonNull
    private String rent;

    public WishedRentEntity(int id, int value, @NonNull String userId, @NonNull String rent) {
        this.id = id;
        this.value = value;
        this.userId = userId;
        this.rent = rent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getRent() {
        return rent;
    }

    public void setRent(@NonNull String rent) {
        this.rent = rent;
    }
}
