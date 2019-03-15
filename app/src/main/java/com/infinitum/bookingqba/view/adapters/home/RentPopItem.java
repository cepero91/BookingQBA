package com.infinitum.bookingqba.view.adapters.home;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RentPopItem extends BaseItem {

    private int idImage;
    private byte[] mImage;

    public RentPopItem(String id, String mName, int idImage) {
        super(id, mName);
        this.idImage = idImage;
    }

    public RentPopItem(String id, String mName, byte[] mImage) {
        super(id, mName);
        this.mImage = mImage;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }

    public byte[] getmImage() {
        return mImage;
    }

    public void setmImage(byte[] mImage) {
        this.mImage = mImage;
    }
}
