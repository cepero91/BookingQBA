package com.infinitum.bookingqba.view.adapters.items.rentdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

import java.util.Date;

public class RentOfferItem implements ViewModel, Parcelable {

    private String id;
    private String title;
    private String description;
    private double price;

    public RentOfferItem(String id, String title, String description, double price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeDouble(this.price);
    }

    protected RentOfferItem(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.price = in.readFloat();
    }

    public static final Creator<RentOfferItem> CREATOR = new Creator<RentOfferItem>() {
        @Override
        public RentOfferItem createFromParcel(Parcel source) {
            return new RentOfferItem(source);
        }

        @Override
        public RentOfferItem[] newArray(int size) {
            return new RentOfferItem[size];
        }
    };
}
