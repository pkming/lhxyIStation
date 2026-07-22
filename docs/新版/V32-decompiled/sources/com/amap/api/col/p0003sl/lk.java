package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;

/* JADX INFO: compiled from: StatisticsEntity.java */
/* JADX INFO: loaded from: classes2.dex */
public final class lk {
    private Context a;
    private String b;
    private String c;
    private String d;
    private String e;

    private static byte[] a(int i) {
        return new byte[]{(byte) ((i >> 24) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)};
    }

    public lk(Context context, String str, String str2, String str3) throws Cif {
        if (TextUtils.isEmpty(str3) || str3.length() > 256) {
            throw new Cif("无效的参数 - IllegalArgumentException");
        }
        this.a = context.getApplicationContext();
        this.c = str;
        this.d = str2;
        this.b = str3;
    }

    public final void a(String str) throws Cif {
        if (TextUtils.isEmpty(str) || str.length() > 65536) {
            throw new Cif("无效的参数 - IllegalArgumentException");
        }
        this.e = str;
    }

    private byte[] b(String str) {
        byte[] bArrA;
        if (!TextUtils.isEmpty(str) && (bArrA = it.a(this.e)) != null) {
            return it.a(bArrA.length);
        }
        return new byte[]{0, 0};
    }

    public final byte[] a() {
        int iCurrentTimeMillis = 0;
        byte[] byteArray = new byte[0];
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            try {
                ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                try {
                    it.a(byteArrayOutputStream2, this.c);
                    it.a(byteArrayOutputStream2, this.d);
                    it.a(byteArrayOutputStream2, this.b);
                    it.a(byteArrayOutputStream2, String.valueOf(ik.j(this.a)));
                    try {
                        iCurrentTimeMillis = (int) (System.currentTimeMillis() / 1000);
                    } catch (Throwable unused) {
                    }
                    byteArrayOutputStream2.write(a(iCurrentTimeMillis));
                    byteArrayOutputStream2.write(b(this.e));
                    byteArrayOutputStream2.write(it.a(this.e));
                    byteArray = byteArrayOutputStream2.toByteArray();
                    byteArrayOutputStream2.close();
                } catch (Throwable th) {
                    th = th;
                    byteArrayOutputStream = byteArrayOutputStream2;
                    try {
                        jw.c(th, "se", "tds");
                        if (byteArrayOutputStream != null) {
                            byteArrayOutputStream.close();
                        }
                        return byteArray;
                    } catch (Throwable th2) {
                        if (byteArrayOutputStream != null) {
                            try {
                                byteArrayOutputStream.close();
                            } catch (Throwable th3) {
                                th3.printStackTrace();
                            }
                        }
                        throw th2;
                    }
                }
            } catch (Throwable th4) {
                th = th4;
            }
        } catch (Throwable th5) {
            th5.printStackTrace();
        }
        return byteArray;
    }
}
