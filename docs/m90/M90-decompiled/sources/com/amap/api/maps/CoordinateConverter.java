package com.amap.api.maps;

import android.content.Context;
import com.amap.api.col.p0003sl.am;
import com.amap.api.col.p0003sl.dq;
import com.amap.api.col.p0003sl.du;
import com.amap.api.col.p0003sl.jw;
import com.amap.api.maps.model.LatLng;

/* JADX INFO: loaded from: classes2.dex */
public class CoordinateConverter {
    private static final String TAG = "CoordinateConverter";
    private Context ctx;
    private CoordType coordType = null;
    private LatLng sourceLatLng = null;

    public enum CoordType {
        BAIDU,
        MAPBAR,
        GPS,
        MAPABC,
        SOSOMAP,
        ALIYUN,
        GOOGLE
    }

    public CoordinateConverter(Context context) {
        this.ctx = context;
    }

    public CoordinateConverter from(CoordType coordType) {
        this.coordType = coordType;
        return this;
    }

    public CoordinateConverter coord(LatLng latLng) {
        this.sourceLatLng = latLng;
        return this;
    }

    public LatLng convert() {
        LatLng latLngA = null;
        if (this.coordType == null || this.sourceLatLng == null) {
            return null;
        }
        try {
            String str = "";
            switch (AnonymousClass1.a[this.coordType.ordinal()]) {
                case 1:
                    latLngA = am.a(this.sourceLatLng);
                    str = "baidu";
                    break;
                case 2:
                    latLngA = am.b(this.ctx, this.sourceLatLng);
                    str = "mapbar";
                    break;
                case 3:
                    str = "mapabc";
                    latLngA = this.sourceLatLng;
                    break;
                case 4:
                    str = "sosomap";
                    latLngA = this.sourceLatLng;
                    break;
                case 5:
                    str = "aliyun";
                    latLngA = this.sourceLatLng;
                    break;
                case 6:
                    str = "google";
                    latLngA = this.sourceLatLng;
                    break;
                case 7:
                    str = "gps";
                    latLngA = am.a(this.ctx, this.sourceLatLng);
                    break;
            }
            du.a(this.ctx, str);
            return latLngA;
        } catch (Throwable th) {
            th.printStackTrace();
            jw.c(th, TAG, "convert");
            return this.sourceLatLng;
        }
    }

    /* JADX INFO: renamed from: com.amap.api.maps.CoordinateConverter$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] a;

        static {
            int[] iArr = new int[CoordType.values().length];
            a = iArr;
            try {
                iArr[CoordType.BAIDU.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                a[CoordType.MAPBAR.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                a[CoordType.MAPABC.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                a[CoordType.SOSOMAP.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                a[CoordType.ALIYUN.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                a[CoordType.GOOGLE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                a[CoordType.GPS.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    public static boolean isAMapDataAvailable(double d, double d2) {
        return dq.a(d, d2);
    }
}
