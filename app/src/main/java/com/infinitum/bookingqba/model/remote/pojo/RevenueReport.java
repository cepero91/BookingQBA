
package com.infinitum.bookingqba.model.remote.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RevenueReport {

    @SerializedName("months")
    @Expose
    private List<Month> months = null;
    @SerializedName("total_reservation_completed")
    @Expose
    private Integer totalReservationCompleted;
    @SerializedName("total_revenue")
    @Expose
    private Integer totalRevenue;

    public List<Month> getMonths() {
        return months;
    }

    public void setMonths(List<Month> months) {
        this.months = months;
    }

    public Integer getTotalReservationCompleted() {
        return totalReservationCompleted;
    }

    public void setTotalReservationCompleted(Integer totalReservationCompleted) {
        this.totalReservationCompleted = totalReservationCompleted;
    }

    public Integer getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Integer totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

}
