package com.infinitum.bookingqba.view.adapters.items.filter;

public class BaseFilterItem {

    protected String id;

    public BaseFilterItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        BaseFilterItem baseFilterItem = (BaseFilterItem) obj;
        return baseFilterItem.id.equals(this.id);
    }
}
