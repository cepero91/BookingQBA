package com.infinitum.bookingqba.view.adapters.rentlist;

import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RentListItem extends BaseItem {

    private int idImage;
    private int mPrice;
    private String mAddress;

    public RentListItem(int id, String mName, int idImage, int mPrice, String mAddress) {
        super(id, mName);
        this.idImage = idImage;
        this.mPrice = mPrice;
        this.mAddress = mAddress;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }

    public int getmPrice() {
        return mPrice;
    }

    public void setmPrice(int mPrice) {
        this.mPrice = mPrice;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }
}
