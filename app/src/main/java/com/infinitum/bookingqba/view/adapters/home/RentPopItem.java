package com.infinitum.bookingqba.view.adapters.home;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RentPopItem extends BaseItem {

    private byte[] mImage;

    public RentPopItem(String id, String mName, byte[] mImage) {
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
