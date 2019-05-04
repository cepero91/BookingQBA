package com.infinitum.bookingqba.view.adapters.items.home;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RZoneItem implements ViewModel{

    private String id;
    private String name;
    private byte[] imageByte;

    public RZoneItem(String id, String name, byte[] imageByte) {
        this.id = id;
        this.name = name;
        this.imageByte = imageByte;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImageByte() {
        return imageByte;
    }

    public void setImageByte(byte[] imageByte) {
        this.imageByte = imageByte;
    }
}
