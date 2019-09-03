package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DisabledDays {

    @SerializedName("dates")
    @Expose
    private List<String> dates;

    public DisabledDays(List<String> dates) {
        this.dates = dates;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }
}
