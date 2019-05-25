package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitAnalitics {

    @SerializedName("total_visit")
    @Expose
    private int totalVisit;

    @SerializedName("total_year")
    @Expose
    private int totalYear;

    @SerializedName("total_month")
    @Expose
    private int totalMonth;

    @SerializedName("total_day")
    @Expose
    private int totalDay;

    public VisitAnalitics(int totalVisit, int totalYear, int totalMonth, int totalDay) {
        this.totalVisit = totalVisit;
        this.totalYear = totalYear;
        this.totalMonth = totalMonth;
        this.totalDay = totalDay;
    }

    public int getTotalVisit() {
        return totalVisit;
    }

    public void setTotalVisit(int totalVisit) {
        this.totalVisit = totalVisit;
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
