package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatabaseUpdate {

    @SerializedName("lastDatabaseUpdate")
    @Expose
    private String lastDatabaseUpdate;

    @SerializedName("totalRents")
    @Expose
    private int totalRents;

    public DatabaseUpdate(String lastDatabaseUpdate, int totalRents) {
        this.lastDatabaseUpdate = lastDatabaseUpdate;
        this.totalRents = totalRents;
    }

    public String getLastDatabaseUpdate() {
        return lastDatabaseUpdate;
    }

    public void setLastDatabaseUpdate(String lastDatabaseUpdate) {
        this.lastDatabaseUpdate = lastDatabaseUpdate;
    }

    public int getTotalRents() {
        return totalRents;
    }

    public void setTotalRents(int totalRents) {
        this.totalRents = totalRents;
    }
}
