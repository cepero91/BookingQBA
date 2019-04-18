package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentAnalitics {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("rentName")
    @Expose
    private String rentName;

    @SerializedName("totalVisitCount")
    @Expose
    private int totalVisitCount;

    @SerializedName("totalComments")
    @Expose
    private int totalComments;

    @SerializedName("totalListWish")
    @Expose
    private int totalListWish;

    @SerializedName("rentDetailPercent")
    @Expose
    private float rentDetailPercent;

    @SerializedName("rating")
    @Expose
    private float rating;


    @SerializedName("ratingByStar")
    @Expose
    private int[] ratingByStar;

    public RentAnalitics(String id) {
        this.id = id;
    }

    public RentAnalitics(String id, String rentName, int totalVisitCount, int totalComments, int totalListWish, float rentDetailPercent, float rating, int[] ratingByStar) {
        this.id = id;
        this.rentName = rentName;
        this.totalVisitCount = totalVisitCount;
        this.totalComments = totalComments;
        this.totalListWish = totalListWish;
        this.rentDetailPercent = rentDetailPercent;
        this.rating = rating;
        this.ratingByStar = ratingByStar;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRentName() {
        return rentName;
    }

    public void setRentName(String rentName) {
        this.rentName = rentName;
    }

    public int getTotalVisitCount() {
        return totalVisitCount;
    }

    public void setTotalVisitCount(int totalVisitCount) {
        this.totalVisitCount = totalVisitCount;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public int getTotalListWish() {
        return totalListWish;
    }

    public void setTotalListWish(int totalListWish) {
        this.totalListWish = totalListWish;
    }

    public float getRentDetailPercent() {
        return rentDetailPercent;
    }

    public void setRentDetailPercent(float rentDetailPercent) {
        this.rentDetailPercent = rentDetailPercent;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int[] getRatingByStar() {
        return ratingByStar;
    }

    public void setRatingByStar(int[] ratingByStar) {
        this.ratingByStar = ratingByStar;
    }
}
