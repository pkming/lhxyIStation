package com.amap.api.maps.model;

/* JADX INFO: loaded from: classes2.dex */
public class MyTrafficStyle {
    private int extremelySmoothColor = -15229099;
    private int smoothColor = -16735735;
    private int slowColor = -35576;
    private int congestedColor = -1441006;
    private int seriousCongestedColor = -7208950;
    private float ratio = 0.8f;
    private int trafficRoadBackgroundColor = -1;

    public int getExtremelySmoothColor() {
        return this.extremelySmoothColor;
    }

    public void setExtremelySmoothColor(int i) {
        this.extremelySmoothColor = i;
    }

    public int getSmoothColor() {
        return this.smoothColor;
    }

    public void setSmoothColor(int i) {
        this.smoothColor = i;
    }

    public int getSlowColor() {
        return this.slowColor;
    }

    public void setSlowColor(int i) {
        this.slowColor = i;
    }

    public int getCongestedColor() {
        return this.congestedColor;
    }

    public void setCongestedColor(int i) {
        this.congestedColor = i;
    }

    public int getSeriousCongestedColor() {
        return this.seriousCongestedColor;
    }

    public void setSeriousCongestedColor(int i) {
        this.seriousCongestedColor = i;
    }

    public float getRatio() {
        return this.ratio;
    }

    public void setRatio(float f) {
        this.ratio = f;
    }

    public int getTrafficRoadBackgroundColor() {
        return this.trafficRoadBackgroundColor;
    }

    public void setTrafficRoadBackgroundColor(int i) {
        this.trafficRoadBackgroundColor = i;
    }
}
