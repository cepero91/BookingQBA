package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class User {

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

    @SerializedName("rents")
    @Expose
    private ArrayList<String> rents;

    public User(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public User(String token, String username, String userid, String avatar, ArrayList<String> rents) {
        this.token = token;
        this.username = username;
        this.userid = userid;
        this.avatar = avatar;
        this.rents = rents;
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

    public ArrayList<String> getRents() {
        return rents;
    }

    public void setRents(ArrayList<String> rents) {
        this.rents = rents;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public boolean equals(Object obj) {
        User otherUser = (User) obj;
        return this.token.equals(otherUser.getToken()) && this.username.equals(otherUser.getUsername())
                && this.userid.equals(otherUser.getUserid());
    }
}
