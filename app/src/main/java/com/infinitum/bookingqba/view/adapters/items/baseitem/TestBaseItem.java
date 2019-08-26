package com.infinitum.bookingqba.view.adapters.items.baseitem;

import android.os.Parcel;
import android.os.Parcelable;

public class TestBaseItem implements Parcelable {

    private String id;
    private String name;
    private String imagePath;

    public TestBaseItem(String id, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }

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

    public static final Creator<TestBaseItem> CREATOR = new Creator<TestBaseItem>() {
        @Override
        public TestBaseItem createFromParcel(Parcel in) {
            return new TestBaseItem(in);
        }

        @Override
        public TestBaseItem[] newArray(int size) {
            return new TestBaseItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imagePath);
    }

    protected TestBaseItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.imagePath = in.readString();
    }

}
