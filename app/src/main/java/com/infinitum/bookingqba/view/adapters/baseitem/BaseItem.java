package com.infinitum.bookingqba.view.adapters.baseitem;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class BaseItem implements ViewModel{

    private int id;
    private String mName;

    public BaseItem(int id, String mName) {
        this.id = id;
        this.mName = mName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final BaseItem that = (BaseItem) o;

        if (!mName.equals(that.mName)) {
            return false;
        }
        return id==that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return mName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
