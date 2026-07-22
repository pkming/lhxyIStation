package com.amap.api.maps.model;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class GL3DModelOptions extends BaseOptions {
    private BitmapDescriptor bitmapDescriptor;
    private LatLng latLng;
    private String modelData;
    private float rotate;
    private String snippet;
    private String title;
    private List<Float> vertextList = new ArrayList();
    private List<Float> textrueList = new ArrayList();
    private boolean isModeltUpdate = false;
    private int fixedLength = 50;
    private double altitude = 0.0d;
    private boolean fixedDisplaySizeEnabled = true;
    private double fixedDisplaySizeX = -1.0d;
    private double fixedDisplaySizeY = -1.0d;
    private boolean visibile = true;
    private int zIndex = 0;

    public GL3DModelOptions() {
        this.type = "GL3DModelOptions";
    }

    public GL3DModelOptions textureDrawable(BitmapDescriptor bitmapDescriptor) {
        this.bitmapDescriptor = bitmapDescriptor;
        return this;
    }

    public GL3DModelOptions vertexData(String str) {
        if (str != null && str.length() > 0) {
            this.modelData = str;
            this.isModeltUpdate = true;
        }
        return this;
    }

    public GL3DModelOptions vertexData(List<Float> list, List<Float> list2) {
        this.vertextList = list;
        this.textrueList = list2;
        StringBuilder sb = new StringBuilder();
        if (this.vertextList != null) {
            for (int i = 0; i < this.vertextList.size() - 3; i += 3) {
                sb.append("v ");
                sb.append(this.vertextList.get(i)).append(" ");
                sb.append(this.vertextList.get(i + 1)).append(" ");
                sb.append(this.vertextList.get(i + 2));
                sb.append("\n");
            }
        }
        if (this.textrueList != null) {
            for (int i2 = 0; i2 < this.textrueList.size() - 2; i2 += 2) {
                sb.append("vt ");
                sb.append(this.textrueList.get(i2)).append(" ");
                sb.append(1.0f - this.textrueList.get(i2 + 1).floatValue());
                sb.append("\n");
            }
        }
        vertexData(sb.toString());
        return this;
    }

    public GL3DModelOptions position(LatLng latLng) {
        this.latLng = latLng;
        return this;
    }

    public GL3DModelOptions angle(float f) {
        this.rotate = f;
        return this;
    }

    public List<Float> getVertext() {
        return this.vertextList;
    }

    public List<Float> getTextrue() {
        return this.textrueList;
    }

    public float getAngle() {
        return this.rotate;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public BitmapDescriptor getBitmapDescriptor() {
        return this.bitmapDescriptor;
    }

    public GL3DModelOptions setModelFixedLength(int i) {
        this.fixedLength = i;
        return this;
    }

    public int getModelFixedLength() {
        return this.fixedLength;
    }

    public GL3DModelOptions setAltitude(double d) {
        this.altitude = d;
        return this;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public GL3DModelOptions setFixedDisplaySizeEnabled(boolean z) {
        this.fixedDisplaySizeEnabled = z;
        return this;
    }

    public boolean isFixedDisplaySizeEnabled() {
        return this.fixedDisplaySizeEnabled;
    }

    public GL3DModelOptions setFixDisplaySize(double d, double d2) {
        this.fixedDisplaySizeX = d;
        this.fixedDisplaySizeY = d2;
        return this;
    }

    public Pair<Double, Double> getFixDisplaySize() {
        return Pair.create(Double.valueOf(this.fixedDisplaySizeX), Double.valueOf(this.fixedDisplaySizeY));
    }

    public GL3DModelOptions setVisible(boolean z) {
        this.visibile = z;
        return this;
    }

    public boolean isVisible() {
        return this.visibile;
    }

    public GL3DModelOptions title(String str) {
        this.title = str;
        return this;
    }

    public GL3DModelOptions snippet(String str) {
        this.snippet = str;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSnippet() {
        return this.snippet;
    }

    public GL3DModelOptions setzIndex(int i) {
        this.zIndex = i;
        return this;
    }

    public int getZIndex() {
        return this.zIndex;
    }
}
