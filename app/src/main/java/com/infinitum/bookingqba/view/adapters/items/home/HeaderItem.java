package com.infinitum.bookingqba.view.adapters.items.home;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class HeaderItem implements ViewModel{

    private String id;
    private String name;
    private char orderType;

    public HeaderItem(String id, String name, char orderType) {
        this.id = id;
        this.name = name;
        this.orderType = orderType;
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

    public char getOrderType() {
        return orderType;
    }

    public void setOrderType(char orderType) {
        this.orderType = orderType;
    }
}
