package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SyncData {

    @SerializedName("rents")
    @Expose
    private int rents;

    @SerializedName("size")
    @Expose
    private float resourceSize;

    public int getRents() {
        return rents;
    }

    public void setRents(int rents) {
        this.rents = rents;
    }

    public float getResourceSize() {
        return resourceSize;
    }

    public void setResourceSize(float resourceSize) {
        this.resourceSize = resourceSize;
    }
}
