package com.infinitum.bookingqba.view.adapters.items.filter;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class CheckableFilterItem extends BaseFilterItem{

    private String name;
    private boolean checked;
    private String levelParam;

    public CheckableFilterItem(String id, String name, boolean checked, String levelParam) {
        super(id);
        this.name = name;
        this.checked = checked;
        this.levelParam = levelParam;
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

    public String getLevelParam() {
        return levelParam;
    }

    public void setLevelParam(String levelParam) {
        this.levelParam = levelParam;
    }
}
