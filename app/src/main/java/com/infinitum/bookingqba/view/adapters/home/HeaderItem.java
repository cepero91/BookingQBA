package com.infinitum.bookingqba.view.adapters.home;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.baseitem.BaseItem;

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
