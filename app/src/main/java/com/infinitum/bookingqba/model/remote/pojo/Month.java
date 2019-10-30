
package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;

public class Month {

    @SerializedName("revenue")
    @Expose
    private Integer revenue;
    @SerializedName("month")
    @Expose
    private String month;
    @SerializedName("reservation_completed")
    @Expose
    private Integer reservationCompleted;

    public Integer getRevenue() {
        return revenue;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getReservationCompleted() {
        return reservationCompleted;
    }

    public void setReservationCompleted(Integer reservationCompleted) {
        this.reservationCompleted = reservationCompleted;
    }

}
