package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.maps.model.LatLng;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Iterator;

/* JADX INFO: compiled from: InfoCollectUtils.java */
/* JADX INFO: loaded from: classes2.dex */
public class dl {
    private static boolean a = false;
    private static volatile dl d;
    private Hashtable<String, String> b = new Hashtable<>();
    private WeakReference<Context> c = null;

    private dl() {
    }

    public static dl a() {
        if (d == null) {
            synchronized (dl.class) {
                if (d == null) {
                    d = new dl();
                }
            }
        }
        return d;
    }

    public static void b() {
        if (d != null) {
            if (d.b != null && d.b.size() > 0) {
                synchronized (d.b) {
                    d.d();
                    if (d.c != null) {
                        d.c.clear();
                    }
                }
            }
            d = null;
        }
        a(false);
    }

    public static void a(boolean z) {
        a = z;
    }

    public static boolean c() {
        return a;
    }

    public final void a(Context context) {
        if (context != null) {
            this.c = new WeakReference<>(context);
        }
    }

    public final void a(LatLng latLng, String str, String str2) {
        if (!a) {
            this.b.clear();
            return;
        }
        if (latLng == null || TextUtils.isEmpty(str)) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{");
        stringBuffer.append("\"lon\":").append(latLng.longitude).append(",");
        stringBuffer.append("\"lat\":").append(latLng.latitude).append(",");
        stringBuffer.append("\"title\":\"").append(str).append("\",");
        if (TextUtils.isEmpty(str2)) {
            str2 = "";
        }
        stringBuffer.append("\"snippet\":\"").append(str2).append("\"");
        stringBuffer.append("}");
        a(stringBuffer.toString());
    }

    private void a(String str) {
        Hashtable<String, String> hashtable;
        if (str == null || (hashtable = this.b) == null) {
            return;
        }
        synchronized (hashtable) {
            String strB = io.b(str);
            Hashtable<String, String> hashtable2 = this.b;
            if (hashtable2 != null && !hashtable2.contains(strB)) {
                this.b.put(strB, str);
            }
            if (e()) {
                d();
            }
        }
    }

    private void d() {
        WeakReference<Context> weakReference;
        if (!a) {
            this.b.clear();
            return;
        }
        if (this.b != null) {
            StringBuffer stringBuffer = new StringBuffer();
            int i = 0;
            int size = this.b.size();
            if (size > 0) {
                stringBuffer.append("[");
                Iterator<String> it = this.b.values().iterator();
                while (it.hasNext()) {
                    i++;
                    stringBuffer.append(it.next());
                    if (i < size) {
                        stringBuffer.append(",");
                    }
                }
                stringBuffer.append("]");
                String string = stringBuffer.toString();
                if (!TextUtils.isEmpty(string) && (weakReference = this.c) != null && weakReference.get() != null) {
                    lh.a(string, this.c.get());
                }
            }
            this.b.clear();
        }
    }

    private boolean e() {
        Hashtable<String, String> hashtable = this.b;
        return hashtable != null && hashtable.size() > 20;
    }
}
