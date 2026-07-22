package com.amap.api.maps.model;

/* JADX INFO: loaded from: classes2.dex */
public class RoutePara {
    private String endName;
    private LatLng endPoint;
    private String startName;
    private LatLng startPoint;
    private int drivingRouteStyle = 0;
    private int transitRouteStyle = 0;

    public int getDrivingRouteStyle() {
        return this.drivingRouteStyle;
    }

    public void setDrivingRouteStyle(int i) {
        if (i < 0 || i >= 9) {
            return;
        }
        this.drivingRouteStyle = i;
    }

    public int getTransitRouteStyle() {
        return this.transitRouteStyle;
    }

    public void setTransitRouteStyle(int i) {
        if (i < 0 || i >= 6) {
            return;
        }
        this.transitRouteStyle = i;
    }

    public LatLng getStartPoint() {
        return this.startPoint;
    }

    public void setStartPoint(LatLng latLng) {
        this.startPoint = latLng;
    }

    public LatLng getEndPoint() {
        return this.endPoint;
    }

    public void setEndPoint(LatLng latLng) {
        this.endPoint = latLng;
    }

    public String getEndName() {
        return this.endName;
    }

    public void setEndName(String str) {
        this.endName = str;
    }

    public String getStartName() {
        return this.startName;
    }

    public void setStartName(String str) {
        this.startName = str;
    }
}
