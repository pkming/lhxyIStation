package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: DTFileInfo.java */
/* JADX INFO: loaded from: classes2.dex */
@kg(a = "update_item_file")
class bk {

    @kh(a = "mAdcode", b = 6)
    private String a;

    @kh(a = "file", b = 6)
    private String b;

    public bk() {
        this.a = "";
        this.b = "";
    }

    public bk(String str, String str2) {
        this.a = "";
        this.b = "";
        this.a = str;
        this.b = str2;
    }

    public final String a() {
        return this.b;
    }

    public static String a(String str) {
        return "mAdcode='" + str + "'";
    }
}
