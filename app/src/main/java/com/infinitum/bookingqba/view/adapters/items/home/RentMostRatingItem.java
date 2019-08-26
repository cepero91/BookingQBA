package com.infinitum.bookingqba.view.adapters.items.home;

import android.os.Parcel;

import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentMostRatingItem extends BaseItem {

    private double price;
    private String rentMode;
    private float rating;
    private int ratingCount;

    public RentMostRatingItem(String id, String name, String imagePath) {
        super(id, name, imagePath);
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String humanRating(){
        return String.format("%.1f",rating);
    }

    public String humanRatingCount(){
        if(ratingCount > 0){
            return String.format("(%s voto%s)",ratingCount,ratingCount==1?"":"s");
        }else{
            return "(Sin votos)";
        }
    }

    public String humanPrice(){
        return String.format("$ %.2f",price);
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
        super.writeToParcel(dest, flags);
        dest.writeDouble(this.price);
        dest.writeString(this.rentMode);
        dest.writeFloat(this.rating);
        dest.writeInt(this.ratingCount);
    }

    protected RentMostRatingItem(Parcel in) {
        super(in);
        this.price = in.readDouble();
        this.rentMode = in.readString();
        this.rating = in.readFloat();
        this.ratingCount = in.readInt();
    }

    public static final Creator<RentMostRatingItem> CREATOR = new Creator<RentMostRatingItem>() {
        @Override
        public RentMostRatingItem createFromParcel(Parcel source) {
            return new RentMostRatingItem(source);
        }

        @Override
        public RentMostRatingItem[] newArray(int size) {
            return new RentMostRatingItem[size];
        }
    };
}
