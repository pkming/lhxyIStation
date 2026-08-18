package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.lb;

/* JADX INFO: compiled from: DownloadManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class kw {
    private ky a;
    private lb b;
    private long c;
    private long d;

    /* JADX INFO: compiled from: DownloadManager.java */
    public interface a {
        void onDownload(byte[] bArr, long j);

        void onException(Throwable th);

        void onFinish();

        void onStop();
    }

    public kw(lb lbVar) {
        this(lbVar, (byte) 0);
    }

    private kw(lb lbVar, byte b) {
        this(lbVar, 0L, -1L, false);
    }

    public kw(lb lbVar, long j, long j2, boolean z) {
        this.b = lbVar;
        this.c = j;
        this.d = j2;
        lbVar.setHttpProtocol(z ? lb.c.HTTPS : lb.c.HTTP);
        this.b.setDegradeAbility(lb.a.SINGLE);
    }

    public final void a(a aVar) {
        try {
            ky kyVar = new ky();
            this.a = kyVar;
            kyVar.b(this.d);
            this.a.a(this.c);
            ku.a();
            if (ku.b(this.b)) {
                this.b.setDegradeType(lb.b.NEVER_GRADE);
                this.a.a(this.b, aVar);
            } else {
                this.b.setDegradeType(lb.b.DEGRADE_ONLY);
                this.a.a(this.b, aVar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void a() {
        ky kyVar = this.a;
        if (kyVar != null) {
            kyVar.a();
        }
    }
}
