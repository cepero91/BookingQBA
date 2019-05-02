package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RemovedItem {

    @SerializedName("uui")
    @Expose
    private String uui;

    public RemovedItem(String uui) {
        this.uui = uui;
    }

    public String getUui() {
        return uui;
    }

    public void setUui(String uui) {
        this.uui = uui;
    }
}
