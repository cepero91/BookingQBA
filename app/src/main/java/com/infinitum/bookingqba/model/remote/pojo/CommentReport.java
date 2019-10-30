package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentReport {

    @SerializedName("total_comment")
    @Expose
    private int totalComment;

    @SerializedName("total_year")
    @Expose
    private int totalYear;

    @SerializedName("total_month")
    @Expose
    private int totalMonth;

    @SerializedName("total_day")
    @Expose
    private int totalDay;

    public CommentReport(int totalComment, int totalYear, int totalMonth, int totalDay) {
        this.totalComment = totalComment;
        this.totalYear = totalYear;
        this.totalMonth = totalMonth;
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

    public int getTotalMonth() {
        return totalMonth;
    }

    public void setTotalMonth(int totalMonth) {
        this.totalMonth = totalMonth;
    }

    public int getTotalDay() {
        return totalDay;
    }

    public void setTotalDay(int totalDay) {
        this.totalDay = totalDay;
    }
}
