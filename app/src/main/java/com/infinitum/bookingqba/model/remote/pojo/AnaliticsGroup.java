package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnaliticsGroup {

    @SerializedName("GeneralCommentAnalitics")
    @Expose
    private GeneralCommentAnalitics generalCommentAnalitics;

    @SerializedName("CommentsEmotionAnalitics")
    @Expose
    private CommentsEmotionAnalitics commentsEmotionAnalitics;

    @SerializedName("RatingStarAnalitics")
    @Expose
    private RatingStarAnalitics ratingStarAnalitics;

    @SerializedName("ProfilePercentAnalitics")
    @Expose
    private ProfilePercentAnalitics profilePercentAnalitics;

    @SerializedName("VisitAnalitics")
    @Expose
    private VisitAnalitics visitAnalitics;

    @SerializedName("WishAnalitics")
    @Expose
    private WishAnalitics wishAnalitics;

    @SerializedName("RentPositionAnalitics")
    @Expose
    private RentPositionAnalitics rentPositionAnalitics;

    public AnaliticsGroup(GeneralCommentAnalitics generalCommentAnalitics, CommentsEmotionAnalitics commentsEmotionAnalitics, RatingStarAnalitics ratingStarAnalitics, ProfilePercentAnalitics profilePercentAnalitics, VisitAnalitics visitAnalitics, WishAnalitics wishAnalitics, RentPositionAnalitics rentPositionAnalitics) {
        this.generalCommentAnalitics = generalCommentAnalitics;
        this.commentsEmotionAnalitics = commentsEmotionAnalitics;
        this.ratingStarAnalitics = ratingStarAnalitics;
        this.profilePercentAnalitics = profilePercentAnalitics;
        this.visitAnalitics = visitAnalitics;
        this.wishAnalitics = wishAnalitics;
        this.rentPositionAnalitics = rentPositionAnalitics;
    }

    public GeneralCommentAnalitics getGeneralCommentAnalitics() {
        return generalCommentAnalitics;
    }

    public void setGeneralCommentAnalitics(GeneralCommentAnalitics generalCommentAnalitics) {
        this.generalCommentAnalitics = generalCommentAnalitics;
    }

    public CommentsEmotionAnalitics getCommentsEmotionAnalitics() {
        return commentsEmotionAnalitics;
    }

    public void setCommentsEmotionAnalitics(CommentsEmotionAnalitics commentsEmotionAnalitics) {
        this.commentsEmotionAnalitics = commentsEmotionAnalitics;
    }

    public RatingStarAnalitics getRatingStarAnalitics() {
        return ratingStarAnalitics;
    }

    public void setRatingStarAnalitics(RatingStarAnalitics ratingStarAnalitics) {
        this.ratingStarAnalitics = ratingStarAnalitics;
    }

    public ProfilePercentAnalitics getProfilePercentAnalitics() {
        return profilePercentAnalitics;
    }

    public void setProfilePercentAnalitics(ProfilePercentAnalitics profilePercentAnalitics) {
        this.profilePercentAnalitics = profilePercentAnalitics;
    }

    public VisitAnalitics getVisitAnalitics() {
        return visitAnalitics;
    }

    public void setVisitAnalitics(VisitAnalitics visitAnalitics) {
        this.visitAnalitics = visitAnalitics;
    }

    public WishAnalitics getWishAnalitics() {
        return wishAnalitics;
    }

    public void setWishAnalitics(WishAnalitics wishAnalitics) {
        this.wishAnalitics = wishAnalitics;
    }

    public RentPositionAnalitics getRentPositionAnalitics() {
        return rentPositionAnalitics;
    }

    public void setRentPositionAnalitics(RentPositionAnalitics rentPositionAnalitics) {
        this.rentPositionAnalitics = rentPositionAnalitics;
    }
}
