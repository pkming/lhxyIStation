package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: IDownloadListener.java */
/* JADX INFO: loaded from: classes2.dex */
public interface by {
    void a(long j, long j2);

    void a(a aVar);

    void m();

    void n();

    void o();

    /* JADX INFO: compiled from: IDownloadListener.java */
    public enum a {
        amap_exception(-1),
        network_exception(-1),
        file_io_exception(0),
        success_no_exception(1),
        cancel_no_exception(2);

        private int f;

        a(int i) {
            this.f = i;
        }
    }
}
