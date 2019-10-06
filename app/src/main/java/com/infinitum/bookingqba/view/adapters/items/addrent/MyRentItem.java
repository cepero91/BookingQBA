package com.infinitum.bookingqba.view.adapters.items.addrent;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class MyRentItem {

    private String uuid;
    private String name;
    private boolean active;
    private double price;
    private String rentMode;
    private String portrait;

    public MyRentItem() {
    }

    public MyRentItem(String uuid, String name, boolean active, double price, String rentMode) {
        this.uuid = uuid;
        this.name = name;
        this.active = active;
        this.price = price;
        this.rentMode = rentMode;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String humanPrice(){
        return String.format("%.2f cuc",price);
    }

    public String humanRentMode(){
        return "/"+rentMode;
    }
}
