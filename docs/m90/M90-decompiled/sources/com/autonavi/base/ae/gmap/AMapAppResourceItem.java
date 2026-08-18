package com.autonavi.base.ae.gmap;

/* JADX INFO: loaded from: classes2.dex */
public class AMapAppResourceItem {
    public static final int RESOURCE_TYPE_ABSPATH = 4;
    public static final int RESOURCE_TYPE_BINARY = 3;
    public static final int RESOURCE_TYPE_IMAGE = 1;
    public static final int RESOURCE_TYPE_NONE = 0;
    public static final int RESOURCE_TYPE_SVG = 2;
    private byte[] data;
    private long size;
    private int resourceType = 0;
    private boolean preAlpha = false;
    private int imageWidth = 0;
    private int imageHeight = 0;
    private float imageScale = 1.0f;

    public int getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(int i) {
        this.resourceType = i;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] bArr) {
        this.data = bArr;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public boolean isPreAlpha() {
        return this.preAlpha;
    }

    public void setPreAlpha(boolean z) {
        this.preAlpha = z;
    }

    public int getImageWidth() {
        return this.imageWidth;
    }

    public void setImageWidth(int i) {
        this.imageWidth = i;
    }

    public int getImageHeight() {
        return this.imageHeight;
    }

    public void setImageHeight(int i) {
        this.imageHeight = i;
    }

    public float getImageScale() {
        return this.imageScale;
    }

    public void setImageScale(float f) {
        this.imageScale = f;
    }
}
