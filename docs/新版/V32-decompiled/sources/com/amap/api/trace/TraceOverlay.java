package com.amap.api.trace;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class TraceOverlay {
    public static final int TRACE_STATUS_FAILURE = 3;
    public static final int TRACE_STATUS_FINISH = 2;
    public static final int TRACE_STATUS_PREPARE = 4;
    public static final int TRACE_STATUS_PROCESSING = 1;
    private Polyline a;
    private PolylineOptions b;
    private AMap c;
    private List<LatLng> d;
    private int e;
    private int f;
    private int g;

    public TraceOverlay(AMap aMap, List<LatLng> list) {
        this.d = new ArrayList();
        this.e = 4;
        this.c = aMap;
        a();
        this.d = list;
        this.b.addAll(list);
        this.a = aMap.addPolyline(this.b);
    }

    public TraceOverlay(AMap aMap) {
        this.d = new ArrayList();
        this.e = 4;
        this.c = aMap;
        a();
    }

    public void add(List<LatLng> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        this.d.addAll(list);
        a();
        if (this.a == null) {
            this.a = this.c.addPolyline(this.b);
        }
        Polyline polyline = this.a;
        if (polyline != null) {
            polyline.setPoints(this.d);
        }
    }

    public void remove() {
        Polyline polyline = this.a;
        if (polyline != null) {
            polyline.remove();
        }
    }

    public void setProperCamera(List<LatLng> list) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        if (list == null || list.size() == 0) {
            return;
        }
        Iterator<LatLng> it = list.iterator();
        while (it.hasNext()) {
            builder.include(it.next());
        }
        try {
            this.c.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void zoopToSpan() {
        setProperCamera(this.b.getPoints());
    }

    private PolylineOptions a() {
        if (this.b == null) {
            PolylineOptions polylineOptions = new PolylineOptions();
            this.b = polylineOptions;
            polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromAsset("tracelinetexture.png"));
            this.b.width(40.0f);
        }
        return this.b;
    }

    public int getTraceStatus() {
        return this.e;
    }

    public void setTraceStatus(int i) {
        this.e = i;
    }

    public int getDistance() {
        return this.f;
    }

    public void setDistance(int i) {
        this.f = i;
    }

    public int getWaitTime() {
        return this.g;
    }

    public void setWaitTime(int i) {
        this.g = i;
    }
}
