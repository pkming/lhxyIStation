package com.tencent.bugly.proguard;

import android.text.format.DateFormat;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;

/* JADX INFO: compiled from: BUGLY */
/* JADX INFO: loaded from: classes2.dex */
public final class f {
    private static final char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.CAPITAL_AM_PM, 'B', 'C', 'D', DateFormat.DAY, 'F'};
    public static final byte[] a = new byte[0];

    public static String a(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        char[] cArr = new char[bArr.length * 2];
        for (int i = 0; i < bArr.length; i++) {
            byte b2 = bArr[i];
            int i2 = i * 2;
            char[] cArr2 = b;
            cArr[i2 + 1] = cArr2[b2 & HSSFErrorConstants.ERROR_VALUE];
            cArr[i2 + 0] = cArr2[((byte) (b2 >>> 4)) & HSSFErrorConstants.ERROR_VALUE];
        }
        return new String(cArr);
    }
}
