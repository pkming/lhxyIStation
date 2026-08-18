package com.amap.api.services.route;

import com.amap.api.services.core.LatLonPoint;

/* JADX INFO: loaded from: classes2.dex */
public class ChargeStationInfo {
    private int a;
    private LatLonPoint b;
    private LatLonPoint c;
    private String d;
    private String e;
    private String f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;
    private int l;

    public int getStepIndex() {
        return this.a;
    }

    public void setStepIndex(int i) {
        this.a = i;
    }

    public LatLonPoint getShowPoint() {
        return this.b;
    }

    public void setShowPoint(LatLonPoint latLonPoint) {
        this.b = latLonPoint;
    }

    public LatLonPoint getProjectivePoint() {
        return this.c;
    }

    public void setProjectivePoint(LatLonPoint latLonPoint) {
        this.c = latLonPoint;
    }

    public String getPoiId() {
        return this.d;
    }

    public void setPoiId(String str) {
        this.d = str;
    }

    public String getName() {
        return this.e;
    }

    public void setName(String str) {
        this.e = str;
    }

    public String getBrandName() {
        return this.f;
    }

    public void setBrandName(String str) {
        this.f = str;
    }

    public int getMaxPower() {
        return this.g;
    }

    public void setMaxPower(int i) {
        this.g = i;
    }

    public int getChargePercent() {
        return this.h;
    }

    public void setChargePercent(int i) {
        this.h = i;
    }

    public int getChargeTime() {
        return this.i;
    }

    public void setChargeTime(int i) {
        this.i = i;
    }

    public int getRemainingCapacity() {
        return this.j;
    }

    public void setRemainingCapacity(int i) {
        this.j = i;
    }

    public int getVoltage() {
        return this.k;
    }

    public void setVoltage(int i) {
        this.k = i;
    }

    public int getAmperage() {
        return this.l;
    }

    public void setAmperage(int i) {
        this.l = i;
    }
}
