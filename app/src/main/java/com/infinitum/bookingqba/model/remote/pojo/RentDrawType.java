package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentDrawType {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("tipo_moneda")
    @Expose
    private List<DrawType> tipo_monedas;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DrawType> getTipo_monedas() {
        return tipo_monedas;
    }

    public void setTipo_monedas(List<DrawType> tipo_monedas) {
        this.tipo_monedas = tipo_monedas;
    }
}
