package com.amap.api.col.p0003sl;

import android.app.backup.FullBackup;
import android.text.TextUtils;

/* JADX INFO: compiled from: SDKInfo.java */
/* JADX INFO: loaded from: classes2.dex */
@kg(a = FullBackup.APK_TREE_TOKEN)
public final class is {

    @kh(a = "a1", b = 6)
    private String a;

    @kh(a = "a2", b = 6)
    private String b;

    @kh(a = "a6", b = 2)
    private int c;

    @kh(a = "a3", b = 6)
    private String d;

    @kh(a = "a4", b = 6)
    private String e;

    @kh(a = "a5", b = 6)
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;
    private String k;
    private String[] l;

    /* synthetic */ is(a aVar, byte b) {
        this(aVar);
    }

    private is() {
        this.c = 1;
        this.l = null;
    }

    private is(a aVar) {
        this.c = 1;
        this.l = null;
        this.g = aVar.a;
        this.h = aVar.b;
        this.j = aVar.c;
        this.i = aVar.d;
        this.c = aVar.e ? 1 : 0;
        this.k = aVar.f;
        this.l = aVar.g;
        this.b = it.b(this.h);
        this.a = it.b(this.j);
        this.d = it.b(this.i);
        this.e = it.b(a(this.l));
        this.f = it.b(this.k);
    }

    /* JADX INFO: compiled from: SDKInfo.java */
    public static class a {
        private String a;
        private String b;
        private String c;
        private String d;
        private boolean e = true;
        private String f = "standard";
        private String[] g = null;

        public a(String str, String str2, String str3) {
            this.a = str2;
            this.b = str2;
            this.d = str3;
            this.c = str;
        }

        public final a a(boolean z) {
            this.e = z;
            return this;
        }

        public final a a(String[] strArr) {
            if (strArr != null) {
                this.g = (String[]) strArr.clone();
            }
            return this;
        }

        public final a a(String str) {
            this.b = str;
            return this;
        }

        public final is a() throws Cif {
            if (this.g == null) {
                throw new Cif("sdk packages is null");
            }
            return new is(this, (byte) 0);
        }
    }

    public final void a(boolean z) {
        this.c = z ? 1 : 0;
    }

    public final String a() {
        if (TextUtils.isEmpty(this.j) && !TextUtils.isEmpty(this.a)) {
            this.j = it.c(this.a);
        }
        return this.j;
    }

    public final String b() {
        return this.g;
    }

    public final String c() {
        if (TextUtils.isEmpty(this.h) && !TextUtils.isEmpty(this.b)) {
            this.h = it.c(this.b);
        }
        return this.h;
    }

    public final String d() {
        if (TextUtils.isEmpty(this.k) && !TextUtils.isEmpty(this.f)) {
            this.k = it.c(this.f);
        }
        if (TextUtils.isEmpty(this.k)) {
            this.k = "standard";
        }
        return this.k;
    }

    public final boolean e() {
        return this.c == 1;
    }

    public final String[] f() {
        String[] strArr = this.l;
        if ((strArr == null || strArr.length == 0) && !TextUtils.isEmpty(this.e)) {
            this.l = a(it.c(this.e));
        }
        return (String[]) this.l.clone();
    }

    private static String[] a(String str) {
        try {
            return str.split(";");
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    private static String a(String[] strArr) {
        if (strArr == null) {
            return null;
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (String str : strArr) {
                sb.append(str).append(";");
            }
            return sb.toString();
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        try {
            if (this.j.equals(((is) obj).j) && this.g.equals(((is) obj).g)) {
                if (this.h.equals(((is) obj).h)) {
                    return true;
                }
            }
        } catch (Throwable unused) {
        }
        return false;
    }
}
