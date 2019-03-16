package com.infinitum.bookingqba.view.adapters.home;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RentPopItem extends BaseItem {

    private byte[] mImage;
    private float rating;

    public RentPopItem(String id, String mName, byte[] mImage, float rating) {
        super(id, mName);
        this.mImage = mImage;
        this.rating = rating;
    }

    public byte[] getmImage() {
        return mImage;
    }

    public void setmImage(byte[] mImage) {
        this.mImage = mImage;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
