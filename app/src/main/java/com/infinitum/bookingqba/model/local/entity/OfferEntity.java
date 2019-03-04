package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.infinitum.bookingqba.util.Constants.OFFER_TABLE_NAME;

@Entity(tableName = OFFER_TABLE_NAME, indices = {@Index("rent")},
        foreignKeys = {@ForeignKey(entity = RentEntity.class,parentColumns = "id",
        childColumns = "rent",onDelete = CASCADE)})
public class OfferEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    private double price;

    @NonNull
    private String rent;

    public OfferEntity(@NonNull String id, @NonNull String name, @NonNull String description, double price, @NonNull String rent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.rent = rent;
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
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @NonNull
    public String getRent() {
        return rent;
    }

    public void setRent(@NonNull String rent) {
        this.rent = rent;
    }
}
