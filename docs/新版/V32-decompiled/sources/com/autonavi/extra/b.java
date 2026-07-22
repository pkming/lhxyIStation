package com.autonavi.extra;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: compiled from: AMapExtraInterfaceManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class b {
    private List<a> a = new ArrayList();

    public final void a() {
        synchronized (b.class) {
            List<a> list = this.a;
            if (list != null) {
                list.add(null);
            }
        }
    }

    public final void b() {
        synchronized (b.class) {
            Iterator<a> it = this.a.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
    }

    public final void c() {
        synchronized (b.class) {
            Iterator<a> it = this.a.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
    }

    public final void d() {
        synchronized (b.class) {
            Iterator<a> it = this.a.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
    }

    public final void e() {
        synchronized (b.class) {
            Iterator<a> it = this.a.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
    }

    public final void f() {
        synchronized (b.class) {
            Iterator<a> it = this.a.iterator();
            while (it.hasNext()) {
                it.next();
            }
            this.a.clear();
        }
    }

    public final String g() {
        String string;
        synchronized (b.class) {
            StringBuffer stringBuffer = new StringBuffer();
            for (a aVar : this.a) {
                if (aVar != null) {
                    String strA = aVar.a();
                    if (!TextUtils.isEmpty(strA)) {
                        stringBuffer.append(strA);
                        if (!strA.endsWith(";")) {
                            stringBuffer.append(";");
                        }
                    }
                }
            }
            string = stringBuffer.toString();
        }
        return string;
    }

    public final void h() {
        synchronized (b.class) {
            Iterator<a> it = this.a.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
    }

    public final void i() {
        synchronized (b.class) {
            Iterator<a> it = this.a.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
    }

    public final Object j() {
        Object objB;
        synchronized (b.class) {
            for (a aVar : this.a) {
                if (aVar != null && (objB = aVar.b()) != null) {
                    return objB;
                }
            }
            return null;
        }
    }
}
