package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Rent {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("direccion")
    @Expose
    private String direccion;
    @SerializedName("eslogan")
    @Expose
    private String eslogan;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("correo")
    @Expose
    private String correo;
    @SerializedName("telcasa")
    @Expose
    private String telcasa;
    @SerializedName("telmovil")
    @Expose
    private String telmovil;
    @SerializedName("latitud")
    @Expose
    private String latitud;
    @SerializedName("longitud")
    @Expose
    private String longitud;
    @SerializedName("rating")
    @Expose
    private Float rating;
    @SerializedName("cant_habitaciones")
    @Expose
    private Integer cantHabitaciones;
    @SerializedName("capacidad")
    @Expose
    private Integer capacidad;
    @SerializedName("cant_camas")
    @Expose
    private Integer cantCamas;
    @SerializedName("cant_bannos")
    @Expose
    private Integer cantBannos;
    @SerializedName("precio")
    @Expose
    private Double precio;
    @SerializedName("modalidad_renta")
    @Expose
    private String modalidadRenta;
    @SerializedName("normas")
    @Expose
    private String normas;
    @SerializedName("municipio")
    @Expose
    private String municipio;
    @SerializedName("zona_referencia")
    @Expose
    private String zonaReferencia;
    @SerializedName("fecha_creado")
    @Expose
    private String fechaCreado;
    @SerializedName("fecha_modificado")
    @Expose
    private String fechaModificado;
    @SerializedName("facilidades")


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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEslogan() {
        return eslogan;
    }

    public void setEslogan(String eslogan) {
        this.eslogan = eslogan;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelcasa() {
        return telcasa;
    }

    public void setTelcasa(String telcasa) {
        this.telcasa = telcasa;
    }

    public String getTelmovil() {
        return telmovil;
    }

    public void setTelmovil(String telmovil) {
        this.telmovil = telmovil;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public Integer getCantHabitaciones() {
        return cantHabitaciones;
    }

    public void setCantHabitaciones(Integer cantHabitaciones) {
        this.cantHabitaciones = cantHabitaciones;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Integer getCantCamas() {
        return cantCamas;
    }

    public void setCantCamas(Integer cantCamas) {
        this.cantCamas = cantCamas;
    }

    public Integer getCantBannos() {
        return cantBannos;
    }

    public void setCantBannos(Integer cantBannos) {
        this.cantBannos = cantBannos;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getModalidadRenta() {
        return modalidadRenta;
    }

    public void setModalidadRenta(String modalidadRenta) {
        this.modalidadRenta = modalidadRenta;
    }

    public String getNormas() {
        return normas;
    }

    public void setNormas(String normas) {
        this.normas = normas;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getZonaReferencia() {
        return zonaReferencia;
    }

    public void setZonaReferencia(String zonaReferencia) {
        this.zonaReferencia = zonaReferencia;
    }

    public String getFechaCreado() {
        return fechaCreado;
    }

    public void setFechaCreado(String fechaCreado) {
        this.fechaCreado = fechaCreado;
    }

    public String getFechaModificado() {
        return fechaModificado;
    }

    public void setFechaModificado(String fechaModificado) {
        this.fechaModificado = fechaModificado;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
