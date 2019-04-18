package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentAnalitics {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("totalVisitCount")
    @Expose
    private String totalVisitCount;

    @SerializedName("totalListWish")
    @Expose
    private String totalListWish;

    @SerializedName("rentDetailPercent")
    @Expose
    private String rentDetailPercent;

    @SerializedName("comments")
    @Expose
    private List<Comment> comments;

    public RentAnalitics(String id, String totalVisitCount, String totalListWish, String rentDetailPercent, List<Comment> comments) {
        this.id = id;
        this.totalVisitCount = totalVisitCount;
        this.totalListWish = totalListWish;
        this.rentDetailPercent = rentDetailPercent;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotalVisitCount() {
        return totalVisitCount;
    }

    public void setTotalVisitCount(String totalVisitCount) {
        this.totalVisitCount = totalVisitCount;
    }

    public String getTotalListWish() {
        return totalListWish;
    }

    public void setTotalListWish(String totalListWish) {
        this.totalListWish = totalListWish;
    }

    public String getRentDetailPercent() {
        return rentDetailPercent;
    }

    public void setRentDetailPercent(String rentDetailPercent) {
        this.rentDetailPercent = rentDetailPercent;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
