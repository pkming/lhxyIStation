package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: AESMD5Util.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ie {
    private static int a = 6;

    private static byte[] a() {
        try {
            return a("16,99,86,77,511,98,86,97,511,99,86,77,511,18,48,97,511,99,86,77,511,58,601,77,511,58,48,77,511,58,86,87,511,18,48,97,511,58,86,87,511,18,48,97,511,98,48,87,511,98,48,97,511,99,86,77,511,58,221,77,511,98,601,87");
        } catch (Throwable th) {
            jw.c(th, "AMU", "grk");
            return null;
        }
    }

    private static byte[] b() {
        try {
            return a("16,18,86,97,511,18,48,97,511,18,86,97,511,58,86,77,511,18,86,97,511,58,48,77,511,18,86,97,511,58,601,77,511,18,86,97,511,58,221,77,511,18,86,97,511,58,86,87,511,18,86,97,511,58,48,87,511,18,86,97,511,58,601,87");
        } catch (Throwable th) {
            jw.c(th, "AMU", "giv");
            return null;
        }
    }

    private static byte[] a(String str) {
        try {
            String[] strArrSplit = new StringBuffer(str).reverse().toString().split(",");
            int length = strArrSplit.length;
            byte[] bArr = new byte[length];
            for (int i = 0; i < length; i++) {
                bArr[i] = Byte.parseByte(strArrSplit[i]);
            }
            String[] strArrSplit2 = new StringBuffer(new String(jk.a(new String(bArr)), "UTF-8")).reverse().toString().split(",");
            byte[] bArr2 = new byte[strArrSplit2.length];
            for (int i2 = 0; i2 < strArrSplit2.length; i2++) {
                bArr2[i2] = Byte.parseByte(strArrSplit2[i2]);
            }
            return bArr2;
        } catch (Throwable th) {
            jw.c(th, "AMU", "rcs");
            return null;
        }
    }

    public static byte[] a(byte[] bArr) {
        try {
            return il.b(a(), bArr, b());
        } catch (Throwable unused) {
            return new byte[0];
        }
    }

    public static byte[] b(byte[] bArr) {
        try {
            return il.a(a(), bArr, b());
        } catch (Exception unused) {
            return new byte[0];
        }
    }
}
