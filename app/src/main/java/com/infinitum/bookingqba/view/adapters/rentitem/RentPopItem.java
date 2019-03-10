package com.infinitum.bookingqba.view.adapters.rentitem;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RentPopItem extends BaseItem {

    private int idImage;

    public RentPopItem(int id, String mName, int idImage) {
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
