package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.apache.tools.ant.util.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: compiled from: HttpLimitUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class kx {
    public static volatile ConcurrentHashMap<String, c> a = new ConcurrentHashMap<>(8);
    public static volatile List<String> b = Collections.synchronizedList(new ArrayList(8));
    private static volatile ConcurrentHashMap<String, b> c = new ConcurrentHashMap<>(8);
    private static Random d = new Random();
    private static ConcurrentHashMap<String, String> e = new ConcurrentHashMap<>(8);
    private static List<lk> f = Collections.synchronizedList(new ArrayList(16));

    public static synchronized void a(is isVar, JSONObject jSONObject) {
        if (isVar == null) {
            return;
        }
        try {
            String strA = isVar.a();
            if (TextUtils.isEmpty(strA)) {
                return;
            }
            if (jSONObject == null) {
                a(strA);
            }
            if (!ih.a(jSONObject.optString("able", null), false)) {
                a(strA);
            } else {
                kj.a(ih.c, "Yb3Blbl9odHRwX2NvbnRyb2w", strA, jSONObject.toString());
                a(strA, jSONObject);
            }
        } catch (Throwable th) {
            jt.a(th, "hlUtil", "par");
        }
    }

    private static void a(String str, JSONObject jSONObject) {
        try {
            c cVar = new c((byte) 0);
            a(cVar, jSONObject);
            b(cVar, jSONObject);
            if (cVar.b == null && cVar.a == null) {
                a(str);
            } else {
                a(str, cVar);
            }
        } catch (Throwable unused) {
        }
    }

    public static synchronized String a(String str, String str2) throws Cif {
        try {
            try {
                System.currentTimeMillis();
                if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str)) {
                    Context context = ih.c;
                    try {
                        if (b == null) {
                            b = Collections.synchronizedList(new ArrayList(8));
                        }
                        if (context != null && !b.contains(str2)) {
                            b.add(str2);
                            String strA = kj.a(context, "Yb3Blbl9odHRwX2NvbnRyb2w", str2);
                            if (!TextUtils.isEmpty(strA)) {
                                a(str2, new JSONObject(strA));
                            }
                        }
                    } catch (Throwable th) {
                        jt.a(th, "hlUtil", "llhl");
                    }
                    if (a != null && a.size() > 0) {
                        if (!a.containsKey(str2)) {
                            return str;
                        }
                        c cVar = a.get(str2);
                        if (cVar == null) {
                            return str;
                        }
                        if (a(str, cVar, str2)) {
                            throw new Cif("服务QPS超限");
                        }
                        return b(str, cVar, str2);
                    }
                    return str;
                }
                return str;
            } finally {
            }
        } catch (Cif e2) {
            throw e2;
        } catch (Throwable th2) {
            jt.a(th2, "hlUtil", "pcr");
            return str;
        }
    }

    /* JADX INFO: compiled from: HttpLimitUtil.java */
    private static class b {
        lc a;
        long b;

        private b() {
        }

        /* synthetic */ b(byte b) {
            this();
        }
    }

    public static void a(URL url, lc lcVar) {
        List<String> list;
        try {
            if (c == null) {
                c = new ConcurrentHashMap<>(8);
            }
            if (lcVar.b != null && lcVar.b.containsKey("nb") && (list = lcVar.b.get("nb")) != null && list.size() > 0) {
                byte b2 = 0;
                String[] strArrSplit = list.get(0).split("#");
                if (strArrSplit.length < 2) {
                    return;
                }
                int i = Integer.parseInt(strArrSplit[0]);
                long j = Integer.parseInt(strArrSplit[1]);
                b bVar = new b(b2);
                bVar.a = lcVar;
                if (j <= 0) {
                    j = 30;
                }
                bVar.b = SystemClock.elapsedRealtime() + (j * 1000);
                if (i == 1) {
                    c.put(Settings.System.SHORTCUT_PATH_TYPE_APP, bVar);
                } else {
                    if (i != 2 || url == null) {
                        return;
                    }
                    c.put(url.getPath(), bVar);
                }
            }
        } catch (Throwable unused) {
        }
    }

    public static lc b(String str, String str2) {
        Uri uri;
        if (c == null) {
            return null;
        }
        if (c.containsKey(Settings.System.SHORTCUT_PATH_TYPE_APP)) {
            b bVar = c.get(Settings.System.SHORTCUT_PATH_TYPE_APP);
            if (SystemClock.elapsedRealtime() <= bVar.b) {
                lc lcVar = bVar.a;
                if (lcVar != null) {
                    lcVar.e = false;
                }
                a(true, str2, str, 1);
                return lcVar;
            }
            c.remove(Settings.System.SHORTCUT_PATH_TYPE_APP);
        } else if (!TextUtils.isEmpty(str) && (uri = Uri.parse(str)) != null) {
            String path = uri.getPath();
            if (c.containsKey(path)) {
                b bVar2 = c.get(path);
                if (SystemClock.elapsedRealtime() <= bVar2.b) {
                    lc lcVar2 = bVar2.a;
                    if (lcVar2 != null) {
                        lcVar2.e = false;
                    }
                    a(true, str2, str, 2);
                    return lcVar2;
                }
                c.remove(path);
            }
        }
        return null;
    }

    private static void a(String str, c cVar) {
        try {
            if (a == null) {
                a = new ConcurrentHashMap<>(8);
            }
            a.put(str, cVar);
        } catch (Throwable th) {
            jt.a(th, "hlUtil", "ucr");
        }
    }

    private static void a(c cVar, JSONObject jSONObject) {
        try {
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("block");
            if (jSONArrayOptJSONArray == null) {
                return;
            }
            HashMap map = new HashMap(8);
            byte b2 = 0;
            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i);
                if (jSONObjectOptJSONObject != null) {
                    String strOptString = jSONObjectOptJSONObject.optString("api");
                    if (!TextUtils.isEmpty(strOptString)) {
                        if (!strOptString.startsWith("/")) {
                            strOptString = "/".concat(String.valueOf(strOptString));
                        }
                        if (strOptString.endsWith("/")) {
                            strOptString = strOptString.substring(0, strOptString.length() - 1);
                        }
                        JSONArray jSONArrayOptJSONArray2 = jSONObjectOptJSONObject.optJSONArray("periods");
                        if (jSONArrayOptJSONArray != null) {
                            ArrayList arrayList = new ArrayList();
                            for (int i2 = 0; i2 < jSONArrayOptJSONArray2.length(); i2++) {
                                JSONObject jSONObjectOptJSONObject2 = jSONArrayOptJSONArray2.optJSONObject(i2);
                                if (jSONObjectOptJSONObject2 != null) {
                                    a aVar = new a(b2);
                                    aVar.a = jSONObjectOptJSONObject2.optString("begin");
                                    aVar.b = jSONObjectOptJSONObject2.optInt("duration");
                                    aVar.c = jSONObjectOptJSONObject2.optDouble("percent");
                                    arrayList.add(aVar);
                                }
                            }
                            map.put(strOptString, arrayList);
                        }
                    }
                }
            }
            cVar.a = map;
        } catch (Throwable th) {
            jt.a(th, "hlUtil", "pbr");
        }
    }

    private static void b(c cVar, JSONObject jSONObject) {
        JSONArray jSONArrayNames;
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("domainMap");
            if (jSONObjectOptJSONObject == null || (jSONArrayNames = jSONObjectOptJSONObject.names()) == null) {
                return;
            }
            HashMap map = new HashMap(8);
            int length = jSONArrayNames.length();
            for (int i = 0; i < length; i++) {
                String strOptString = jSONArrayNames.optString(i);
                map.put(strOptString, jSONObjectOptJSONObject.optString(strOptString));
            }
            cVar.b = map;
        } catch (Throwable th) {
            jt.a(th, "hlUtil", "pdr");
        }
    }

    private static synchronized void a(String str) {
        try {
            if (a.containsKey(str)) {
                a.remove(str);
            }
            SharedPreferences.Editor editorA = kj.a(ih.c, "Yb3Blbl9odHRwX2NvbnRyb2w");
            kj.a(editorA, str);
            kj.a(editorA);
        } catch (Throwable th) {
            jt.a(th, "hlUtil", "rc");
        }
    }

    /* JADX INFO: compiled from: HttpLimitUtil.java */
    private static class c {
        Map<String, List<a>> a;
        Map<String, String> b;

        private c() {
            this.a = new HashMap(8);
            this.b = new HashMap(8);
        }

        /* synthetic */ c(byte b) {
            this();
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && getClass() == obj.getClass()) {
                c cVar = (c) obj;
                if (this.a.equals(cVar.a) && this.b.equals(cVar.b)) {
                    return true;
                }
            }
            return false;
        }

        public final int hashCode() {
            Map<String, List<a>> map = this.a;
            int iHashCode = map != null ? map.hashCode() : 0;
            Map<String, String> map2 = this.b;
            return iHashCode + (map2 != null ? map2.hashCode() : 0);
        }
    }

    /* JADX INFO: compiled from: HttpLimitUtil.java */
    private static class a {
        String a;
        int b;
        double c;

        private a() {
        }

        /* synthetic */ a(byte b) {
            this();
        }
    }

    private static boolean a(String str, c cVar, String str2) {
        Map<String, List<a>> map;
        try {
            map = cVar.a;
        } catch (Throwable th) {
            jt.a(th, "hlUtil", "inb");
        }
        if (map != null && map.size() > 0) {
            if (map.containsKey("*")) {
                Iterator<Map.Entry<String, List<a>>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    if (a(it.next().getValue())) {
                        a(false, str2, str, 1);
                        return true;
                    }
                }
            } else {
                String path = Uri.parse(str).getPath();
                if (map.containsKey(path) && a(map.get(path))) {
                    a(false, str2, str, 2);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean a(List<a> list) {
        if (list != null && list.size() > 0) {
            Iterator<a> it = list.iterator();
            while (it.hasNext()) {
                if (a(it.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean a(a aVar) {
        if (aVar == null || aVar.c == 1.0d) {
            return false;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (!TextUtils.isEmpty(aVar.a) && aVar.b > 0) {
            long timeInMillis = jCurrentTimeMillis - it.a(aVar.a, DateUtils.ISO8601_TIME_PATTERN).getTimeInMillis();
            if (timeInMillis > 0 && timeInMillis < aVar.b * 1000) {
                if (aVar.c == 0.0d) {
                    return true;
                }
                if (d == null) {
                    d = new Random();
                }
                d.setSeed(((long) UUID.randomUUID().hashCode()) + jCurrentTimeMillis);
                if (d.nextDouble() > aVar.c) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String b(String str, c cVar, String str2) {
        try {
            Map<String, String> map = cVar.b;
            if (map != null && map.size() > 0) {
                Uri uri = Uri.parse(str);
                String authority = uri.getAuthority();
                if (!map.containsKey(authority)) {
                    return str;
                }
                String str3 = map.get(authority);
                str = uri.buildUpon().authority(str3).toString();
                a(str2, authority, str3);
                return str;
            }
            return str;
        } catch (Throwable th) {
            jt.a(th, "hlUtil", "pdr");
            return str;
        }
    }

    public static void a(boolean z, String str) {
        try {
            Context context = ih.c;
            if (context != null && !TextUtils.isEmpty(str)) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("timestamp", Long.valueOf(System.currentTimeMillis()));
                if (z) {
                    jSONObject.put("type", jh.g);
                } else {
                    jSONObject.put("type", jh.f);
                }
                jSONObject.put("name", str);
                jSONObject.put("version", jh.a(str));
                String string = jSONObject.toString();
                lk lkVar = new lk(context, "core", EjbJar.CMPVersion.CMP2_0, "O005");
                lkVar.a(string);
                ll.a(lkVar, context);
            }
        } catch (Throwable unused) {
        }
    }

    private static void a(String str, String str2, String str3) {
        try {
            Context context = ih.c;
            if (context != null && !TextUtils.isEmpty(str)) {
                if (e == null) {
                    e = new ConcurrentHashMap<>(8);
                }
                synchronized (e) {
                    if (e.containsKey(str2)) {
                        return;
                    }
                    e.put(str2, str3);
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("timestamp", System.currentTimeMillis());
                    jSONObject.put("type", jh.j);
                    jSONObject.put("name", str);
                    jSONObject.put("version", jh.a(str));
                    jSONObject.put("hostname", str2 + "#" + str3);
                    String string = jSONObject.toString();
                    if (TextUtils.isEmpty(string)) {
                        return;
                    }
                    lk lkVar = new lk(context, "core", EjbJar.CMPVersion.CMP2_0, "O005");
                    lkVar.a(string);
                    ll.a(lkVar, context);
                }
            }
        } catch (Throwable unused) {
        }
    }

    private static void a(boolean z, String str, String str2, int i) {
        try {
            Context context = ih.c;
            if (context != null && !TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("timestamp", System.currentTimeMillis());
                String strA = jh.a(str);
                if (z) {
                    jSONObject.put("type", jh.i);
                } else {
                    jSONObject.put("type", jh.h);
                }
                jSONObject.put("name", str);
                jSONObject.put("version", strA);
                jSONObject.put("uri", Uri.parse(str2).getPath());
                jSONObject.put("blockLevel", i);
                String string = jSONObject.toString();
                if (TextUtils.isEmpty(string)) {
                    return;
                }
                lk lkVar = new lk(context, "core", EjbJar.CMPVersion.CMP2_0, "O005");
                lkVar.a(string);
                if (f == null) {
                    f = Collections.synchronizedList(new ArrayList(16));
                }
                synchronized (f) {
                    f.add(lkVar);
                    if (f.size() >= 15) {
                        a();
                    }
                }
            }
        } catch (Throwable unused) {
        }
    }

    public static void a() {
        try {
            Context context = ih.c;
            if (context == null) {
                return;
            }
            ll.a(b(), context);
        } catch (Throwable unused) {
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:19:0x0028
        	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1182)
        	at jadx.core.dex.visitors.regions.maker.ExcHandlersRegionMaker.collectHandlerRegions(ExcHandlersRegionMaker.java:53)
        	at jadx.core.dex.visitors.regions.maker.ExcHandlersRegionMaker.process(ExcHandlersRegionMaker.java:38)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:27)
        */
    public static java.util.List<com.amap.api.col.p0003sl.lk> b() {
        /*
            r0 = 0
            java.util.List<com.amap.api.col.3sl.lk> r1 = com.amap.api.col.p0003sl.kx.f     // Catch: java.lang.Throwable -> L2a
            monitor-enter(r1)     // Catch: java.lang.Throwable -> L2a
            java.util.List<com.amap.api.col.3sl.lk> r2 = com.amap.api.col.p0003sl.kx.f     // Catch: java.lang.Throwable -> L20
            if (r2 == 0) goto L1e
            int r2 = r2.size()     // Catch: java.lang.Throwable -> L20
            if (r2 <= 0) goto L1e
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L20
            r2.<init>()     // Catch: java.lang.Throwable -> L20
            java.util.List<com.amap.api.col.3sl.lk> r0 = com.amap.api.col.p0003sl.kx.f     // Catch: java.lang.Throwable -> L28
            r2.addAll(r0)     // Catch: java.lang.Throwable -> L28
            java.util.List<com.amap.api.col.3sl.lk> r0 = com.amap.api.col.p0003sl.kx.f     // Catch: java.lang.Throwable -> L28
            r0.clear()     // Catch: java.lang.Throwable -> L28
            r0 = r2
        L1e:
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L20
            goto L2a
        L20:
            r2 = move-exception
            r3 = r2
            r2 = r0
            r0 = r3
        L24:
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L28
            throw r0     // Catch: java.lang.Throwable -> L26
        L26:
            r0 = r2
            goto L2a
        L28:
            r0 = move-exception
            goto L24
        L2a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.kx.b():java.util.List");
    }
}
