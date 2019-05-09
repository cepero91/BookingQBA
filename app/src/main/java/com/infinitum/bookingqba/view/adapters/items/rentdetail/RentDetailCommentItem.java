package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

import java.util.Date;

public class RentDetailCommentItem implements ViewModel, Parcelable {

    private String id;
    private String username;
    private String description;
    private byte[] avatar;
    private boolean owner;
    private Date created;

    public RentDetailCommentItem(String id, String username, String description, byte[] avatar, boolean owner, Date created) {
        this.id = id;
        this.username = username;
        this.description = description;
        this.avatar = avatar;
        this.owner = owner;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.description);
        dest.writeByteArray(this.avatar);
        dest.writeByte(this.owner ? (byte) 1 : (byte) 0);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
    }

    protected RentDetailCommentItem(Parcel in) {
        this.id = in.readString();
        this.username = in.readString();
        this.description = in.readString();
        this.avatar = in.createByteArray();
        this.owner = in.readByte() != 0;
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
    }

    public static final Parcelable.Creator<RentDetailCommentItem> CREATOR = new Parcelable.Creator<RentDetailCommentItem>() {
        @Override
        public RentDetailCommentItem createFromParcel(Parcel source) {
            return new RentDetailCommentItem(source);
        }

        @Override
        public RentDetailCommentItem[] newArray(int size) {
            return new RentDetailCommentItem[size];
        }
    };
}
