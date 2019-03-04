package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.infinitum.bookingqba.util.Constants.GALERIE_TABLE_NAME;

@Entity(tableName = GALERIE_TABLE_NAME, indices = {@Index("rent")},
        foreignKeys = {@ForeignKey(entity = RentEntity.class,
        parentColumns = "id",
        childColumns = "rent",
        onDelete = CASCADE)})
public class GalerieEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "imageUrl")
    @NonNull
    private String imageUrl;

    @ColumnInfo(name = "imageByte",typeAffinity = ColumnInfo.BLOB)
    private byte[] imageByte;

    @NonNull
    private String rent;


    public GalerieEntity(@NonNull String id, @NonNull String imageUrl,byte[] imageByte, @NonNull String rent) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.imageByte = imageByte;
        this.rent = rent;
    }

    @Ignore
    public GalerieEntity(@NonNull String id, @NonNull String imageUrl,@NonNull String rent) {
        this.id = id;
        this.imageUrl = imageUrl;
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
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NonNull String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public byte[] getImageByte() {
        return imageByte;
    }

    public void setImageByte(@NonNull byte[] imageByte) {
        this.imageByte = imageByte;
    }

    @NonNull
    public String getRent() {
        return rent;
    }

    public void setRent(@NonNull String rent) {
        this.rent = rent;
    }
}
