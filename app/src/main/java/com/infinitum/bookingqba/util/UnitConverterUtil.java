package com.infinitum.bookingqba.util;

import android.util.Pair;

import java.util.Locale;

public class UnitConverterUtil {
    public static final double METERS_OF_FEET = 0.3048;
    public static final double METERS_OF_MILE = 1609.344;
    public static final double METERS_OF_KM = 1000.0;
    public static final double FEETS_OF_MILE = 5280.0;

    public static String getString(double m) {
        if (m < METERS_OF_KM) return Math.round(m) + " metros";
        return (((int) (m / 100)) / 10f) + " km";
    }

    public static Pair<String,String> getPairDistanceUnit(double distance){
        if(distance < METERS_OF_KM)
            return new Pair<>(String.valueOf(Math.round(distance)),"m");
        return new Pair<>(String.valueOf(((int) (distance / 100)) / 10f),"km");
    }

    /**
     * Returns a rounded Value of KM or MI.
     *
     * @param pdp Post decimal positions.
     **/
    public static String getBigDistance(double m, int pdp) {
        m = m / METERS_OF_KM;
        return String.format(Locale.getDefault(), "%." + pdp + "f", m);
    }
}
