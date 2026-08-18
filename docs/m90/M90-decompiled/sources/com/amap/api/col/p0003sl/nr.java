package com.amap.api.col.p0003sl;

import android.text.format.DateFormat;

/* JADX INFO: compiled from: AmapWifi.java */
/* JADX INFO: loaded from: classes2.dex */
public final class nr {
    public long a;
    public String b;
    public int d;
    public long e;
    public short g;
    public boolean h;
    public int c = -113;
    public long f = 0;

    public nr(boolean z) {
        this.h = z;
    }

    public final String a() {
        return this.h + "#" + this.a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public nr clone() {
        nr nrVar = new nr(this.h);
        nrVar.a = this.a;
        nrVar.b = this.b;
        nrVar.c = this.c;
        nrVar.d = this.d;
        nrVar.e = this.e;
        nrVar.f = this.f;
        nrVar.g = this.g;
        nrVar.h = this.h;
        return nrVar;
    }

    public final String toString() {
        return "AmapWifi{mac=" + this.a + ", ssid='" + this.b + DateFormat.QUOTE + ", rssi=" + this.c + ", frequency=" + this.d + ", timestamp=" + this.e + ", lastUpdateUtcMills=" + this.f + ", freshness=" + ((int) this.g) + ", connected=" + this.h + '}';
    }

    public static String a(long j) {
        if (j < 0 || j > 281474976710655L) {
            return null;
        }
        return nz.a(nz.a(j), ":");
    }

    public static long a(String str) {
        long j;
        if (str == null || str.length() == 0) {
            return 0L;
        }
        int i = 0;
        long j2 = 0;
        for (int length = str.length() - 1; length >= 0; length--) {
            long jCharAt = str.charAt(length);
            if (jCharAt < 48 || jCharAt > 57) {
                long j3 = 97;
                if (jCharAt < 97 || jCharAt > 102) {
                    j3 = 65;
                    if (jCharAt < 65 || jCharAt > 70) {
                        if (jCharAt != 58 && jCharAt != 124) {
                            return 0L;
                        }
                    }
                }
                j = (jCharAt - j3) + 10;
            } else {
                j = jCharAt - 48;
            }
            j2 += j << i;
            i += 4;
        }
        if (i != 48) {
            return 0L;
        }
        return j2;
    }
}
