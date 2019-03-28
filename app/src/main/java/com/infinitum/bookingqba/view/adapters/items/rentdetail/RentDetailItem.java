package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import com.infinitum.bookingqba.model.local.entity.RentEntity;

import java.util.ArrayList;
import java.util.List;

public class RentDetailItem {

    private RentEntity rentEntity;

    private ArrayList<RentDetailGalerieItem> galerieItems;

    private ArrayList<RentDetailPoiItem> poiItems;

    private ArrayList<RentDetailAmenitieItem> amenitieItems;

    private String rentModeName;

    public RentDetailItem(RentEntity rentEntity) {
        this.rentEntity = rentEntity;
    }

    public RentEntity getRentEntity() {
        return rentEntity;
    }

    public void setRentEntity(RentEntity rentEntity) {
        this.rentEntity = rentEntity;
    }

    public ArrayList<RentDetailGalerieItem> getGalerieItems() {
        return galerieItems;
    }

    public void setGalerieItems(ArrayList<RentDetailGalerieItem> galerieItems) {
        this.galerieItems = galerieItems;
    }

    public ArrayList<RentDetailPoiItem> getPoiItems() {
        return poiItems;
    }

    public void setPoiItems(ArrayList<RentDetailPoiItem> poiItems) {
        this.poiItems = poiItems;
    }

    public ArrayList<RentDetailAmenitieItem> getAmenitieItems() {
        return amenitieItems;
    }

    public void setAmenitieItems(ArrayList<RentDetailAmenitieItem> amenitieItems) {
        this.amenitieItems = amenitieItems;
    }

    public String getRentModeName() {
        return rentModeName;
    }

    public void setRentModeName(String rentModeName) {
        this.rentModeName = rentModeName;
    }

    public byte[] getFirstImage(){
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
