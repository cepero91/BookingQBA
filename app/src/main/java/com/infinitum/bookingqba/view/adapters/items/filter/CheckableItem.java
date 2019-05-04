package com.infinitum.bookingqba.view.adapters.items.filter;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class CheckableItem implements ViewModel{

    private String id;
    private String name;
    private boolean checked;

    public CheckableItem(String id, String name, boolean checked) {
        this.id = id;
        this.name = name;
        this.checked = checked;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
