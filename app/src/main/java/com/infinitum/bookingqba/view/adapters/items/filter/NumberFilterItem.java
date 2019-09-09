package com.infinitum.bookingqba.view.adapters.items.filter;

public class NumberFilterItem extends BaseFilterItem{

    private int maxNumber;

    public NumberFilterItem(String id, int maxNumber) {
        super(id);
        this.maxNumber = maxNumber;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

}
