package com.infinitum.bookingqba.view.adapters.home;

import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RZoneItem extends BaseItem{

    private byte[] mImage;
    private int idImage;

    public RZoneItem(String id, String mName, byte[] mImage) {
        super(id, mName);
        this.mImage = mImage;
    }

    public RZoneItem(String id, String mName, int idImage) {
        super(id, mName);
        this.idImage = idImage;
    }

    public byte[] getmImage() {
        return mImage;
    }

    public void setmImage(byte[] mImage) {
        this.mImage = mImage;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }
}
