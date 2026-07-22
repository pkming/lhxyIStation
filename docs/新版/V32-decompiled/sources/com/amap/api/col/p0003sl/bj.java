package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: DTDownloadInfo.java */
/* JADX INFO: loaded from: classes2.dex */
@kg(a = "update_item_download_info")
class bj {

    @kh(a = "mAdcode", b = 6)
    private String a;

    @kh(a = "fileLength", b = 5)
    private long b;

    @kh(a = "splitter", b = 2)
    private int c;

    @kh(a = "startPos", b = 5)
    private long d;

    @kh(a = "endPos", b = 5)
    private long e;

    public bj() {
        this.a = "";
        this.b = 0L;
        this.c = 0;
        this.d = 0L;
        this.e = 0L;
    }

    public bj(String str, long j, int i, long j2, long j3) {
        this.a = "";
        this.b = 0L;
        this.c = 0;
        this.d = 0L;
        this.e = 0L;
        this.a = str;
        this.b = j;
        this.c = i;
        this.d = j2;
        this.e = j3;
    }

    public static String a(String str) {
        return "mAdcode='" + str + "'";
    }
}
