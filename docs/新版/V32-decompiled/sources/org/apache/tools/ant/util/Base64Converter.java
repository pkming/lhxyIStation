package org.apache.tools.ant.util;

import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;

/* JADX INFO: loaded from: classes3.dex */
public class Base64Converter {
    private static final char[] ALPHABET;
    private static final int BYTE = 8;
    private static final int BYTE_MASK = 255;
    private static final int POS_0_MASK = 63;
    private static final int POS_1_MASK = 4032;
    private static final int POS_1_SHIFT = 6;
    private static final int POS_2_MASK = 258048;
    private static final int POS_2_SHIFT = 12;
    private static final int POS_3_MASK = 16515072;
    private static final int POS_3_SHIFT = 18;
    private static final int WORD = 16;
    public static final char[] alphabet;

    static {
        char[] cArr = {DateFormat.CAPITAL_AM_PM, 'B', 'C', 'D', DateFormat.DAY, 'F', 'G', 'H', 'I', 'J', 'K', DateFormat.STANDALONE_MONTH, DateFormat.MONTH, PhoneNumberUtils.WILD, 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', DateFormat.AM_PM, 'b', 'c', DateFormat.DATE, 'e', 'f', 'g', DateFormat.HOUR, 'i', 'j', DateFormat.HOUR_OF_DAY, 'l', DateFormat.MINUTE, 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', DateFormat.YEAR, DateFormat.TIME_ZONE, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
        ALPHABET = cArr;
        alphabet = cArr;
    }

    public String encode(String str) {
        return encode(str.getBytes());
    }

    public String encode(byte[] bArr) {
        char[] cArr = new char[(((bArr.length - 1) / 3) + 1) * 4];
        int i = 0;
        int i2 = 0;
        while (i + 3 <= bArr.length) {
            int i3 = i + 1;
            int i4 = i3 + 1;
            int i5 = ((bArr[i] & 255) << 16) | ((bArr[i3] & 255) << 8);
            int i6 = i4 + 1;
            int i7 = i5 | (bArr[i4] & 255);
            int i8 = (i7 & POS_3_MASK) >> 18;
            int i9 = i2 + 1;
            char[] cArr2 = ALPHABET;
            cArr[i2] = cArr2[i8];
            int i10 = i9 + 1;
            cArr[i9] = cArr2[(i7 & POS_2_MASK) >> 12];
            int i11 = i10 + 1;
            cArr[i10] = cArr2[(i7 & POS_1_MASK) >> 6];
            i2 = i11 + 1;
            cArr[i11] = cArr2[i7 & 63];
            i = i6;
        }
        if (bArr.length - i == 2) {
            int i12 = ((bArr[i + 1] & 255) << 8) | ((bArr[i] & 255) << 16);
            int i13 = (i12 & POS_3_MASK) >> 18;
            int i14 = i2 + 1;
            char[] cArr3 = ALPHABET;
            cArr[i2] = cArr3[i13];
            int i15 = i14 + 1;
            cArr[i14] = cArr3[(i12 & POS_2_MASK) >> 12];
            cArr[i15] = cArr3[(i12 & POS_1_MASK) >> 6];
            cArr[i15 + 1] = '=';
        } else if (bArr.length - i == 1) {
            int i16 = (bArr[i] & 255) << 16;
            int i17 = (i16 & POS_3_MASK) >> 18;
            int i18 = i2 + 1;
            char[] cArr4 = ALPHABET;
            cArr[i2] = cArr4[i17];
            int i19 = i18 + 1;
            cArr[i18] = cArr4[(i16 & POS_2_MASK) >> 12];
            cArr[i19] = '=';
            cArr[i19 + 1] = '=';
        }
        return new String(cArr);
    }
}
