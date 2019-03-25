package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("user_name")
    @Expose
    private String userName;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("max_rents")
    @Expose
    private int maxRents;

    public User(String token, String userName, String userId, int maxRents) {
        this.token = token;
        this.userName = userName;
        this.userId = userId;
        this.maxRents = maxRents;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMaxRents() {
        return maxRents;
    }

    public void setMaxRents(int maxRents) {
        this.maxRents = maxRents;
    }
}
