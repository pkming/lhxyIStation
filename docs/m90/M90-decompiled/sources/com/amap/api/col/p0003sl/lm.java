package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.col.p0003sl.kr;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/* JADX INFO: compiled from: Utils.java */
/* JADX INFO: loaded from: classes2.dex */
public final class lm {
    public static void a(Context context, lf lfVar, String str, int i, int i2, String str2) {
        lfVar.a = ju.c(context, str);
        lfVar.d = i;
        lfVar.b = i2;
        lfVar.c = str2;
    }

    public static lf a(WeakReference<lf> weakReference) {
        if (weakReference == null || weakReference.get() == null) {
            weakReference = new WeakReference<>(new lf());
        }
        return weakReference.get();
    }

    static byte[] a(kr krVar, String str) {
        kr.b bVarA;
        byte[] bArr = new byte[0];
        InputStream inputStream = null;
        try {
            bVarA = krVar.a(str);
            if (bVarA == null) {
                if (bVarA != null) {
                    try {
                        bVarA.close();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
                return bArr;
            }
            try {
                InputStream inputStreamA = bVarA.a();
                if (inputStreamA == null) {
                    if (inputStreamA != null) {
                        try {
                            inputStreamA.close();
                        } catch (Throwable th2) {
                            th2.printStackTrace();
                        }
                    }
                    if (bVarA != null) {
                        try {
                            bVarA.close();
                        } catch (Throwable th3) {
                            th3.printStackTrace();
                        }
                    }
                    return bArr;
                }
                bArr = new byte[inputStreamA.available()];
                inputStreamA.read(bArr);
                if (inputStreamA != null) {
                    try {
                        inputStreamA.close();
                    } catch (Throwable th4) {
                        th4.printStackTrace();
                    }
                }
                if (bVarA != null) {
                    try {
                        bVarA.close();
                    } catch (Throwable th5) {
                        th5.printStackTrace();
                    }
                }
                return bArr;
            } catch (Throwable th6) {
                th = th6;
                try {
                    jw.c(th, "sui", "rdS");
                    if (0 != 0) {
                        try {
                            inputStream.close();
                        } catch (Throwable th7) {
                            th7.printStackTrace();
                        }
                    }
                    if (bVarA != null) {
                        try {
                            bVarA.close();
                        } catch (Throwable th8) {
                            th8.printStackTrace();
                        }
                    }
                    return bArr;
                } finally {
                }
            }
        } catch (Throwable th9) {
            th = th9;
            bVarA = null;
        }
    }

    public static String a() {
        return it.a(System.currentTimeMillis());
    }

    public static String a(Context context, is isVar) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("\"sim\":\"").append(ik.e()).append("\",\"sdkversion\":\"").append(isVar.c()).append("\",\"product\":\"").append(isVar.a()).append("\",\"ed\":\"").append(isVar.d()).append("\",\"nt\":\"").append(ik.c(context)).append("\",\"np\":\"").append(ik.b(context)).append("\",\"mnc\":\"").append(ik.d()).append("\",\"ant\":\"").append(ik.d(context)).append("\"");
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return sb.toString();
    }

    public static String a(String str, String str2, int i, String str3, String str4) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str).append(",\"timestamp\":\"");
        stringBuffer.append(str2);
        stringBuffer.append("\",\"et\":\"");
        stringBuffer.append(i);
        stringBuffer.append("\",\"classname\":\"");
        stringBuffer.append(str3);
        stringBuffer.append("\",");
        stringBuffer.append("\"detail\":\"");
        stringBuffer.append(str4);
        stringBuffer.append("\"");
        return stringBuffer.toString();
    }

    public static String a(String str, String str2, String str3, String str4) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str).append(",\"timestamp\":\"");
        stringBuffer.append(str2);
        stringBuffer.append("\",\"et\":\"");
        stringBuffer.append(1);
        stringBuffer.append("\",\"classname\":\"");
        stringBuffer.append(str3);
        stringBuffer.append("\",");
        stringBuffer.append("\"detail\":\"");
        stringBuffer.append(str4);
        stringBuffer.append("\"");
        return stringBuffer.toString();
    }
}
