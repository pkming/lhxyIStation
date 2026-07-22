package com.lianhexinye.m90.common.utils;

import android.text.Spanned;
import android.text.format.DateFormat;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.tools.tar.TarConstants;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class JavaUtils {
    private static String format = "yyyy-MM-dd HH:mm:ss";

    public static byte[] getBytes(int i) {
        return new byte[]{(byte) (i & 255), (byte) ((65280 & i) >> 8), (byte) ((16711680 & i) >> 16), (byte) ((i & (-16777216)) >> 24)};
    }

    public static byte[] intToBytesBig(int i) {
        return new byte[]{(byte) ((i >> 24) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)};
    }

    public static int leftDisplacement(int i) {
        if (i > 0) {
            return 1 << i;
        }
        return 1;
    }

    public static byte[] longToBytesBig(long j) {
        return new byte[]{(byte) ((j >> 56) & 255), (byte) ((j >> 48) & 255), (byte) ((j >> 40) & 255), (byte) ((j >> 32) & 255), (byte) ((j >> 24) & 255), (byte) ((j >> 16) & 255), (byte) ((j >> 8) & 255), (byte) (j & 255)};
    }

    public static byte[] longToBytes_Little(long j) {
        return new byte[]{(byte) (j & 255), (byte) ((j >> 8) & 255), (byte) ((j >> 16) & 255), (byte) ((j >> 24) & 255), (byte) ((j >> 32) & 255), (byte) ((j >> 40) & 255), (byte) ((j >> 48) & 255), (byte) ((j >> 56) & 255)};
    }

    public static String dateToString(Date date, String... strArr) {
        SimpleDateFormat simpleDateFormat;
        if (strArr.length > 0) {
            simpleDateFormat = new SimpleDateFormat(strArr[0], Locale.US);
        } else {
            simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        }
        return simpleDateFormat.format(date);
    }

    public static int getMonth(String str) {
        try {
            Date date = new SimpleDateFormat(format, Locale.US).parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(2) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return " this object is null ! ";
        }
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("{");
            for (int i = 0; i < declaredFields.length && declaredFields.length > 0; i++) {
                declaredFields[i].setAccessible(true);
                if (i == declaredFields.length - 1) {
                    stringBuffer.append(declaredFields[i].getName() + ":" + declaredFields[i].get(obj));
                } else {
                    stringBuffer.append(declaredFields[i].getName() + ":" + declaredFields[i].get(obj) + ",");
                }
            }
            stringBuffer.append("}" + obj.getClass().getName());
            return stringBuffer.toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return null;
        } catch (SecurityException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    public static String toNumberFormat(double d, int i) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(i);
        decimalFormat.setGroupingSize(0);
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        return decimalFormat.format(d).replaceAll(",", "");
    }

    public static boolean isEmpty(Object obj) {
        return obj == null || "".equals(obj.toString().trim()) || obj.toString().trim().length() == 0;
    }

    public static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static String bytesToHexString(byte[] bArr, int i) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (int i2 = 0; i2 < i; i2++) {
            String hexString = Integer.toHexString(bArr[i2] & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String str) {
        int length = str.length();
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    public static byte[] hexStringToBytes(String str) {
        String upperCase = str.replaceAll(" ", "").toUpperCase();
        if (upperCase.length() <= 0) {
            return null;
        }
        int length = upperCase.length() / 2;
        char[] charArray = upperCase.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (charToByte(charArray[i2 + 1]) | (charToByte(charArray[i2]) << 4));
        }
        return bArr;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String stringToHexadecimal(String str) {
        String upperCase = null;
        for (int i = 0; i < str.length(); i++) {
            if (i == 0) {
                upperCase = Integer.toHexString(str.charAt(i)).toUpperCase();
            } else {
                upperCase = upperCase + Integer.toHexString(str.charAt(i)).toUpperCase();
            }
        }
        return upperCase;
    }

    public static boolean compare(String str, String str2) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.parse(str).before(simpleDateFormat.parse(str2));
    }

    public static long compareDifferSize(String str, String str2) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        long time = simpleDateFormat.parse(str).getTime() - simpleDateFormat.parse(str2).getTime();
        long j = time / 86400000;
        long j2 = 24 * j;
        long j3 = (time / 3600000) - j2;
        long j4 = j2 * 60;
        long j5 = j3 * 60;
        long j6 = ((time / 60000) - j4) - j5;
        System.out.println("" + j + "天" + j3 + "小时" + j6 + "分" + ((((time / 1000) - (j4 * 60)) - (j5 * 60)) - (60 * j6)) + "秒");
        return j;
    }

    public static String compareDifferTimeSize(String str, String str2) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        long time = simpleDateFormat.parse(str).getTime() - simpleDateFormat.parse(str2).getTime();
        long j = time / 86400000;
        long j2 = 24 * j;
        long j3 = (time / 3600000) - j2;
        long j4 = j2 * 60;
        long j5 = j3 * 60;
        long j6 = ((time / 60000) - j4) - j5;
        return j + "," + j3 + "," + j6 + "," + ((((time / 1000) - (j4 * 60)) - (j5 * 60)) - (60 * j6));
    }

    public static String formatDouble(double d) {
        return new DecimalFormat("#0.0").format(d);
    }

    public static byte sumCheck(byte[] bArr, int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            i2 += bArr[i3];
        }
        return (byte) (i2 & 255);
    }

    public static int sumCheckINT(byte[] bArr, int i, int i2) {
        int i3 = 0;
        if (bArr == null) {
            return 0;
        }
        while (i < i2) {
            i3 += bArr[i] & 255;
            i++;
        }
        return i3;
    }

    public static int HexToInt(String str) {
        if (!IsHex(str)) {
            return 0;
        }
        String upperCase = str.toUpperCase();
        if (upperCase.length() > 2 && upperCase.charAt(0) == '0' && upperCase.charAt(1) == 'X') {
            upperCase = upperCase.substring(2);
        }
        int length = upperCase.length();
        int iGetHex = 0;
        for (int i = 0; i < length; i++) {
            try {
                iGetHex += GetHex(upperCase.charAt((length - i) - 1)) * GetPower(16, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return iGetHex;
    }

    private static int GetHex(char c) throws Exception {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        char c2 = DateFormat.AM_PM;
        if (c < 'a' || c > 'f') {
            c2 = DateFormat.CAPITAL_AM_PM;
            if (c < 'A' || c > 'F') {
                throw new Exception("error param");
            }
        }
        return (c - c2) + 10;
    }

    private static int GetPower(int i, int i2) throws Exception {
        if (i2 < 0) {
            throw new Exception("nCount can't small than 1!");
        }
        int i3 = 1;
        if (i2 == 0) {
            return 1;
        }
        for (int i4 = 0; i4 < i2; i4++) {
            i3 *= i;
        }
        return i3;
    }

    private static boolean IsHex(String str) {
        int i = 2;
        if (str.length() <= 2 || str.charAt(0) != '0' || (str.charAt(1) != 'X' && str.charAt(1) != 'x')) {
            i = 0;
        }
        while (i < str.length()) {
            char cCharAt = str.charAt(i);
            if ((cCharAt < '0' || cCharAt > '9') && ((cCharAt < 'A' || cCharAt > 'F') && (cCharAt < 'a' || cCharAt > 'f'))) {
                return false;
            }
            i++;
        }
        return true;
    }

    public static String stringToGBK(String str) throws UnsupportedEncodingException {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            int i3 = i2 + 1;
            bArr[i] = (byte) (Byte.parseByte(str.substring(i3, i2 + 2), 16) | (Byte.parseByte(str.substring(i2, i3), 16) << 4));
        }
        return new String(bArr, "gb2312");
    }

    public static byte[] doubleToBytes_Little(double d) {
        long jDoubleToLongBits = Double.doubleToLongBits(d);
        return new byte[]{(byte) (jDoubleToLongBits & 255), (byte) ((jDoubleToLongBits >> 8) & 255), (byte) ((jDoubleToLongBits >> 16) & 255), (byte) ((jDoubleToLongBits >> 24) & 255), (byte) ((jDoubleToLongBits >> 32) & 255), (byte) ((jDoubleToLongBits >> 40) & 255), (byte) ((jDoubleToLongBits >> 48) & 255), (byte) ((jDoubleToLongBits >> 56) & 255)};
    }

    public static int bytesToInt(byte[] bArr, int i) {
        return (bArr[i + 3] & 255) | ((bArr[i] & 255) << 24) | ((bArr[i + 1] & 255) << 16) | ((bArr[i + 2] & 255) << 8);
    }

    public static short byteToShort_HL(byte[] bArr, int i) {
        return (short) ((bArr[i] & 255) | ((bArr[i + 1] << 8) & 65280));
    }

    public static int byteToInt_HL(byte[] bArr, int i) {
        return (bArr[i + 0] & 255) | (((bArr[i + 3] & 255) << 24) & (-16777216)) | (((bArr[i + 2] & 255) << 16) & Spanned.SPAN_PRIORITY) | (((bArr[i + 1] & 255) << 8) & 65280);
    }

    public static long bytesToLongBig(byte[] bArr) {
        return ((((long) bArr[0]) & 255) << 56) | ((((long) bArr[1]) & 255) << 48) | ((((long) bArr[2]) & 255) << 40) | ((((long) bArr[3]) & 255) << 32) | ((((long) bArr[4]) & 255) << 24) | ((((long) bArr[5]) & 255) << 16) | ((((long) bArr[6]) & 255) << 8) | ((255 & ((long) bArr[7])) << 0);
    }

    public static byte[] hexToBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bArr = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) Integer.parseInt(str.substring(i2, i2 + 2), 16);
        }
        return bArr;
    }

    public static void setSystemDate(String str) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("sh").getOutputStream());
            dataOutputStream.writeBytes("date -s " + str + "; \n");
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int crcXModem(byte[] bArr, int i, int i2) {
        int i3 = 0;
        while (i <= i2) {
            byte b = bArr[i];
            for (int i4 = 0; i4 < 8; i4++) {
                boolean z = ((b >> (7 - i4)) & 1) == 1;
                boolean z2 = ((i3 >> 15) & 1) == 1;
                i3 <<= 1;
                if (z ^ z2) {
                    i3 ^= 4129;
                }
            }
            i++;
        }
        return 65535 & i3;
    }

    public static byte[] stringBCD(String str) {
        byte[] bytes = str.getBytes();
        int length = bytes.length / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) ((bytes[i2 + 1] & HSSFErrorConstants.ERROR_VALUE) | (bytes[i2] << 4));
        }
        return bArr;
    }

    public static String bcdString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            sb.append((bArr[i] >> 4) & 15).append(bArr[i] & HSSFErrorConstants.ERROR_VALUE);
        }
        return sb.toString();
    }

    public static byte[] str2Cbcd(String str) {
        if (str.length() % 2 != 0) {
            str = "0" + str;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i += 2) {
            byteArrayOutputStream.write(((charArray[i] - '0') << 4) | (charArray[i + 1] - '0'));
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String cbcd2String(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bArr.length; i++) {
            stringBuffer.append((char) (((bArr[i] & 255) >> 4) + 48));
            stringBuffer.append((char) ((bArr[i] & HSSFErrorConstants.ERROR_VALUE) + 48));
        }
        return stringBuffer.toString();
    }

    public static String bcd2Str(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (int i = 0; i < bArr.length; i++) {
            stringBuffer.append((int) ((byte) ((bArr[i] & 240) >>> 4)));
            stringBuffer.append((int) ((byte) (bArr[i] & HSSFErrorConstants.ERROR_VALUE)));
        }
        return stringBuffer.toString().substring(0, 1).equalsIgnoreCase("0") ? stringBuffer.toString().substring(1) : stringBuffer.toString();
    }

    public static byte[] str2Bcd(String str) {
        int i;
        int i2;
        int length = str.length();
        if (length % 2 != 0) {
            str = "0" + str;
            length = str.length();
        }
        byte[] bArr = new byte[length];
        if (length >= 2) {
            length /= 2;
        }
        byte[] bArr2 = new byte[length];
        byte[] bytes = str.getBytes();
        for (int i3 = 0; i3 < str.length() / 2; i3++) {
            int i4 = i3 * 2;
            if (bytes[i4] >= 48 && bytes[i4] <= 57) {
                i = bytes[i4] - TarConstants.LF_NORMAL;
            } else {
                i = ((bytes[i4] >= 97 && bytes[i4] <= 122) ? bytes[i4] - 97 : bytes[i4] - 65) + 10;
            }
            int i5 = i4 + 1;
            if (bytes[i5] >= 48 && bytes[i5] <= 57) {
                i2 = bytes[i5] - TarConstants.LF_NORMAL;
            } else {
                i2 = ((bytes[i5] >= 97 && bytes[i5] <= 122) ? bytes[i5] - 97 : bytes[i5] - 65) + 10;
            }
            bArr2[i3] = (byte) ((i << 4) + i2);
        }
        return bArr2;
    }

    public static String getValueByName(JSONObject jSONObject, String str) throws JSONException {
        return jSONObject.has(str) ? jSONObject.getString(str) : "";
    }

    public static boolean isDecimal(String str) {
        return Pattern.compile("[0-9]*\\.?[0-9]+").matcher(str).matches();
    }
}
