package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentPoi {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lugares_interes")
    @Expose
    private List<Poi> lugar_interes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Poi> getLugar_interes() {
        return lugar_interes;
    }

    public void setLugar_interes(List<Poi> lugar_interes) {
        this.lugar_interes = lugar_interes;
    }
}
