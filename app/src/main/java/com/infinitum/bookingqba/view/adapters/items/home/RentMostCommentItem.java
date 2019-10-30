package com.infinitum.bookingqba.view.adapters.items.home;

import android.os.Parcel;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.items.baseitem.BaseItem;

public class RentMostCommentItem extends BaseItem {

    private int totalComment;
    private int emotionAvg;
    private float rating;
    private int ratingCount;

    public RentMostCommentItem(String id, String name, String imagePath, float price, String rentMode) {
        super(id, name, imagePath, price, rentMode);
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

    public String humanRatingCount(){
        return String.format("(%s)",ratingCount);
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
        dest.writeFloat(this.rating);
        dest.writeInt(this.ratingCount);
    }

    protected RentMostCommentItem(Parcel in) {
        super(in);
        this.totalComment = in.readInt();
        this.emotionAvg = in.readInt();
        this.rating = in.readFloat();
        this.ratingCount = in.readInt();
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
