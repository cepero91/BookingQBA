package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingEmbeded {

    @SerializedName("average")
    @Expose
    private float average;

    @SerializedName("count")
    @Expose
    private int count;

    public RatingEmbeded(float average, int count) {
        this.average = average;
        this.count = count;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
