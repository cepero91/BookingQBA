package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RatingVote {

    @SerializedName("rent")
    @Expose
    private String rent;

    @SerializedName("rating")
    @Expose
    private float rating;

    @SerializedName("comment")
    @Expose
    private String comment;

    public RatingVote(String rent, float rating, String comment) {
        this.rent = rent;
        this.rating = rating;
        this.comment = comment;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
