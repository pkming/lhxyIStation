package com.autonavi.base.ae.gmap;

/* JADX INFO: loaded from: classes2.dex */
public class MapPoi {
    private String extendInfo;
    private long iconXMax;
    private long iconXMin;
    private long iconYMax;
    private long iconYMin;
    private boolean isFocus;
    private double latitude;
    private double longitude;
    private long mainKey;
    private String name;
    private long poiType;
    private String poiid;
    private float screenX;
    private float screenY;
    private long subKey;
    private long subType;
    private long timestamp;
    private double z;

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPoiid() {
        return this.poiid;
    }

    public void setPoiid(String str) {
        this.poiid = str;
    }

    public long getMainKey() {
        return this.mainKey;
    }

    public void setMainKey(long j) {
        this.mainKey = j;
    }

    public long getSubKey() {
        return this.subKey;
    }

    public void setSubKey(long j) {
        this.subKey = j;
    }

    public long getPoiType() {
        return this.poiType;
    }

    public void setPoiType(long j) {
        this.poiType = j;
    }

    public long getSubType() {
        return this.subType;
    }

    public void setSubType(long j) {
        this.subType = j;
    }

    public boolean isFocus() {
        return this.isFocus;
    }

    public void setFocus(boolean z) {
        this.isFocus = z;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long j) {
        this.timestamp = j;
    }

    public String getExtendInfo() {
        return this.extendInfo;
    }

    public void setExtendInfo(String str) {
        this.extendInfo = str;
    }

    public float getScreenX() {
        return this.screenX;
    }

    public void setScreenX(float f) {
        this.screenX = f;
    }

    public float getScreenY() {
        return this.screenY;
    }

    public void setScreenY(float f) {
        this.screenY = f;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double d) {
        this.longitude = d;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double d) {
        this.latitude = d;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double d) {
        this.z = d;
    }

    public long getIconXMin() {
        return this.iconXMin;
    }

    public void setIconXMin(long j) {
        this.iconXMin = j;
    }

    public long getIconXMax() {
        return this.iconXMax;
    }

    public void setIconXMax(long j) {
        this.iconXMax = j;
    }

    public long getIconYMin() {
        return this.iconYMin;
    }

    public void setIconYMin(long j) {
        this.iconYMin = j;
    }

    public long getIconYMax() {
        return this.iconYMax;
    }

    public void setIconYMax(long j) {
        this.iconYMax = j;
    }
}
