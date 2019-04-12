package com.infinitum.bookingqba.view.adapters.items.home;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentNewItem extends BaseItem {

    private String imagePath;
    private float rating;

    public RentNewItem(String id, String mName, String imagePath, float rating) {
        super(id, mName);
        this.imagePath = imagePath;
        this.rating = rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
