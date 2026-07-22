package com.amap.api.col.p0003sl;

import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: compiled from: MD5.java */
/* JADX INFO: loaded from: classes2.dex */
public final class io {
    public static String a(String str) {
        FileInputStream fileInputStream;
        try {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            File file = new File(str);
            if (file.isFile() && file.exists()) {
                byte[] bArr = new byte[2048];
                MessageDigest messageDigest = MessageDigest.getInstance(it.c("ETUQ1"));
                fileInputStream = new FileInputStream(file);
                while (true) {
                    try {
                        int i = fileInputStream.read(bArr);
                        if (i == -1) {
                            break;
                        }
                        messageDigest.update(bArr, 0, i);
                    } catch (Throwable th) {
                        th = th;
                    }
                }
                String strE = it.e(messageDigest.digest());
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    jt.a(e, "MD5", "gfm");
                }
                return strE;
            }
            return null;
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
        }
        try {
            jt.a(th, "MD5", "gfm");
            return null;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e2) {
                    jt.a(e2, "MD5", "gfm");
                }
            }
        }
    }

    public static String b(String str) {
        if (str == null) {
            return null;
        }
        return it.e(d(str));
    }

    public static String c(String str) {
        return it.f(e(str));
    }

    public static byte[] a(byte[] bArr, String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(str);
            messageDigest.update(bArr);
            return messageDigest.digest();
        } catch (Throwable th) {
            jt.a(th, "MD5", "gmb");
            return null;
        }
    }

    private static byte[] d(String str) {
        try {
            return f(str);
        } catch (Throwable th) {
            jt.a(th, "MD5", "gmb");
            return new byte[0];
        }
    }

    private static byte[] e(String str) {
        try {
            return f(str);
        } catch (Throwable th) {
            th.printStackTrace();
            return new byte[0];
        }
    }

    private static byte[] f(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        MessageDigest messageDigest = MessageDigest.getInstance(it.c("ETUQ1"));
        messageDigest.update(it.a(str));
        return messageDigest.digest();
    }

    public static String a(byte[] bArr) {
        return it.e(a(bArr, it.c("ETUQ1")));
    }
}
