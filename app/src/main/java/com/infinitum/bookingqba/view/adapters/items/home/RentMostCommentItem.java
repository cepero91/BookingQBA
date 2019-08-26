package com.infinitum.bookingqba.view.adapters.items.home;

import android.os.Parcel;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentMostCommentItem extends BaseItem {

    private int totalComment;
    private int emotionAvg;
    private String rentMode;
    private double price;

    public RentMostCommentItem(String id, String name, String imagePath) {
        super(id, name, imagePath);
    }

    public RentMostCommentItem(String id, String name, String imagePath, int totalComment, int emotionAvg, String rentMode, double price) {
        super(id, name, imagePath);
        this.totalComment = totalComment;
        this.emotionAvg = emotionAvg;
        this.rentMode = rentMode;
        this.price = price;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public int getEmotionAvg() {
        return emotionAvg;
    }

    public void setEmotionAvg(int emotionAvg) {
        this.emotionAvg = emotionAvg;
    }

    public String getRentMode() {
        return rentMode;
    }

    public void setRentMode(String rentMode) {
        this.rentMode = rentMode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getHumanPrice(){
        return String.format("$ %.2f",price);
    }

    public String getHumanRentMode(){
        return "/"+rentMode;
    }

    public String getHumanTotalComment(){
        if(totalComment > 0){
            return String.format("%s comentario%s",totalComment,totalComment==1?"":"s");
        }else{
            return "Sin comentarios";
        }
    }

    public int getEmotionDrawableId() {
        int id = -1;
        switch (emotionAvg) {
            case 1:
                id = R.drawable.ic_angry_emotion;
                break;
            case 2:
                id = R.drawable.ic_frown_emotion;
                break;
            case 3:
                id = R.drawable.ic_meh_emotion;
                break;
            case 4:
                id = R.drawable.ic_grin_emotion;
                break;
            case 5:
                id = R.drawable.ic_laugh_emotion;
                break;
        }
        return id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.totalComment);
        dest.writeInt(this.emotionAvg);
        dest.writeString(this.rentMode);
        dest.writeDouble(this.price);
    }

    protected RentMostCommentItem(Parcel in) {
        super(in);
        this.totalComment = in.readInt();
        this.emotionAvg = in.readInt();
        this.rentMode = in.readString();
        this.price = in.readDouble();
    }

    public static final Creator<RentMostCommentItem> CREATOR = new Creator<RentMostCommentItem>() {
        @Override
        public RentMostCommentItem createFromParcel(Parcel source) {
            return new RentMostCommentItem(source);
        }

        @Override
        public RentMostCommentItem[] newArray(int size) {
            return new RentMostCommentItem[size];
        }
    };

}
