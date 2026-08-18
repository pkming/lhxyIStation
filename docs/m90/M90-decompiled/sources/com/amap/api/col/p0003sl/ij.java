package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/* JADX INFO: compiled from: ClientInfo.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ij {
    public static String a(Context context, String str, String str2) {
        try {
            return io.b(ig.e(context) + ":" + str.substring(0, str.length() - 3) + ":" + str2);
        } catch (Throwable th) {
            jt.a(th, "CI", "Sco");
            return null;
        }
    }

    public static String a() {
        try {
            String strValueOf = String.valueOf(System.currentTimeMillis());
            String str = ig.a() ? "1" : "0";
            int length = strValueOf.length();
            return strValueOf.substring(0, length - 2) + str + strValueOf.substring(length - 1);
        } catch (Throwable th) {
            jt.a(th, "CI", "TS");
            return null;
        }
    }

    public static String a(Context context) {
        try {
            a aVar = new a((byte) 0);
            aVar.d = ig.c(context);
            aVar.i = ig.d(context);
            return a(aVar);
        } catch (Throwable th) {
            jt.a(th, "CI", "IX");
            return null;
        }
    }

    public static byte[] a(byte[] bArr) throws BadPaddingException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeyException, CertificateException, NullPointerException {
        return il.a(bArr);
    }

    public static byte[] a(Context context, boolean z, boolean z2) {
        try {
            return b(b(context, z, z2));
        } catch (Throwable th) {
            jt.a(th, "CI", "gz");
            return null;
        }
    }

    public static String b(Context context) {
        return c(context);
    }

    private static String c(Context context) {
        try {
            return a(b(context, false, false));
        } catch (Throwable th) {
            jt.a(th, "CI", "gCXi");
            return null;
        }
    }

    private static byte[] b(byte[] bArr) throws BadPaddingException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeyException, CertificateException, NullPointerException {
        PublicKey publicKeyD = it.d();
        if (bArr.length > 117) {
            byte[] bArr2 = new byte[117];
            System.arraycopy(bArr, 0, bArr2, 0, 117);
            byte[] bArrA = il.a(bArr2, publicKeyD);
            byte[] bArr3 = new byte[(bArr.length + 128) - 117];
            System.arraycopy(bArrA, 0, bArr3, 0, 128);
            System.arraycopy(bArr, 117, bArr3, 128, bArr.length - 117);
            return bArr3;
        }
        return il.a(bArr, publicKeyD);
    }

    private static String a(a aVar) {
        return il.b(b(aVar));
    }

    private static byte[] b(a aVar) {
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                a(byteArrayOutputStream, aVar.a);
                a(byteArrayOutputStream, aVar.b);
                a(byteArrayOutputStream, aVar.c);
                a(byteArrayOutputStream, aVar.d);
                a(byteArrayOutputStream, aVar.e);
                a(byteArrayOutputStream, aVar.f);
                a(byteArrayOutputStream, aVar.g);
                a(byteArrayOutputStream, aVar.h);
                a(byteArrayOutputStream, aVar.i);
                a(byteArrayOutputStream, aVar.j);
                a(byteArrayOutputStream, aVar.k);
                a(byteArrayOutputStream, aVar.l);
                a(byteArrayOutputStream, aVar.m);
                a(byteArrayOutputStream, aVar.n);
                a(byteArrayOutputStream, aVar.o);
                a(byteArrayOutputStream, aVar.p);
                a(byteArrayOutputStream, aVar.q);
                a(byteArrayOutputStream, aVar.r);
                a(byteArrayOutputStream, aVar.s);
                a(byteArrayOutputStream, aVar.t);
                a(byteArrayOutputStream, aVar.u);
                a(byteArrayOutputStream, aVar.v);
                a(byteArrayOutputStream, aVar.w);
                a(byteArrayOutputStream, aVar.x);
                a(byteArrayOutputStream, aVar.y);
                a(byteArrayOutputStream, aVar.z);
                a(byteArrayOutputStream, aVar.A);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                new String(byteArray);
                byte[] bArrB = b(it.b(byteArray));
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                return bArrB;
            } catch (Throwable th2) {
                th = th2;
                try {
                    jt.a(th, "CI", "gzx");
                    return null;
                } finally {
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (Throwable th3) {
                            th3.printStackTrace();
                        }
                    }
                }
            }
        } catch (Throwable th4) {
            th = th4;
            byteArrayOutputStream = null;
        }
    }

    private static void a(ByteArrayOutputStream byteArrayOutputStream, String str) {
        if (!TextUtils.isEmpty(str)) {
            it.a(byteArrayOutputStream, str.getBytes().length > 255 ? (byte) -1 : (byte) str.getBytes().length, it.a(str));
        } else {
            it.a(byteArrayOutputStream, (byte) 0, new byte[0]);
        }
    }

    private static a b(Context context, boolean z, boolean z2) {
        a aVar = new a((byte) 0);
        aVar.a = ik.k();
        aVar.b = ik.h();
        String strF = ik.f(context);
        if (strF == null) {
            strF = "";
        }
        aVar.c = strF;
        aVar.d = ig.c(context);
        aVar.e = Build.MODEL;
        aVar.f = Build.MANUFACTURER;
        aVar.g = Build.DEVICE;
        aVar.h = ig.b(context);
        aVar.i = ig.d(context);
        aVar.j = String.valueOf(Build.VERSION.SDK_INT);
        aVar.k = ik.n();
        aVar.l = ik.m(context);
        aVar.m = new StringBuilder().append(ik.j(context)).toString();
        aVar.n = new StringBuilder().append(ik.i(context)).toString();
        aVar.o = ik.s(context);
        aVar.p = ik.h(context);
        aVar.q = "";
        aVar.r = "";
        if (z) {
            aVar.s = "";
            aVar.t = "";
        } else {
            String[] strArrI = ik.i();
            aVar.s = strArrI[0];
            aVar.t = strArrI[1];
        }
        aVar.w = ik.a();
        String strA = ik.a(context);
        if (!TextUtils.isEmpty(strA)) {
            aVar.x = strA;
        } else {
            aVar.x = "";
        }
        aVar.y = "aid=" + ik.g();
        if ((z2 && jh.d) || jh.e) {
            String strE = ik.e(context);
            if (!TextUtils.isEmpty(strE)) {
                aVar.y += "|oaid=" + strE;
            }
        }
        String strJ = ik.j();
        if (!TextUtils.isEmpty(strJ)) {
            aVar.y += "|multiImeis=" + strJ;
        }
        String strM = ik.m();
        if (!TextUtils.isEmpty(strM)) {
            aVar.y += "|meid=" + strM;
        }
        aVar.y += "|serial=" + ik.f();
        String strB = ik.b();
        if (!TextUtils.isEmpty(strB)) {
            aVar.y += "|adiuExtras=" + strB;
        }
        aVar.y += "|storage=" + ik.o() + "|ram=" + ik.r(context) + "|arch=" + ik.p();
        String strB2 = js.a().b();
        if (!TextUtils.isEmpty(strB2)) {
            aVar.z = strB2;
        } else {
            aVar.z = "";
        }
        if (z) {
            String strA2 = iy.a(context).a();
            if (!TextUtils.isEmpty(strA2)) {
                aVar.A = strA2;
            }
        }
        return aVar;
    }

    /* JADX INFO: compiled from: ClientInfo.java */
    private static class a {
        String A;
        String a;
        String b;
        String c;
        String d;
        String e;
        String f;
        String g;
        String h;
        String i;
        String j;
        String k;
        String l;
        String m;
        String n;
        String o;
        String p;
        String q;
        String r;
        String s;
        String t;
        String u;
        String v;
        String w;
        String x;
        String y;
        String z;

        private a() {
        }

        /* synthetic */ a(byte b) {
            this();
        }
    }
}
