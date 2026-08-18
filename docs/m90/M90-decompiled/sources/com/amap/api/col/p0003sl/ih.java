package com.amap.api.col.p0003sl;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.security.KeyChain;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.ky;
import com.amap.api.col.p0003sl.lb;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.json.JSONObject;

/* JADX INFO: compiled from: AuthConfigManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ih {
    private static volatile boolean D = false;
    public static int a = -1;
    public static String b = "";
    public static Context c = null;
    private static String k = "6";
    private static String l = "4";
    private static String m = "9";
    private static String n = "8";
    private static volatile boolean o = true;
    private static Vector<e> p = new Vector<>();
    private static Map<String, Integer> q = new HashMap();
    private static String r = null;
    private static long s = 0;
    public static volatile boolean d = false;
    private static volatile ConcurrentHashMap<String, g> t = new ConcurrentHashMap<>(8);
    private static volatile ConcurrentHashMap<String, Long> u = new ConcurrentHashMap<>(8);
    private static volatile ConcurrentHashMap<String, d> v = new ConcurrentHashMap<>(8);
    private static boolean w = false;
    private static boolean x = false;
    public static int e = 5000;
    public static boolean f = true;
    public static boolean g = false;
    private static int y = 3;
    public static boolean h = true;
    public static boolean i = false;
    private static int z = 3;
    public static boolean j = false;
    private static ConcurrentHashMap<String, Boolean> A = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Boolean> B = new ConcurrentHashMap<>();
    private static ArrayList<ky.a> C = new ArrayList<>();
    private static Queue<ky.c> E = new LinkedList();

    /* JADX INFO: compiled from: AuthConfigManager.java */
    public interface a {
        void a(b bVar);
    }

    /* JADX INFO: compiled from: AuthConfigManager.java */
    public static class f {
        public static boolean a = true;
        public static boolean b = false;
        public static boolean c = true;
        public static int d = 0;
        public static boolean e = false;
        public static int f;
    }

    /* JADX INFO: compiled from: AuthConfigManager.java */
    public static class g {
        public long a;
        public String b;

        g(Long l, String str) {
            this.a = 0L;
            this.b = "";
            this.a = l.longValue();
            this.b = str;
        }
    }

    public static void a(Context context, String str) {
        ig.a(context, str);
    }

    /* JADX INFO: compiled from: AuthConfigManager.java */
    public static class b {

        @Deprecated
        public JSONObject a;

        @Deprecated
        public JSONObject b;
        public String c;
        public int d = -1;
        public long e = 0;
        public JSONObject f;
        public a g;
        public C0015b h;
        private boolean i;

        /* JADX INFO: compiled from: AuthConfigManager.java */
        public static class a {
            public boolean a;
            public boolean b;
            public JSONObject c;
        }

        /* JADX INFO: renamed from: com.amap.api.col.3sl.ih$b$b, reason: collision with other inner class name */
        /* JADX INFO: compiled from: AuthConfigManager.java */
        public static class C0015b {
            public boolean a;
        }
    }

    public static boolean a(String str, boolean z2) {
        try {
            if (TextUtils.isEmpty(str)) {
                return z2;
            }
            String[] strArrSplit = URLDecoder.decode(str).split("/");
            return strArrSplit[strArrSplit.length - 1].charAt(4) % 2 == 1;
        } catch (Throwable unused) {
            return z2;
        }
    }

    public static b a(Context context, is isVar, String str, Map<String, String> map) {
        return b(context, isVar, str, map);
    }

    private static b b(Context context, is isVar, String str, Map<String, String> map) {
        return a(context, isVar, str, map, null, null, null);
    }

    public static b a(Context context, is isVar, String str, String str2, String str3, String str4) {
        return a(context, isVar, str, null, str2, str3, str4);
    }

    public static void a(Context context) {
        if (context != null) {
            c = context.getApplicationContext();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0196 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0197  */
    /* JADX WARN: Type inference failed for: r13v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r13v1 */
    /* JADX WARN: Type inference failed for: r13v10, types: [com.amap.api.col.3sl.ih$b] */
    /* JADX WARN: Type inference failed for: r13v14, types: [com.amap.api.col.3sl.ih$b] */
    /* JADX WARN: Type inference failed for: r13v15 */
    /* JADX WARN: Type inference failed for: r13v16 */
    /* JADX WARN: Type inference failed for: r13v17 */
    /* JADX WARN: Type inference failed for: r13v18 */
    /* JADX WARN: Type inference failed for: r13v2 */
    /* JADX WARN: Type inference failed for: r13v3 */
    /* JADX WARN: Type inference failed for: r13v4 */
    /* JADX WARN: Type inference failed for: r13v5 */
    /* JADX WARN: Type inference failed for: r13v6 */
    /* JADX WARN: Type inference failed for: r13v7 */
    /* JADX WARN: Type inference failed for: r13v8 */
    /* JADX WARN: Type inference failed for: r13v9, types: [com.amap.api.col.3sl.ih$b] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static com.amap.api.col.3sl.ih.b a(android.content.Context r23, com.amap.api.col.p0003sl.is r24, java.lang.String r25, java.util.Map<java.lang.String, java.lang.String> r26, java.lang.String r27, java.lang.String r28, java.lang.String r29) throws com.amap.api.col.p0003sl.Cif {
        /*
            Method dump skipped, instruction units count: 604
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ih.a(android.content.Context, com.amap.api.col.3sl.is, java.lang.String, java.util.Map, java.lang.String, java.lang.String, java.lang.String):com.amap.api.col.3sl.ih$b");
    }

    private static String b(List<String> list) {
        if (list == null) {
            return "";
        }
        try {
            if (list.size() <= 0) {
                return "";
            }
            String str = list.get(0);
            return !TextUtils.isEmpty(str) ? str : "";
        } catch (Exception unused) {
            return "";
        }
    }

    public static long a(List<String> list) {
        if (list == null) {
            return 0L;
        }
        try {
            if (list.size() <= 0) {
                return 0L;
            }
            String str = list.get(0);
            if (TextUtils.isEmpty(str)) {
                return 0L;
            }
            return Long.valueOf(str).longValue();
        } catch (Exception e2) {
            e2.printStackTrace();
            return 0L;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:116:0x02c9  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x0232 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:151:0x0328 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:157:0x02eb A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:161:0x02d3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:168:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:93:0x0226  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void a(android.content.Context r18, com.amap.api.col.p0003sl.is r19, java.lang.String r20, com.amap.api.col.3sl.ih.b r21, org.json.JSONObject r22) throws org.json.JSONException {
        /*
            Method dump skipped, instruction units count: 861
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ih.a(android.content.Context, com.amap.api.col.3sl.is, java.lang.String, com.amap.api.col.3sl.ih$b, org.json.JSONObject):void");
    }

    private static void a(Context context, is isVar, Throwable th) {
        a(context, isVar, th.getMessage());
    }

    public static void a(String str, boolean z2, boolean z3, boolean z4) {
        if (TextUtils.isEmpty(str) || c == null) {
            return;
        }
        HashMap map = new HashMap();
        map.put("url", str);
        map.put("downLevel", String.valueOf(z2));
        map.put("ant", ik.j(c) == 0 ? "0" : "1");
        if (z4) {
            map.put("type", z2 ? m : n);
        } else {
            map.put("type", z2 ? k : l);
        }
        map.put("status", z3 ? "0" : "1");
        String string = new JSONObject(map).toString();
        if (TextUtils.isEmpty(string)) {
            return;
        }
        try {
            lk lkVar = new lk(c, "core", EjbJar.CMPVersion.CMP2_0, "O002");
            lkVar.a(string);
            ll.a(lkVar, c);
        } catch (Cif unused) {
        }
    }

    public static void a(ky.c cVar) {
        if (cVar == null || c == null) {
            return;
        }
        HashMap map = new HashMap();
        map.put("serverip", cVar.c);
        map.put("hostname", cVar.e);
        map.put("path", cVar.d);
        map.put("csid", cVar.a);
        map.put("degrade", String.valueOf(cVar.b.a()));
        map.put("errorcode", String.valueOf(cVar.m));
        map.put("errorsubcode", String.valueOf(cVar.n));
        map.put("connecttime", String.valueOf(cVar.h));
        map.put("writetime", String.valueOf(cVar.i));
        map.put("readtime", String.valueOf(cVar.j));
        map.put("datasize", String.valueOf(cVar.l));
        map.put("totaltime", String.valueOf(cVar.f));
        String string = new JSONObject(map).toString();
        "--埋点--".concat(String.valueOf(string));
        ky.b();
        if (TextUtils.isEmpty(string)) {
            return;
        }
        try {
            lk lkVar = new lk(c, "core", EjbJar.CMPVersion.CMP2_0, "O008");
            lkVar.a(string);
            ll.a(lkVar, c);
        } catch (Cif unused) {
        }
    }

    private static void a(Context context, is isVar, String str) {
        HashMap map = new HashMap();
        map.put("amap_sdk_auth_fail", "1");
        map.put("amap_sdk_auth_fail_type", str);
        map.put("amap_sdk_name", isVar.a());
        map.put("amap_sdk_version", isVar.c());
        String string = new JSONObject(map).toString();
        if (TextUtils.isEmpty(string)) {
            return;
        }
        try {
            lk lkVar = new lk(context, "core", EjbJar.CMPVersion.CMP2_0, "O001");
            lkVar.a(string);
            ll.a(lkVar, context);
        } catch (Cif unused) {
        }
    }

    /* JADX INFO: compiled from: AuthConfigManager.java */
    static class c extends kv {
        private String d;
        private Map<String, String> e;
        private String f;
        private String g;
        private String h;

        @Override // com.amap.api.col.p0003sl.kv
        public final byte[] c() {
            return null;
        }

        @Override // com.amap.api.col.p0003sl.kv
        protected final String e() {
            return "3.0";
        }

        c(Context context, is isVar, String str, Map<String, String> map, String str2, String str3, String str4) {
            super(context, isVar);
            this.d = str;
            this.e = map;
            this.f = str2;
            this.g = str3;
            this.h = str4;
            setHttpProtocol(lb.c.HTTPS);
            setDegradeAbility(lb.a.FIX);
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final Map<String, String> getRequestHead() {
            if (TextUtils.isEmpty(this.h)) {
                return null;
            }
            HashMap map = new HashMap();
            map.put(KeyChain.EXTRA_HOST, this.h);
            return map;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final String getURL() {
            return a("https://restsdk.amap.com/v3/iasdkauth", this.f);
        }

        @Override // com.amap.api.col.p0003sl.in, com.amap.api.col.p0003sl.lb
        public final String getIPV6URL() {
            return a("https://dualstack-arestapi.amap.com/v3/iasdkauth", this.g);
        }

        private static String a(String str, String str2) {
            try {
                return !TextUtils.isEmpty(str2) ? Uri.parse(str).buildUpon().encodedAuthority(str2).build().toString() : str;
            } catch (Throwable unused) {
                return str;
            }
        }

        @Override // com.amap.api.col.p0003sl.lb
        protected final String getIPDNSName() {
            if (!TextUtils.isEmpty(this.h)) {
                return this.h;
            }
            return super.getIPDNSName();
        }

        @Override // com.amap.api.col.p0003sl.kv
        public final byte[] d() {
            String strP = ik.p(this.a);
            if (!TextUtils.isEmpty(strP)) {
                strP = io.b(new StringBuilder(strP).reverse().toString());
            }
            HashMap map = new HashMap();
            map.put("authkey", TextUtils.isEmpty(this.d) ? "" : this.d);
            map.put("plattype", "android");
            map.put("ccver", "1");
            map.put("product", this.b.a());
            map.put("version", this.b.b());
            map.put(MediaStore.EXTRA_OUTPUT, "json");
            map.put("androidversion", new StringBuilder().append(Build.VERSION.SDK_INT).toString());
            map.put("deviceId", strP);
            map.put("manufacture", Build.MANUFACTURER);
            Map<String, String> map2 = this.e;
            if (map2 != null && !map2.isEmpty()) {
                map.putAll(this.e);
            }
            map.put("abitype", it.a(this.a));
            map.put("ext", this.b.d());
            return it.a(it.a(map));
        }
    }

    public static boolean a() {
        e eVarA;
        if (c != null) {
            i();
            if (!c()) {
                return false;
            }
            if (b()) {
                return true;
            }
        }
        return o && (eVarA = a(c, "IPV6_CONFIG_NAME", "open_common")) != null && eVarA.a() < 5;
    }

    private static boolean a(InetAddress inetAddress) {
        return inetAddress.isLoopbackAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isAnyLocalAddress();
    }

    private static void i() {
        try {
            Context context = c;
            if (context != null) {
                String strO = ik.o(context);
                if (!TextUtils.isEmpty(r) && !TextUtils.isEmpty(strO) && r.equals(strO) && System.currentTimeMillis() - s < 60000) {
                    return;
                }
                if (!TextUtils.isEmpty(strO)) {
                    r = strO;
                }
            } else if (System.currentTimeMillis() - s < 10000) {
                return;
            }
            s = System.currentTimeMillis();
            q.clear();
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!networkInterface.getInterfaceAddresses().isEmpty()) {
                    String displayName = networkInterface.getDisplayName();
                    int i2 = 0;
                    Iterator<InterfaceAddress> it = networkInterface.getInterfaceAddresses().iterator();
                    while (it.hasNext()) {
                        InetAddress address = it.next().getAddress();
                        if (address instanceof Inet6Address) {
                            if (!a((Inet6Address) address)) {
                                i2 |= 2;
                            }
                        } else if (address instanceof Inet4Address) {
                            Inet4Address inet4Address = (Inet4Address) address;
                            if (!a(inet4Address) && !inet4Address.getHostAddress().startsWith(it.c("FMTkyLjE2OC40My4"))) {
                                i2 |= 1;
                            }
                        }
                    }
                    if (i2 != 0) {
                        if (displayName != null && displayName.startsWith("wlan")) {
                            q.put("WIFI", Integer.valueOf(i2));
                        } else if (displayName != null && displayName.startsWith("rmnet")) {
                            q.put("MOBILE", Integer.valueOf(i2));
                        }
                    }
                }
            }
        } catch (Throwable th) {
            jt.a(th, "at", "ipstack");
        }
    }

    public static boolean b() {
        Integer num;
        Context context = c;
        if (context == null) {
            return false;
        }
        String strO = ik.o(context);
        return (TextUtils.isEmpty(strO) || (num = q.get(strO.toUpperCase())) == null || num.intValue() != 2) ? false : true;
    }

    public static boolean c() {
        Integer num;
        Context context = c;
        if (context == null) {
            return false;
        }
        String strO = ik.o(context);
        return (TextUtils.isEmpty(strO) || (num = q.get(strO.toUpperCase())) == null || num.intValue() < 2) ? false : true;
    }

    public static void b(Context context) {
        if (context == null) {
            return;
        }
        o = kj.a(context, "open_common", "a2", true);
    }

    private static void c(Context context) {
        if (context == null) {
            return;
        }
        f = kj.a(context, "open_common", "a13", true);
        h = kj.a(context, "open_common", "a6", true);
        g = kj.a(context, "open_common", "a7", false);
        e = kj.a(context, "open_common", "a8", 5000);
        y = kj.a(context, "open_common", "a9", 3);
        i = kj.a(context, "open_common", "a10", false);
        z = kj.a(context, "open_common", "a11", 3);
        j = kj.a(context, "open_common", "a12", false);
    }

    private static void a(Context context, String str, String str2, e eVar) {
        if (eVar == null || TextUtils.isEmpty(eVar.a)) {
            return;
        }
        String strB = eVar.b();
        if (TextUtils.isEmpty(strB) || context == null) {
            return;
        }
        SharedPreferences.Editor editorA = kj.a(context, str2);
        editorA.putString(str, strB);
        kj.a(editorA);
    }

    /* JADX INFO: compiled from: AuthConfigManager.java */
    public static class e {
        private String a;
        private String b;
        private AtomicInteger c;

        public e(String str, String str2, int i) {
            this.a = str;
            this.b = str2;
            this.c = new AtomicInteger(i);
        }

        public final void a(String str) {
            this.b = str;
        }

        public final int a() {
            AtomicInteger atomicInteger = this.c;
            if (atomicInteger == null) {
                return 0;
            }
            return atomicInteger.get();
        }

        public final String b() {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(FullBackup.APK_TREE_TOKEN, this.a);
                jSONObject.put(FullBackup.DATA_TREE_TOKEN, this.b);
                jSONObject.put(ju.g, this.c.get());
                return jSONObject.toString();
            } catch (Throwable unused) {
                return "";
            }
        }

        public static e b(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            try {
                JSONObject jSONObject = new JSONObject(str);
                return new e(jSONObject.optString(FullBackup.APK_TREE_TOKEN), jSONObject.optString(FullBackup.DATA_TREE_TOKEN), jSONObject.optInt(ju.g));
            } catch (Throwable unused) {
                return null;
            }
        }
    }

    public static void e() {
        if (d) {
            return;
        }
        try {
            Context context = c;
            if (context == null) {
                return;
            }
            d = true;
            im.a().a(context);
            b(context);
            c(context);
            f.a = kj.a(context, "open_common", "ucf", f.a);
            f.b = kj.a(context, "open_common", "fsv2", f.b);
            f.c = kj.a(context, "open_common", "usc", f.c);
            f.d = kj.a(context, "open_common", "umv", f.d);
            f.e = kj.a(context, "open_common", "ust", f.e);
            f.f = kj.a(context, "open_common", "ustv", f.f);
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: compiled from: AuthConfigManager.java */
    private static class d {
        is a;
        String b;
        a c;

        private d() {
        }

        /* synthetic */ d(byte b) {
            this();
        }
    }

    public static String a(String str) {
        d dVar;
        if (!v.containsKey(str) || (dVar = v.get(str)) == null) {
            return null;
        }
        return dVar.b;
    }

    public static is b(String str) {
        d dVar = v.get(str);
        if (dVar != null) {
            return dVar.a;
        }
        return null;
    }

    public static String c(String str) {
        return !TextUtils.isEmpty(str) ? str + ";15K;16H;17I;1A4;17S;183" : str;
    }

    public static synchronized void a(Context context, is isVar, String str, a aVar) {
        if (context == null || isVar == null) {
            return;
        }
        try {
            if (c == null) {
                c = context.getApplicationContext();
            }
            String strA = isVar.a();
            if (TextUtils.isEmpty(strA)) {
                return;
            }
            a(isVar);
            if (v == null) {
                v = new ConcurrentHashMap<>(8);
            }
            if (u == null) {
                u = new ConcurrentHashMap<>(8);
            }
            if (t == null) {
                t = new ConcurrentHashMap<>(8);
            }
            if (!v.containsKey(strA)) {
                d dVar = new d((byte) 0);
                dVar.a = isVar;
                dVar.b = str;
                dVar.c = aVar;
                v.put(strA, dVar);
                t.put(strA, new g(Long.valueOf(kj.a(c, "open_common", strA, 0L)), kj.b(c, "open_common", strA + "lct-info", "")));
                d(c);
                e(c);
            }
        } catch (Throwable th) {
            jt.a(th, "at", "rglc");
        }
    }

    public static synchronized boolean a(String str, long j2) {
        boolean z2 = false;
        try {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            g gVarF = f(str);
            long jLongValue = 0;
            if (j2 != (gVarF != null ? gVarF.a : 0L)) {
                if (u != null && u.containsKey(str)) {
                    jLongValue = u.get(str).longValue();
                }
                if (SystemClock.elapsedRealtime() - jLongValue > 30000) {
                    z2 = true;
                }
            }
        } catch (Throwable unused) {
        }
        return z2;
    }

    public static synchronized void b(String str, boolean z2) {
        a(str, z2, (String) null, (String) null, (String) null);
    }

    public static synchronized void a(final String str, boolean z2, final String str2, final String str3, final String str4) {
        try {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            if (u == null) {
                u = new ConcurrentHashMap<>(8);
            }
            u.put(str, Long.valueOf(SystemClock.elapsedRealtime()));
            if (v == null) {
                return;
            }
            if (v.containsKey(str)) {
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                if (z2) {
                    kx.a(true, str);
                }
                mc.a().a(new md() { // from class: com.amap.api.col.3sl.ih.1
                    @Override // com.amap.api.col.p0003sl.md
                    public final void runTask() {
                        d dVar = (d) ih.v.get(str);
                        if (dVar == null) {
                            return;
                        }
                        a aVar = dVar.c;
                        b bVarA = ih.a(ih.c, dVar.a, dVar.b, str2, str3, str4);
                        if (bVarA == null || aVar == null) {
                            return;
                        }
                        aVar.a(bVarA);
                    }
                });
            }
        } catch (Throwable th) {
            jt.a(th, "at", "lca");
        }
    }

    public static synchronized boolean d(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            if (v == null) {
                return false;
            }
            if (u == null) {
                u = new ConcurrentHashMap<>(8);
            }
            if (v.containsKey(str) && !u.containsKey(str)) {
                u.put(str, Long.valueOf(SystemClock.elapsedRealtime()));
                return true;
            }
        } finally {
        }
        return false;
    }

    public static synchronized void e(String str) {
        if (u == null) {
            return;
        }
        if (u.containsKey(str)) {
            u.remove(str);
        }
    }

    public static synchronized g f(String str) {
        try {
            if (t == null) {
                t = new ConcurrentHashMap<>(8);
            }
        } catch (Throwable th) {
            jt.a(th, "at", "glcut");
        }
        if (t.containsKey(str)) {
            return t.get(str);
        }
        return new g(0L, "");
    }

    private static synchronized void a(String str, long j2, String str2) {
        try {
            if (v != null && v.containsKey(str)) {
                if (t == null) {
                    t = new ConcurrentHashMap<>(8);
                }
                t.put(str, new g(Long.valueOf(j2), str2));
                Context context = c;
                if (context != null) {
                    SharedPreferences.Editor editorA = kj.a(context, "open_common");
                    kj.a(editorA, str, j2);
                    kj.a(editorA, str + "lct-info", str2);
                    kj.a(editorA);
                }
            }
        } catch (Throwable th) {
            jt.a(th, "at", "ucut");
        }
    }

    private static void a(is isVar) {
        if (isVar != null) {
            try {
                if (TextUtils.isEmpty(isVar.a())) {
                    return;
                }
                String strC = isVar.c();
                if (TextUtils.isEmpty(strC)) {
                    strC = isVar.b();
                }
                if (TextUtils.isEmpty(strC)) {
                    return;
                }
                jh.a(isVar.a(), strC);
            } catch (Throwable unused) {
            }
        }
    }

    private static void d(Context context) {
        try {
            if (w) {
                return;
            }
            jh.d = kj.a(context, "open_common", "a4", true);
            jh.e = kj.a(context, "open_common", "a5", true);
            w = true;
        } catch (Throwable unused) {
        }
    }

    private static void e(Context context) {
        try {
            if (x) {
                return;
            }
            iv.d = a(kj.b(context, "open_common", "a16", ""), true);
            iv.b = kj.a(context, "open_common", "a17", iv.a);
            x = true;
        } catch (Throwable unused) {
        }
    }

    public static void a(boolean z2, String str) {
        try {
            "--markHostNameFailed---hostname=".concat(String.valueOf(str));
            ky.b();
            if (f || z2) {
                if ((i || !z2) && !TextUtils.isEmpty(str)) {
                    if (!z2) {
                        if (A.get(str) != null) {
                            return;
                        }
                        A.put(str, Boolean.TRUE);
                        a(b(str, "a14"), "open_common");
                        return;
                    }
                    if (B.get(str) != null) {
                        return;
                    }
                    B.put(str, Boolean.TRUE);
                    a(b(str, "a15"), "open_common");
                }
            }
        } catch (Throwable unused) {
        }
    }

    private static void a(String str, String str2) {
        e eVarA = a(c, str, str2);
        String strA = it.a(System.currentTimeMillis(), "yyyyMMdd");
        if (!strA.equals(eVarA.b)) {
            eVarA.a(strA);
            eVarA.c.set(0);
        }
        eVarA.c.incrementAndGet();
        a(c, str, str2, eVarA);
    }

    public static boolean g(String str) {
        e eVarA;
        try {
            if (TextUtils.isEmpty(str)) {
                return true;
            }
            if (!f) {
                return false;
            }
            if (!(A.get(str) == null)) {
                return false;
            }
            Context context = c;
            if (context == null || (eVarA = a(context, b(str, "a14"), "open_common")) == null) {
                return true;
            }
            return eVarA.a() < y;
        } catch (Throwable unused) {
            return true;
        }
    }

    public static boolean h(String str) {
        e eVarA;
        try {
            if (TextUtils.isEmpty(str) || !i) {
                return false;
            }
            if (!(B.get(str) == null)) {
                return false;
            }
            Context context = c;
            if (context == null || (eVarA = a(context, b(str, "a15"), "open_common")) == null) {
                return true;
            }
            if (eVarA.a() < z) {
                return true;
            }
        } catch (Throwable unused) {
        }
        return false;
    }

    private static String b(String str, String str2) {
        return str2 + "_" + io.a(str.getBytes());
    }

    public static void b(ky.c cVar) {
        synchronized (C) {
            boolean z2 = false;
            for (int i2 = 0; i2 < C.size(); i2++) {
                ky.a aVar = C.get(i2);
                if (cVar.c.equals(aVar.b) && cVar.d.equals(aVar.e) && cVar.m == aVar.f) {
                    if (aVar.f == 1) {
                        aVar.i = ((((long) aVar.j.get()) * aVar.i) + cVar.f) / ((long) (aVar.j.get() + 1));
                    }
                    aVar.j.getAndIncrement();
                    z2 = true;
                }
            }
            if (!z2) {
                C.add(new ky.a(cVar));
            }
            ky.b();
        }
    }

    public static ky.a f() {
        if (D) {
            return null;
        }
        synchronized (C) {
            if (D) {
                return null;
            }
            Collections.sort(C);
            if (C.size() <= 0) {
                return null;
            }
            ky.a aVarClone = C.get(0).clone();
            D = true;
            return aVarClone;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x006e A[Catch: all -> 0x00a7, LOOP:0: B:23:0x0068->B:25:0x006e, LOOP_END, TryCatch #0 {, blocks: (B:9:0x000d, B:10:0x0013, B:12:0x0019, B:14:0x0029, B:16:0x0033, B:18:0x0039, B:20:0x003f, B:21:0x0046, B:22:0x005c, B:23:0x0068, B:25:0x006e, B:26:0x00a2, B:27:0x00a5), top: B:33:0x000d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void a(boolean r4, com.amap.api.col.3sl.ky.a r5) {
        /*
            boolean r0 = com.amap.api.col.p0003sl.ih.D
            if (r0 == 0) goto Laa
            if (r5 != 0) goto L8
            goto Laa
        L8:
            java.util.ArrayList<com.amap.api.col.3sl.ky$a> r0 = com.amap.api.col.p0003sl.ih.C
            monitor-enter(r0)
            if (r4 == 0) goto L5c
            java.util.ArrayList<com.amap.api.col.3sl.ky$a> r4 = com.amap.api.col.p0003sl.ih.C     // Catch: java.lang.Throwable -> La7
            java.util.Iterator r4 = r4.iterator()     // Catch: java.lang.Throwable -> La7
        L13:
            boolean r1 = r4.hasNext()     // Catch: java.lang.Throwable -> La7
            if (r1 == 0) goto L5c
            java.lang.Object r1 = r4.next()     // Catch: java.lang.Throwable -> La7
            com.amap.api.col.3sl.ky$a r1 = (com.amap.api.col.3sl.ky.a) r1     // Catch: java.lang.Throwable -> La7
            java.lang.String r2 = r1.b     // Catch: java.lang.Throwable -> La7
            java.lang.String r3 = r5.b     // Catch: java.lang.Throwable -> La7
            boolean r2 = r2.equals(r3)     // Catch: java.lang.Throwable -> La7
            if (r2 == 0) goto L13
            java.lang.String r2 = r1.e     // Catch: java.lang.Throwable -> La7
            java.lang.String r3 = r5.e     // Catch: java.lang.Throwable -> La7
            boolean r2 = r2.equals(r3)     // Catch: java.lang.Throwable -> La7
            if (r2 == 0) goto L13
            int r2 = r1.f     // Catch: java.lang.Throwable -> La7
            int r3 = r5.f     // Catch: java.lang.Throwable -> La7
            if (r2 != r3) goto L13
            java.util.concurrent.atomic.AtomicInteger r2 = r1.j     // Catch: java.lang.Throwable -> La7
            java.util.concurrent.atomic.AtomicInteger r3 = r5.j     // Catch: java.lang.Throwable -> La7
            if (r2 != r3) goto L46
            r4.remove()     // Catch: java.lang.Throwable -> La7
            com.amap.api.col.p0003sl.ky.b()     // Catch: java.lang.Throwable -> La7
            goto L13
        L46:
            java.util.concurrent.atomic.AtomicInteger r2 = r1.j     // Catch: java.lang.Throwable -> La7
            java.util.concurrent.atomic.AtomicInteger r1 = r1.j     // Catch: java.lang.Throwable -> La7
            int r1 = r1.get()     // Catch: java.lang.Throwable -> La7
            java.util.concurrent.atomic.AtomicInteger r3 = r5.j     // Catch: java.lang.Throwable -> La7
            int r3 = r3.get()     // Catch: java.lang.Throwable -> La7
            int r1 = r1 - r3
            r2.set(r1)     // Catch: java.lang.Throwable -> La7
            com.amap.api.col.p0003sl.ky.b()     // Catch: java.lang.Throwable -> La7
            goto L13
        L5c:
            r4 = 0
            com.amap.api.col.p0003sl.ih.D = r4     // Catch: java.lang.Throwable -> La7
            java.util.ArrayList<com.amap.api.col.3sl.ky$a> r4 = com.amap.api.col.p0003sl.ih.C     // Catch: java.lang.Throwable -> La7
            java.util.Iterator r4 = r4.iterator()     // Catch: java.lang.Throwable -> La7
            com.amap.api.col.p0003sl.ky.b()     // Catch: java.lang.Throwable -> La7
        L68:
            boolean r5 = r4.hasNext()     // Catch: java.lang.Throwable -> La7
            if (r5 == 0) goto La2
            java.lang.Object r5 = r4.next()     // Catch: java.lang.Throwable -> La7
            com.amap.api.col.3sl.ky$a r5 = (com.amap.api.col.3sl.ky.a) r5     // Catch: java.lang.Throwable -> La7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La7
            java.lang.String r2 = "----path="
            r1.<init>(r2)     // Catch: java.lang.Throwable -> La7
            java.lang.String r2 = r5.e     // Catch: java.lang.Throwable -> La7
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> La7
            java.lang.String r2 = "-counts="
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> La7
            java.util.concurrent.atomic.AtomicInteger r2 = r5.j     // Catch: java.lang.Throwable -> La7
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> La7
            java.lang.String r2 = "-code="
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> La7
            int r5 = r5.f     // Catch: java.lang.Throwable -> La7
            java.lang.StringBuilder r5 = r1.append(r5)     // Catch: java.lang.Throwable -> La7
            java.lang.String r1 = "----"
            r5.append(r1)     // Catch: java.lang.Throwable -> La7
            com.amap.api.col.p0003sl.ky.b()     // Catch: java.lang.Throwable -> La7
            goto L68
        La2:
            com.amap.api.col.p0003sl.ky.b()     // Catch: java.lang.Throwable -> La7
            monitor-exit(r0)     // Catch: java.lang.Throwable -> La7
            return
        La7:
            r4 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> La7
            throw r4
        Laa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ih.a(boolean, com.amap.api.col.3sl.ky$a):void");
    }

    public static void c(ky.c cVar) {
        if (cVar != null && j) {
            synchronized (E) {
                E.offer(cVar);
                ky.b();
            }
        }
    }

    public static ky.c g() {
        synchronized (E) {
            ky.c cVarPoll = E.poll();
            if (cVarPoll != null) {
                return cVarPoll;
            }
            return null;
        }
    }

    public static void d() {
        try {
            e eVarA = a(c, "IPV6_CONFIG_NAME", "open_common");
            String strA = it.a(System.currentTimeMillis(), "yyyyMMdd");
            if (!strA.equals(eVarA.b)) {
                eVarA.a(strA);
                eVarA.c.set(0);
            }
            eVarA.c.incrementAndGet();
            a(c, "IPV6_CONFIG_NAME", "open_common", eVarA);
        } catch (Throwable unused) {
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x002f A[DONT_GENERATE] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0031  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static synchronized com.amap.api.col.3sl.ih.e a(android.content.Context r6, java.lang.String r7, java.lang.String r8) {
        /*
            java.lang.Class<com.amap.api.col.3sl.ih> r0 = com.amap.api.col.p0003sl.ih.class
            monitor-enter(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r7)     // Catch: java.lang.Throwable -> L6b
            r2 = 0
            r3 = 0
            if (r1 != 0) goto L2c
            r1 = r3
        Lc:
            java.util.Vector<com.amap.api.col.3sl.ih$e> r4 = com.amap.api.col.p0003sl.ih.p     // Catch: java.lang.Throwable -> L6b
            int r4 = r4.size()     // Catch: java.lang.Throwable -> L6b
            if (r1 >= r4) goto L2c
            java.util.Vector<com.amap.api.col.3sl.ih$e> r4 = com.amap.api.col.p0003sl.ih.p     // Catch: java.lang.Throwable -> L6b
            java.lang.Object r4 = r4.get(r1)     // Catch: java.lang.Throwable -> L6b
            com.amap.api.col.3sl.ih$e r4 = (com.amap.api.col.3sl.ih.e) r4     // Catch: java.lang.Throwable -> L6b
            if (r4 == 0) goto L29
            java.lang.String r5 = com.amap.api.col.3sl.ih.e.c(r4)     // Catch: java.lang.Throwable -> L6b
            boolean r5 = r7.equals(r5)     // Catch: java.lang.Throwable -> L6b
            if (r5 == 0) goto L29
            goto L2d
        L29:
            int r1 = r1 + 1
            goto Lc
        L2c:
            r4 = r2
        L2d:
            if (r4 == 0) goto L31
            monitor-exit(r0)
            return r4
        L31:
            if (r6 != 0) goto L35
            monitor-exit(r0)
            return r2
        L35:
            java.lang.String r1 = ""
            java.lang.String r6 = com.amap.api.col.p0003sl.kj.b(r6, r8, r7, r1)     // Catch: java.lang.Throwable -> L6b
            com.amap.api.col.3sl.ih$e r6 = com.amap.api.col.3sl.ih.e.b(r6)     // Catch: java.lang.Throwable -> L6b
            long r1 = java.lang.System.currentTimeMillis()     // Catch: java.lang.Throwable -> L6b
            java.lang.String r8 = "yyyyMMdd"
            java.lang.String r8 = com.amap.api.col.p0003sl.it.a(r1, r8)     // Catch: java.lang.Throwable -> L6b
            if (r6 != 0) goto L50
            com.amap.api.col.3sl.ih$e r6 = new com.amap.api.col.3sl.ih$e     // Catch: java.lang.Throwable -> L6b
            r6.<init>(r7, r8, r3)     // Catch: java.lang.Throwable -> L6b
        L50:
            java.lang.String r7 = com.amap.api.col.3sl.ih.e.a(r6)     // Catch: java.lang.Throwable -> L6b
            boolean r7 = r8.equals(r7)     // Catch: java.lang.Throwable -> L6b
            if (r7 != 0) goto L64
            r6.a(r8)     // Catch: java.lang.Throwable -> L6b
            java.util.concurrent.atomic.AtomicInteger r7 = com.amap.api.col.3sl.ih.e.b(r6)     // Catch: java.lang.Throwable -> L6b
            r7.set(r3)     // Catch: java.lang.Throwable -> L6b
        L64:
            java.util.Vector<com.amap.api.col.3sl.ih$e> r7 = com.amap.api.col.p0003sl.ih.p     // Catch: java.lang.Throwable -> L6b
            r7.add(r6)     // Catch: java.lang.Throwable -> L6b
            monitor-exit(r0)
            return r6
        L6b:
            r6 = move-exception
            monitor-exit(r0)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ih.a(android.content.Context, java.lang.String, java.lang.String):com.amap.api.col.3sl.ih$e");
    }
}
