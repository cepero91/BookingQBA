package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserEsentialData {
    @SerializedName("is_wish")
    @Expose
    private Boolean isWish;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userTime")
    @Expose
    private String userTime;
    @SerializedName("user_last_login")
    @Expose
    private String userLastLogin;
    @SerializedName("rating")
    @Expose
    private String ratingAverage;
    @SerializedName("rating_description")
    @Expose
    private String ratingDescription;
    @SerializedName("comment_count")
    @Expose
    private String commentCount;
    @SerializedName("comment_average")
    @Expose
    private String commentAverage;
    @SerializedName("reserved_count")
    @Expose
    private String reservedCount;
    @SerializedName("commentLast")
    @Expose
    private String commentLast;

    public Boolean getWish() {
        return isWish;
    }

    public void setWish(Boolean wish) {
        isWish = wish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTime() {
        return userTime;
    }

    public void setUserTime(String userTime) {
        this.userTime = userTime;
    }

    public String getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(String ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public String getRatingDescription() {
        return ratingDescription;
    }

    public void setRatingDescription(String ratingDescription) {
        this.ratingDescription = ratingDescription;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getCommentAverage() {
        return commentAverage;
    }

    public void setCommentAverage(String commentAverage) {
        this.commentAverage = commentAverage;
    }

    public String getReservedCount() {
        return reservedCount;
    }

    public void setReservedCount(String reservedCount) {
        this.reservedCount = reservedCount;
    }

    public String getCommentLast() {
        return commentLast;
    }

    public void setCommentLast(String commentLast) {
        this.commentLast = commentLast;
    }

    public String getUserLastLogin() {
        return userLastLogin;
    }

    public void setUserLastLogin(String userLastLogin) {
        this.userLastLogin = userLastLogin;
    }
}
