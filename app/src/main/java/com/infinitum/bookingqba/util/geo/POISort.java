package com.infinitum.bookingqba.util.geo;

import android.support.annotation.NonNull;

import com.infinitum.bookingqba.view.adapters.items.map.PoiItem;

import org.mapsforge.core.model.LatLong;

import java.util.Comparator;

public class POISort implements Comparator<PoiItem> {
    LatLong currentLoc;

    public POISort(LatLong current){
        currentLoc = current;
    }

    @Override
    public int compare(final PoiItem place1, final PoiItem place2) {
        double lat1 = place1.getLatitude();
        double lon1 = place1.getLongitude();
        double lat2 = place2.getLatitude();
        double lon2 = place2.getLongitude();

        double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
        double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
        return (int) (distanceToPlace1 - distanceToPlace2);
    }

    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }

}
