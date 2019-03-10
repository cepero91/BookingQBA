package com.infinitum.bookingqba.view.adapters.rzoneitem;

import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RZoneItem extends BaseItem{

    private int idImage;

    public RZoneItem(int id, String mName, int idImage) {
        super(id, mName);
        this.idImage = idImage;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }
}
