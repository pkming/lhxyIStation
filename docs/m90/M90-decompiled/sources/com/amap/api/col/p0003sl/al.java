package com.amap.api.col.p0003sl;

import android.graphics.Point;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.autonavi.amap.mapcore.AbstractCameraUpdateMessage;
import com.autonavi.amap.mapcore.DPoint;
import com.autonavi.amap.mapcore.VirtualEarthProjection;

/* JADX INFO: compiled from: CameraUpdateFactoryDelegate.java */
/* JADX INFO: loaded from: classes2.dex */
public final class al {
    public static AbstractCameraUpdateMessage a() {
        ak akVar = new ak();
        akVar.nowType = AbstractCameraUpdateMessage.Type.zoomBy;
        akVar.amount = 1.0f;
        return akVar;
    }

    public static AbstractCameraUpdateMessage b() {
        ak akVar = new ak();
        akVar.nowType = AbstractCameraUpdateMessage.Type.zoomBy;
        akVar.amount = -1.0f;
        return akVar;
    }

    public static AbstractCameraUpdateMessage a(float f, float f2) {
        aj ajVar = new aj();
        ajVar.nowType = AbstractCameraUpdateMessage.Type.scrollBy;
        ajVar.xPixel = f;
        ajVar.yPixel = f2;
        return ajVar;
    }

    public static AbstractCameraUpdateMessage a(float f) {
        ai aiVar = new ai();
        aiVar.nowType = AbstractCameraUpdateMessage.Type.newCameraPosition;
        aiVar.zoom = f;
        return aiVar;
    }

    public static AbstractCameraUpdateMessage b(float f) {
        return a(f, (Point) null);
    }

    public static AbstractCameraUpdateMessage a(float f, Point point) {
        ak akVar = new ak();
        akVar.nowType = AbstractCameraUpdateMessage.Type.zoomBy;
        akVar.amount = f;
        akVar.focus = point;
        return akVar;
    }

    public static AbstractCameraUpdateMessage a(CameraPosition cameraPosition) {
        ai aiVar = new ai();
        aiVar.nowType = AbstractCameraUpdateMessage.Type.newCameraPosition;
        if (cameraPosition != null && cameraPosition.target != null) {
            DPoint dPointLatLongToPixelsDouble = VirtualEarthProjection.latLongToPixelsDouble(cameraPosition.target.latitude, cameraPosition.target.longitude, 20);
            aiVar.geoPoint = new DPoint(dPointLatLongToPixelsDouble.x, dPointLatLongToPixelsDouble.y);
            aiVar.zoom = cameraPosition.zoom;
            aiVar.bearing = cameraPosition.bearing;
            aiVar.tilt = cameraPosition.tilt;
            aiVar.cameraPosition = cameraPosition;
        }
        return aiVar;
    }

    public static AbstractCameraUpdateMessage a(Point point) {
        ai aiVar = new ai();
        aiVar.nowType = AbstractCameraUpdateMessage.Type.newCameraPosition;
        aiVar.geoPoint = new DPoint(point.x, point.y);
        return aiVar;
    }

    public static AbstractCameraUpdateMessage c(float f) {
        ai aiVar = new ai();
        aiVar.nowType = AbstractCameraUpdateMessage.Type.newCameraPosition;
        aiVar.tilt = f;
        return aiVar;
    }

    public static AbstractCameraUpdateMessage d(float f) {
        ai aiVar = new ai();
        aiVar.nowType = AbstractCameraUpdateMessage.Type.newCameraPosition;
        aiVar.bearing = f;
        return aiVar;
    }

    public static AbstractCameraUpdateMessage b(float f, Point point) {
        ai aiVar = new ai();
        aiVar.nowType = AbstractCameraUpdateMessage.Type.newCameraPosition;
        aiVar.geoPoint = new DPoint(point.x, point.y);
        aiVar.bearing = f;
        return aiVar;
    }

    public static AbstractCameraUpdateMessage a(LatLng latLng) {
        return a(CameraPosition.builder().target(latLng).zoom(Float.NaN).bearing(Float.NaN).tilt(Float.NaN).build());
    }

    public static AbstractCameraUpdateMessage a(LatLng latLng, float f) {
        return a(CameraPosition.builder().target(latLng).zoom(f).bearing(Float.NaN).tilt(Float.NaN).build());
    }

    public static AbstractCameraUpdateMessage a(LatLngBounds latLngBounds, int i) {
        ah ahVar = new ah();
        ahVar.nowType = AbstractCameraUpdateMessage.Type.newLatLngBounds;
        ahVar.bounds = latLngBounds;
        ahVar.paddingLeft = i;
        ahVar.paddingRight = i;
        ahVar.paddingTop = i;
        ahVar.paddingBottom = i;
        return ahVar;
    }

    public static AbstractCameraUpdateMessage a(LatLngBounds latLngBounds, int i, int i2, int i3) {
        ah ahVar = new ah();
        ahVar.nowType = AbstractCameraUpdateMessage.Type.newLatLngBoundsWithSize;
        ahVar.bounds = latLngBounds;
        ahVar.paddingLeft = i3;
        ahVar.paddingRight = i3;
        ahVar.paddingTop = i3;
        ahVar.paddingBottom = i3;
        ahVar.width = i;
        ahVar.height = i2;
        return ahVar;
    }

    public static AbstractCameraUpdateMessage a(LatLngBounds latLngBounds, int i, int i2, int i3, int i4) {
        ah ahVar = new ah();
        ahVar.nowType = AbstractCameraUpdateMessage.Type.newLatLngBounds;
        ahVar.bounds = latLngBounds;
        ahVar.paddingLeft = i;
        ahVar.paddingRight = i2;
        ahVar.paddingTop = i3;
        ahVar.paddingBottom = i4;
        return ahVar;
    }

    public static AbstractCameraUpdateMessage c() {
        return new ai();
    }
}
