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

@Entity(tableName = COMMENT_TABLE_NAME, indices = {@Index("rent")},
        foreignKeys = {@ForeignKey(entity = RentEntity.class,
                parentColumns = "id",
                childColumns = "rent",
                onDelete = CASCADE)})
@TypeConverters({DateTypeConverter.class})
public class CommentEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "username")
    @NonNull
    private String username;

    @NonNull
    private String description;

    private Date created;

    @NonNull
    private byte[] avatar;

    @NonNull
    private String rent;

    private boolean owner;

    private boolean active;

    @TypeConverters(CommentEmotion.class)
    private CommentEmotion emotion;

    @Ignore
    public CommentEntity(@NonNull String id, @NonNull String username, @NonNull String description) {
        this.id = id;
        this.username = username;
        this.description = description;
    }

    public CommentEntity(@NonNull String id, @NonNull String username, @NonNull String description, Date created, @NonNull byte[] avatar, @NonNull String rent, boolean owner, boolean active, CommentEmotion emotion) {
        this.id = id;
        this.username = username;
        this.description = description;
        this.created = created;
        this.avatar = avatar;
        this.rent = rent;
        this.owner = owner;
        this.active = active;
        this.emotion = emotion;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(@NonNull byte[] avatar) {
        this.avatar = avatar;
    }

    @NonNull
    public String getRent() {
        return rent;
    }

    public void setRent(@NonNull String rent) {
        this.rent = rent;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CommentEmotion getEmotion() {
        return emotion;
    }

    public void setEmotion(CommentEmotion emotion) {
        this.emotion = emotion;
    }
}
