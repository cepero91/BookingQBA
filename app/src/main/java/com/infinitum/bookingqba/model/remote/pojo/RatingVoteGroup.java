package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RatingVoteGroup {

    @SerializedName("imei")
    @Expose
    private String imei;

    @SerializedName("ratingVotes")
    @Expose
    private List<RatingVote> ratingVotes;

    public RatingVoteGroup(String imei, List<RatingVote> ratingVotes) {
        this.imei = imei;
        this.ratingVotes = ratingVotes;
    }

    public RatingVoteGroup(String imei) {
        this.imei = imei;
        this.ratingVotes = new ArrayList<>();
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<RatingVote> getRatingVotes() {
        return ratingVotes;
    }

    public void setRatingVotes(List<RatingVote> ratingVotes) {
        this.ratingVotes = ratingVotes;
    }

    public void addRatingVote(RatingVote ratingVote){
        this.ratingVotes.add(ratingVote);
    }
}
