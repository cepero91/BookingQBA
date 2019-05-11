package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("created")
    @Expose
    private String created;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("rent")
    @Expose
    private String rent;

    @SerializedName("is_owner")
    @Expose
    private boolean is_owner;

    @SerializedName("emotion")
    @Expose
    private int emotion;

    @SerializedName("active")
    @Expose
    private boolean active;

    @SerializedName("userid")
    @Expose
    private String userid;

    public Comment(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public Comment(String id, String username, String description, String created, String avatar, String rent, boolean is_owner, int emotion, boolean active) {
        this.id = id;
        this.username = username;
        this.description = description;
        this.created = created;
        this.avatar = avatar;
        this.rent = rent;
        this.is_owner = is_owner;
        this.emotion = emotion;
        this.active = active;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public boolean isIs_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
