package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfilePercentAnalitics {

    @SerializedName("percent")
    @Expose
    private float percent;

    @SerializedName("missing_list")
    @Expose
    private List<String> missingList;

    public ProfilePercentAnalitics(float percent, List<String> missingList) {
        this.percent = percent;
        this.missingList = missingList;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public List<String> getMissingList() {
        return missingList;
    }

    public void setMissingList(List<String> missingList) {
        this.missingList = missingList;
    }
}
