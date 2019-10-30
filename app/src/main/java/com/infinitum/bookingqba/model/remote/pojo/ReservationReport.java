
package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReservationReport {

    @SerializedName("totalBusyDays")
    @Expose
    private Integer totalBusyDays;
    @SerializedName("totalPending")
    @Expose
    private Integer totalPending;
    @SerializedName("totalAccepted")
    @Expose
    private Integer totalAccepted;
    @SerializedName("totalCanceled")
    @Expose
    private Integer totalCanceled;

    public Integer getTotalBusyDays() {
        return totalBusyDays;
    }

    public void setTotalBusyDays(Integer totalBusyDays) {
        this.totalBusyDays = totalBusyDays;
    }

    public Integer getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(Integer totalPending) {
        this.totalPending = totalPending;
    }

    public Integer getTotalAccepted() {
        return totalAccepted;
    }

    public void setTotalAccepted(Integer totalAccepted) {
        this.totalAccepted = totalAccepted;
    }

    public Integer getTotalCanceled() {
        return totalCanceled;
    }

    public void setTotalCanceled(Integer totalCanceled) {
        this.totalCanceled = totalCanceled;
    }

}
