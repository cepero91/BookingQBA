package com.infinitum.bookingqba.util.geo;

import android.location.Location;

import com.infinitum.bookingqba.view.adapters.items.map.GeoRent;
import com.infinitum.bookingqba.view.adapters.items.map.PoiItem;

import org.mapsforge.core.model.LatLong;

import java.util.Comparator;

public class GeoRentSort implements Comparator<GeoRent> {
    Location userLocation;

    public GeoRentSort(Location userLocation){
        userLocation = userLocation;
    }

    @Override
    public int compare(final GeoRent rent1, final GeoRent rent2) {
        return (int) (rent1.getDistanceBetween(userLocation) - rent2.getDistanceBetween(userLocation));
    }

}
