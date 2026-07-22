package com.amap.api.maps.model;

import com.autonavi.amap.mapcore.IPoint;

/* JADX INFO: loaded from: classes2.dex */
public class MultiPointItem {
    private String customerId = null;
    private IPoint iPoint;
    private LatLng latLng;
    private Object object;
    private String snippet;
    private String title;

    private MultiPointItem() {
    }

    public MultiPointItem(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String str) {
        this.customerId = str;
    }

    public String getSnippet() {
        return this.snippet;
    }

    public void setSnippet(String str) {
        this.snippet = str;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public IPoint getIPoint() {
        return this.iPoint;
    }

    public void setIPoint(IPoint iPoint) {
        this.iPoint = iPoint;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object obj) {
        this.object = obj;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MultiPointItem)) {
            return false;
        }
        if (this.customerId != null) {
            MultiPointItem multiPointItem = (MultiPointItem) obj;
            if (multiPointItem.getCustomerId() != null) {
                return this.customerId.equals(multiPointItem.getCustomerId());
            }
        }
        return super.equals(obj);
    }
}
