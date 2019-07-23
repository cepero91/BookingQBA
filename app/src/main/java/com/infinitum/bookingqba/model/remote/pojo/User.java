package com.infinitum.bookingqba.model.remote.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class User implements Parcelable {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("userid")
    @Expose
    private String userid;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("rentOwner")
    @Expose
    private boolean rentOwner;

    public User(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public User(String token, String username, String userid, String avatar, boolean rentOwner) {
        this.token = token;
        this.username = username;
        this.userid = userid;
        this.avatar = avatar;
        this.rentOwner = rentOwner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isRentOwner() {
        return rentOwner;
    }

    public void setRentOwner(boolean rentOwner) {
        this.rentOwner = rentOwner;
    }

    @Override
    public boolean equals(Object obj) {
        User otherUser = (User) obj;
        return this.token.equals(otherUser.getToken()) && this.username.equals(otherUser.getUsername())
                && this.userid.equals(otherUser.getUserid());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.username);
        dest.writeString(this.userid);
        dest.writeString(this.avatar);
        dest.writeByte(this.rentOwner ? (byte) 1 : (byte) 0);
    }

    protected User(Parcel in) {
        this.token = in.readString();
        this.username = in.readString();
        this.userid = in.readString();
        this.avatar = in.readString();
        this.rentOwner = in.readByte() != 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
