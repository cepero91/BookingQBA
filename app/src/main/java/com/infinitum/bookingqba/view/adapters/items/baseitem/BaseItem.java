package com.infinitum.bookingqba.view.adapters.items.baseitem;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class BaseItem implements ViewModel{

    private String id;
    private String mName;

    public BaseItem(String id, String mName) {
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
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
