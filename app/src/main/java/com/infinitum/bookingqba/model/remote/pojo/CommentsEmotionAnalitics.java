package com.infinitum.bookingqba.model.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentsEmotionAnalitics {

    @SerializedName("emotion_average")
    @Expose
    private float emotionAverage;

    @SerializedName("terrible")
    @Expose
    private int terribleCount;

    @SerializedName("bad")
    @Expose
    private int badCount;

    @SerializedName("ok")
    @Expose
    private int okCount;

    @SerializedName("good")
    @Expose
    private int goodCount;

    @SerializedName("excelent")
    @Expose
    private int excelentCount;

    public CommentsEmotionAnalitics(float emotionAverage, int terribleCount, int badCount, int okCount, int goodCount, int excelentCount) {
        this.emotionAverage = emotionAverage;
        this.terribleCount = terribleCount;
        this.badCount = badCount;
        this.okCount = okCount;
        this.goodCount = goodCount;
        this.excelentCount = excelentCount;
    }

    public float getEmotionAverage() {
        return emotionAverage;
    }

    public void setEmotionAverage(float emotionAverage) {
        this.emotionAverage = emotionAverage;
    }

    public int getTerribleCount() {
        return terribleCount;
    }

    public void setTerribleCount(int terribleCount) {
        this.terribleCount = terribleCount;
    }

    public int getBadCount() {
        return badCount;
    }

    public void setBadCount(int badCount) {
        this.badCount = badCount;
    }

    public int getOkCount() {
        return okCount;
    }

    public void setOkCount(int okCount) {
        this.okCount = okCount;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    public int getExcelentCount() {
        return excelentCount;
    }

    public void setExcelentCount(int excelentCount) {
        this.excelentCount = excelentCount;
    }
}
