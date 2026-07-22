package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.col.p0003sl.kw;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;

/* JADX INFO: compiled from: AuthTaskDownload.java */
/* JADX INFO: loaded from: classes2.dex */
public final class u implements kw.a {
    a a;
    private final Context b;
    private RandomAccessFile c;
    private ld d;
    private String e;

    @Override // com.amap.api.col.3sl.kw.a
    public final void onStop() {
    }

    public u(Context context, a aVar) {
        this.b = context.getApplicationContext();
        this.a = aVar;
        this.d = new ld(new b(aVar));
        this.e = aVar.c();
    }

    public final void a() {
        ld ldVar;
        if (ab.a == null || ip.a(ab.a, dx.a()).a == ip.c.SuccessCode) {
            try {
                if (!b() || (ldVar = this.d) == null) {
                    return;
                }
                ldVar.a(this);
            } catch (Throwable th) {
                jw.c(th, "AuthTaskDownload", "startDownload()");
            }
        }
    }

    private boolean b() {
        c cVarE = this.a.e();
        return (cVarE != null && cVarE.c() && dn.a(this.b, cVarE.a(), cVarE.b(), "").equalsIgnoreCase(this.a.b())) ? false : true;
    }

    @Override // com.amap.api.col.3sl.kw.a
    public final void onDownload(byte[] bArr, long j) {
        try {
            if (this.c == null) {
                File file = new File(this.e);
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                this.c = new RandomAccessFile(file, "rw");
            }
            this.c.seek(j);
            this.c.write(bArr);
        } catch (Throwable th) {
            jw.c(th, "AuthTaskDownload", "onDownload()");
        }
    }

    @Override // com.amap.api.col.3sl.kw.a
    public final void onFinish() {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = this.c;
        } catch (Throwable th) {
            jw.c(th, "AuthTaskDownload", "onFinish()");
        }
        if (randomAccessFile == null) {
            return;
        }
        try {
            randomAccessFile.close();
        } catch (Throwable th2) {
            jw.c(th2, "AuthTaskDownload", "onFinish3");
        }
        String strB = this.a.b();
        String strA = io.a(this.e);
        if (strA != null && strB.equalsIgnoreCase(strA)) {
            String strD = this.a.d();
            try {
                bp bpVar = new bp();
                File file = new File(this.e);
                bpVar.a(file, new File(strD), -1L, bv.a(file), null);
                c cVarE = this.a.e();
                if (cVarE != null && cVarE.c()) {
                    dn.a(this.b, cVarE.a(), cVarE.b(), (Object) strA);
                }
                new File(this.e).delete();
                return;
            } catch (Throwable th3) {
                jw.c(th3, "AuthTaskDownload", "onFinish1");
                return;
            }
        }
        try {
            new File(this.e).delete();
            return;
        } catch (Throwable th4) {
            jw.c(th4, "AuthTaskDownload", "onFinish");
            return;
        }
        jw.c(th, "AuthTaskDownload", "onFinish()");
    }

    @Override // com.amap.api.col.3sl.kw.a
    public final void onException(Throwable th) {
        try {
            RandomAccessFile randomAccessFile = this.c;
            if (randomAccessFile == null) {
                return;
            }
            randomAccessFile.close();
        } catch (Throwable th2) {
            jw.c(th2, "AuthTaskDownload", "onException()");
        }
    }

    /* JADX INFO: compiled from: AuthTaskDownload.java */
    static class c {
        protected String a;
        protected String b;

        public c(String str, String str2) {
            this.a = str;
            this.b = str2;
        }

        public final String a() {
            return this.a;
        }

        public final String b() {
            return this.b;
        }

        public final boolean c() {
            return (TextUtils.isEmpty(this.a) || TextUtils.isEmpty(this.b)) ? false : true;
        }
    }

    /* JADX INFO: compiled from: AuthTaskDownload.java */
    static class a {
        protected String a;
        protected String b;
        protected String c;
        protected String d;
        protected String e;
        protected c f;

        public a(String str, String str2, String str3, String str4) {
            this.a = str;
            this.b = str2;
            this.c = str3;
            this.d = str4 + ".tmp";
            this.e = str4;
        }

        public final String a() {
            return this.a;
        }

        public final String b() {
            return this.b;
        }

        public final String c() {
            return this.d;
        }

        public final String d() {
            return this.e;
        }

        public final void a(c cVar) {
            this.f = cVar;
        }

        public final c e() {
            return this.f;
        }
    }

    /* JADX INFO: compiled from: AuthTaskDownload.java */
    static class d extends a {
        public d(String str, String str2, String str3, String str4) {
            super(str, str2, str3, str4);
        }

        public final void a(String str, String str2) {
            a(new c(str, str2));
        }
    }

    /* JADX INFO: compiled from: AuthTaskDownload.java */
    static class b extends db {
        private final a a;

        @Override // com.amap.api.col.p0003sl.db, com.amap.api.col.p0003sl.lb
        public final Map<String, String> getParams() {
            return null;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final Map<String, String> getRequestHead() {
            return null;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final boolean isSupportIPV6() {
            return false;
        }

        b(a aVar) {
            this.a = aVar;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final String getURL() {
            a aVar = this.a;
            if (aVar != null) {
                return aVar.a();
            }
            return null;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final String getIPV6URL() {
            return getURL();
        }
    }
}
