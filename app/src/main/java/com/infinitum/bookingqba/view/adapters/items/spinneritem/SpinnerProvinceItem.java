package com.infinitum.bookingqba.view.adapters.items.spinneritem;

public class SpinnerProvinceItem {

    private String uuid;
    private String name;

    public SpinnerProvinceItem(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
