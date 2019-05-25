package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WishAnalitics {

    @SerializedName("total_wish")
    @Expose
    private int totalWish;

    @SerializedName("total_year")
    @Expose
    private int totalYear;

    @SerializedName("total_month")
    @Expose
    private int totalMonth;

    @SerializedName("total_day")
    @Expose
    private int totalDay;

    public WishAnalitics(int totalWish, int totalYear, int totalMonth, int totalDay) {
        this.totalWish = totalWish;
        this.totalYear = totalYear;
        this.totalMonth = totalMonth;
        this.totalDay = totalDay;
    }

    public int getTotalWish() {
        return totalWish;
    }

    public void setTotalWish(int totalWish) {
        this.totalWish = totalWish;
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
