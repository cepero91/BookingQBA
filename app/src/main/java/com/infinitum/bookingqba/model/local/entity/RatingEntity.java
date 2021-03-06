package com.infinitum.bookingqba.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.infinitum.bookingqba.model.local.tconverter.CommentEmotion;
import com.infinitum.bookingqba.model.local.tconverter.DateTypeConverter;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.infinitum.bookingqba.util.Constants.COMMENT_TABLE_NAME;
import static com.infinitum.bookingqba.util.Constants.RATING_TABLE_NAME;

@Entity(tableName = RATING_TABLE_NAME, indices = {@Index(value = "rent", unique = true)},
        foreignKeys = {@ForeignKey(entity = RentEntity.class,
                parentColumns = "id",
                childColumns = "rent",
                onDelete = CASCADE)})
@TypeConverters({DateTypeConverter.class})
public class RatingEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    private float rating;

    private String comment;

    @NonNull
    private String userId;

    @NonNull
    private String rent;

    private int version;

    public RatingEntity(@NonNull String id, float rating, String comment, @NonNull String userId, @NonNull String rent, int version) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.userId = userId;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }
}
