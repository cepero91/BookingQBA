package com.infinitum.bookingqba.view.profile;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class GaleryModelItem implements ViewModel{

    private String imagePath;

    public GaleryModelItem(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
