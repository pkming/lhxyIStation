package com.amap.api.col.p0003sl;

import android.util.Log;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: compiled from: OverlayerStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ec implements ea {
    private static Map<String, a> a = new ConcurrentHashMap();

    /* JADX INFO: compiled from: OverlayerStrategy.java */
    class a {
        String a;
        String b;
        int c;
        final AtomicInteger d = new AtomicInteger(0);

        public a(int i, String str, String str2) {
            this.a = "";
            this.b = "";
            this.a = str;
            this.b = str2;
            this.c = i;
        }

        public final int a() {
            return this.d.incrementAndGet();
        }
    }

    private static String b(int i, String str, String str2) {
        StringBuilder sbAppend = new StringBuilder().append(i);
        if (str == null) {
            str = "";
        }
        StringBuilder sbAppend2 = sbAppend.append(str);
        if (str2 == null) {
            str2 = "";
        }
        return sbAppend2.append(str2).toString();
    }

    @Override // com.amap.api.col.p0003sl.ea
    public final void a(int i, String str, String str2) {
        try {
            String strB = b(i, str, str2);
            a aVar = a.get(strB);
            if (aVar == null) {
                aVar = new a(i, str, str2);
                a.put(strB, aVar);
            }
            if (aVar.a() > 100) {
                a(aVar.c, aVar.a, aVar.b, aVar.d.get());
                a.remove(strB);
            }
        } catch (Throwable unused) {
        }
    }

    @Override // com.amap.api.col.p0003sl.ea
    public final void a() {
        try {
            Iterator<Map.Entry<String, a>> it = a.entrySet().iterator();
            while (it.hasNext()) {
                a value = it.next().getValue();
                if (value != null) {
                    a(value.c, value.a, value.b, value.d.get());
                }
            }
            a.clear();
            jd.a(dx.a()).a();
        } catch (Throwable unused) {
        }
    }

    private static void a(int i, String str, String str2, int i2) {
        if (i == 0) {
            jd.a(dx.a()).a(jc.a(str, str2 + " counter " + i2));
        } else {
            jd.a(dx.a()).a(jc.a(str, str2 + " counter " + i2));
        }
        if (dy.b) {
            c(i, str, str2 + " counter " + i2);
        }
    }

    private static void c(int i, String str, String str2) {
        if (i == 0) {
            Log.i("linklog", str + " " + str2);
        } else {
            Log.e("linklog", str + " " + str2);
        }
    }
}
