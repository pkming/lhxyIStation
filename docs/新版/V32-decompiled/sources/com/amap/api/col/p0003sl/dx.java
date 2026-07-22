package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.amap.api.col.p0003sl.is;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BaseHoleOptions;
import com.amap.api.maps.model.CircleHoleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolygonHoleOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.autonavi.amap.api.mapcore.IGLMapState;
import com.autonavi.amap.mapcore.AbstractCameraUpdateMessage;
import com.autonavi.amap.mapcore.DPoint;
import com.autonavi.amap.mapcore.IPoint;
import com.autonavi.amap.mapcore.VirtualEarthProjection;
import com.autonavi.amap.mapcore.interfaces.IMapConfig;
import com.autonavi.base.amap.mapcore.AeUtil;
import com.autonavi.base.amap.mapcore.FPoint;
import com.autonavi.base.amap.mapcore.FileUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: compiled from: Util.java */
/* JADX INFO: loaded from: classes2.dex */
public final class dx {
    private static FPoint[] a = {FPoint.obtain(), FPoint.obtain(), FPoint.obtain(), FPoint.obtain()};
    private static List<Float> b = new ArrayList(4);
    private static List<Float> c = new ArrayList(4);
    private static int d = 0;

    private static double a(double d2, double d3, double d4, double d5, double d6, double d7) {
        return ((d4 - d2) * (d7 - d3)) - ((d6 - d2) * (d5 - d3));
    }

