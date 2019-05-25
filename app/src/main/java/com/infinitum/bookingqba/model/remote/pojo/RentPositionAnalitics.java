package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RentPositionAnalitics {

    @SerializedName("place_rating")
    @Expose
    private int placeRating;

    @SerializedName("place_views")
    @Expose
    private int placeViews;

    public RentPositionAnalitics(int placeRating, int placeViews) {
        this.placeRating = placeRating;
        this.placeViews = placeViews;
    }

    public int getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(int placeRating) {
        this.placeRating = placeRating;
    }

    public int getPlaceViews() {
        return placeViews;
    }

    public void setPlaceViews(int placeViews) {
        this.placeViews = placeViews;
    }
}
