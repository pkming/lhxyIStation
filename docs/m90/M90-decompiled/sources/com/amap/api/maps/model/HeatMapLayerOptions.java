package com.amap.api.maps.model;

import android.graphics.Color;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public class HeatMapLayerOptions extends BaseOptions {
    public static final Gradient DEFAULT_GRADIENT;
    private static final int[] DEFAULT_GRADIENT_COLORS;
    private static final float[] DEFAULT_GRADIENT_START_POINTS;
    public static final double DEFAULT_OPACITY = 0.6d;
    public static final int DEFAULT_RADIUS = 12;
    public static final int TYPE_GRID = 1;
    public static final int TYPE_HEXAGON = 2;
    public static final int TYPE_NORMAL = 0;
    private int[] mColors;
    private Collection<WeightedLatLng> mData;
    private double mLatitude;
    private float[] mStartPoints;
    private double[] pointList;
    private Gradient mGradient = DEFAULT_GRADIENT;
    private float mSize = 2000.0f;
    private float mOpacity = 1.0f;
    private double maxIntensity = 0.0d;
    private float maxZoom = 20.0f;
    private float minZoom = 3.0f;
    private float mGap = 0.0f;
    private int mType = 2;
    private float zIndex = 0.0f;
    private boolean isVisible = true;
    private boolean isPointsUpdated = false;

    static {
        int[] iArr = {Color.rgb(102, 225, 0), Color.rgb(255, 0, 0)};
        DEFAULT_GRADIENT_COLORS = iArr;
        float[] fArr = {0.2f, 1.0f};
        DEFAULT_GRADIENT_START_POINTS = fArr;
        DEFAULT_GRADIENT = new Gradient(iArr, fArr);
    }

    public HeatMapLayerOptions() {
        this.type = "HeatMapLayerOptions";
    }

    public HeatMapLayerOptions data(Collection<LatLng> collection) {
        return weightedData(a(collection));
    }

    public HeatMapLayerOptions weightedData(Collection<WeightedLatLng> collection) {
        this.mData = collection;
        this.isPointsUpdated = true;
        a();
        return this;
    }

    public HeatMapLayerOptions size(float f) {
        this.mSize = f;
        return this;
    }

    public HeatMapLayerOptions gradient(Gradient gradient) {
        this.mGradient = gradient;
        if (gradient != null) {
            this.mColors = gradient.getColors();
            this.mStartPoints = this.mGradient.getStartPoints();
        }
        return this;
    }

    public HeatMapLayerOptions opacity(float f) {
        this.mOpacity = Math.max(0.0f, Math.min(f, 1.0f));
        return this;
    }

    public HeatMapLayerOptions maxIntensity(double d) {
        this.maxIntensity = d;
        return this;
    }

    public HeatMapLayerOptions maxZoom(float f) {
        this.maxZoom = f;
        return this;
    }

    public HeatMapLayerOptions minZoom(float f) {
        this.minZoom = f;
        return this;
    }

    public HeatMapLayerOptions gap(float f) {
        this.mGap = f;
        return this;
    }

    public HeatMapLayerOptions type(int i) {
        this.mType = i;
        return this;
    }

    public HeatMapLayerOptions zIndex(float f) {
        this.zIndex = f;
        return this;
    }

    public HeatMapLayerOptions visible(boolean z) {
        this.isVisible = z;
        return this;
    }

    public Gradient getGradient() {
        return this.mGradient;
    }

    public Collection<WeightedLatLng> getData() {
        return this.mData;
    }

    public float getSize() {
        return this.mSize;
    }

    public float getOpacity() {
        return this.mOpacity;
    }

    public double getMaxIntensity() {
        return this.maxIntensity;
    }

    public float getMaxZoom() {
        return this.maxZoom;
    }

    public float getMinZoom() {
        return this.minZoom;
    }

    public float getGap() {
        return this.mGap;
    }

    public int getType() {
        return this.mType;
    }

    public float getZIndex() {
        return this.zIndex;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    private static Collection<WeightedLatLng> a(Collection<LatLng> collection) {
        ArrayList arrayList = new ArrayList();
        Iterator<LatLng> it = collection.iterator();
        while (it.hasNext()) {
            arrayList.add(new WeightedLatLng(it.next()));
        }
        return arrayList;
    }

    private void a() {
        if (this.isPointsUpdated) {
            double d = 0.0d;
            Collection<WeightedLatLng> data = getData();
            if (data == null) {
                return;
            }
            this.pointList = new double[data.size() * 3];
            int i = 0;
            double d2 = Double.NaN;
            double d3 = Double.NaN;
            for (WeightedLatLng weightedLatLng : data) {
                if (weightedLatLng != null && weightedLatLng.latLng != null) {
                    int i2 = i * 3;
                    this.pointList[i2] = weightedLatLng.latLng.latitude;
                    this.pointList[i2 + 1] = weightedLatLng.latLng.longitude;
                    this.pointList[i2 + 2] = weightedLatLng.intensity;
                    i++;
                    double d4 = weightedLatLng.latLng.latitude;
                    if (Double.isNaN(d2)) {
                        d2 = d4;
                    }
                    if (Double.isNaN(d3)) {
                        d3 = d4;
                    }
                    if (d4 > d3) {
                        d3 = d4;
                    }
                    if (d4 < d2) {
                        d2 = d4;
                    }
                } else {
                    Log.e("mapcore", "read file failed");
                }
            }
            if (!Double.isNaN(d2) && !Double.isNaN(d3)) {
                d = (d2 + d3) / 2.0d;
            }
            this.mLatitude = d;
        }
    }
}
