package com.infinitum.bookingqba.view.interaction;

import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;

import java.util.ArrayList;
import java.util.List;

public interface InnerDetailInteraction {

    void onGaleryClick(String id);

    void onAddressClick(ArrayList<GeoRent> geoRentList);

    void phoneCallClick(String phone);

    void phoneSMSClick(String phone);

    void phoneHomeClick(String phone);

    void phoneEmailClick(String email);

    void onBookRequestClick();

    void onDrawChangeClick();

}
