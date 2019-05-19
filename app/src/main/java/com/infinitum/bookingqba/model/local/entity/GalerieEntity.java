package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Nullable
    private String imageLocalPath;

    @NonNull
    private String rent;

    private int version;

    public GalerieEntity(@NonNull String id, @NonNull String imageUrl, String imageLocalPath, @NonNull String rent, int version) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.imageLocalPath = imageLocalPath;
        this.rent = rent;
        this.version = version;
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

    @Nullable
    public String getImageLocalPath() {
        return imageLocalPath;
    }

    public void setImageLocalPath(@Nullable String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }

    @NonNull
    public String getRent() {
        return rent;
    }

    public void setRent(@NonNull String rent) {
        this.rent = rent;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
