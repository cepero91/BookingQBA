package com.infinitum.bookingqba.view.map;

import android.support.annotation.Nullable;

import com.graphhopper.PathWrapper;

public class RouteHelper {

    private PathWrapper pathWrapper;
    private String distanceValue;
    private String distanceUnit;
    private String timeValue;
    private String timeUnit;

    public RouteHelper(PathWrapper pathWrapper, String distanceValue, String distanceUnit, String timeValue, String timeUnit) {
        this.pathWrapper = pathWrapper;
        this.distanceValue = distanceValue;
        this.distanceUnit = distanceUnit;
        this.timeValue = timeValue;
        this.timeUnit = timeUnit;
    }

    public PathWrapper getPathWrapper() {
        return pathWrapper;
    }

    public void setPathWrapper(PathWrapper pathWrapper) {
        this.pathWrapper = pathWrapper;
    }

    public String getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(String distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }
}
