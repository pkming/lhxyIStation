package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;

/* JADX INFO: compiled from: StatisticsUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gy {
    static lk a;

    public static void a(Context context, String str, long j, boolean z) {
        try {
            String strA = a(str, j, z);
            if (strA != null && strA.length() > 0) {
                if (a == null) {
                    a = new lk(context, "sea", "9.7.1", "O002");
                }
                a.a(strA);
                ll.a(a, context);
            }
        } catch (Throwable th) {
            fp.a(th, "StatisticsUtil", "recordResponseAction");
        }
    }

    private static String a(String str, long j, boolean z) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"RequestPath\":\"").append(str).append("\"");
            sb.append(",");
            sb.append("\"ResponseTime\":").append(j);
            sb.append(",");
            sb.append("\"Success\":").append(z);
            sb.append("}");
            return sb.toString();
        } catch (Throwable th) {
            fp.a(th, "StatisticsUtil", "generateNetWorkResponseStatisticsEntity");
            return null;
        }
    }

    public static void a(String str, String str2, AMapException aMapException) {
        if (str != null) {
            String errorType = aMapException.getErrorType();
            String strA = a(aMapException);
            if (strA == null || strA.length() <= 0) {
                return;
            }
            jw.a(fo.a(true), str, errorType, str2, strA);
        }
    }

    private static String a(AMapException aMapException) {
        if (aMapException == null) {
            return null;
        }
        if (aMapException.getErrorLevel() == 0) {
            int errorCode = aMapException.getErrorCode();
            if (errorCode == 0) {
                return "4";
            }
            int iPow = (int) Math.pow(10.0d, Math.floor(Math.log10(errorCode)));
            return String.valueOf((errorCode % iPow) + (iPow * 4));
        }
        return new StringBuilder().append(aMapException.getErrorCode()).toString();
    }

    public static void a(Context context, String str, boolean z) {
        try {
            String strA = a(str, z);
            if (strA != null && strA.length() > 0) {
                lk lkVar = new lk(context, "sea", "9.7.1", "O006");
                lkVar.a(strA);
                ll.a(lkVar, context);
            }
        } catch (Throwable th) {
            fp.a(th, "StatisticsUtil", "recordResponseAction");
        }
    }

    private static String a(String str, boolean z) {
        String strSubstring;
        try {
            strSubstring = "";
            int iIndexOf = str.indexOf("?");
            int length = str.length();
            if (iIndexOf > 0) {
                String strSubstring2 = str.substring(0, iIndexOf);
                int i = iIndexOf + 1;
                strSubstring = i < length ? str.substring(i) : "";
                str = strSubstring2;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"RequestPath\":\"").append(str).append("\"");
            sb.append(",");
            sb.append("\"RequestParm\":\"").append(strSubstring).append("\"");
            sb.append(",");
            sb.append("\"IsCacheRequest\":").append(z);
            sb.append("}");
            return sb.toString();
        } catch (Throwable th) {
            fp.a(th, "StatisticsUtil", "generateNetWorkResponseStatisticsEntity");
            return null;
        }
    }
}
