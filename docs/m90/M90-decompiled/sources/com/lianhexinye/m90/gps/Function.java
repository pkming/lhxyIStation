package com.lianhexinye.m90.gps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.Contacts;
import android.provider.Settings;
import android.provider.UserDictionary;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TimedRemoteCaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class Function {
    private static final int DAY = 86400;
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm";
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMAT_MONTH_DAY = "MM月dd日";
    public static final String FORMAT_MONTH_DAY_TIME = "MM月dd日  hh:mm";
    public static final String FORMAT_TIME = "HH:mm";
    public static final String FORMAT_YEAR = "yyyy";
    private static final int HOUR = 3600;
    private static final int MINUTE = 60;
    private static final int MONTH = 2592000;
    private static final int YEAR = 31536000;
    static SharedPreferences.Editor ed = null;
    static SharedPreferences sp = null;
    public static String strFlashPath = "/mnt/internalsd";
    public static String strSDPath = "/mnt/extsd";
    public static String strSDPath1 = "/mnt/extsd1";

    public static int BCDToInt(int i) {
        return (((i >> 4) & 15) * 10) + (i & 15);
    }

    public static int BIT_CLEAR(int i, int i2) {
        return i | (1 << i2);
    }

    public static int BIT_GET(int i, int i2) {
        return (i >> i2) & 1;
    }

    public static int BIT_SET(int i, int i2) {
        return i & (~(1 << i2));
    }

    public static int GetBit(int i, int i2) {
        return (i >> i2) & 1;
    }

    public static int MakeWord_2(int i, int i2) {
        if (i < 0 || i2 < 0) {
            return -1;
        }
        return (i * 256) + i2;
    }

    public static int SetBit0(int i, int i2) {
        return i & (~(1 << i2));
    }

    public static int SetBit1(int i, int i2) {
        return i | (1 << i2);
    }

    public static double degreeToRadian(double d) {
        return (d * 3.141592653589793d) / 180.0d;
    }

    public static byte[] intToByteArray1(int i) {
        return new byte[]{(byte) ((i >> 24) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)};
    }

    public static boolean SetNavi(Context context, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14) {
        if (str == null || str2 == null || str3 == null || str4 == null || str.length() <= 0 || str2.length() <= 0 || str3.length() <= 0 || str4.length() <= 0) {
            return false;
        }
        String str15 = ((("androidamap://navi?sourceApplication=appname&poiname=fangheng&poiid=BGVIS&lat=" + str) + "&lon=" + str2) + "&dev=" + str3) + "&style=" + str4;
        if (str5 == null || str6 == null) {
            return false;
        }
        if (str5.length() > 0 && str6.length() > 0) {
            str15 = str15 + "&throughpoint=" + str5 + "," + str6;
            if (str7 != null && str8 != null && str7.length() > 0 && str8.length() > 0) {
                str15 = str15 + ";" + str7 + "," + str8;
                if (str9 != null && str10 != null && str9.length() > 0 && str10.length() > 0) {
                    str15 = str15 + ";" + str9 + "," + str10;
                    if (str11 != null && str12 != null && str11.length() > 0 && str12.length() > 0) {
                        str15 = str15 + ";" + str11 + "," + str12;
                        if (str13 != null && str14 != null && str13.length() > 0 && str14.length() > 0) {
                            str15 = str15 + ";" + str13 + "," + str14;
                        }
                    }
                }
            }
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str15));
        intent.setPackage("com.autonavi.minimap");
        context.startActivity(intent);
        return true;
    }

    public static boolean isPkgInstalled(Context context, String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        try {
            return context.getPackageManager().getApplicationInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private static boolean isAvilible(Context context, String str) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            if (installedPackages.get(i).packageName.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    public static int WeatherToInt(String str) {
        if (str.equals("晴")) {
            return 0;
        }
        if (str.equals("多云") || str.equals("阴")) {
            return 1;
        }
        if (str.equals("阵雨") || str.equals("雷阵雨") || str.equals("小雨") || str.equals("中雨") || str.equals("大雨") || str.equals("暴雨") || str.equals("大暴雨") || str.equals("特大暴雨") || str.equals("冻雨") || str.equals("小雨-中雨") || str.equals("中雨-大雨") || str.equals("大雨-暴雨") || str.equals("暴雨-大暴雨") || str.equals("大暴雨-特大暴雨")) {
            return 2;
        }
        if (str.equals("雷阵雨并伴有冰雹") || str.equals("雨夹雪") || str.equals("阵雪") || str.equals("小雪") || str.equals("中雪") || str.equals("大雪") || str.equals("暴雪") || str.equals("小雪-中雪") || str.equals("中雪-大雪") || str.equals("大雪-暴雪") || str.equals("弱高吹雪")) {
            return 3;
        }
        if (str.equals("雾") || str.equals("轻霾") || str.equals("霾")) {
            return 4;
        }
        return (str.equals("飑") || str.equals("沙尘暴") || str.equals("浮尘") || str.equals("扬沙") || str.equals("强沙尘暴") || str.equals("龙卷风")) ? 5 : -1;
    }

    public static int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2dip(Context context, float f) {
        return (int) ((f / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int[] GetScreenX(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        return null;
    }

    public static String IntToString(int i) {
        return Integer.toString(i);
    }

    public static long getFileSize(File file) throws Exception {
        if (file.exists()) {
            return new FileInputStream(file).available();
        }
        return 0L;
    }

    public static void DeleteOldFils(String str) {
        File[] fileArrListFiles;
        if (str == null || (fileArrListFiles = new File(str).listFiles()) == null) {
            return;
        }
        String str2 = "";
        for (File file : fileArrListFiles) {
            String string = file.toString();
            if (str2 == "" || str2.compareTo(string) > 0) {
                str2 = string;
            }
        }
        if (str2 == null || str2.length() <= 0) {
            return;
        }
        deleteFile(str2);
    }

    public static void CreateFolder(String str) {
        File file = new File(str);
        if (file.exists()) {
            return;
        }
        file.mkdirs();
    }

    public static String GetCurrentFromPathToName(String str) {
        if (str == null) {
            return "";
        }
        String[] strArrSplit = str.split("/");
        if (strArrSplit.length <= 0) {
            return "";
        }
        String[] strArrSplit2 = strArrSplit[strArrSplit.length - 1].split("\\.");
        if (strArrSplit2.length < 2) {
            return "";
        }
        if (strArrSplit2.length == 2) {
            return strArrSplit2[0];
        }
        return strArrSplit2[0] + "...";
    }

    public static long getSDFreeSize(String str) {
        StatFs statFs = new StatFs(str);
        return ((((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) / 1024) / 1024;
    }

    public static long getSDTotalSize(String str) {
        StatFs statFs = new StatFs(str);
        return ((((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize())) / 1024) / 1024;
    }

    public static String Byte2ToString(byte[] bArr, String str) {
        try {
            return new String(bArr, str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String Byte2ToString(byte[] bArr, String str, int i) {
        if (i <= 0) {
            return null;
        }
        byte[] bArr2 = new byte[i];
        System.arraycopy(bArr, 0, bArr2, 0, i);
        try {
            return new String(bArr2, str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] StringToByte2(String str, String str2) {
        try {
            return str.getBytes(str2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String str2HexStr(String str) {
        char[] charArray = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(charArray[(bytes[i] & 240) >> 4]);
            sb.append(charArray[bytes[i] & HSSFErrorConstants.ERROR_VALUE]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static String hexStr2Str(String str) {
        char[] charArray = str.toCharArray();
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) ((("0123456789ABCDEF".indexOf(charArray[i2]) * 16) + "0123456789ABCDEF".indexOf(charArray[i2 + 1])) & 255);
        }
        return new String(bArr);
    }

    public static String byte2HexStr(byte[] bArr, String str) {
        StringBuilder sb = new StringBuilder("");
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            sb.append(hexString);
            sb.append(str);
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String byte2HexStr(byte[] bArr, int i, String str) {
        StringBuilder sb = new StringBuilder("");
        for (int i2 = 0; i2 < i; i2++) {
            String hexString = Integer.toHexString(bArr[i2] & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            sb.append(hexString);
            sb.append(str);
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String int2HexStr(int[] iArr, String str) {
        StringBuilder sb = new StringBuilder("");
        for (int i : iArr) {
            String hexString = Integer.toHexString(i & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            sb.append(hexString);
            sb.append(str);
        }
        return sb.toString().toUpperCase().trim();
    }

    public static byte[] hexStr2Bytes(String str) {
        int length = str.length() / 2;
        System.out.println(length);
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            int i3 = i2 + 1;
            bArr[i] = Byte.decode("0x" + str.substring(i2, i3) + str.substring(i3, i3 + 1)).byteValue();
        }
        return bArr;
    }

    public static String strToUnicode(String str) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            String hexString = Integer.toHexString(cCharAt);
            if (cCharAt > 128) {
                sb.append("\\u" + hexString);
            } else {
                sb.append("\\u00" + hexString);
            }
        }
        return sb.toString();
    }

    public static String unicodeToString(String str) {
        int length = str.length() / 6;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < length) {
            int i2 = i * 6;
            i++;
            String strSubstring = str.substring(i2, i * 6);
            sb.append(new String(Character.toChars(Integer.valueOf(strSubstring.substring(2, 4) + TarConstants.VERSION_POSIX, 16).intValue() + Integer.valueOf(strSubstring.substring(4), 16).intValue())));
        }
        return sb.toString();
    }

    public static String ToStringDouble(Double d, boolean z) {
        if (z) {
            return String.valueOf(d);
        }
        return new DecimalFormat("#.0").format(d);
    }

    public static String ToStringFloat(Float f, boolean z) {
        if (z) {
            return String.valueOf(f);
        }
        return new DecimalFormat("#.0").format(f);
    }

    public static int StringToInt(String str) {
        if (str == null) {
            return 0;
        }
        return Integer.valueOf(str).intValue();
    }

    public static double StringToDouble(String str) {
        if (str == null) {
            return 0.0d;
        }
        return Double.parseDouble(str);
    }

    private byte[] getBytes(char[] cArr) {
        Charset charsetForName = Charset.forName("UTF-8");
        CharBuffer charBufferAllocate = CharBuffer.allocate(cArr.length);
        charBufferAllocate.put(cArr);
        charBufferAllocate.flip();
        return charsetForName.encode(charBufferAllocate).array();
    }

    private char[] getChars(byte[] bArr) {
        Charset charsetForName = Charset.forName("UTF-8");
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(bArr.length);
        byteBufferAllocate.put(bArr);
        byteBufferAllocate.flip();
        return charsetForName.decode(byteBufferAllocate).array();
    }

    public static boolean deleteFile(String str) {
        File file = new File(str);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public boolean deleteDirectory(String str) {
        if (!str.endsWith(File.separator)) {
            str = str + File.separator;
        }
        File file = new File(str);
        if (!file.exists() || !file.isDirectory()) {
            return false;
        }
        File[] fileArrListFiles = file.listFiles();
        boolean zDeleteDirectory = true;
        for (int i = 0; i < fileArrListFiles.length; i++) {
            if (fileArrListFiles[i].isFile()) {
                zDeleteDirectory = deleteFile(fileArrListFiles[i].getAbsolutePath());
                if (!zDeleteDirectory) {
                    break;
                }
            } else {
                zDeleteDirectory = deleteDirectory(fileArrListFiles[i].getAbsolutePath());
                if (!zDeleteDirectory) {
                    break;
                }
            }
        }
        if (zDeleteDirectory) {
            return file.delete();
        }
        return false;
    }

    public boolean DeleteFolder(String str) {
        File file = new File(str);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return deleteFile(str);
        }
        return deleteDirectory(str);
    }

    public static boolean FileExists(String str) {
        return str != null && str.length() > 0 && new File(str).exists();
    }

    public static int CheckSum(int[] iArr, int i, int i2) {
        int i3 = 0;
        while (i <= i2) {
            i3 ^= iArr[i];
            i++;
        }
        return i3;
    }

    public static String getDescriptionTimeFromTimestamp(long j) {
        long jCurrentTimeMillis = (System.currentTimeMillis() - j) / 1000;
        System.out.println("timeGap: " + jCurrentTimeMillis);
        if (jCurrentTimeMillis > 31536000) {
            return (jCurrentTimeMillis / 31536000) + "年前";
        }
        if (jCurrentTimeMillis > 2592000) {
            return (jCurrentTimeMillis / 2592000) + "个月前";
        }
        if (jCurrentTimeMillis > 86400) {
            return (jCurrentTimeMillis / 86400) + "天前";
        }
        if (jCurrentTimeMillis > 3600) {
            return (jCurrentTimeMillis / 3600) + "小时前";
        }
        return jCurrentTimeMillis > 60 ? (jCurrentTimeMillis / 60) + "分钟前" : "刚刚";
    }

    public static String dateToString(Date date, String str) {
        return new SimpleDateFormat(str, Locale.US).format(date);
    }

    public static String longToString(long j, String str) {
        return dateToString(longToDate(j, str), str);
    }

    public static Date stringToDate(String str, String str2) {
        try {
            return new SimpleDateFormat(str2, Locale.US).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date longToDate(long j, String str) {
        return stringToDate(dateToString(new Date(j), str), str);
    }

    public static long stringToLong(String str, String str2) {
        Date dateStringToDate = stringToDate(str, str2);
        if (dateStringToDate == null) {
            return 0L;
        }
        return dateToLong(dateStringToDate);
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static String getTime(long j) {
        return new SimpleDateFormat("yy-MM-dd HH:mm", Locale.US).format(new Date(j));
    }

    public static String getHourAndMin(long j) {
        return new SimpleDateFormat(FORMAT_TIME, Locale.US).format(new Date(j));
    }

    public static String getFormatTime(long j, String str) {
        return new SimpleDateFormat(str, Locale.US).format(new Date(j));
    }

    public static String getChatTime(long j) {
        long j2 = j * 1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd", Locale.US);
        int i = Integer.parseInt(simpleDateFormat.format(new Date(System.currentTimeMillis()))) - Integer.parseInt(simpleDateFormat.format(new Date(j2)));
        if (i == 0) {
            return "今天 " + getHourAndMin(j2);
        }
        if (i == 1) {
            return "昨天 " + getHourAndMin(j2);
        }
        if (i == 2) {
            return "前天 " + getHourAndMin(j2);
        }
        return getTime(j2);
    }

    public static int[] IntToInt(int i, int i2) {
        int[] iArr = new int[i2];
        if (i2 == 2) {
            iArr[1] = (byte) (i & 255);
            iArr[0] = (byte) ((i >> 8) & 255);
        } else if (i2 == 4) {
            iArr[3] = (byte) (i & 255);
            iArr[2] = (byte) ((i >> 8) & 255);
            iArr[1] = (byte) ((i >> 16) & 255);
            iArr[0] = (byte) ((i >> 24) & 255);
        }
        return iArr;
    }

    public static int IntToBCD(int i) {
        return Integer.valueOf(Integer.parseInt(Integer.toString(i), 16)).intValue();
    }

    public static String GetCurrentTimeName() {
        Time time = new Time();
        time.setToNow();
        return IntToString(time.year) + "-" + IntToString(time.month) + "-" + IntToString(time.monthDay) + "-" + IntToString(time.hour) + "-" + IntToString(time.minute) + "-" + IntToString(time.second);
    }

    public static String toDBC(String str) {
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == 12288) {
                charArray[i] = ' ';
            } else if (charArray[i] > 65280 && charArray[i] < 65375) {
                charArray[i] = (char) (charArray[i] - 65248);
            }
        }
        return new String(charArray);
    }

    public static byte[] Ints2Bytes(int[] iArr) {
        byte[] bArr = new byte[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            bArr[i] = (byte) iArr[i];
        }
        return bArr;
    }

    public static int[] Bytes2Ints(byte[] bArr) {
        int[] iArr = new int[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            iArr[i] = bArr[i] & 255;
        }
        return iArr;
    }

    public static String Int2ToString(int[] iArr, String str) {
        byte[] bArr = new byte[iArr.length];
        try {
            return new String(Ints2Bytes(iArr), str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void search(File file, String[] strArr, ArrayList<String> arrayList) {
        if (file != null) {
            int i = 0;
            if (file.isDirectory()) {
                File[] fileArrListFiles = file.listFiles();
                if (fileArrListFiles != null) {
                    while (i < fileArrListFiles.length) {
                        search(fileArrListFiles[i], strArr, arrayList);
                        i++;
                    }
                    return;
                }
                return;
            }
            String absolutePath = file.getAbsolutePath();
            while (i < strArr.length) {
                if (absolutePath.endsWith(strArr[i])) {
                    arrayList.add(absolutePath);
                    return;
                }
                i++;
            }
        }
    }

    public static String Mp3TimeToTime(int i) {
        String str;
        String str2;
        String str3;
        int i2 = i / 1000;
        int i3 = i2 / 60;
        int i4 = i3 / 60;
        int i5 = i3 % 60;
        int i6 = i2 % 60;
        if (i4 < 10) {
            str = "0" + i4;
        } else {
            str = i4 + "";
        }
        if (i5 < 10) {
            str2 = "0" + i5;
        } else {
            str2 = i5 + "";
        }
        if (i6 < 10) {
            str3 = "0" + i6;
        } else {
            str3 = i6 + "";
        }
        return str + ":" + str2 + ":" + str3;
    }

    public static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static int atoi(String str) {
        if (str != null && isNumeric(str)) {
            return Integer.valueOf(str).intValue();
        }
        return 0;
    }

    public static String itoa_Time(int i) {
        String string = Integer.toString(i);
        return string.length() == 1 ? "0" + string : string;
    }

    public static String itoa(int i) {
        return Integer.toString(i);
    }

    public static void initSP(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences("SP", 0);
        }
        if (ed == null) {
            ed = sp.edit();
        }
    }

    public static void keepGPS(Context context, boolean z) {
        initSP(context);
        if (z) {
            ed.putString("position_j_old", sp.getString("position_j", "0"));
            ed.putString("position_w_old", sp.getString("position_w", "0"));
            ed.putString("position_t_old", sp.getString("position_t", "0"));
        } else {
            ed.putString("position_j_old", "0");
            ed.putString("position_w_old", "0");
            ed.putString("position_t_old", "0");
        }
        ed.commit();
    }

    public static String getPosition_j_old(Context context) {
        initSP(context);
        return sp.getString("position_j_old", "0");
    }

    public static String getPosition_w_old(Context context) {
        initSP(context);
        return sp.getString("position_w_old", "0");
    }

    public static String getPosition_t_old(Context context) {
        initSP(context);
        return sp.getString("position_t_old", "0");
    }

    public static void setPosition(Context context, Double d, Double d2, long j, long j2) {
        initSP(context);
        ed.putString("position_j", String.valueOf(d));
        ed.putString("position_w", String.valueOf(d2));
        ed.putString("position_t", String.valueOf(j));
        ed.putLong("position_time", j2);
        ed.commit();
    }

    public static String getPosition_j(Context context) {
        initSP(context);
        return sp.getString("position_j", "0");
    }

    public static String getPosition_w(Context context) {
        initSP(context);
        return sp.getString("position_w", "0");
    }

    public static String getPosition_t(Context context) {
        initSP(context);
        return sp.getString("position_t", "1420041600000");
    }

    public static Long getPosition_time(Context context) {
        initSP(context);
        return Long.valueOf(sp.getLong("position_time", 0L));
    }

    public static void setlastP(Context context) {
        initSP(context);
        ed.putString("lastzuobiao_j", sp.getString("position_j", "0"));
        ed.putString("lastzuobiao_w", sp.getString("position_w", "0"));
        ed.commit();
    }

    public static boolean islike(Context context) {
        initSP(context);
        return sp.getString("position_j", "0").equals(sp.getString("lastzuobiao_j", "0")) && sp.getString("position_w", "0").equals(sp.getString("lastzuobiao_w", "0"));
    }

    public static String getlastP_j(Context context) {
        initSP(context);
        return sp.getString("lastzuobiao_j", "0");
    }

    public static String getlastP_w(Context context) {
        initSP(context);
        return sp.getString("lastzuobiao_w", "0");
    }

    public static void setWeather(Context context, String str, String str2, String str3) {
        initSP(context);
        ed.putString("weather_city", str);
        ed.putString("weather_weather", str2);
        ed.putString("weather_air", str3);
        ed.commit();
    }

    public static String getWeather(Context context) {
        initSP(context);
        return sp.getString("weather_weather", "多云") + "," + sp.getString("weather_city", "深圳") + "," + sp.getString("weather_air", "");
    }

    public static void setisssid(Context context, String str) {
        initSP(context);
        ed.putString("isssid", str);
        ed.commit();
    }

    public static String getisssid(Context context) {
        initSP(context);
        return sp.getString("isssid", "0");
    }

    public static void setRestarttcpcount(Context context, int i) {
        initSP(context);
        ed.putInt("Restarttcpcount", i);
        ed.commit();
    }

    public static int getRestarttcpcount(Context context) {
        initSP(context);
        return sp.getInt("Restarttcpcount", 0);
    }

    public static void setissleep(Context context, boolean z, long j) {
        initSP(context);
        ed.putBoolean("issleep", z);
        ed.putLong("sleeptime", j);
        ed.commit();
    }

    public static boolean getissleep(Context context) {
        initSP(context);
        if (sp.getBoolean("issleep", false)) {
            long time = new Date().getTime() - sp.getLong("sleeptime", 0L);
            if (time > TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS && time < 864000000) {
                return true;
            }
        }
        return false;
    }

    public static void setsdpath(Context context, String str) {
        initSP(context);
        ed.putString("sdpath", str);
        ed.commit();
    }

    public static String getsdpath(Context context) {
        initSP(context);
        return sp.getString("sdpath", "/mnt/extsd1/");
    }

    public static void setisFM(Context context, boolean z) {
        initSP(context);
        ed.putBoolean("isfm", z);
        ed.commit();
    }

    public static boolean getisFM(Context context) {
        initSP(context);
        return sp.getBoolean("isfm", false);
    }

    public static void setFMfrequency(Context context, String str) {
        initSP(context);
        ed.putString(UserDictionary.Words.FREQUENCY, str);
        ed.commit();
    }

    public static String getFMfrequency(Context context) {
        initSP(context);
        return sp.getString(UserDictionary.Words.FREQUENCY, "88.8");
    }

    public static String getFMfrequencyForWrite(Context context) {
        initSP(context);
        double d = Double.parseDouble(sp.getString(UserDictionary.Words.FREQUENCY, "88.8"));
        return ((d * 1000.0d) + "").substring(0, d >= 100.0d ? 5 : 4);
    }

    public static void setwifipass(Context context, String str) {
        initSP(context);
        ed.putString("wifipass", str);
        ed.commit();
    }

    public static String getwifipass(Context context) {
        initSP(context);
        return sp.getString("wifipass", "12345678");
    }

    public static void setlinagdu(Context context, int i) {
        initSP(context);
        ed.putInt("liangdu", i);
        ed.commit();
    }

    public static int getliangdu(Context context) {
        initSP(context);
        return sp.getInt("liangdu", 100);
    }

    public static void setisWakeup(Context context, String str) {
        initSP(context);
        ed.putString("iswakeup", str);
        ed.commit();
    }

    public static String getisWakeup(Context context) {
        initSP(context);
        return sp.getString("iswakeup", "1");
    }

    public static void setWakeupname(Context context, String str) {
        initSP(context);
        ed.putString("wakeupname", str);
        ed.commit();
    }

    public static String getWakeupname(Context context) {
        initSP(context);
        return sp.getString("wakeupname", "小灵");
    }

    public static void setLeng(Context context, int i) {
        initSP(context);
        ed.putInt("leng", i);
        ed.commit();
    }

    public static int getLeng(Context context) {
        initSP(context);
        return sp.getInt("leng", 3);
    }

    public static void setSen(Context context, String str) {
        initSP(context);
        ed.putString(Context.SENSOR_SERVICE, str);
        ed.commit();
    }

    public static String getSen(Context context) {
        initSP(context);
        return sp.getString(Context.SENSOR_SERVICE, "1");
    }

    public static void setisSen(Context context, String str) {
        initSP(context);
        ed.putString("issensor", str);
        ed.commit();
    }

    public static String getisSen(Context context) {
        initSP(context);
        return sp.getString("issensor", "1");
    }

    public static void setStartAfter(Context context, String str) {
        initSP(context);
        ed.putString("whatsdo", str);
        ed.commit();
    }

    public static String getStartAfter(Context context) {
        initSP(context);
        return sp.getString("whatsdo", "1");
    }

    public static void setPhotoPath(Context context, String str) {
        initSP(context);
        ed.putString("photopath", str);
        ed.commit();
    }

    public static String getPhotoPath(Context context) {
        initSP(context);
        return sp.getString("photopath", null);
    }

    public static void setis24(Context context, String str) {
        initSP(context);
        ed.putString("is24", str);
        ed.commit();
    }

    public static String getis24(Context context) {
        initSP(context);
        return sp.getString("is24", "1");
    }

    public static String getTime(Context context) {
        initSP(context);
        String str = new SimpleDateFormat(FORMAT_TIME, Locale.US).format(new Date());
        if (!sp.getString("is24", "0").equals("0")) {
            return str;
        }
        String[] strArrSplit = str.split(":");
        int i = Integer.parseInt(strArrSplit[0]);
        int i2 = Integer.parseInt(strArrSplit[1]);
        if (i > 12) {
            return addZero(i - 12) + ":" + addZero(i2) + " pm";
        }
        return addZero(i) + ":" + addZero(i2) + " am";
    }

    public static String getTime2(Context context) {
        initSP(context);
        return new SimpleDateFormat(FORMAT_TIME, Locale.US).format(new Date());
    }

    public static void setismute(Context context, String str) {
        initSP(context);
        ed.putString("ismute", str);
        ed.commit();
    }

    public static String getismute(Context context) {
        initSP(context);
        return sp.getString("ismute", "0-15");
    }

    public static void setchejiNumber(Context context, String str) {
        initSP(context);
        ed.putString("chejinumber", str);
        ed.commit();
    }

    public static String getchejiNumber(Context context) {
        initSP(context);
        return sp.getString("chejinumber", "0");
    }

    public static void setContacts(Context context, String str) {
        initSP(context);
        ed.putString(Contacts.AUTHORITY, str);
        ed.commit();
    }

    public static HashMap<String, String> getContacts(Context context) {
        initSP(context);
        String[] strArrSplit = sp.getString(Contacts.AUTHORITY, "").split("-llx-");
        if (strArrSplit.length <= 0) {
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        for (String str : strArrSplit) {
            String[] strArrSplit2 = str.split("---");
            if (strArrSplit2.length == 2) {
                map.put(strArrSplit2[0], strArrSplit2[1]);
            }
        }
        return map;
    }

    public static ArrayList<HashMap<String, String>> getContacts2list(Context context) {
        initSP(context);
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        String[] strArrSplit = sp.getString(Contacts.AUTHORITY, "").split("-llx-");
        if (strArrSplit.length <= 0) {
            return null;
        }
        for (String str : strArrSplit) {
            HashMap<String, String> map = new HashMap<>();
            String[] strArrSplit2 = str.split("---");
            if (strArrSplit2.length == 2) {
                map.put("name", strArrSplit2[0]);
                map.put("number", strArrSplit2[1]);
                arrayList.add(map);
            }
        }
        return arrayList;
    }

    public static String Number2Name(Context context, String str) {
        initSP(context);
        String[] strArrSplit = sp.getString(Contacts.AUTHORITY, "").split("-llx-");
        for (int i = 0; i < strArrSplit.length; i++) {
            if (strArrSplit[i].contains(str)) {
                return strArrSplit[i].split("---")[0];
            }
        }
        return "";
    }

    public static String Name2Number(Context context, String str) {
        initSP(context);
        String[] strArrSplit = sp.getString(Contacts.AUTHORITY, "").split("-llx-");
        for (int i = 0; i < strArrSplit.length; i++) {
            if (strArrSplit[i].split("---")[0].equals(str)) {
                return strArrSplit[i].split("---")[1];
            }
        }
        return "";
    }

    public static String addZero(int i) {
        if (i > -1 && i < 10) {
            return "0" + i;
        }
        return "" + i;
    }

    public static int getScreenBrightness(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception unused) {
            return 255;
        }
    }

    public static void saveScreenBrightness(Context context, int i) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getVersion(Context context) {
        try {
            return "   " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "   ";
        }
    }

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void long2Byte(byte[] bArr, long j) {
        bArr[0] = (byte) (j >> 24);
        bArr[1] = (byte) (j >> 16);
        bArr[2] = (byte) (j >> 8);
        bArr[3] = (byte) (j >> 0);
    }

    public static long getLong(byte[] bArr) {
        return ((((long) bArr[0]) & 255) << 56) | ((((long) bArr[1]) & 255) << 48) | ((((long) bArr[2]) & 255) << 40) | ((((long) bArr[3]) & 255) << 32) | ((((long) bArr[4]) & 255) << 24) | ((((long) bArr[5]) & 255) << 16) | ((((long) bArr[6]) & 255) << 8) | ((255 & ((long) bArr[7])) << 0);
    }

    public static void StartMain(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, (Uri) null);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(270532608);
        context.startActivity(intent);
    }

    public static boolean SendMessage(int i, int i2, int i3, String str, Handler handler) {
        if (handler == null) {
            return false;
        }
        Message message = new Message();
        message.what = i;
        message.arg1 = i2;
        message.arg2 = i3;
        if (str != null) {
            message.obj = str;
        }
        handler.sendMessage(message);
        return true;
    }

    public static String InsertString(String str, int i, String str2) {
        if (i > str.length() || i < 0) {
            return "";
        }
        return str.substring(0, i) + str2 + str.substring(i);
    }

    public static String GetTimeBeiJing(String str, String str2) {
        if (str == null || str.length() < 6) {
            return " ";
        }
        return (str2 == null || str2.length() != 6) ? " " : dateToString(longToDate(stringToLong(str2 + str.substring(0, 5), "ddMMyyHHmmss") + 28800000, "yyyyMMddHHmmss"), FORMAT_DATE_TIME_SECOND);
    }

    public static String getDistance(int i) {
        if (i > -1 && i < 1000) {
            return i + "m";
        }
        if (i < 1000) {
            return "--km";
        }
        int i2 = (i % 1000) / 100;
        if (i2 == 0) {
            return (i / 1000) + "km";
        }
        return (i / 1000) + "." + i2 + "km";
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

    public static void ClearArray(byte[] bArr, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            bArr[i2] = 0;
        }
    }
}
