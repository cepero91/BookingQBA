package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingReport {

    @SerializedName("total_votes")
    @Expose
    private int totalVotes;

    @SerializedName("rating_average")
    @Expose
    private float ratingAverage;

    @SerializedName("five_star")
    @Expose
    private int fiveStar;

    @SerializedName("four_star")
    @Expose
    private int fourStar;

    @SerializedName("three_star")
    @Expose
    private int threeStar;

    @SerializedName("two_star")
    @Expose
    private int twoStar;

    @SerializedName("one_star")
    @Expose
    private int oneStar;

    public RatingReport(int totalVotes, float ratingAverage, int fiveStar, int fourStar, int threeStar, int twoStar, int oneStar) {
        this.totalVotes = totalVotes;
        this.ratingAverage = ratingAverage;
        this.fiveStar = fiveStar;
        this.fourStar = fourStar;
        this.threeStar = threeStar;
        this.twoStar = twoStar;
        this.oneStar = oneStar;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public float getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(float ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public int getFiveStar() {
        return fiveStar;
    }

    public void setFiveStar(int fiveStar) {
        this.fiveStar = fiveStar;
    }

    public int getFourStar() {
        return fourStar;
    }

    public void setFourStar(int fourStar) {
        this.fourStar = fourStar;
    }

    public int getThreeStar() {
        return threeStar;
    }

    public void setThreeStar(int threeStar) {
        this.threeStar = threeStar;
    }

    public int getTwoStar() {
        return twoStar;
    }

    public void setTwoStar(int twoStar) {
        this.twoStar = twoStar;
    }

    public int getOneStar() {
        return oneStar;
    }

    public void setOneStar(int oneStar) {
        this.oneStar = oneStar;
    }
}
