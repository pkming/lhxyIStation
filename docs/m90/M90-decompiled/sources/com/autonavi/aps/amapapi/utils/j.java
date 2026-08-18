package com.autonavi.aps.amapapi.utils;

import android.Manifest;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.ig;
import com.amap.api.col.p0003sl.ik;
import com.amap.api.col.p0003sl.il;
import com.amap.api.col.p0003sl.it;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.DPoint;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.json.JSONObject;

/* JADX INFO: compiled from: Utils.java */
/* JADX INFO: loaded from: classes2.dex */
public final class j {
    static WifiManager a;
    private static int b;
    private static String[] c;
    private static String d;

    public static float a(float f) {
        return (float) (((long) (((double) f) * 100.0d)) / 100.0d);
    }

    /*  JADX ERROR: UnsupportedOperationException in pass: RegionMakerVisitor
        java.lang.UnsupportedOperationException
        	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1068)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker$1.leaveRegion(SwitchRegionMaker.java:390)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:23)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaksForCase(SwitchRegionMaker.java:370)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaks(SwitchRegionMaker.java:85)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.leaveRegion(PostProcessRegions.java:33)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:19)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.process(PostProcessRegions.java:23)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:31)
        */
    public static java.lang.String a(int r2) {
        /*
            r0 = 33
            java.lang.String r1 = "其他错误"
            if (r2 == r0) goto L43
            switch(r2) {
                case 0: goto L40;
                case 1: goto L3d;
                case 2: goto L3a;
                case 3: goto L37;
                case 4: goto L34;
                case 5: goto L31;
                case 6: goto L2e;
                case 7: goto L2b;
                case 8: goto L45;
                case 9: goto L28;
                case 10: goto L25;
                case 11: goto L22;
                case 12: goto L1f;
                case 13: goto L1c;
                case 14: goto L19;
                case 15: goto L16;
                default: goto L9;
            }
        L9:
            switch(r2) {
                case 18: goto L13;
                case 19: goto L10;
                case 20: goto Ld;
                default: goto Lc;
            }
        Lc:
            goto L45
        Ld:
            java.lang.String r1 = "模糊定位失败，具体可查看错误信息/详细信息描述"
            goto L45
        L10:
            java.lang.String r1 = "定位失败，没有检查到SIM卡，并且关闭了WIFI开关，请打开WIFI开关或者插入SIM卡"
            goto L45
        L13:
            java.lang.String r1 = "定位失败，飞行模式下关闭了WIFI开关，请关闭飞行模式或者打开WIFI开关"
            goto L45
        L16:
            java.lang.String r1 = "当前返回位置为模拟软件返回，请关闭模拟软件，或者在option中设置允许模拟"
            goto L45
        L19:
            java.lang.String r1 = "GPS 定位失败，由于设备当前 GPS 状态差,建议持设备到相对开阔的露天场所再次尝试"
            goto L45
        L1c:
            java.lang.String r1 = "网络定位失败，请检查设备是否插入sim卡，是否开启移动网络或开启了wifi模块"
            goto L45
        L1f:
            java.lang.String r1 = "缺少定位权限"
            goto L45
        L22:
            java.lang.String r1 = "错误的基站信息，请检查是否插入SIM卡"
            goto L45
        L25:
            java.lang.String r1 = "定位服务启动失败"
            goto L45
        L28:
            java.lang.String r1 = "初始化异常"
            goto L45
        L2b:
            java.lang.String r1 = "KEY错误"
            goto L45
        L2e:
            java.lang.String r1 = "定位结果错误"
            goto L45
        L31:
            java.lang.String r1 = "解析数据异常"
            goto L45
        L34:
            java.lang.String r1 = "网络连接异常"
            goto L45
        L37:
            java.lang.String r1 = "请求参数获取出现异常"
            goto L45
        L3a:
            java.lang.String r1 = "WIFI信息不足"
            goto L45
        L3d:
            java.lang.String r1 = "重要参数为空"
            goto L45
        L40:
            java.lang.String r1 = "success"
            goto L45
        L43:
            java.lang.String r1 = "补偿定位失败，未命中缓存"
        L45:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.utils.j.a(int):java.lang.String");
    }

    public static double b(double d2) {
        return ((long) (d2 * 1000000.0d)) / 1000000.0d;
    }

    public static double c(double d2) {
        return ((long) (d2 * 100.0d)) / 100.0d;
    }

    public static boolean a(com.autonavi.aps.amapapi.model.a aVar) {
        if (aVar == null || "8".equals(aVar.d()) || "5".equals(aVar.d()) || "6".equals(aVar.d())) {
            return false;
        }
        return b(aVar);
    }

    public static boolean a(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            return b(aMapLocation);
        }
        return false;
    }

    public static boolean b(AMapLocation aMapLocation) {
        double longitude = aMapLocation.getLongitude();
        double latitude = aMapLocation.getLatitude();
        return !(longitude == 0.0d && latitude == 0.0d) && longitude <= 180.0d && latitude <= 90.0d && longitude >= -180.0d && latitude >= -90.0d;
    }

    public static String[] a(TelephonyManager telephonyManager) {
        int i;
        String[] strArr;
        String networkOperator = telephonyManager != null ? telephonyManager.getNetworkOperator() : null;
        String[] strArr2 = {"0", "0"};
        if (!TextUtils.isEmpty(networkOperator) && TextUtils.isDigitsOnly(networkOperator) && networkOperator.length() > 4) {
            strArr2[0] = networkOperator.substring(0, 3);
            char[] charArray = networkOperator.substring(3).toCharArray();
            int i2 = 0;
            while (i2 < charArray.length && Character.isDigit(charArray[i2])) {
                i2++;
            }
            strArr2[1] = networkOperator.substring(3, i2 + 3);
        }
        try {
            i = Integer.parseInt(strArr2[0]);
        } catch (Throwable th) {
            b.a(th, "Utils", "getMccMnc");
            i = 0;
        }
        if (i == 0) {
            strArr2[0] = "0";
        }
        if ("0".equals(strArr2[0]) || "0".equals(strArr2[1])) {
            return ("0".equals(strArr2[0]) && "0".equals(strArr2[1]) && (strArr = c) != null) ? strArr : strArr2;
        }
        c = strArr2;
        return strArr2;
    }

    public static long a() {
        return System.currentTimeMillis();
    }

    public static long b() {
        return SystemClock.elapsedRealtime();
    }

    public static boolean a(Context context) {
        if (context == null) {
            return false;
        }
        try {
            if (c() < 17) {
                return d(context, "android.provider.Settings$System");
            }
            return d(context, "android.provider.Settings$Global");
        } catch (Throwable unused) {
            return false;
        }
    }

    private static boolean d(Context context, String str) throws Throwable {
        return ((Integer) f.a(str, "getInt", new Object[]{context.getContentResolver(), ((String) f.a(str, "AIRPLANE_MODE_ON")).toString()}, (Class<?>[]) new Class[]{ContentResolver.class, String.class})).intValue() == 1;
    }

    public static float a(double[] dArr) {
        float[] fArr = new float[1];
        Location.distanceBetween(dArr[0], dArr[1], dArr[2], dArr[3], fArr);
        return fArr[0];
    }

    public static float a(AMapLocation aMapLocation, AMapLocation aMapLocation2) {
        return a(new double[]{aMapLocation.getLatitude(), aMapLocation.getLongitude(), aMapLocation2.getLatitude(), aMapLocation2.getLongitude()});
    }

    public static float a(DPoint dPoint, DPoint dPoint2) {
        return a(new double[]{dPoint.getLatitude(), dPoint.getLongitude(), dPoint2.getLatitude(), dPoint2.getLongitude()});
    }

    public static Object a(Context context, String str) {
        if (context == null) {
            return null;
        }
        try {
            return context.getApplicationContext().getSystemService(str);
        } catch (Throwable th) {
            b.a(th, "Utils", "getServ");
            return null;
        }
    }

    public static int c() {
        int i = b;
        if (i > 0) {
            return i;
        }
        try {
            try {
                return f.b("android.os.Build$VERSION", "SDK_INT");
            } catch (Throwable unused) {
                return 0;
            }
        } catch (Throwable unused2) {
            return Integer.parseInt(f.a("android.os.Build$VERSION", "SDK").toString());
        }
    }

    public static byte[] a(byte[] bArr) {
        return it.b(bArr);
    }

    public static String b(Context context) {
        PackageInfo packageInfo;
        if (!TextUtils.isEmpty(b.j)) {
            return b.j;
        }
        if (context == null) {
            return null;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(ig.c(context), 64);
        } catch (Throwable th) {
            b.a(th, "Utils", "getAppName part");
            packageInfo = null;
        }
        try {
            if (TextUtils.isEmpty(b.k)) {
                b.k = null;
            }
        } catch (Throwable th2) {
            b.a(th2, "Utils", "getAppName");
        }
        StringBuilder sb = new StringBuilder();
        if (packageInfo != null) {
            CharSequence charSequenceLoadLabel = packageInfo.applicationInfo != null ? packageInfo.applicationInfo.loadLabel(context.getPackageManager()) : null;
            if (charSequenceLoadLabel != null) {
                sb.append(charSequenceLoadLabel.toString());
            }
            if (!TextUtils.isEmpty(packageInfo.versionName)) {
                sb.append(packageInfo.versionName);
            }
        }
        String strC = ig.c(context);
        if (!TextUtils.isEmpty(strC)) {
            sb.append(",").append(strC);
        }
        if (!TextUtils.isEmpty(b.k)) {
            sb.append(",").append(b.k);
        }
        String string = sb.toString();
        b.j = string;
        return string;
    }

    public static NetworkInfo c(Context context) {
        try {
            return ik.k(context);
        } catch (Throwable th) {
            b.a(th, "Utils", "getNetWorkInfo");
            return null;
        }
    }

    public static int d() {
        return new Random().nextInt(65536) - 32768;
    }

    public static boolean a(JSONObject jSONObject, String str) {
        return it.a(jSONObject, str);
    }

    public static boolean a(String str) {
        return (TextUtils.isEmpty(str) || "00:00:00:00:00:00".equals(str) || "02:00:00:00:00:00".equals(str) || str.contains(" :")) ? false : true;
    }

    public static String e() {
        try {
            return il.b("S128DF1572465B890OE3F7A13167KLEI".getBytes("UTF-8")).substring(20);
        } catch (Throwable unused) {
            return "";
        }
    }

    public static int a(NetworkInfo networkInfo) {
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo.getType();
        }
        return -1;
    }

    public static boolean d(Context context) {
        try {
            NetworkInfo networkInfoC = c(context);
            if (networkInfoC != null) {
                if (networkInfoC.isConnectedOrConnecting()) {
                    return true;
                }
            }
        } catch (Throwable unused) {
        }
        return false;
    }

    public static String a(ConnectivityManager connectivityManager) {
        int subtype = 0;
        if (connectivityManager != null) {
            try {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    subtype = activeNetworkInfo.getSubtype();
                }
            } catch (Throwable unused) {
            }
        }
        switch (subtype) {
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "EVDO_0";
            case 6:
                return "EVDO_A";
            case 7:
                return "1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "IDEN";
            case 12:
                return "EVDO_B";
            case 13:
                return "LTE";
            case 14:
                return "EHRPD";
            case 15:
                return "HSPAP";
            default:
                return "UNKWN";
        }
    }

    public static byte[] a(long j) {
        byte[] bArr = new byte[8];
        for (int i = 0; i < 8; i++) {
            bArr[i] = (byte) ((j >> (i * 8)) & 255);
        }
        return bArr;
    }

    public static byte[] a(int i, byte[] bArr) {
        if (bArr == null || bArr.length < 2) {
            bArr = new byte[2];
        }
        bArr[0] = (byte) (i & 255);
        bArr[1] = (byte) ((i & 65280) >> 8);
        return bArr;
    }

    public static String a(long j, String str) {
        SimpleDateFormat simpleDateFormat;
        if (TextUtils.isEmpty(str)) {
            str = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat simpleDateFormat2 = null;
        try {
            simpleDateFormat = new SimpleDateFormat(str, Locale.CHINA);
            try {
                simpleDateFormat.applyPattern(str);
            } catch (Throwable th) {
                th = th;
                simpleDateFormat2 = simpleDateFormat;
                b.a(th, "Utils", "formatUTC");
                simpleDateFormat = simpleDateFormat2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
        if (j <= 0) {
            j = a();
        }
        return simpleDateFormat == null ? "NULL" : simpleDateFormat.format(Long.valueOf(j));
    }

    public static byte[] b(int i, byte[] bArr) {
        if (bArr == null || bArr.length < 4) {
            bArr = new byte[4];
        }
        for (int i2 = 0; i2 < bArr.length; i2++) {
            bArr[i2] = (byte) ((i >> (i2 * 8)) & 255);
        }
        return bArr;
    }

    public static double a(double d2) {
        return b(d2);
    }

    public static int b(byte[] bArr) {
        int i = 0;
        for (int i2 = 0; i2 < 2; i2++) {
            i |= (bArr[i2] & 255) << ((1 - i2) * 8);
        }
        return i;
    }

    public static boolean e(Context context) {
        int iB;
        if (Build.VERSION.SDK_INT >= 23 && context.getApplicationInfo().targetSdkVersion >= 23) {
            Application application = (Application) context;
            for (String str : com.autonavi.aps.amapapi.b.D) {
                try {
                    iB = f.b(application.getBaseContext(), "checkSelfPermission", str);
                } catch (Throwable unused) {
                    iB = 0;
                }
                if (iB == 0) {
                }
            }
            return true;
        }
        for (String str2 : com.autonavi.aps.amapapi.b.D) {
            if (context.checkCallingOrSelfPermission(str2) == 0) {
            }
        }
        return true;
        return false;
    }

    public static boolean f(Context context) {
        int iB;
        if (context.getApplicationInfo().targetSdkVersion < 29 || Build.VERSION.SDK_INT < 29) {
            return true;
        }
        try {
            iB = f.b(((Application) context).getBaseContext(), "checkSelfPermission", com.autonavi.aps.amapapi.b.E);
        } catch (Throwable unused) {
            iB = 0;
        }
        return iB == 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0010 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:12:0x0011 A[Catch: all -> 0x0040, TRY_ENTER, TryCatch #0 {all -> 0x0040, blocks: (B:3:0x0001, B:12:0x0011, B:14:0x0017, B:19:0x0024, B:21:0x002e, B:23:0x0037), top: B:30:0x0001 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean a(android.location.Location r7, int r8) {
        /*
            r0 = 0
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Throwable -> L40
            r2 = 18
            if (r1 < r2) goto Lc
            boolean r1 = r7.isFromMockProvider()     // Catch: java.lang.Throwable -> Lc
            goto Ld
        Lc:
            r1 = r0
        Ld:
            r2 = 1
            if (r1 == 0) goto L11
            return r2
        L11:
            android.os.Bundle r1 = r7.getExtras()     // Catch: java.lang.Throwable -> L40
            if (r1 == 0) goto L1e
            java.lang.String r3 = "satellites"
            int r1 = r1.getInt(r3)     // Catch: java.lang.Throwable -> L40
            goto L1f
        L1e:
            r1 = r0
        L1f:
            if (r1 > 0) goto L22
            return r2
        L22:
            if (r8 != 0) goto L40
            double r3 = r7.getAltitude()     // Catch: java.lang.Throwable -> L40
            r5 = 0
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 != 0) goto L40
            float r8 = r7.getBearing()     // Catch: java.lang.Throwable -> L40
            r1 = 0
            int r8 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r8 != 0) goto L40
            float r7 = r7.getSpeed()     // Catch: java.lang.Throwable -> L40
            int r7 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r7 != 0) goto L40
            return r2
        L40:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.utils.j.a(android.location.Location, int):boolean");
    }

    public static boolean a(SQLiteDatabase sQLiteDatabase, String str) {
        boolean z = false;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String strReplace = "2.0.201501131131".replace(".", "");
        Cursor cursorQuery = null;
        if (sQLiteDatabase != null) {
            try {
                if (sQLiteDatabase.isOpen()) {
                    cursorQuery = sQLiteDatabase.query("sqlite_master", new String[]{"count(*) as c"}, "type = 'table' AND name = '" + str.trim() + strReplace + "'", null, null, null, null);
                    if (cursorQuery != null && cursorQuery.moveToFirst()) {
                        if (cursorQuery.getInt(0) > 0) {
                            z = true;
                        }
                    }
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return z;
                }
            } catch (Throwable unused) {
                if (cursorQuery == null) {
                    return true;
                }
                cursorQuery.close();
                return true;
            }
        }
        return false;
    }

    public static boolean a(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            ArrayList<String> arrayListB = b(str);
            String[] strArrSplit = str2.toString().split("#");
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                if (strArrSplit[i3].contains(",nb") || strArrSplit[i3].contains(",access")) {
                    i++;
                    if (arrayListB.contains(strArrSplit[i3])) {
                        i2++;
                    }
                }
            }
            if (i2 * 2 >= ((double) (arrayListB.size() + i)) * 0.618d) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> b(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (!TextUtils.isEmpty(str)) {
            String[] strArrSplit = str.split("#");
            for (int i = 0; i < strArrSplit.length; i++) {
                if (strArrSplit[i].contains(",nb") || strArrSplit[i].contains(",access")) {
                    arrayList.add(strArrSplit[i]);
                }
            }
        }
        return arrayList;
    }

    public static boolean g(Context context) {
        boolean zIsWifiEnabled;
        if (context == null) {
            return true;
        }
        if (a == null) {
            a = (WifiManager) a(context, "wifi");
        }
        try {
            if (c(context, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19XSUZJX1NUQVRF")) {
                zIsWifiEnabled = a.isWifiEnabled();
            } else {
                b.a(new Exception("n_aws"), "OPENSDK_UTS", "iwfal_n_aws");
                zIsWifiEnabled = false;
            }
        } catch (Throwable unused) {
            zIsWifiEnabled = false;
        }
        try {
            d.b();
        } catch (Throwable unused2) {
            d.c();
        }
        if (zIsWifiEnabled || c() <= 17) {
            return zIsWifiEnabled;
        }
        try {
            return "true".equals(String.valueOf(f.a(a, "isScanAlwaysAvailable", new Object[0])));
        } catch (Throwable unused3) {
            return zIsWifiEnabled;
        }
    }

    public static String h(Context context) {
        NetworkInfo networkInfoC = c(context);
        if (networkInfoC == null || !networkInfoC.isConnectedOrConnecting()) {
            return "DISCONNECTED";
        }
        int type = networkInfoC.getType();
        if (type == 1) {
            return "WIFI";
        }
        if (type != 0) {
            return MediaPlayer.CHARSET_UNKNOWN;
        }
        String subtypeName = networkInfoC.getSubtypeName();
        switch (networkInfoC.getSubtype()) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
            case 16:
                break;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
            case 17:
                return "3G";
            case 13:
                return "4G";
            default:
                if (!"GSM".equalsIgnoreCase(subtypeName)) {
                    return ("TD-SCDMA".equalsIgnoreCase(subtypeName) || "WCDMA".equalsIgnoreCase(subtypeName) || "CDMA2000".equalsIgnoreCase(subtypeName)) ? "3G" : subtypeName;
                }
                break;
        }
        return "2G";
    }

    public static String i(Context context) {
        String strH = ik.h();
        if (TextUtils.isEmpty(strH) || strH.equals("00:00:00:00:00:00")) {
            strH = i.a(context);
        }
        return TextUtils.isEmpty(strH) ? "00:00:00:00:00:00" : strH;
    }

    public static double c(String str) throws NumberFormatException {
        return Double.parseDouble(str);
    }

    public static float d(String str) throws NumberFormatException {
        return Float.parseFloat(str);
    }

    public static int e(String str) throws NumberFormatException {
        return Integer.parseInt(str);
    }

    public static int f(String str) throws NumberFormatException {
        return Integer.parseInt(str, 16);
    }

    public static byte g(String str) throws NumberFormatException {
        return Byte.parseByte(str);
    }

    public static boolean j(Context context) {
        return Build.VERSION.SDK_INT >= 28 && context.getApplicationInfo().targetSdkVersion >= 28;
    }

    public static boolean k(Context context) {
        ServiceInfo serviceInfo;
        try {
            serviceInfo = context.getPackageManager().getServiceInfo(new ComponentName(context, "com.amap.api.location.APSService"), 128);
        } catch (Throwable unused) {
            serviceInfo = null;
        }
        return serviceInfo != null;
    }

    public static boolean b(Context context, String str) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(str, 256);
        } catch (Throwable unused) {
            packageInfo = null;
        }
        return packageInfo != null;
    }

    public static List<String> a(File file) {
        FileInputStream fileInputStreamB;
        InputStreamReader inputStreamReader;
        ArrayList arrayList = new ArrayList();
        BufferedReader bufferedReader = null;
        try {
            try {
                fileInputStreamB = b(file);
                try {
                    inputStreamReader = new InputStreamReader(fileInputStreamB, Charset.defaultCharset());
                    try {
                        BufferedReader bufferedReader2 = new BufferedReader(inputStreamReader);
                        while (true) {
                            try {
                                String line = bufferedReader2.readLine();
                                if (line == null) {
                                    break;
                                }
                                arrayList.add(line);
                            } catch (Throwable unused) {
                                bufferedReader = bufferedReader2;
                                if (bufferedReader != null) {
                                    bufferedReader.close();
                                }
                                if (inputStreamReader != null) {
                                    inputStreamReader.close();
                                }
                                if (fileInputStreamB != null) {
                                    fileInputStreamB.close();
                                }
                            }
                        }
                        bufferedReader2.close();
                        inputStreamReader.close();
                        fileInputStreamB.close();
                    } catch (Throwable unused2) {
                    }
                } catch (Throwable unused3) {
                    inputStreamReader = null;
                }
            } catch (Throwable unused4) {
                fileInputStreamB = null;
                inputStreamReader = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private static FileInputStream b(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
            return new FileInputStream(file);
        }
        throw new FileNotFoundException("File '" + file + "' does not exist");
    }

    public static void a(File file, String str) {
        FileOutputStream fileOutputStreamC = null;
        try {
            try {
                fileOutputStreamC = c(file);
                if (str != null) {
                    fileOutputStreamC.write(str.getBytes());
                }
                try {
                    fileOutputStreamC.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                if (fileOutputStreamC != null) {
                    try {
                        fileOutputStreamC.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }
        } catch (Throwable th) {
            if (fileOutputStreamC != null) {
                try {
                    fileOutputStreamC.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    private static FileOutputStream c(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
                    throw new IOException("Directory '" + parentFile + "' could not be created");
                }
                file.createNewFile();
            }
        }
        return new FileOutputStream(file, false);
    }

    public static String l(Context context) {
        if (d == null) {
            d = com.autonavi.aps.amapapi.security.a.a("MD5", ig.c(context));
        }
        return d;
    }

    public static boolean m(Context context) {
        try {
            if (!p(context) && !o(context)) {
                if (!n(context)) {
                    return false;
                }
            }
            return true;
        } catch (Throwable unused) {
            d.b();
            return false;
        }
    }

    private static boolean n(Context context) {
        return h("huawei") && q(context) && s(context);
    }

    private static boolean o(Context context) {
        return h("vivo") && q(context) && r(context);
    }

    private static boolean p(Context context) {
        try {
            if (Build.VERSION.SDK_INT < 31 || context == null || context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
                return false;
            }
            return context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == 0;
        } catch (Throwable unused) {
            d.b();
            return false;
        }
    }

    public static boolean c(Context context, String str) {
        boolean z = false;
        try {
            if (Build.VERSION.SDK_INT < 23 ? !(context == null || context.checkCallingOrSelfPermission(it.c(str)) != 0) : !(context == null || context.checkSelfPermission(it.c(str)) != 0)) {
                z = true;
            }
        } catch (Throwable unused) {
            d.b();
        }
        return z;
    }

    private static boolean q(Context context) {
        try {
            int i = Build.VERSION.SDK_INT;
            int i2 = context.getApplicationInfo().targetSdkVersion;
            return ((i == 30) && (i2 >= 23)) || ((i == 31) && (i2 <= 30 && i2 >= 23));
        } catch (Throwable unused) {
            d.b();
            return false;
        }
    }

    private static boolean h(String str) {
        try {
            String str2 = Build.MANUFACTURER;
            String str3 = Build.BRAND;
            if (!str2.equalsIgnoreCase(str)) {
                if (!str3.toLowerCase().contains(str)) {
                    return false;
                }
            }
            return true;
        } catch (Throwable unused) {
            d.b();
            return false;
        }
    }

    private static boolean r(Context context) {
        Cursor cursorQuery;
        boolean z = false;
        try {
            cursorQuery = context.getContentResolver().query(Uri.parse("content://com.vivo.permissionmanager.provider.permission/fuzzy_location_apps"), new String[]{"package_name", "selected_fuzzy"}, "package_name=?", new String[]{context.getPackageName()}, null);
            boolean z2 = false;
            while (cursorQuery != null) {
                try {
                    if (!cursorQuery.moveToNext()) {
                        break;
                    }
                    if (cursorQuery.getString(0) != null && cursorQuery.getInt(1) == 1) {
                        z2 = true;
                    }
                } catch (Throwable unused) {
                    z = z2;
                    try {
                        d.b();
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return z;
                    } catch (Throwable unused2) {
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return z;
                    }
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return z2;
        } catch (Throwable unused3) {
            cursorQuery = null;
        }
    }

    private static boolean s(Context context) {
        try {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (context == null || context.checkSelfPermission("com.huawei.permission.ACCESS_APPROXIMATELY_LOCATION") != 0) {
                        return false;
                    }
                } else if (context == null || context.checkCallingOrSelfPermission("com.huawei.permission.ACCESS_APPROXIMATELY_LOCATION") != 0) {
                    return false;
                }
                return true;
            } catch (Throwable unused) {
                return false;
            }
        } catch (Throwable unused2) {
            d.b();
            return false;
        }
    }
}
