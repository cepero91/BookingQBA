package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralCommentAnalitics {

    @SerializedName("total_comment")
    @Expose
    private int totalComment;

    @SerializedName("total_year")
    @Expose
    private int totalYear;

    @SerializedName("total_month")
    @Expose
    private int totalMont;

    @SerializedName("total_day")
    @Expose
    private int totalDay;

    public GeneralCommentAnalitics(int totalComment, int totalYear, int totalMont, int totalDay) {
        this.totalComment = totalComment;
        this.totalYear = totalYear;
        this.totalMont = totalMont;
        this.totalDay = totalDay;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public int getTotalYear() {
        return totalYear;
    }

    public void setTotalYear(int totalYear) {
        this.totalYear = totalYear;
    }

    public int getTotalMont() {
        return totalMont;
    }

    public void setTotalMont(int totalMont) {
        this.totalMont = totalMont;
    }

    public int getTotalDay() {
        return totalDay;
    }

    public void setTotalDay(int totalDay) {
        this.totalDay = totalDay;
    }
}