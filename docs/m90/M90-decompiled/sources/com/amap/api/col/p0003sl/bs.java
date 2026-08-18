package com.amap.api.col.p0003sl;

import android.content.Context;
import android.view.Window;
import com.amap.api.col.p0003sl.by;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.col.p0003sl.kw;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapsInitializer;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/* JADX INFO: compiled from: NetFileFetch.java */
/* JADX INFO: loaded from: classes2.dex */
public final class bs implements kw.a {
    bt a;
    long d;
    bn f;
    a h;
    private Context i;
    private by j;
    private String k;
    private ld l;
    private bo m;
    long b = 0;
    long c = 0;
    boolean e = true;
    long g = 0;
    private boolean n = false;

    /* JADX INFO: compiled from: NetFileFetch.java */
    public interface a {
        void c();
    }

    public bs(bt btVar, String str, Context context, by byVar) throws IOException {
        this.a = null;
        this.f = bn.a(context.getApplicationContext());
        this.a = btVar;
        this.i = context;
        this.k = str;
        this.j = byVar;
        d();
    }

    private void c() throws IOException {
        bz bzVar = new bz(this.k);
        bzVar.setConnectionTimeout(Window.PROGRESS_SECONDARY_END);
        bzVar.setSoTimeout(Window.PROGRESS_SECONDARY_END);
        this.l = new ld(bzVar, this.b, this.c, MapsInitializer.getProtocol() == 2);
        this.m = new bo(this.a.b() + File.separator + this.a.c(), this.b);
    }

    private void d() {
        File file = new File(this.a.b() + this.a.c());
        if (file.exists()) {
            this.e = false;
            this.b = file.length();
            try {
                long jG = g();
                this.d = jG;
                this.c = jG;
                return;
            } catch (IOException unused) {
                by byVar = this.j;
                if (byVar != null) {
                    byVar.a(by.a.file_io_exception);
                    return;
                }
                return;
            }
        }
        this.b = 0L;
        this.c = 0L;
    }

    public final void a() {
        try {
            if (dx.d(this.i)) {
                f();
                if (ih.a != 1) {
                    by byVar = this.j;
                    if (byVar != null) {
                        byVar.a(by.a.amap_exception);
                        return;
                    }
                    return;
                }
                if (!e()) {
                    this.e = true;
                }
                if (this.e) {
                    long jG = g();
                    this.d = jG;
                    if (jG != -1 && jG != -2) {
                        this.c = jG;
                    }
                    this.b = 0L;
                }
                by byVar2 = this.j;
                if (byVar2 != null) {
                    byVar2.m();
                }
                if (this.b >= this.c) {
                    onFinish();
                    return;
                } else {
                    c();
                    this.l.a(this);
                    return;
                }
            }
            by byVar3 = this.j;
            if (byVar3 != null) {
                byVar3.a(by.a.network_exception);
            }
        } catch (AMapException e) {
            jw.c(e, "SiteFileFetch", Context.DOWNLOAD_SERVICE);
            by byVar4 = this.j;
            if (byVar4 != null) {
                byVar4.a(by.a.amap_exception);
            }
        } catch (IOException unused) {
            by byVar5 = this.j;
            if (byVar5 != null) {
                byVar5.a(by.a.file_io_exception);
            }
        }
    }

    private boolean e() {
        return new File(new StringBuilder().append(this.a.b()).append(File.separator).append(this.a.c()).toString()).length() >= 10;
    }

    private void f() throws AMapException {
        if (ih.a != 1) {
            for (int i = 0; i < 3; i++) {
                try {
                    ih.a(this.i, dx.a(), "", (Map<String, String>) null);
                } catch (Throwable th) {
                    jw.c(th, "SiteFileFetch", "authOffLineDownLoad");
                    th.printStackTrace();
                }
                if (ih.a == 1) {
                    return;
                }
            }
        }
    }

    /* JADX INFO: compiled from: NetFileFetch.java */
    private static class b extends db {
        private final String a;

        @Override // com.amap.api.col.p0003sl.lb
        public final Map<String, String> getRequestHead() {
            return null;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final boolean isSupportIPV6() {
            return false;
        }

        public b(String str) {
            this.a = str;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final String getURL() {
            return this.a;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final String getIPV6URL() {
            return getURL();
        }
    }

    private long g() throws IOException {
        if (ip.a(this.i, dx.a()).a != ip.c.SuccessCode) {
            return -1L;
        }
        String strA = this.a.a();
        Map<String, String> mapD = null;
        try {
            la.b();
            mapD = la.d((lb) new b(strA), MapsInitializer.getProtocol() == 2);
        } catch (Cif e) {
            e.printStackTrace();
        }
        int i = -1;
        if (mapD != null) {
            for (String str : mapD.keySet()) {
                if ("Content-Length".equalsIgnoreCase(str)) {
                    i = Integer.parseInt(mapD.get(str));
                }
            }
        }
        return i;
    }

    private void h() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (this.a == null || jCurrentTimeMillis - this.g <= 500) {
            return;
        }
        i();
        this.g = jCurrentTimeMillis;
        a(this.b);
    }

    private void i() {
        this.f.a(this.a.e(), this.a.d(), this.d, this.b, this.c);
    }

    private void a(long j) {
        by byVar;
        long j2 = this.d;
        if (j2 <= 0 || (byVar = this.j) == null) {
            return;
        }
        byVar.a(j2, j);
        this.g = System.currentTimeMillis();
    }

    public final void b() {
        ld ldVar = this.l;
        if (ldVar != null) {
            ldVar.a();
        }
    }

    @Override // com.amap.api.col.3sl.kw.a
    public final void onStop() {
        if (this.n) {
            return;
        }
        by byVar = this.j;
        if (byVar != null) {
            byVar.o();
        }
        i();
    }

    @Override // com.amap.api.col.3sl.kw.a
    public final void onFinish() {
        h();
        by byVar = this.j;
        if (byVar != null) {
            byVar.n();
        }
        bo boVar = this.m;
        if (boVar != null) {
            boVar.a();
        }
        a aVar = this.h;
        if (aVar != null) {
            aVar.c();
        }
    }

    @Override // com.amap.api.col.3sl.kw.a
    public final void onException(Throwable th) {
        bo boVar;
        this.n = true;
        b();
        by byVar = this.j;
        if (byVar != null) {
            byVar.a(by.a.network_exception);
        }
        if ((th instanceof IOException) || (boVar = this.m) == null) {
            return;
        }
        boVar.a();
    }

    @Override // com.amap.api.col.3sl.kw.a
    public final void onDownload(byte[] bArr, long j) {
        try {
            this.m.a(bArr);
            this.b = j;
            h();
        } catch (IOException e) {
            e.printStackTrace();
            jw.c(e, "fileAccessI", "fileAccessI.write(byte[] data)");
            by byVar = this.j;
            if (byVar != null) {
                byVar.a(by.a.file_io_exception);
            }
            ld ldVar = this.l;
            if (ldVar != null) {
                ldVar.a();
            }
        }
    }

    public final void a(a aVar) {
        this.h = aVar;
    }
}
