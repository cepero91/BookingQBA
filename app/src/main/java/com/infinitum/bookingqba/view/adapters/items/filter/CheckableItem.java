package com.infinitum.bookingqba.view.adapters.items.filter;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class CheckableItem extends BaseItem{

    private boolean checked;

    public CheckableItem(String id, String mName, boolean checked) {
        super(id, mName);
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
