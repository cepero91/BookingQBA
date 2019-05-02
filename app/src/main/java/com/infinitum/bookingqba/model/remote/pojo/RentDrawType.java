package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentDrawType {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("drawType")
    @Expose
    private List<String> drawTypes;

    public RentDrawType(String id, List<String> drawTypes) {
        this.id = id;
        this.drawTypes = drawTypes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getDrawTypes() {
        return drawTypes;
    }

    public void setDrawTypes(List<String> drawTypes) {
        this.drawTypes = drawTypes;
    }
}
