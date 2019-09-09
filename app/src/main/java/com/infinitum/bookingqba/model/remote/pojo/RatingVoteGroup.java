package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RatingVoteGroup {

    @SerializedName("ratingVotes")
    @Expose
    private List<RatingVote> ratingVotes;

    public RatingVoteGroup() {
        this.ratingVotes = new ArrayList<>();
    }

    public List<RatingVote> getRatingVotes() {
        return ratingVotes;
    }

    public void setRatingVotes(List<RatingVote> ratingVotes) {
        this.ratingVotes = ratingVotes;
    }

    public void addRatingVote(RatingVote ratingVote) {
        this.ratingVotes.add(ratingVote);
    }
}
