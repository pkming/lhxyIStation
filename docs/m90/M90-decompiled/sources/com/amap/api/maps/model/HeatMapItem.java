package com.amap.api.maps.model;

/* JADX INFO: loaded from: classes2.dex */
public class HeatMapItem {
    private LatLng center;
    private int[] indexes;
    private double intensity;

    public LatLng getCenter() {
        return this.center;
    }

    public void setCenter(double d, double d2) {
        this.center = new LatLng(d, d2);
    }

    public double getIntensity() {
        return this.intensity;
    }

    public void setIntensity(double d) {
        this.intensity = d;
    }

    public int[] getIndexes() {
        return this.indexes;
    }

    public void setIndexes(int[] iArr) {
        this.indexes = iArr;
    }
}
