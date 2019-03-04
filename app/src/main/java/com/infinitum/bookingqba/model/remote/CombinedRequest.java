package com.infinitum.bookingqba.model.remote;

import com.infinitum.bookingqba.model.remote.pojo.Removed;
import com.infinitum.bookingqba.model.remote.pojo.Rent;

import java.util.List;

public class CombinedRequest {
    List<Rent> rentList;
    List<Removed> rentRemoded;

    public CombinedRequest(List<Rent> rentList, List<Removed> rentRemoded) {
        this.rentList = rentList;
        this.rentRemoded = rentRemoded;
    }

    public List<Rent> getRentList() {
        return rentList;
    }

    public void setRentList(List<Rent> rentList) {
        this.rentList = rentList;
    }

    public List<Removed> getRentRemoded() {
        return rentRemoded;
    }

    public void setRentRemoded(List<Removed> rentRemoded) {
        this.rentRemoded = rentRemoded;
    }
}
