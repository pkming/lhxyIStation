package com.autonavi.aps.amapapi.restruct;

import android.net.wifi.WifiInfo;
import android.text.TextUtils;

/* JADX INFO: compiled from: WifiInfoWrapper.java */
/* JADX INFO: loaded from: classes2.dex */
public final class j {
    private WifiInfo a;
    private String b;
    private String c;
    private int d = -1;

    public j(WifiInfo wifiInfo) {
        this.a = wifiInfo;
    }

    public final String a() {
        if (this.c == null) {
            this.c = h.a(this.a);
        }
        return this.c;
    }

    public final String b() {
        if (this.b == null) {
            this.b = h.b(this.a);
        }
        return this.b;
    }

    public final int c() {
        if (this.d == -1) {
            this.d = h.c(this.a);
        }
        return this.d;
    }

    public final boolean d() {
        return (this.a == null || TextUtils.isEmpty(b()) || !com.autonavi.aps.amapapi.utils.j.a(a())) ? false : true;
    }
}
