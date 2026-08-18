package com.amap.api.col.p0003sl;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: compiled from: CrashManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jy {
    private is a;

    /* JADX INFO: compiled from: CrashManager.java */
    static class a {
        public static Map<String, jy> a = new HashMap();
    }

    private jy(is isVar) {
        this.a = isVar;
    }

    public static jy a(is isVar) {
        if (a.a.get(isVar.a()) == null) {
            a.a.put(isVar.a(), new jy(isVar));
        }
        return a.a.get(isVar.a());
    }

    public final void a(Context context, boolean z, boolean z2) {
        kb.a(context, this.a, "sckey", String.valueOf(z));
        if (z) {
            kb.a(context, this.a, "scisf", String.valueOf(z2));
        }
    }

    public final boolean a(Context context) {
        try {
            return Boolean.parseBoolean(kb.a(context, this.a, "sckey"));
        } catch (Throwable unused) {
            return false;
        }
    }

    public final boolean b(Context context) {
        try {
            return Boolean.parseBoolean(kb.a(context, this.a, "scisf"));
        } catch (Throwable unused) {
            return true;
        }
    }
}
