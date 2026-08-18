package com.amap.api.services.core;

import android.content.Context;
import com.amap.api.col.p0003sl.fp;
import java.security.MessageDigest;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class SearchUtils {
    public static String getVersion() {
        return "9.7.1";
    }

    public static String getSHA1(Context context) {
        try {
            byte[] bArrDigest = MessageDigest.getInstance("SHA1").digest(context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures[0].toByteArray());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bArrDigest) {
                String upperCase = Integer.toHexString(b & 255).toUpperCase(Locale.US);
                if (upperCase.length() == 1) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(upperCase);
                stringBuffer.append(":");
            }
            return stringBuffer.toString();
        } catch (Throwable th) {
            fp.a(th, "SearchUtils", "getSHA1");
            return null;
        }
    }

    public static String getPkgName(Context context) {
        try {
            return context.getApplicationContext().getPackageName();
        } catch (Throwable th) {
            fp.a(th, "SearchUtils", "getPkgName");
            return null;
        }
    }
}
