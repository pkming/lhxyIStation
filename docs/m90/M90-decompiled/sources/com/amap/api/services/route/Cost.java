package com.amap.api.services.route;

/* JADX INFO: loaded from: classes2.dex */
public class Cost {
    private float a;
    private float b;
    private String c;
    private float d;
    private int e;

    public float getDuration() {
        return this.a;
    }

    public void setDuration(float f) {
        this.a = f;
    }

    public float getTollDistance() {
        return this.b;
    }

    public void setTollDistance(float f) {
        this.b = f;
    }

    public String getTollRoad() {
        return this.c;
    }

    public void setTollRoad(String str) {
        this.c = str;
    }

    public float getTolls() {
        return this.d;
    }

    public void setTolls(float f) {
        this.d = f;
    }

    public int getTrafficLights() {
        return this.e;
    }

    public void setTrafficLights(int i) {
        this.e = i;
    }
}
