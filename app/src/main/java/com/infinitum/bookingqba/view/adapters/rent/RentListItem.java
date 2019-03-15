package com.infinitum.bookingqba.view.adapters.rent;

import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class RentListItem extends BaseItem {

    private int idImage;
    private double mPrice;
    private String mAddress;
    private byte[] imageByte;

    public RentListItem(String id, String mName, int idImage, double mPrice, String mAddress, byte[] imageByte) {
        super(id, mName);
        this.idImage = idImage;
        this.mPrice = mPrice;
        this.mAddress = mAddress;
        this.imageByte = imageByte;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }

    public double getmPrice() {
        return mPrice;
    }

    public void setmPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public byte[] getImageByte() {
        return imageByte;
    }

    public void setImageByte(byte[] imageByte) {
        this.imageByte = imageByte;
    }
}
