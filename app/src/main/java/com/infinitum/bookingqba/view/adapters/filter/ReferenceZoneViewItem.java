package com.infinitum.bookingqba.view.adapters.filter;

import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

public class ReferenceZoneViewItem extends CheckableItem {

    private byte[] byteImage;

    public ReferenceZoneViewItem(String id, String mName, boolean checked, byte[] byteImage) {
        super(id, mName, checked);
        this.byteImage = byteImage;
    }

    public byte[] getByteImage() {
        return byteImage;
    }

    public void setByteImage(byte[] byteImage) {
        this.byteImage = byteImage;
    }
}
