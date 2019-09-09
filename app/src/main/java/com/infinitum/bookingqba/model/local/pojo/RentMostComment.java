package com.infinitum.bookingqba.model.local.pojo;

import android.arch.persistence.room.Relation;

import com.infinitum.bookingqba.model.local.entity.GalerieEntity;
import com.infinitum.bookingqba.model.local.entity.RentModeEntity;

import java.util.List;
import java.util.Set;

public class RentMostComment {

    private String id;
    private String name;
    private double price;
    private String rentMode;
    private float rating;
    private int ratingCount;
    private int totalComment;
    private float emotionAvg;

    @Relation(parentColumn = "rentMode", entityColumn = "id",entity = RentModeEntity.class,projection = {"name"})
    private Set<String> rentModeSet;

    @Relation(parentColumn = "id", entityColumn = "rent")
    private List<GalerieEntity> galeries;

    public String getImageAtPos(int pos){
        String url = "";
        if(galeries.size() > 0) {
            url = galeries.get(pos).getImageLocalPath()!=null?galeries.get(pos).getImageLocalPath():galeries.get(pos).getImageUrl();
        }
        return url;
    }

    public RentMostComment(String id, String name, double price, String rentMode, float rating, int ratingCount, int totalComment, float emotionAvg) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rentMode = rentMode;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.totalComment = totalComment;
        this.emotionAvg = emotionAvg;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRentMode() {
        return rentModeSet.toArray()[0].toString();
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public float getEmotionAvg() {
        return emotionAvg;
    }

    public void setEmotionAvg(float emotionAvg) {
        this.emotionAvg = emotionAvg;
    }

    public Set<String> getRentModeSet() {
        return rentModeSet;
    }

    public void setRentModeSet(Set<String> rentModeSet) {
        this.rentModeSet = rentModeSet;
    }

    public List<GalerieEntity> getGaleries() {
        return galeries;
    }

    public void setGaleries(List<GalerieEntity> galeries) {
        this.galeries = galeries;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
