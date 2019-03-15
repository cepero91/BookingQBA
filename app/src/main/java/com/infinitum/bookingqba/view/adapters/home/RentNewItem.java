package com.infinitum.bookingqba.view.adapters.home;

import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RentNewItem extends BaseItem {

    private byte[] mImage;

    public RentNewItem(String id, String mName, byte[] mImage) {
        super(id, mName);
        this.mImage = mImage;
    }

    public byte[] getmImage() {
        return mImage;
    }

    public void setmImage(byte[] mImage) {
        this.mImage = mImage;
    }
}
