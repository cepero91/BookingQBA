package com.infinitum.bookingqba.view.profile.uploaditem;

import java.util.UUID;

public class OfferFormObject extends FormObject{

    private String uuid;
    private String name;
    private String description;
    private String price;
    private String rent;

    public OfferFormObject() {
        this.uuid = UUID.randomUUID().toString();
    }

    public OfferFormObject(String uuid, String name, String description, String price, String rent) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.rent = rent;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }
}