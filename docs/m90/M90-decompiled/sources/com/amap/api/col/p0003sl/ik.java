package com.amap.api.col.p0003sl;

import android.Manifest;
import android.app.ActivityManager;
import android.app.backup.FullBackup;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Map;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: compiled from: DeviceInfo.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ik {
    private static boolean A = false;
    private static String B = "";
    private static boolean C = false;
    private static String D = "";
    private static String E = "";
    private static String F = "";
    private static boolean G = false;
    private static boolean H = false;
    private static String I = "";
    private static boolean J = false;
    private static boolean K = false;
    private static long L = 0;
    private static int M = 0;
    private static String N = null;
    private static String O = "";
    private static boolean P = true;
    private static boolean Q = false;
    private static String R = "";
    private static boolean S = false;
    private static int T = -1;
    private static boolean U = false;
    private static int V = -1;
    private static boolean W = false;
    private static volatile b X = null;
    static String a = "";
    static String b = "";
    static volatile boolean c = true;
    public static boolean d = false;
    static String e = "";
    static boolean f = false;
    public static a g = null;
    static int h = -1;
    static String i = "";
    static String j = "";
    private static String k = null;
    private static boolean l = false;
    private static String m = "";
    private static volatile boolean n = false;
    private static String o = "";
    private static boolean p = false;
    private static boolean q = true;
    private static String r = null;
    private static IBinder s = null;
    private static boolean t = false;
    private static boolean u = false;
    private static String v = "";
    private static String w = "";
    private static boolean x = false;
    private static boolean y = false;
    private static String z = "";

    /* JADX INFO: compiled from: DeviceInfo.java */
    public interface a {
        lb a(byte[] bArr, Map<String, String> map);

        String a();

        String a(Context context, String str);

        String a(String str, String str2, String str3, String str4);

        Map<String, String> b();
    }

    public static String e() {
        return "";
    }

    public static String f() {
        return "";
    }

    public static String g() {
        return "";
    }

    public static String m() {
        return "";
    }

    public static String n() {
        return "";
    }

    static /* synthetic */ boolean r() {
        t = true;
        return true;
    }

    public static void a(String str) {
        k = str;
    }

    public static String a() {
        return k;
    }

    public static String b() {
        try {
            if (!TextUtils.isEmpty(e)) {
                return e;
            }
            a aVar = g;
            return aVar == null ? "" : aVar.a();
        } catch (Throwable unused) {
            return "";
        }
    }

    public static void a(a aVar) {
        if (g == null) {
            g = aVar;
        }
    }

    public static a c() {
        return g;
    }

    public static String a(final Context context) {
        if (!TextUtils.isEmpty(b)) {
            return b;
        }
        if (context == null) {
            return "";
        }
        String strH = H(context);
        b = strH;
        if (!TextUtils.isEmpty(strH)) {
            return b;
        }
        if (c() == null || n) {
            return "";
        }
        n = true;
        mc.a().a(new md() { // from class: com.amap.api.col.3sl.ik.1
            @Override // com.amap.api.col.p0003sl.md
            public final void runTask() {
                try {
                    Map<String, String> mapB = ik.g.b();
                    String strA = ik.g.a(ik.f(context), "", "", ik.n());
                    if (TextUtils.isEmpty(strA)) {
                        return;
                    }
                    ku.a();
                    String strA2 = ik.g.a(context, new String(ku.a(ik.g.a(strA.getBytes(), mapB)).a));
                    if (TextUtils.isEmpty(strA2)) {
                        return;
                    }
                    ik.b = strA2;
                } catch (Throwable unused) {
                }
            }
        });
        return "";
    }

    public static String b(Context context) {
        try {
            return C(context);
        } catch (Throwable th) {
            th.printStackTrace();
            return "";
        }
    }

    public static String d() {
        if (Build.VERSION.SDK_INT >= 29) {
            return "";
        }
        try {
            String strN = n();
            return strN.length() < 5 ? "" : strN.substring(3, 5);
        } catch (Throwable th) {
            th.printStackTrace();
            return "";
        }
    }

    public static int c(Context context) {
        try {
            return F(context);
        } catch (Throwable th) {
            th.printStackTrace();
            return -1;
        }
    }

    public static int d(Context context) {
        try {
            return D(context);
        } catch (Throwable th) {
            th.printStackTrace();
            return -1;
        }
    }

    private static String v(Context context) {
        try {
            String strB = kj.b(context, "Alvin2", "UTDID2", "");
            return TextUtils.isEmpty(strB) ? kj.b(context, "Alvin2", "UTDID", "") : strB;
        } catch (Throwable unused) {
            return "";
        }
    }

    private static String w(Context context) {
        FileInputStream fileInputStream = null;
        try {
            if (it.a(context, Manifest.permission.READ_EXTERNAL_STORAGE) && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.UTSystemConfig/Global/Alvin2.xml");
                XmlPullParser xmlPullParserNewPullParser = Xml.newPullParser();
                FileInputStream fileInputStream2 = new FileInputStream(file);
                try {
                    xmlPullParserNewPullParser.setInput(fileInputStream2, "utf-8");
                    boolean z2 = false;
                    for (int eventType = xmlPullParserNewPullParser.getEventType(); 1 != eventType; eventType = xmlPullParserNewPullParser.next()) {
                        if (eventType != 2) {
                            if (eventType == 3) {
                                z2 = false;
                            } else if (eventType == 4 && z2) {
                                String text = xmlPullParserNewPullParser.getText();
                                try {
                                    fileInputStream2.close();
                                } catch (Throwable unused) {
                                }
                                return text;
                            }
                        } else if (xmlPullParserNewPullParser.getAttributeCount() > 0) {
                            int attributeCount = xmlPullParserNewPullParser.getAttributeCount();
                            for (int i2 = 0; i2 < attributeCount; i2++) {
                                String attributeValue = xmlPullParserNewPullParser.getAttributeValue(i2);
                                if ("UTDID2".equals(attributeValue) || "UTDID".equals(attributeValue)) {
                                    z2 = true;
                                }
                            }
                        }
                    }
                    fileInputStream = fileInputStream2;
                } catch (Throwable unused2) {
                    fileInputStream = fileInputStream2;
                    if (fileInputStream == null) {
                        return "";
                    }
                }
            }
            if (fileInputStream == null) {
                return "";
            }
        } catch (Throwable unused3) {
        }
        try {
            fileInputStream.close();
            return "";
        } catch (Throwable unused4) {
            return "";
        }
    }

    /* JADX INFO: compiled from: DeviceInfo.java */
    static class c implements ServiceConnection {
        @Override // android.content.ServiceConnection
        public final void onServiceDisconnected(ComponentName componentName) {
        }

        c() {
        }

        @Override // android.content.ServiceConnection
        public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IBinder unused = ik.s = iBinder;
        }
    }

    private static String x(Context context) {
        try {
            if (!TextUtils.isEmpty(r)) {
                return r;
            }
            byte[] bArrDigest = MessageDigest.getInstance(it.c("IU0hBMQ")).digest(context.getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures[0].toByteArray());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b2 : bArrDigest) {
                stringBuffer.append(Integer.toHexString((b2 & 255) | 256).substring(1, 3));
            }
            String string = stringBuffer.toString();
            if (!TextUtils.isEmpty(string)) {
                r = string;
            }
            return string;
        } catch (Throwable unused) {
            return "";
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x00c3 A[Catch: all -> 0x00f0, TRY_ENTER, TryCatch #0 {all -> 0x00f0, blocks: (B:3:0x0001, B:7:0x000f, B:9:0x0014, B:12:0x004f, B:17:0x0060, B:19:0x0068, B:21:0x006e, B:32:0x00c3, B:33:0x00c6, B:37:0x00d4, B:39:0x00d9, B:40:0x00df, B:41:0x00e0, B:42:0x00e7, B:43:0x00ed, B:10:0x002d, B:11:0x003d, B:36:0x00cb, B:31:0x00b5, B:26:0x007e, B:27:0x009f, B:28:0x00a9), top: B:48:0x0001, inners: #1, #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00b5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String a(android.content.Context r12, int r13) {
        /*
            Method dump skipped, instruction units count: 255
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ik.a(android.content.Context, int):java.lang.String");
    }

    private static String y(Context context) {
        try {
            Class<?> cls = Class.forName(it.c("WY29tLmFuZHJvaWQuaWQuaW1wbC5JZFByb3ZpZGVySW1wbA"));
            Object objInvoke = cls.getMethod(it.c("MZ2V0T0FJRA"), Context.class).invoke(cls.newInstance(), context);
            if (objInvoke != null) {
                String str = (String) objInvoke;
                o = str;
                return str;
            }
        } catch (Throwable th) {
            jt.a(th, "oa", "xm");
            p = true;
        }
        return o;
    }

    private static String z(Context context) {
        try {
            Cursor cursorQuery = context.getContentResolver().query(Uri.parse(it.c("QY29udGVudDovL2NvbS52aXZvLnZtcy5JZFByb3ZpZGVyL0lkZW50aWZpZXJJZC9PQUlE")), null, null, null, null);
            if (cursorQuery != null) {
                while (cursorQuery.moveToNext()) {
                    int columnCount = cursorQuery.getColumnCount();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= columnCount) {
                            break;
                        }
                        if (it.c("IdmFsdWU").equals(cursorQuery.getColumnName(i2))) {
                            o = cursorQuery.getString(i2);
                            break;
                        }
                        i2++;
                    }
                }
                cursorQuery.close();
            }
        } catch (Throwable th) {
            p = true;
            jt.a(th, "oa", "vivo");
        }
        return o;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String A(Context context) {
        if (it.c("IeGlhb21p").equalsIgnoreCase(Build.MANUFACTURER) || it.c("IeGlhb21p").equalsIgnoreCase(Build.BRAND) || it.c("IUkVETUk=").equalsIgnoreCase(Build.MANUFACTURER) || it.c("IUkVETUk=").equalsIgnoreCase(Build.BRAND)) {
            return y(context);
        }
        if (it.c("Idml2bw").equalsIgnoreCase(Build.MANUFACTURER) || it.c("Idml2bw").equalsIgnoreCase(Build.BRAND)) {
            return z(context);
        }
        if (it.c("IaHVhd2Vp").equalsIgnoreCase(Build.MANUFACTURER) || it.c("IaHVhd2Vp").equalsIgnoreCase(Build.BRAND) || it.c("ISE9OT1I=").equalsIgnoreCase(Build.MANUFACTURER)) {
            return a(context, 2);
        }
        if (it.c("Mc2Ftc3VuZw").equalsIgnoreCase(Build.MANUFACTURER) || it.c("Mc2Ftc3VuZw").equalsIgnoreCase(Build.BRAND)) {
            return a(context, 4);
        }
        if (it.c("IT1BQTw").equalsIgnoreCase(Build.MANUFACTURER) || it.c("IT1BQTw").equalsIgnoreCase(Build.BRAND) || it.c("MT25lUGx1cw").equalsIgnoreCase(Build.MANUFACTURER) || it.c("MT25lUGx1cw").equalsIgnoreCase(Build.BRAND) || it.c("IUkVBTE1F").equalsIgnoreCase(Build.BRAND)) {
            return a(context, 5);
        }
        p = true;
        return o;
    }

    public static String e(final Context context) {
        if (!q || p) {
            return "";
        }
        if (!TextUtils.isEmpty(o)) {
            return o;
        }
        if (t) {
            return o;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            mc.a().a(new md() { // from class: com.amap.api.col.3sl.ik.2
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    ik.A(context);
                    ik.r();
                }
            });
            return o;
        }
        t = true;
        return A(context);
    }

    public static void a(boolean z2) {
        q = z2;
    }

    public static String f(Context context) {
        String str;
        if (u) {
            String str2 = a;
            return str2 == null ? "" : str2;
        }
        try {
            str = a;
        } catch (Throwable unused) {
        }
        if (str != null && !"".equals(str)) {
            return a;
        }
        if (b(context, it.c("WYW5kcm9pZC5wZXJtaXNzaW9uLldSSVRFX1NFVFRJTkdT"))) {
            a = Settings.System.getString(context.getContentResolver(), "mqBRboGZkQPcAkyk");
        }
        if (!TextUtils.isEmpty(a)) {
            u = true;
            return a;
        }
        try {
            String strV = v(context);
            a = strV;
            if (!TextUtils.isEmpty(strV)) {
                u = true;
                return a;
            }
        } catch (Throwable unused2) {
        }
        try {
            a = w(context);
            u = true;
        } catch (Throwable unused3) {
        }
        String str3 = a;
        return str3 == null ? "" : str3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean b(Context context, String str) {
        return context != null && context.checkCallingOrSelfPermission(str) == 0;
    }

    public static String h() {
        return z;
    }

    static String[] i() {
        return new String[]{"", ""};
    }

    static String g(Context context) {
        try {
            TelephonyManager telephonyManagerG = G(context);
            if (telephonyManagerG == null) {
                return "";
            }
            String networkOperator = telephonyManagerG.getNetworkOperator();
            if (!TextUtils.isEmpty(networkOperator) && networkOperator.length() >= 3) {
                return networkOperator.substring(0, 3);
            }
            return "";
        } catch (Throwable unused) {
            return "";
        }
    }

    static String h(Context context) {
        TelephonyManager telephonyManagerG;
        if (C) {
            return B;
        }
        try {
            L(context);
            telephonyManagerG = G(context);
        } catch (Throwable unused) {
        }
        if (telephonyManagerG == null) {
            return B;
        }
        String networkOperator = telephonyManagerG.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator) && networkOperator.length() >= 3) {
            B = networkOperator.substring(3);
            C = true;
            return B;
        }
        C = true;
        return B;
    }

    public static int i(Context context) {
        try {
            return F(context);
        } catch (Throwable unused) {
            return -1;
        }
    }

    public static int j(Context context) {
        try {
            return D(context);
        } catch (Throwable unused) {
            return -1;
        }
    }

    public static NetworkInfo k(Context context) {
        ConnectivityManager connectivityManagerE;
        if (b(context, it.c("AYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19ORVRXT1JLX1NUQVRF")) && (connectivityManagerE = E(context)) != null) {
            return connectivityManagerE.getActiveNetworkInfo();
        }
        return null;
    }

    static String l(Context context) {
        try {
            NetworkInfo networkInfoK = k(context);
            if (networkInfoK == null) {
                return null;
            }
            return networkInfoK.getExtraInfo();
        } catch (Throwable unused) {
            return null;
        }
    }

    static String m(Context context) {
        String str;
        String str2;
        try {
            str = D;
        } catch (Throwable unused) {
        }
        if (str != null && !"".equals(str)) {
            return D;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return "";
        }
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int i2 = displayMetrics.widthPixels;
        int i3 = displayMetrics.heightPixels;
        if (i3 > i2) {
            str2 = i2 + "*" + i3;
        } else {
            str2 = i3 + "*" + i2;
        }
        D = str2;
        return D;
    }

    public static String n(Context context) {
        try {
            if (!b(context, it.c("WYW5kcm9pZC5wZXJtaXNzaW9uLlJFQURfUEhPTkVfU1RBVEU="))) {
                return O;
            }
            TelephonyManager telephonyManagerG = G(context);
            return telephonyManagerG == null ? "" : telephonyManagerG.getNetworkOperatorName();
        } catch (Throwable unused) {
            return "";
        }
    }

    public static String j() {
        return i;
    }

    public static String p(Context context) {
        try {
            String strK = k();
            try {
                if (TextUtils.isEmpty(strK)) {
                    strK = a(context);
                }
                if (TextUtils.isEmpty(strK)) {
                    strK = f(context);
                }
                if (TextUtils.isEmpty(strK)) {
                    strK = e(context);
                }
                if (TextUtils.isEmpty(strK)) {
                    strK = g();
                }
                return TextUtils.isEmpty(strK) ? B(context) : strK;
            } catch (Throwable unused) {
                return strK;
            }
        } catch (Throwable unused2) {
            return "";
        }
    }

    private static String B(Context context) {
        if (!TextUtils.isEmpty(I)) {
            return I;
        }
        try {
            String strB = kj.b(context, "open_common", "a1", "");
            if (TextUtils.isEmpty(strB)) {
                I = "amap" + UUID.randomUUID().toString().replace("_", "").toLowerCase();
                SharedPreferences.Editor editorA = kj.a(context, "open_common");
                kj.a(editorA, "a1", it.b(I));
                kj.a(editorA);
            } else {
                I = it.c(strB);
            }
            return I;
        } catch (Throwable unused) {
            return I;
        }
    }

    public static String k() {
        return E;
    }

    public static void l() {
        try {
            js.a();
        } catch (Throwable unused) {
        }
    }

    public static String q(Context context) {
        return k() + "#" + a(context) + "#" + p(context);
    }

    public static long o() {
        long blockCount;
        long blockCount2;
        long j2 = L;
        if (j2 != 0) {
            return j2;
        }
        try {
            StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            StatFs statFs2 = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            if (Build.VERSION.SDK_INT >= 18) {
                blockCount = (statFs.getBlockCountLong() * statFs.getBlockSizeLong()) / TrafficStats.MB_IN_BYTES;
                blockCount2 = (statFs2.getBlockCountLong() * statFs2.getBlockSizeLong()) / TrafficStats.MB_IN_BYTES;
            } else {
                blockCount = (((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize())) / TrafficStats.MB_IN_BYTES;
                blockCount2 = (((long) statFs2.getBlockCount()) * ((long) statFs2.getBlockSize())) / TrafficStats.MB_IN_BYTES;
            }
            L = blockCount + blockCount2;
        } catch (Throwable unused) {
        }
        return L;
    }

    public static int r(Context context) {
        int i2 = M;
        if (i2 != 0) {
            return i2;
        }
        int iIntValue = 0;
        if (Build.VERSION.SDK_INT >= 16) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) {
                return 0;
            }
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            iIntValue = (int) (memoryInfo.totalMem / 1024);
        } else {
            BufferedReader bufferedReader = null;
            try {
                try {
                    BufferedReader bufferedReader2 = new BufferedReader(new FileReader(new File("/proc/meminfo")));
                    try {
                        iIntValue = Integer.valueOf(bufferedReader2.readLine().split("\\s+")[1]).intValue();
                        bufferedReader2.close();
                    } catch (Throwable unused) {
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                        int i3 = iIntValue / 1024;
                        M = i3;
                        return i3;
                    }
                } catch (IOException unused2) {
                }
            } catch (Throwable unused3) {
            }
        }
        int i32 = iIntValue / 1024;
        M = i32;
        return i32;
    }

    public static String p() {
        if (!TextUtils.isEmpty(N)) {
            return N;
        }
        String property = System.getProperty("os.arch");
        N = property;
        return property;
    }

    static String s(Context context) {
        try {
            return C(context);
        } catch (Throwable unused) {
            return "";
        }
    }

    private static String C(Context context) {
        if (S) {
            return R;
        }
        L(context);
        TelephonyManager telephonyManagerG = G(context);
        if (telephonyManagerG == null) {
            return R;
        }
        String simOperatorName = telephonyManagerG.getSimOperatorName();
        R = simOperatorName;
        if (TextUtils.isEmpty(simOperatorName)) {
            R = telephonyManagerG.getNetworkOperatorName();
        }
        S = true;
        return R;
    }

    private static int D(Context context) {
        if (U) {
            return T;
        }
        L(context);
        if (context == null || !b(context, it.c("AYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19ORVRXT1JLX1NUQVRF"))) {
            return T;
        }
        ConnectivityManager connectivityManagerE = E(context);
        if (connectivityManagerE == null) {
            return T;
        }
        NetworkInfo activeNetworkInfo = connectivityManagerE.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            U = true;
            return T;
        }
        int type = activeNetworkInfo.getType();
        T = type;
        U = true;
        return type;
    }

    private static ConnectivityManager E(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static int F(Context context) {
        if (W) {
            return V;
        }
        L(context);
        if (!b(context, it.c("AYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19ORVRXT1JLX1NUQVRF"))) {
            return V;
        }
        ConnectivityManager connectivityManagerE = E(context);
        if (connectivityManagerE == null) {
            return V;
        }
        NetworkInfo activeNetworkInfo = connectivityManagerE.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
            V = activeNetworkInfo.getSubtype();
            W = true;
        }
        return V;
    }

    private static TelephonyManager G(Context context) {
        return (TelephonyManager) context.getSystemService("phone");
    }

    private static String H(Context context) {
        if (!c) {
            return "";
        }
        String strI = null;
        try {
            strI = I(context);
        } catch (Throwable unused) {
        }
        if (TextUtils.isEmpty(strI)) {
            c = false;
            return "";
        }
        try {
            byte[] bytes = it.c("MAAAAAAAAAAAAAAAAAAAAAA").getBytes("UTF-8");
            return new String(il.a(it.c("HYW1hcGFkaXVhbWFwYWRpdWFtYXBhZGl1YW1hcGFkaXU").getBytes("UTF-8"), il.b(strI), bytes), "UTF-8");
        } catch (Throwable unused2) {
            c = false;
            return "";
        }
    }

    private static String I(Context context) {
        String strJ;
        try {
            strJ = J(context);
        } catch (Throwable unused) {
            strJ = "";
        }
        return !TextUtils.isEmpty(strJ) ? strJ : context == null ? "" : context.getSharedPreferences(it.c("SU2hhcmVkUHJlZmVyZW5jZUFkaXU"), 0).getString(io.b(it.c("RYW1hcF9kZXZpY2VfYWRpdQ")), "");
    }

    private static String J(Context context) {
        RandomAccessFile randomAccessFile;
        byte[] bArr;
        ByteArrayOutputStream byteArrayOutputStream;
        String[] strArrSplit;
        if (Build.VERSION.SDK_INT >= 19 && !b(context, it.c("EYW5kcm9pZC5wZXJtaXNzaW9uLlJFQURfRVhURVJOQUxfU1RPUkFHRQ=="))) {
            return "";
        }
        String strB = io.b(it.c("LYW1hcF9kZXZpY2VfYWRpdQ"));
        String strK = K(context);
        if (TextUtils.isEmpty(strK)) {
            return "";
        }
        File file = new File(strK + File.separator + it.c("KYmFja3Vwcw"), it.c("MLmFkaXU"));
        if (file.exists() && file.canRead()) {
            if (file.length() == 0) {
                file.delete();
                return "";
            }
            ByteArrayOutputStream byteArrayOutputStream2 = null;
            try {
                randomAccessFile = new RandomAccessFile(file, FullBackup.ROOT_TREE_TOKEN);
                try {
                    bArr = new byte[1024];
                    byteArrayOutputStream = new ByteArrayOutputStream();
                } catch (Throwable unused) {
                }
            } catch (Throwable unused2) {
                randomAccessFile = null;
            }
            while (true) {
                try {
                    int i2 = randomAccessFile.read(bArr);
                    if (i2 == -1) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, i2);
                } catch (Throwable unused3) {
                    byteArrayOutputStream2 = byteArrayOutputStream;
                    a(byteArrayOutputStream2);
                }
                a(randomAccessFile);
            }
            String str = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            if (!TextUtils.isEmpty(str) && str.contains(it.c("SIw")) && (strArrSplit = str.split(it.c("SIw"))) != null && strArrSplit.length == 2 && TextUtils.equals(strB, strArrSplit[0])) {
                String str2 = strArrSplit[1];
                a(byteArrayOutputStream);
                a(randomAccessFile);
                return str2;
            }
            a(byteArrayOutputStream);
            a(randomAccessFile);
        }
        return "";
    }

    private static void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable unused) {
            }
        }
    }

    private static String K(Context context) {
        try {
            File externalCacheDir = Build.VERSION.SDK_INT >= 8 ? context.getExternalCacheDir() : null;
            if (externalCacheDir == null) {
                externalCacheDir = context.getCacheDir();
            }
            if (externalCacheDir != null) {
                return externalCacheDir.getAbsolutePath();
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public static void q() {
        T = -1;
        U = false;
        V = -1;
        W = false;
        R = "";
        S = false;
        B = "";
        C = false;
    }

    /* JADX INFO: compiled from: DeviceInfo.java */
    public static class b {
        private static Context a;
        private static BroadcastReceiver b;
        private static ConnectivityManager c;
        private static NetworkRequest d;
        private static ConnectivityManager.NetworkCallback e;

        public final void a(Context context) {
            if (Build.VERSION.SDK_INT >= 24) {
                if (ik.b(context, it.c("AYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19ORVRXT1JLX1NUQVRF")) && context != null && c == null) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    c = connectivityManager;
                    if (connectivityManager != null) {
                        d = new NetworkRequest.Builder().addCapability(12).addTransportType(1).addTransportType(0).build();
                        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.amap.api.col.3sl.ik.b.2
                            @Override // android.net.ConnectivityManager.NetworkCallback
                            public final void onLost(Network network) {
                                super.onLost(network);
                                ik.q();
                            }

                            @Override // android.net.ConnectivityManager.NetworkCallback
                            public final void onAvailable(Network network) {
                                super.onAvailable(network);
                                ik.q();
                            }
                        };
                        e = networkCallback;
                        c.registerNetworkCallback(d, networkCallback);
                        a = context;
                        return;
                    }
                    return;
                }
                return;
            }
            if (context == null || b != null) {
                return;
            }
            b = new BroadcastReceiver() { // from class: com.amap.api.col.3sl.ik.b.1
                @Override // android.content.BroadcastReceiver
                public final void onReceive(Context context2, Intent intent) {
                    if (it.c("WYW5kcm9pZC5uZXQuY29ubi5DT05ORUNUSVZJVFlfQ0hBTkdF").equals(intent.getAction())) {
                        ik.q();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(it.c("WYW5kcm9pZC5uZXQuY29ubi5DT05ORUNUSVZJVFlfQ0hBTkdF"));
            context.registerReceiver(b, intentFilter);
        }
    }

    private static synchronized b L(Context context) {
        if (X == null) {
            if (context == null) {
                return null;
            }
            b bVar = new b();
            X = bVar;
            bVar.a(context.getApplicationContext());
        }
        return X;
    }

    public static String t(Context context) {
        try {
            if (TextUtils.isEmpty(m)) {
                m = ja.a(context);
            }
        } catch (Throwable unused) {
        }
        return m;
    }

    public static String o(Context context) {
        ConnectivityManager connectivityManagerE;
        NetworkInfo activeNetworkInfo;
        try {
            return (!b(context, it.c("AYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19ORVRXT1JLX1NUQVRF")) || (connectivityManagerE = E(context)) == null || (activeNetworkInfo = connectivityManagerE.getActiveNetworkInfo()) == null) ? "" : activeNetworkInfo.getTypeName();
        } catch (Throwable unused) {
            return "";
        }
    }
}
