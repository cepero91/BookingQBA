package com.infinitum.bookingqba.view.adapters.items.baseitem;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class BaseItem implements ViewModel, Parcelable {

    private String id;
    private String name;
    private String imagePath;
    private float price;
    private String rentMode;

    public BaseItem(String id, String name, String imagePath, float price, String rentMode) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.rentMode = rentMode;
    }

    public static final Creator<BaseItem> CREATOR = new Creator<BaseItem>() {
        @Override
        public BaseItem createFromParcel(Parcel in) {
            return new BaseItem(in);
        }

        @Override
        public BaseItem[] newArray(int size) {
            return new BaseItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public String humanShortPrice(){
        return String.format("%s",(int)price);
    }

    public String humanShortRentMode(){
        return rentMode;
    }

    public String humanPrice(){
        return String.format("%.2f cuc",price);
    }

    public String humanRentMode(){
        return "/"+rentMode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imagePath);
        dest.writeFloat(this.price);
        dest.writeString(this.rentMode);
    }

    protected BaseItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.imagePath = in.readString();
        this.price = in.readFloat();
        this.rentMode = in.readString();
    }

}
