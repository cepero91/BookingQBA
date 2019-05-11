package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import com.infinitum.bookingqba.model.local.entity.RentEntity;

import java.util.ArrayList;

public class RentItem {

    private RentEntity rentEntity;

    private ArrayList<RentGalerieItem> galerieItems;

    private ArrayList<RentCommentItem> commentItems;

    private ArrayList<RentPoiItem> poiItems;

    private ArrayList<RentAmenitieItem> amenitieItems;

    private String rentModeName;

    public RentItem(RentEntity rentEntity) {
        this.rentEntity = rentEntity;
    }

    public RentEntity getRentEntity() {
        return rentEntity;
    }

    public void setRentEntity(RentEntity rentEntity) {
        this.rentEntity = rentEntity;
    }

    public ArrayList<RentGalerieItem> getGalerieItems() {
        return galerieItems;
    }

    public ArrayList<RentCommentItem> getCommentItems() {
        return commentItems;
    }

    public void setCommentItems(ArrayList<RentCommentItem> commentItems) {
        this.commentItems = commentItems;
    }

    public void setGalerieItems(ArrayList<RentGalerieItem> galerieItems) {
        this.galerieItems = galerieItems;
    }

    public ArrayList<RentPoiItem> getPoiItems() {
        return poiItems;
    }

    public void setPoiItems(ArrayList<RentPoiItem> poiItems) {
        this.poiItems = poiItems;
    }

    public ArrayList<RentAmenitieItem> getAmenitieItems() {
        return amenitieItems;
    }

    public void setAmenitieItems(ArrayList<RentAmenitieItem> amenitieItems) {
        this.amenitieItems = amenitieItems;
    }

    public String getRentModeName() {
        return rentModeName;
    }

    public void setRentModeName(String rentModeName) {
        this.rentModeName = rentModeName;
    }

    public String getFirstImage(){
        return galerieItems.get(0).getImage();
    }

    public int galerieSize(){
        if(galerieItems!=null){
            return galerieItems.size();
        }else{
            return 0;
        }
    }
}
