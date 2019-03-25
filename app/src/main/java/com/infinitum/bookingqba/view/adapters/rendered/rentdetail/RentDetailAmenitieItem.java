package com.infinitum.bookingqba.view.adapters.rendered.rentdetail;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class RentDetailAmenitieItem implements ViewModel{

    private String mName;

    public RentDetailAmenitieItem(String mName) {
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

        final RentDetailAmenitieItem that = (RentDetailAmenitieItem) o;
        return !mName.equals(that.mName);
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
