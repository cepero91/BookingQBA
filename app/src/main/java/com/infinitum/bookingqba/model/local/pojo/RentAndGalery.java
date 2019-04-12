package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.remote.pojo.Galerie;

import java.util.ArrayList;
import java.util.List;

public class RentAndGalery {

    private String id;
    private String name;
    private String address;
    private double price;
    private float rating;

    @Relation(parentColumn = "id", entityColumn = "rent")
    private List<GalerieEntity> galeries;

    public RentAndGalery(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public RentAndGalery(String id, String name, String address, double price, float rating, List<GalerieEntity> galeries) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.price = price;
        this.rating = rating;
        this.galeries = galeries;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GalerieEntity> getGaleries() {
        return galeries;
    }

    public void setGaleries(List<GalerieEntity> galeries) {
        this.galeries = galeries;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public GalerieEntity getGalerieAtPos(int pos){
        return galeries.get(pos);
    }
}
