package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PositionReport {

    @SerializedName("total_national_rent")
    @Expose
    private int totalNationalRent;

    @SerializedName("total_province_rent")
    @Expose
    private int totalProvinceRent;

    @SerializedName("place_national_rating")
    @Expose
    private int placeNationalRating;

    @SerializedName("place_province_rating")
    @Expose
    private int placeProvinceRating;

    @SerializedName("place_national_views")
    @Expose
    private int placeNationalViews;

    @SerializedName("place_province_views")
    @Expose
    private int placeProvinceViews;

    public PositionReport(int totalNationalRent, int totalProvinceRent, int placeNationalRating, int placeProvinceRating, int placeNationalViews, int placeProvinceViews) {
        this.totalNationalRent = totalNationalRent;
        this.totalProvinceRent = totalProvinceRent;
        this.placeNationalRating = placeNationalRating;
        this.placeProvinceRating = placeProvinceRating;
        this.placeNationalViews = placeNationalViews;
        this.placeProvinceViews = placeProvinceViews;
    }

    public int getTotalNationalRent() {
        return totalNationalRent;
    }

    public void setTotalNationalRent(int totalNationalRent) {
        this.totalNationalRent = totalNationalRent;
    }

    public int getTotalProvinceRent() {
        return totalProvinceRent;
    }

    public void setTotalProvinceRent(int totalProvinceRent) {
        this.totalProvinceRent = totalProvinceRent;
    }

    public int getPlaceNationalRating() {
        return placeNationalRating;
    }

    public void setPlaceNationalRating(int placeNationalRating) {
        this.placeNationalRating = placeNationalRating;
    }

    public int getPlaceProvinceRating() {
        return placeProvinceRating;
    }

    public void setPlaceProvinceRating(int placeProvinceRating) {
        this.placeProvinceRating = placeProvinceRating;
    }

    public int getPlaceNationalViews() {
        return placeNationalViews;
    }

    public void setPlaceNationalViews(int placeNationalViews) {
        this.placeNationalViews = placeNationalViews;
    }

    public int getPlaceProvinceViews() {
        return placeProvinceViews;
    }

    public void setPlaceProvinceViews(int placeProvinceViews) {
        this.placeProvinceViews = placeProvinceViews;
    }
}
