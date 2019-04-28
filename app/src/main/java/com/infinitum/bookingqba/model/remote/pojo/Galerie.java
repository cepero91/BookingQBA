package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Galerie {

    @SerializedName("uui")
    @Expose
    private String id;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("rent")
    @Expose
    private String rent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }
}
