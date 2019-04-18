package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("userName")
    @Expose
    private String userName;

    @SerializedName("userAvatar")
    @Expose
    private String userAvatar;

    @SerializedName("rents")
    @Expose
    private List<String> rentsId;

    public User(String token, String userName, String userAvatar, List<String> rentsId) {
        this.token = token;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rentsId = rentsId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public List<String> getRentsId() {
        return rentsId;
    }

    public void setRentsId(List<String> rentsId) {
        this.rentsId = rentsId;
    }
}