    private static boolean a(double d2, double d3, double d4, double d5, double d6, double d7, double d8) {
        double d9 = d4 - d2;
        double d10 = d8 - d7;
        double d11 = d5 - d3;
        double d12 = 180.0d - d6;
        double d13 = (d9 * d10) - (d11 * d12);
        if (d13 != 0.0d) {
            double d14 = d3 - d7;
            double d15 = d2 - d6;
            double d16 = ((d12 * d14) - (d10 * d15)) / d13;
            double d17 = ((d14 * d9) - (d15 * d11)) / d13;
            if (d16 >= 0.0d && d16 <= 1.0d && d17 >= 0.0d && d17 <= 1.0d) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap a(Context context, String str) {
        try {
            InputStream inputStreamOpen = dr.a(context).open(str);
            Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpen);
            inputStreamOpen.close();
            return bitmapDecodeStream;
        } catch (Throwable th) {
            jw.c(th, "Util", "fromAsset");
            a(th);
            return null;
        }
    }

    public static void a(Drawable drawable) {
        if (drawable != null) {
            drawable.setCallback(null);
        }
    }

    public static String a(String str, Object obj) {
        return str + "=" + String.valueOf(obj);
    }

    public static float a(IMapConfig iMapConfig, float f, float f2) {
        boolean z;
        if (iMapConfig != null) {
            boolean zIsAbroadEnable = iMapConfig.isAbroadEnable();
            z = iMapConfig.getAbroadState() != 1;
            z = zIsAbroadEnable;
        } else {
            z = false;
        }
        float f3 = f >= 0.0f ? f : 0.0f;
        if (z && z) {
            if (f3 > 40.0f) {
                return 40.0f;
            }
            return f3;
        }
        if (iMapConfig != null && iMapConfig.isTerrainEnable()) {
            if (f3 > 80.0f) {
                return 80.0f;
            }
            return f3;
        }
        if (f <= 40.0f) {
            return f3;
        }
        float f4 = f2 <= 15.0f ? 40 : f2 <= 16.0f ? 56 : f2 <= 17.0f ? 66 : f2 <= 18.0f ? 74 : f2 <= 18.0f ? 78 : 80;
        return f3 > f4 ? f4 : f3;
    }

    public static float a(IMapConfig iMapConfig, float f) {
        if (iMapConfig != null) {
            if (f > iMapConfig.getMaxZoomLevel()) {
                return iMapConfig.getMaxZoomLevel();
            }
            return f < iMapConfig.getMinZoomLevel() ? iMapConfig.getMinZoomLevel() : f;
        }
        if (f > 20.0f) {
            return 20.0f;
        }
        if (f < 3.0f) {
            return 3.0f;
        }
        return f;
    }

    public static String a(String... strArr) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String str : strArr) {
            sb.append(str);
            if (i != strArr.length - 1) {
                sb.append(",");
            }
            i++;
        }
        return sb.toString();
    }

    public static int a(Object[] objArr) {
        return Arrays.hashCode(objArr);
    }

    public static Bitmap a(Bitmap bitmap, float f) {
        if (bitmap == null) {
            return null;
        }
        return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * f), (int) (bitmap.getHeight() * f), true);
    }

    public static String a(Context context) {
        File file = new File(FileUtil.getMapBaseStorage(context), AeUtil.ROOT_DATA_PATH_NAME);
        if (!file.exists()) {
            file.mkdir();
        }
        File file2 = new File(file.toString() + File.separator);
        if (!file2.exists()) {
            file2.mkdir();
        }
        return file.toString() + File.separator;
    }

    public static String b(Context context) {
        return FileUtil.getMapBaseStorage(context) + File.separator + "data" + File.separator;
    }

    public static String c(Context context) {
        String strA = a(context);
        if (strA == null) {
            return null;
        }
        File file = new File(strA, "VMAP2");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.toString() + File.separator;
    }

    public static String a(int i) {
        if (i < 1000) {
            return i + "m";
        }
        return (i / 1000) + "km";
    }

    public static boolean d(Context context) {
        ConnectivityManager connectivityManager;
        NetworkInfo activeNetworkInfo;
        NetworkInfo.State state;
        return (context == null || (connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)) == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null || (state = activeNetworkInfo.getState()) == null || state == NetworkInfo.State.DISCONNECTED || state == NetworkInfo.State.DISCONNECTING) ? false : true;
    }

    public static String a(InputStream inputStream) {
        try {
            return new String(b(inputStream), "utf-8");
        } catch (Throwable th) {
            jw.c(th, "Util", "decodeAssetResData");
            th.printStackTrace();
            return null;
        }
    }

    private static byte[] b(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[2048];
        while (true) {
            int i = inputStream.read(bArr, 0, 2048);
            if (i != -1) {
                byteArrayOutputStream.write(bArr, 0, i);
            } else {
                return byteArrayOutputStream.toByteArray();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:102:0x00dc  */
    /* JADX WARN: Removed duplicated region for block: B:103:0x00de A[Catch: IOException -> 0x00e2, TRY_ENTER, TRY_LEAVE, TryCatch #19 {IOException -> 0x00e2, blocks: (B:103:0x00de, B:94:0x00cd), top: B:116:0x00bf }] */
    /* JADX WARN: Removed duplicated region for block: B:128:0x00c1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0081 A[Catch: IOException -> 0x00b4, TRY_ENTER, TRY_LEAVE, TryCatch #9 {IOException -> 0x00b4, blocks: (B:10:0x0026, B:16:0x0031, B:56:0x0081, B:47:0x0070, B:78:0x00b0, B:69:0x009f, B:62:0x0093, B:9:0x0023, B:40:0x0064), top: B:117:0x0008, inners: #0, #12, #14 }] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x00b0 A[Catch: IOException -> 0x00b4, TRY_ENTER, TRY_LEAVE, TryCatch #9 {IOException -> 0x00b4, blocks: (B:10:0x0026, B:16:0x0031, B:56:0x0081, B:47:0x0070, B:78:0x00b0, B:69:0x009f, B:62:0x0093, B:9:0x0023, B:40:0x0064), top: B:117:0x0008, inners: #0, #12, #14 }] */
    /* JADX WARN: Type inference failed for: r7v1, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r7v11, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r7v13, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r7v15, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r7v18 */
    /* JADX WARN: Type inference failed for: r7v21 */
    /* JADX WARN: Type inference failed for: r7v23 */
    /* JADX WARN: Type inference failed for: r7v24, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r7v25 */
    /* JADX WARN: Type inference failed for: r7v26 */
    /* JADX WARN: Type inference failed for: r7v27 */
    /* JADX WARN: Type inference failed for: r7v28 */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v7 */
    /* JADX WARN: Type inference failed for: r7v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String a(java.io.File r7) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 231
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.dx.a(java.io.File):java.lang.String");
    }

    public static boolean a(LatLng latLng, List<LatLng> list) throws Throwable {
        boolean z;
        List<LatLng> list2 = list;
        if (latLng == null || list2 == null) {
            return false;
        }
        double d2 = latLng.longitude;
        double d3 = latLng.latitude;
        double d4 = latLng.latitude;
        if (list.size() < 3) {
            return false;
        }
        if (list2.get(0).equals(list2.get(list.size() - 1))) {
            z = false;
        } else {
            list2.add(list2.get(0));
            z = true;
        }
        int i = 0;
        int i2 = 0;
        while (i < list.size() - 1) {
            try {
                double d5 = list2.get(i).longitude;
                double d6 = list2.get(i).latitude;
                int i3 = i + 1;
                double d7 = list2.get(i3).longitude;
                try {
                    double d8 = list2.get(i3).latitude;
                    double d9 = d4;
                    double d10 = d3;
                    double d11 = d2;
                    if (b(d2, d3, d5, d6, d7, d8)) {
                        if (z) {
                            list.remove(list.size() - 1);
                        }
                        return true;
                    }
                    list2 = list;
                    if (Math.abs(d8 - d6) >= 1.0E-9d) {
                        if (b(d5, d6, d11, d10, 180.0d, d9)) {
                            if (d6 > d8) {
                                i2++;
                            }
                        } else if (b(d7, d8, d11, d10, 180.0d, d9)) {
                            if (d8 > d6) {
                                i2++;
                            }
                        } else if (a(d5, d6, d7, d8, d11, d10, d9)) {
                            i2++;
                        }
                    }
                    i = i3;
                    d4 = d9;
                    d3 = d10;
                    d2 = d11;
                } catch (Throwable th) {
                    th = th;
                    list2 = list;
                    if (z) {
                        list2.remove(list.size() - 1);
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
        boolean z2 = i2 % 2 != 0;
        if (z) {
            list2.remove(list.size() - 1);
        }
        return z2;
    }

    private static boolean b(double d2, double d3, double d4, double d5, double d6, double d7) {
        return Math.abs(a(d2, d3, d4, d5, d6, d7)) < 1.0E-9d && (d2 - d4) * (d2 - d6) <= 0.0d && (d3 - d5) * (d3 - d7) <= 0.0d;
    }

    public static boolean a(BaseHoleOptions baseHoleOptions, LatLng latLng) {
        if (baseHoleOptions instanceof CircleHoleOptions) {
            CircleHoleOptions circleHoleOptions = (CircleHoleOptions) baseHoleOptions;
            LatLng center = circleHoleOptions.getCenter();
            return center != null && ((double) AMapUtils.calculateLineDistance(center, latLng)) <= circleHoleOptions.getRadius();
        }
        List<LatLng> points = ((PolygonHoleOptions) baseHoleOptions).getPoints();
        if (points == null || points.size() == 0) {
            return false;
        }
        return a(latLng, points);
    }

    public static is a() {
        try {
            if (w.e == null) {
                w.e = new is.a("3dmap", "10.0.600", w.c).a(new String[]{"com.amap.api.maps", "com.amap.api.mapcore", "com.autonavi.amap.mapcore", "com.autonavi.amap", "com.autonavi.ae", "com.autonavi.base", "com.autonavi.patch", "com.amap.api.3dmap.admic", "com.amap.api.trace", "com.amap.api.trace.core"}).a("10.0.600").a();
            }
            return w.e;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static void c(View view) {
        int i = 0;
        if (!(view instanceof ViewGroup)) {
            if (view instanceof TextView) {
                ((TextView) view).setHorizontallyScrolling(false);
            }
        } else {
            while (true) {
                ViewGroup viewGroup = (ViewGroup) view;
                if (i >= viewGroup.getChildCount()) {
                    return;
                }
                c(viewGroup.getChildAt(i));
                i++;
            }
        }
    }

    public static Bitmap a(View view) {
        try {
            c(view);
            view.destroyDrawingCache();
            view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            Bitmap drawingCache = view.getDrawingCache();
            if (drawingCache != null) {
                return drawingCache.copy(Bitmap.Config.ARGB_8888, false);
            }
            return null;
        } catch (Throwable th) {
            jw.c(th, "Utils", "getBitmapFromView");
            th.printStackTrace();
            return null;
        }
    }

    public static DPoint a(LatLng latLng) {
        double d2 = (latLng.longitude / 360.0d) + 0.5d;
        double dSin = Math.sin(Math.toRadians(latLng.latitude));
        return DPoint.obtain(d2 * 1.0d, (((Math.log((dSin + 1.0d) / (1.0d - dSin)) * 0.5d) / (-6.283185307179586d)) + 0.5d) * 1.0d);
    }

    public static boolean a(Rect rect, int i, int i2) {
        return rect.contains(i, i2);
    }

    public static Pair<Float, IPoint> a(AbstractCameraUpdateMessage abstractCameraUpdateMessage, IMapConfig iMapConfig) {
        return a(iMapConfig, Math.max(abstractCameraUpdateMessage.paddingLeft, 1), Math.max(abstractCameraUpdateMessage.paddingRight, 1), Math.max(abstractCameraUpdateMessage.paddingTop, 1), Math.max(abstractCameraUpdateMessage.paddingBottom, 1), abstractCameraUpdateMessage.bounds, abstractCameraUpdateMessage.width, abstractCameraUpdateMessage.height);
    }

    public static Pair<Float, IPoint> a(IMapConfig iMapConfig, int i, int i2, int i3, int i4, LatLngBounds latLngBounds, int i5, int i6) {
        int i7;
        float f;
        float f2;
        int i8;
        if (latLngBounds == null || latLngBounds.northeast == null || latLngBounds.southwest == null || iMapConfig == null) {
            return null;
        }
        Point pointLatLongToPixels = VirtualEarthProjection.latLongToPixels(latLngBounds.northeast.latitude, latLngBounds.northeast.longitude, 20);
        Point pointLatLongToPixels2 = VirtualEarthProjection.latLongToPixels(latLngBounds.southwest.latitude, latLngBounds.southwest.longitude, 20);
        int i9 = pointLatLongToPixels.x - pointLatLongToPixels2.x;
        int i10 = pointLatLongToPixels2.y - pointLatLongToPixels.y;
        int i11 = i5 - (i + i2);
        int i12 = i6 - (i3 + i4);
        if (i9 < 0 && i10 < 0) {
            return null;
        }
        int i13 = i9 <= 0 ? 1 : i9;
        int i14 = i10 <= 0 ? 1 : i10;
        Pair<Float, Boolean> pairB = b(iMapConfig, pointLatLongToPixels.x, pointLatLongToPixels.y, pointLatLongToPixels2.x, pointLatLongToPixels2.y, i11 <= 0 ? 1 : i11, i12 <= 0 ? 1 : i12);
        float fFloatValue = pairB.first.floatValue();
        boolean zBooleanValue = pairB.second.booleanValue();
        float fA = a(iMapConfig.getMapZoomScale(), fFloatValue, i13);
        float fA2 = a(iMapConfig.getMapZoomScale(), fFloatValue, i14);
        if (fFloatValue >= iMapConfig.getMaxZoomLevel()) {
            i7 = (int) (pointLatLongToPixels2.x + ((((i2 - i) + fA) * i13) / (fA * 2.0f)));
            i8 = pointLatLongToPixels.y;
        } else if (zBooleanValue) {
            i7 = (int) (pointLatLongToPixels2.x + ((((i5 / 2) - i) / fA) * i13));
            i8 = pointLatLongToPixels.y;
        } else {
            i7 = (int) (pointLatLongToPixels2.x + ((((i2 - i) + fA) * i13) / (fA * 2.0f)));
            f = pointLatLongToPixels.y;
            f2 = (((i6 / 2) - i3) / fA2) * i14;
            return new Pair<>(Float.valueOf(fFloatValue), IPoint.obtain((int) (i7 + a(iMapConfig.getMapZoomScale(), fFloatValue, iMapConfig.getAnchorX() - (iMapConfig.getMapWidth() >> 1))), (int) (((int) (f + f2)) + a(iMapConfig.getMapZoomScale(), fFloatValue, iMapConfig.getAnchorY() - (iMapConfig.getMapHeight() >> 1)))));
        }
        f = i8;
        f2 = (((i4 - i3) + fA2) * i14) / (fA2 * 2.0f);
        return new Pair<>(Float.valueOf(fFloatValue), IPoint.obtain((int) (i7 + a(iMapConfig.getMapZoomScale(), fFloatValue, iMapConfig.getAnchorX() - (iMapConfig.getMapWidth() >> 1))), (int) (((int) (f + f2)) + a(iMapConfig.getMapZoomScale(), fFloatValue, iMapConfig.getAnchorY() - (iMapConfig.getMapHeight() >> 1)))));
    }

    private static double a(float f, double d2, double d3) {
        return 20.0d - (Math.log(d3 / (d2 * ((double) f))) / Math.log(2.0d));
    }

    private static float a(float f, float f2, double d2) {
        return (float) (d2 / (Math.pow(2.0d, 20.0f - f2) * ((double) f)));
    }

    private static float a(float f, float f2, float f3) {
        return (float) (((double) f3) * Math.pow(2.0d, 20.0f - f2) * ((double) f));
    }

    private static Pair<Float, Boolean> b(IMapConfig iMapConfig, int i, int i2, int i3, int i4, int i5, int i6) {
        float fMin;
        iMapConfig.getSZ();
        if (i == i3 && i2 == i4) {
            fMin = iMapConfig.getMaxZoomLevel();
        } else {
            float fA = (float) a(iMapConfig.getMapZoomScale(), i6, Math.abs(i4 - i2));
            float fA2 = (float) a(iMapConfig.getMapZoomScale(), i5, Math.abs(i3 - i));
            float fMin2 = Math.min(fA2, fA);
            z = fMin2 == fA2;
            fMin = Math.min(iMapConfig.getMaxZoomLevel(), Math.max(iMapConfig.getMinZoomLevel(), fMin2));
        }
        return new Pair<>(Float.valueOf(fMin), Boolean.valueOf(z));
    }

    public static float a(IMapConfig iMapConfig, int i, int i2, int i3, int i4, int i5, int i6) {
        float sz = iMapConfig.getSZ();
        if (i == i3 || i2 == i4) {
            return sz;
        }
        return Math.max((float) a(iMapConfig.getMapZoomScale(), i5, Math.abs(i3 - i)), (float) a(iMapConfig.getMapZoomScale(), i6, Math.abs(i4 - i2)));
    }

    public static boolean a(int i, int i2) {
        if (i > 0 && i2 > 0) {
            return true;
        }
        Log.w("3dmap", "the map must have a size");
        return false;
    }

    public static float a(IGLMapState iGLMapState, int i, int i2, double d2, double d3, int i3) {
        IPoint iPointObtain = IPoint.obtain();
        VirtualEarthProjection.latLongToPixels(d2, d3, 20, iPointObtain);
        float fA = a(iGLMapState, i, i2, iPointObtain.x, iPointObtain.y, i3);
        iPointObtain.recycle();
        return fA;
    }

    private static float a(IGLMapState iGLMapState, int i, int i2, int i3, int i4, int i5) {
        if (iGLMapState != null) {
            return iGLMapState.calculateMapZoomer(i, i2, i3, i4, i5);
        }
        return 3.0f;
    }

    public static synchronized int[] a(int i, int i2, int i3, int i4, IMapConfig iMapConfig, IGLMapState iGLMapState, int i5, int i6) {
        int mapWidth;
        int mapHeight;
        mapWidth = iMapConfig.getMapWidth();
        mapHeight = iMapConfig.getMapHeight();
        return new int[]{(int) Math.max(i3 + a(iMapConfig.getMapZoomScale(), iGLMapState.getMapZoomer(), iMapConfig.getAnchorX()), Math.min(i5, i - a(iMapConfig.getMapZoomScale(), iGLMapState.getMapZoomer(), mapWidth - r3))), (int) Math.max(i2 + a(iMapConfig.getMapZoomScale(), iGLMapState.getMapZoomer(), iMapConfig.getAnchorY()), Math.min(i6, i4 - a(iMapConfig.getMapZoomScale(), iGLMapState.getMapZoomer(), mapHeight - r4)))};
    }

    public static byte[] a(byte[] bArr, int i) {
        return a(bArr, i, i, true);
    }

    public static byte[] a(byte[] bArr, int i, int i2, boolean z) {
        try {
            Bitmap bitmapDecodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
            Bitmap bitmapCopy = bitmapDecodeByteArray.copy(bitmapDecodeByteArray.getConfig(), true);
            int width = bitmapDecodeByteArray.getWidth();
            int height = bitmapDecodeByteArray.getHeight();
            for (int i3 = 0; i3 < width; i3++) {
                for (int i4 = 0; i4 < height; i4++) {
                    if (i3 != 0 && i4 != 0) {
                        bitmapCopy.setPixel(i3, i4, i);
                    } else if (!z) {
                        bitmapCopy.setPixel(i3, i4, i2);
                    }
                }
            }
            byte[] bArrB = b(bitmapCopy);
            if (bArrB == null) {
                bArrB = bArr;
            }
            a(bitmapCopy);
            a(bitmapDecodeByteArray);
            return bArrB;
        } catch (Throwable th) {
            th.printStackTrace();
            return bArr;
        }
    }

    private static byte[] b(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
        } catch (Throwable unused) {
            byteArrayOutputStream = null;
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
            } catch (Throwable th) {
                th.printStackTrace();
            }
            return byteArray;
        } catch (Throwable unused2) {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th2) {
                    th2.printStackTrace();
                }
            }
            return null;
        }
    }

    public static boolean a(List<BaseHoleOptions> list, PolygonHoleOptions polygonHoleOptions) {
        boolean zB = false;
        for (int i = 0; i < list.size(); i++) {
            BaseHoleOptions baseHoleOptions = list.get(i);
            if (baseHoleOptions instanceof PolygonHoleOptions) {
                zB = a(((PolygonHoleOptions) baseHoleOptions).getPoints(), polygonHoleOptions.getPoints());
                if (zB) {
                    return true;
                }
            } else if (baseHoleOptions instanceof CircleHoleOptions) {
                zB = b(polygonHoleOptions.getPoints(), (CircleHoleOptions) baseHoleOptions);
                if (zB) {
                    return true;
                }
            } else {
                continue;
            }
        }
        return zB;
    }

    public static boolean a(List<BaseHoleOptions> list, CircleHoleOptions circleHoleOptions) {
        boolean zA = false;
        for (int i = 0; i < list.size(); i++) {
            BaseHoleOptions baseHoleOptions = list.get(i);
            if (baseHoleOptions instanceof PolygonHoleOptions) {
                zA = b(((PolygonHoleOptions) baseHoleOptions).getPoints(), circleHoleOptions);
                if (zA) {
                    return true;
                }
            } else if ((baseHoleOptions instanceof CircleHoleOptions) && (zA = a(circleHoleOptions, (CircleHoleOptions) baseHoleOptions))) {
                return true;
            }
        }
        return zA;
    }

    private static boolean a(CircleHoleOptions circleHoleOptions, CircleHoleOptions circleHoleOptions2) {
        try {
            return ((double) AMapUtils.calculateLineDistance(circleHoleOptions2.getCenter(), circleHoleOptions.getCenter())) < circleHoleOptions.getRadius() + circleHoleOptions2.getRadius();
        } catch (Throwable th) {
            jw.c(th, "Util", "isPolygon2CircleIntersect");
            th.printStackTrace();
            return false;
        }
    }

    private static boolean b(List<LatLng> list, CircleHoleOptions circleHoleOptions) {
        int i;
        try {
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < list.size(); i2++) {
                arrayList.add(list.get(i2));
            }
            arrayList.add(list.get(0));
            ArrayList arrayList2 = new ArrayList();
            int i3 = 0;
            while (i3 < arrayList.size() && (i = i3 + 1) < arrayList.size()) {
                if (circleHoleOptions.getRadius() < AMapUtils.calculateLineDistance(circleHoleOptions.getCenter(), (LatLng) arrayList.get(i3)) && circleHoleOptions.getRadius() < AMapUtils.calculateLineDistance(circleHoleOptions.getCenter(), (LatLng) arrayList.get(i))) {
                    arrayList2.clear();
                    arrayList2.add(arrayList.get(i3));
                    arrayList2.add(arrayList.get(i));
                    if (circleHoleOptions.getRadius() >= ((double) AMapUtils.calculateLineDistance(circleHoleOptions.getCenter(), SpatialRelationUtil.calShortestDistancePoint(arrayList2, circleHoleOptions.getCenter()).second))) {
                        return true;
                    }
                    i3 = i;
                }
                return true;
            }
        } catch (Throwable th) {
            jw.c(th, "Util", "isPolygon2CircleIntersect");
            th.printStackTrace();
        }
        return false;
    }

    private static boolean a(List<LatLng> list, List<LatLng> list2) {
        for (int i = 0; i < list2.size(); i++) {
            try {
                if (a(list2.get(i), list)) {
                    return true;
                }
            } catch (Throwable th) {
                jw.c(th, "Util", "isPolygon2PolygonIntersect");
                th.printStackTrace();
                return false;
            }
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (a(list.get(i2), list2)) {
                return true;
            }
        }
        return b(list, list2);
    }

    private static boolean b(List<LatLng> list, List<LatLng> list2) {
        int i;
        int i2;
        int i3 = 0;
        while (i3 < list.size() && (i = i3 + 1) < list.size()) {
            try {
                int i4 = 0;
                while (i4 < list2.size() && (i2 = i4 + 1) < list2.size()) {
                    boolean zA = ds.a(list.get(i3), list.get(i), list2.get(i4), list2.get(i2));
                    if (zA) {
                        return zA;
                    }
                    i4 = i2;
                }
                i3 = i;
            } catch (Throwable th) {
                jw.c(th, "Util", "isSegmentsIntersect");
                th.printStackTrace();
            }
        }
        return false;
    }

    public static boolean e(Context context) {
        File file = new File(b(context));
        if (file.exists()) {
            return FileUtil.deleteFile(file);
        }
        return true;
    }

    public static float a(DPoint dPoint, DPoint dPoint2) {
        if (dPoint == null || dPoint2 == null) {
            return 0.0f;
        }
        double d2 = dPoint.x;
        double d3 = dPoint2.x;
        return (float) ((Math.atan2(dPoint2.y - dPoint.y, d3 - d2) / 3.141592653589793d) * 180.0d);
    }

    public static boolean b(List<LatLng> list, PolygonHoleOptions polygonHoleOptions) {
        boolean z = false;
        if (list == null || polygonHoleOptions == null) {
            return false;
        }
        try {
            List<LatLng> points = polygonHoleOptions.getPoints();
            boolean zA = false;
            for (int i = 0; i < points.size(); i++) {
                try {
                    zA = a(points.get(i), list);
                    if (!zA) {
                        return zA;
                    }
                } catch (Throwable th) {
                    th = th;
                    z = zA;
                    jw.c(th, "PolygonDelegateImp", "isPolygonInPolygon");
                    th.printStackTrace();
                    return z;
                }
            }
            return zA;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean a(List<LatLng> list, List<BaseHoleOptions> list2, CircleHoleOptions circleHoleOptions) {
        try {
            if (b(list, circleHoleOptions)) {
                return false;
            }
            return a(list, list2, circleHoleOptions.getCenter());
        } catch (Throwable th) {
            jw.c(th, "PolygonDelegateImp", "isCircleInPolygon");
            th.printStackTrace();
            return false;
        }
    }

    private static boolean a(List<LatLng> list, List<BaseHoleOptions> list2, LatLng latLng) throws RemoteException {
        if (latLng == null) {
            return false;
        }
        if (list2 != null) {
            try {
                if (list2.size() > 0) {
                    Iterator<BaseHoleOptions> it = list2.iterator();
                    while (it.hasNext()) {
                        if (a(it.next(), latLng)) {
                            return false;
                        }
                    }
                }
            } catch (Throwable th) {
                jw.c(th, "PolygonDelegateImp", "contains");
                th.printStackTrace();
                return false;
            }
        }
        return a(latLng, list);
    }

    public static boolean a(double d2, LatLng latLng, List<BaseHoleOptions> list, PolygonHoleOptions polygonHoleOptions) {
        boolean zA = true;
        try {
            List<LatLng> points = polygonHoleOptions.getPoints();
            for (int i = 0; i < points.size() && (zA = a(d2, latLng, list, points.get(i))); i++) {
            }
        } catch (Throwable th) {
            jw.c(th, "CircleDelegateImp", "isPolygonInCircle");
            th.printStackTrace();
        }
        return zA;
    }

    public static boolean a(double d2, LatLng latLng, CircleHoleOptions circleHoleOptions) {
        try {
            return ((double) AMapUtils.calculateLineDistance(circleHoleOptions.getCenter(), latLng)) <= d2 - circleHoleOptions.getRadius();
        } catch (Throwable th) {
            jw.c(th, "CircleDelegateImp", "isCircleInCircle");
            th.printStackTrace();
            return true;
        }
    }

    private static boolean a(double d2, LatLng latLng, List<BaseHoleOptions> list, LatLng latLng2) throws RemoteException {
        if (list != null && list.size() > 0) {
            Iterator<BaseHoleOptions> it = list.iterator();
            while (it.hasNext()) {
                if (a(it.next(), latLng2)) {
                    return false;
                }
            }
        }
        return d2 >= ((double) AMapUtils.calculateLineDistance(latLng, latLng2));
    }

    public static void a(Bitmap bitmap) {
        if (bitmap == null || Build.VERSION.SDK_INT > 10 || bitmap.isRecycled()) {
            return;
        }
        bitmap.recycle();
    }

    public static void a(Throwable th) {
        try {
            if (MapsInitializer.getExceptionLogger() != null) {
                MapsInitializer.getExceptionLogger().onException(th);
            }
        } catch (Throwable unused) {
        }
    }

    public static String a(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            Uri uri = Uri.parse(str);
            if (uri.getAuthority() != null && uri.getAuthority().startsWith("dualstack-")) {
                return str;
            }
            if (uri.getAuthority() != null && uri.getAuthority().startsWith("restsdk.amap.com")) {
                return uri.buildUpon().authority("dualstack-arestapi.amap.com").build().toString();
            }
            return uri.buildUpon().authority("dualstack-" + uri.getAuthority()).build().toString();
        } catch (Throwable unused) {
            return str;
        }
    }

    public static Bitmap a(int[] iArr, int i, int i2) {
        return a(iArr, i, i2, false);
    }

    public static Bitmap a(int[] iArr, int i, int i2, boolean z) {
        try {
            int[] iArr2 = new int[iArr.length];
            for (int i3 = 0; i3 < i2; i3++) {
                for (int i4 = 0; i4 < i; i4++) {
                    int i5 = (i3 * i) + i4;
                    int i6 = iArr[i5];
                    int i7 = (i6 & Color.GREEN) | ((i6 << 16) & Spanned.SPAN_PRIORITY) | ((i6 >> 16) & 255);
                    if (z) {
                        iArr2[(((i2 - i3) - 1) * i) + i4] = i7;
                    } else {
                        iArr2[i5] = i7;
                    }
                }
            }
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
            bitmapCreateBitmap.setPixels(iArr2, 0, i, 0, 0, i, i2);
            return bitmapCreateBitmap;
        } catch (Throwable th) {
            jw.c(th, "Util", "rgbaToArgb");
            th.printStackTrace();
            return null;
        }
    }

    public static String b(View view) {
        StringBuilder sb = new StringBuilder();
        if (view != null) {
            try {
                if (view instanceof TextView) {
                    sb = new StringBuilder(((TextView) view).getText().toString());
                }
                if (view instanceof ViewGroup) {
                    int childCount = ((ViewGroup) view).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        String strB = b(((ViewGroup) view).getChildAt(i));
                        if (!TextUtils.isEmpty(strB)) {
                            sb.append("--");
                            sb.append(strB);
                        }
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static synchronized int b() {
        int i = d + 1;
        d = i;
        if (i == Integer.MAX_VALUE) {
            d = 0;
        }
        return d;
    }
}
