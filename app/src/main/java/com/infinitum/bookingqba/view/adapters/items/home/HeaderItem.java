package com.infinitum.bookingqba.view.adapters.items.home;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class HeaderItem extends BaseItem{

    private char orderType;

    public HeaderItem(String id, String mName, char orderType) {
        super(id, mName);
        this.orderType = orderType;
    }

    public char getOrderType() {
        return orderType;
    }

    public void setOrderType(char orderType) {
        this.orderType = orderType;
    }
}
