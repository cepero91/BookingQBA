package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class RemovedList {

    @SerializedName("entity")
    @Expose
    private String entity;

    @SerializedName("items")
    @Expose
    private List<RemovedItem> items;

    public RemovedList(String entity, List<RemovedItem> items) {
        this.entity = entity;
        this.items = items;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public List<RemovedItem> getItems() {
        return items;
    }

    public void setItems(List<RemovedItem> items) {
        this.items = items;
    }
}
