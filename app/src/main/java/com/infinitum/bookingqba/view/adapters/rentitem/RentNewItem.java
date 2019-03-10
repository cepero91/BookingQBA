package com.infinitum.bookingqba.view.adapters.rentitem;

import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RentNewItem extends BaseItem {

    private int idImage;

    public RentNewItem(int id, String mName, int idImage) {
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
