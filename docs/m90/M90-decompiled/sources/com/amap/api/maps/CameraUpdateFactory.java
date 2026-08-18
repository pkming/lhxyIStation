package com.amap.api.maps;

import android.graphics.Point;
import android.util.Log;
import com.amap.api.col.p0003sl.al;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.autonavi.amap.mapcore.IPoint;
import com.autonavi.amap.mapcore.VirtualEarthProjection;

/* JADX INFO: loaded from: classes2.dex */
public final class CameraUpdateFactory {
    private static final String CLASSNAME = "CameraUpdateFactory";

    public static CameraUpdate zoomIn() {
        return new CameraUpdate(al.a());
    }

    public static CameraUpdate zoomOut() {
        return new CameraUpdate(al.b());
    }

    public static CameraUpdate scrollBy(float f, float f2) {
        return new CameraUpdate(al.a(f, f2));
    }

    public static CameraUpdate zoomTo(float f) {
        return new CameraUpdate(al.a(f));
    }

    public static CameraUpdate zoomBy(float f) {
        return new CameraUpdate(al.b(f));
    }

    public static CameraUpdate zoomBy(float f, Point point) {
        return new CameraUpdate(al.a(f, point));
    }

    public static CameraUpdate newCameraPosition(CameraPosition cameraPosition) {
        if (cameraPosition == null) {
            Log.w(CLASSNAME, "cameraPosition is null");
            return new CameraUpdate(al.c());
        }
        return new CameraUpdate(al.a(cameraPosition));
    }

    public static CameraUpdate newLatLng(LatLng latLng) {
        if (latLng == null) {
            Log.w(CLASSNAME, "latLng is null");
            return new CameraUpdate(al.c());
        }
        return new CameraUpdate(al.a(latLng));
    }

    public static CameraUpdate newLatLngZoom(LatLng latLng, float f) {
        if (latLng == null) {
            Log.w(CLASSNAME, "target is null");
            return new CameraUpdate(al.c());
        }
        return new CameraUpdate(al.a(latLng, f));
    }

    public static CameraUpdate newLatLngBounds(LatLngBounds latLngBounds, int i) {
        if (latLngBounds == null) {
            Log.w(CLASSNAME, "bounds is null");
            return new CameraUpdate(al.c());
        }
        return new CameraUpdate(al.a(latLngBounds, i));
    }

    public static CameraUpdate changeLatLng(LatLng latLng) {
        if (latLng == null) {
            Log.w(CLASSNAME, "target is null");
            return new CameraUpdate(al.c());
        }
        return new CameraUpdate(al.a(VirtualEarthProjection.latLongToPixels(latLng.latitude, latLng.longitude, 20)));
    }

    public static CameraUpdate changeBearing(float f) {
        return new CameraUpdate(al.d(f % 360.0f));
    }

    public static CameraUpdate changeBearingGeoCenter(float f, IPoint iPoint) {
        if (iPoint == null) {
            Log.w(CLASSNAME, "geoPoint is null");
            return new CameraUpdate(al.c());
        }
        return new CameraUpdate(al.b(f % 360.0f, new Point(iPoint.x, iPoint.y)));
    }

    public static CameraUpdate changeTilt(float f) {
        return new CameraUpdate(al.c(f));
    }

    public static CameraUpdate newLatLngBounds(LatLngBounds latLngBounds, int i, int i2, int i3) {
        if (latLngBounds == null) {
            Log.w(CLASSNAME, "bounds is null");
            return new CameraUpdate(al.c());
        }
        return new CameraUpdate(al.a(latLngBounds, i, i2, i3));
    }

    public static CameraUpdate newLatLngBoundsRect(LatLngBounds latLngBounds, int i, int i2, int i3, int i4) {
        if (latLngBounds == null) {
            Log.w(CLASSNAME, "bounds is null");
            return new CameraUpdate(al.c());
        }
        return new CameraUpdate(al.a(latLngBounds, i, i2, i3, i4));
    }
}
