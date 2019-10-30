package com.infinitum.bookingqba.util.geo;


import android.location.Location;

import org.mapsforge.core.model.LatLong;

public class DistanceUtil {

    public static String distanceBetween(double lat1, double lon1, double lat2, double lon2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);
        return distanceBetween(loc1,loc2);
    }

    private static String distanceBetween(Location startLocation, Location endLocation){
        float distance = startLocation.distanceTo(endLocation);
        if(distance/1000 < 1){
            return String.format("%.1f m", distance);
        }else{
            return String.format("%.1f km", distance/1000);
        }
    }
}
