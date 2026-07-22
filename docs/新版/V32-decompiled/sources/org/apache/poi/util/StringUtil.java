package org.apache.poi.util;

import java.io.UnsupportedEncodingException;
import java.text.FieldPosition;
import java.text.NumberFormat;

/* JADX INFO: loaded from: classes3.dex */
public class StringUtil {
    private static final String ENCODING = "ISO-8859-1";

    public static String getPreferredEncoding() {
        return "ISO-8859-1";
    }

    private StringUtil() {
    }

    public static String getFromUnicodeHigh(byte[] bArr, int i, int i2) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (i < 0 || i >= bArr.length) {
            throw new ArrayIndexOutOfBoundsException("Illegal offset");
        }
        if (i2 < 0 || (bArr.length - i) / 2 < i2) {
            throw new IllegalArgumentException("Illegal length");
        }
        char[] cArr = new char[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = i3 * 2;
            cArr[i3] = (char) ((bArr[(i4 + 1) + i] << 8) | (bArr[i + i4] & 255));
        }
        return new String(cArr);
    }

    public static String getFromUnicodeHigh(byte[] bArr) {
        return getFromUnicodeHigh(bArr, 0, bArr.length / 2);
    }

    public static String getFromUnicode(byte[] bArr, int i, int i2) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (i < 0 || i >= bArr.length) {
            throw new ArrayIndexOutOfBoundsException("Illegal offset");
        }
        if (i2 < 0 || (bArr.length - i) / 2 < i2) {
            throw new IllegalArgumentException("Illegal length");
        }
        char[] cArr = new char[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = i3 * 2;
            cArr[i3] = (char) ((bArr[i + i4] << 8) + bArr[i4 + 1 + i]);
        }
        return new String(cArr);
    }

    public static String getFromUnicode(byte[] bArr) {
        return getFromUnicode(bArr, 0, bArr.length / 2);
    }

    public static String getFromCompressedUnicode(byte[] bArr, int i, int i2) {
        try {
            return new String(bArr, i, i2, "ISO-8859-1");
        } catch (UnsupportedEncodingException unused) {
            throw new InternalError();
        }
    }

    public static void putCompressedUnicode(String str, byte[] bArr, int i) {
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            bArr[i + i2] = (byte) str.charAt(i2);
        }
    }

    public static void putUncompressedUnicode(String str, byte[] bArr, int i) {
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            int i3 = (i2 * 2) + i;
            bArr[i3] = (byte) cCharAt;
            bArr[i3 + 1] = (byte) (cCharAt >> '\b');
        }
    }

    public static void putUncompressedUnicodeHigh(String str, byte[] bArr, int i) {
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            int i3 = (i2 * 2) + i;
            bArr[i3] = (byte) (cCharAt >> '\b');
            bArr[i3] = (byte) cCharAt;
        }
    }

    public static String format(String str, Object[] objArr) {
        int i;
        int i2;
        StringBuffer stringBuffer = new StringBuffer();
        int iMatchOptionalFormatting = 0;
        int i3 = 0;
        while (iMatchOptionalFormatting < str.length()) {
            if (str.charAt(iMatchOptionalFormatting) == '%') {
                if (i3 >= objArr.length) {
                    stringBuffer.append("?missing data?");
                } else if ((objArr[i3] instanceof Number) && (i2 = iMatchOptionalFormatting + 1) < str.length()) {
                    iMatchOptionalFormatting += matchOptionalFormatting((Number) objArr[i3], str.substring(i2), stringBuffer);
                    i3++;
                } else {
                    stringBuffer.append(objArr[i3].toString());
                    i3++;
                }
            } else if (str.charAt(iMatchOptionalFormatting) == '\\' && (i = iMatchOptionalFormatting + 1) < str.length() && str.charAt(i) == '%') {
                stringBuffer.append('%');
                iMatchOptionalFormatting = i;
            } else {
                stringBuffer.append(str.charAt(iMatchOptionalFormatting));
            }
            iMatchOptionalFormatting++;
        }
        return stringBuffer.toString();
    }

    private static int matchOptionalFormatting(Number number, String str, StringBuffer stringBuffer) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        if (str.length() > 0 && Character.isDigit(str.charAt(0))) {
            numberFormat.setMinimumIntegerDigits(Integer.parseInt(new StringBuffer().append(str.charAt(0)).append("").toString()));
            if (2 < str.length() && str.charAt(1) == '.' && Character.isDigit(str.charAt(2))) {
                numberFormat.setMaximumFractionDigits(Integer.parseInt(new StringBuffer().append(str.charAt(2)).append("").toString()));
                numberFormat.format(number, stringBuffer, new FieldPosition(0));
                return 3;
            }
            numberFormat.format(number, stringBuffer, new FieldPosition(0));
            return 1;
        }
        if (str.length() > 0 && str.charAt(0) == '.' && 1 < str.length() && Character.isDigit(str.charAt(1))) {
            numberFormat.setMaximumFractionDigits(Integer.parseInt(new StringBuffer().append(str.charAt(1)).append("").toString()));
            numberFormat.format(number, stringBuffer, new FieldPosition(0));
            return 2;
        }
        numberFormat.format(number, stringBuffer, new FieldPosition(0));
        return 1;
    }
}
