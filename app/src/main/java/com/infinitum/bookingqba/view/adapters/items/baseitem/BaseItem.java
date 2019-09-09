package com.infinitum.bookingqba.view.adapters.items.baseitem;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class BaseItem implements ViewModel, Parcelable {

    private String id;
    private String name;
    private String imagePath;

    public BaseItem(String id, String name, String imagePath) {
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

    protected BaseItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.imagePath = in.readString();
    }

}
