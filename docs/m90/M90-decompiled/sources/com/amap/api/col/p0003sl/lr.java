package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import java.io.ByteArrayOutputStream;

/* JADX INFO: compiled from: StatisticsHeaderDataStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class lr extends lt {
    public static int a = 13;
    public static int b = 6;
    private Context e;

    public lr(Context context, lt ltVar) {
        super(ltVar);
        this.e = context;
    }

    @Override // com.amap.api.col.p0003sl.lt
    protected final byte[] a(byte[] bArr) {
        byte[] bArrA = a(this.e);
        byte[] bArr2 = new byte[bArrA.length + bArr.length];
        System.arraycopy(bArrA, 0, bArr2, 0, bArrA.length);
        System.arraycopy(bArr, 0, bArr2, bArrA.length, bArr.length);
        return bArr2;
    }

    private static byte[] a(Context context) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] byteArray = new byte[0];
        try {
            try {
                it.a(byteArrayOutputStream, "1.2." + a + "." + b);
                it.a(byteArrayOutputStream, "Android");
                it.a(byteArrayOutputStream, ik.k());
                it.a(byteArrayOutputStream, ik.h());
                it.a(byteArrayOutputStream, ik.f(context));
                it.a(byteArrayOutputStream, Build.MANUFACTURER);
                it.a(byteArrayOutputStream, Build.MODEL);
                it.a(byteArrayOutputStream, Build.DEVICE);
                it.a(byteArrayOutputStream, ik.n());
                it.a(byteArrayOutputStream, ig.c(context));
                it.a(byteArrayOutputStream, ig.d(context));
                it.a(byteArrayOutputStream, ig.f(context));
                byteArrayOutputStream.write(new byte[]{0});
                byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
            } catch (Throwable th) {
                try {
                    jw.c(th, "sm", "gh");
                    byteArrayOutputStream.close();
                } catch (Throwable th2) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (Throwable th3) {
                        th3.printStackTrace();
                    }
                    throw th2;
                }
            }
        } catch (Throwable th4) {
            th4.printStackTrace();
        }
        return byteArray;
    }
}
