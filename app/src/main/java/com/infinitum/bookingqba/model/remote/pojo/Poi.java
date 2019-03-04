package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Poi {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("tipo_lugar_interes")
    @Expose
    private String tipoLugarInteres;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoLugarInteres() {
        return tipoLugarInteres;
    }

    public void setTipoLugarInteres(String tipoLugarInteres) {
        this.tipoLugarInteres = tipoLugarInteres;
    }
}
