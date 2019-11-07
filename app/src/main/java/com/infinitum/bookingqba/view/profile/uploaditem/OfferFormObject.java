package com.infinitum.bookingqba.view.profile.uploaditem;

import java.util.UUID;

public class OfferFormObject extends FormObject implements Cloneable{

    private String uuid;
    private String name;
    private String description;
    private String price;
    private String rent;
    private int version;

    public OfferFormObject() {
        this.version = 0;
    }

    public OfferFormObject(String uuid, String name, String description, String price, String rent) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.rent = rent;
        this.version = 1;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(getClass() != obj.getClass())
            return false;
        OfferFormObject other = (OfferFormObject) obj;
        return this.name.equals(other.name) && this.description.equals(other.description)
                && this.price.equals(other.price);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
