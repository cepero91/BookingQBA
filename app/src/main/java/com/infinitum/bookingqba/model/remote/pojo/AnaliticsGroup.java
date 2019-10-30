package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnaliticsGroup {

    @SerializedName("CommentReport")
    @Expose
    private CommentReport commentReport;

    @SerializedName("EmotionReport")
    @Expose
    private EmotionReport emotionReport;

    @SerializedName("RatingReport")
    @Expose
    private RatingReport ratingReport;

    @SerializedName("VisitReport")
    @Expose
    private VisitReport visitReport;

    @SerializedName("WishReport")
    @Expose
    private WishReport wishReport;

    @SerializedName("PositionReport")
    @Expose
    private PositionReport positionReport;

    @SerializedName("RevenueReport")
    @Expose
    private RevenueReport revenueReport;

    @SerializedName("ReservationReport")
    @Expose
    private ReservationReport reservationReport;

    public AnaliticsGroup(CommentReport commentReport, EmotionReport emotionReport, RatingReport ratingReport, VisitReport visitReport, WishReport wishReport, PositionReport positionReport, RevenueReport revenueReport, ReservationReport reservationReport) {
        this.commentReport = commentReport;
        this.emotionReport = emotionReport;
        this.ratingReport = ratingReport;
        this.visitReport = visitReport;
        this.wishReport = wishReport;
        this.positionReport = positionReport;
        this.revenueReport = revenueReport;
        this.reservationReport = reservationReport;
    }

    public CommentReport getCommentReport() {
        return commentReport;
    }

    public void setCommentReport(CommentReport commentReport) {
        this.commentReport = commentReport;
    }

    public EmotionReport getEmotionReport() {
        return emotionReport;
    }

    public void setEmotionReport(EmotionReport emotionReport) {
        this.emotionReport = emotionReport;
    }

    public RatingReport getRatingReport() {
        return ratingReport;
    }

    public void setRatingReport(RatingReport ratingReport) {
        this.ratingReport = ratingReport;
    }

    public VisitReport getVisitReport() {
        return visitReport;
    }

    public void setVisitReport(VisitReport visitReport) {
        this.visitReport = visitReport;
    }

    public WishReport getWishReport() {
        return wishReport;
    }

    public void setWishReport(WishReport wishReport) {
        this.wishReport = wishReport;
    }

    public PositionReport getPositionReport() {
        return positionReport;
    }

    public void setPositionReport(PositionReport positionReport) {
        this.positionReport = positionReport;
    }

    public RevenueReport getRevenueReport() {
        return revenueReport;
    }

    public void setRevenueReport(RevenueReport revenueReport) {
        this.revenueReport = revenueReport;
    }

    public ReservationReport getReservationReport() {
        return reservationReport;
    }

    public void setReservationReport(ReservationReport reservationReport) {
        this.reservationReport = reservationReport;
    }
}
