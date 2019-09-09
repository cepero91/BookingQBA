package com.infinitum.bookingqba.view.adapters.items.filter;

public class RangeFilterItem extends BaseFilterItem{

    private float maxPrice;

    public RangeFilterItem(String id, float maxPrice) {
        super(id);
        this.maxPrice = maxPrice;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

}
