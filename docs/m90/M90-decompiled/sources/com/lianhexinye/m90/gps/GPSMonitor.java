package com.lianhexinye.m90.gps;

import android.media.MediaPlayer;
import android.util.Log;
import com.lianhexinye.m90.common.utils.JavaUtils;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class GPSMonitor {
    static int GPGSA = 1;
    static int GPGSV = 2;
    static int GPRMC = 4;
    static int MAX_COUNT = 512;
    public static int MAX_STAR_COUNT = 24;
    static boolean bFrameStarted = false;
    public static boolean bHasData = false;
    public static boolean bValidData = false;
    static int m_dwParsedGPSType;
    static int nPorIndex;
    public static String wAltitude;
    public static String wCourse;
    public static String wDate;
    public static int wSatellitesInView_bd;
    public static int wSatellitesInView_gps;
    public static String wSpeed;
    public static String wTime;
    public static int wUsedSatellites_bd;
    public static int wUsedSatellites_gps;
    public static int wViewedSatellites_bd;
    public static int wViewedSatellites_gps;
    public static String wszLatitude;
    public static String wszLatitudeE;
    public static String wszLatitudeN;
    public static String wszLongitude;
    public static int[] wID_gps = new int[24];
    public static boolean[] bViewed_gps = new boolean[24];
    public static boolean[] bValid_gps = new boolean[24];
    public static int[] wSNR_gps = new int[24];
    public static int[] wID_bd = new int[24];
    public static boolean[] bViewed_bd = new boolean[24];
    public static boolean[] bValid_bd = new boolean[24];
    public static int[] wSNR_bd = new int[24];
    public static int[] m_wUsedSatellitesID_gps = new int[24];
    public static int[] m_wUsedSatellitesID_bd = new int[24];
    static byte[] abBuffer = new byte[512];

    public static void getPortData(byte[] bArr, int i, GPSSerialPortResultListener gPSSerialPortResultListener) {
        int i2;
        for (int i3 = 0; i3 < i; i3++) {
            byte b = bArr[i3];
            if (b == 10) {
                bFrameStarted = false;
                byte[] bArr2 = abBuffer;
                int i4 = nPorIndex;
                int i5 = i4 + 1;
                nPorIndex = i5;
                bArr2[i4] = bArr[i3];
                byte[] bArr3 = new byte[i5];
                System.arraycopy(bArr2, 0, bArr3, 0, i5);
                ProcessGPSData(Function.Byte2ToString(bArr3, MediaPlayer.CHARSET_GBK), gPSSerialPortResultListener);
            } else if (b == 36) {
                bFrameStarted = true;
                ClearArray(abBuffer, MAX_COUNT);
                nPorIndex = 0;
                byte[] bArr4 = abBuffer;
                nPorIndex = 0 + 1;
                bArr4[0] = bArr[i3];
            } else if (bFrameStarted && (i2 = nPorIndex) < MAX_COUNT) {
                byte b2 = bArr[i3];
                byte[] bArr5 = abBuffer;
                nPorIndex = i2 + 1;
                bArr5[i2] = bArr[i3];
            }
        }
    }

    private static void ProcessGPSData(String str, GPSSerialPortResultListener gPSSerialPortResultListener) {
        if (str != null && str.length() >= 6) {
            String strSubstring = str.substring(0, 6);
            if (strSubstring.equals("$GPRMC") || strSubstring.equals("$BDRMC") || strSubstring.equals("$GNRMC")) {
                ParseGPRMC(str, gPSSerialPortResultListener);
            }
        }
    }

    private static void ParseGPRMC(String str, GPSSerialPortResultListener gPSSerialPortResultListener) {
        String[] strArrSplit = str.split(",");
        int length = strArrSplit.length;
        if (length == 14 || length == 13) {
            wTime = strArrSplit[1];
            bValidData = false;
            if (strArrSplit[2] != null && strArrSplit[2].equals("A")) {
                bValidData = true;
            }
            String str2 = strArrSplit[3];
            if (str2 == null || str2.trim().equals("") || JavaUtils.isDecimal(str2)) {
                if (str2.trim().equals("")) {
                    wszLatitude = str2;
                } else {
                    wszLatitude = String.format(Locale.CHINA, "%.4f", Double.valueOf(str2));
                }
                wszLatitudeN = strArrSplit[4];
                String str3 = strArrSplit[5];
                if (str3 == null || str3.trim().equals("") || JavaUtils.isDecimal(str3)) {
                    if (str3.trim().equals("")) {
                        wszLongitude = str3;
                    } else {
                        wszLongitude = String.format(Locale.CHINA, "%.4f", Double.valueOf(str3));
                    }
                    wszLatitudeE = strArrSplit[6];
                    String str4 = strArrSplit[7];
                    wSpeed = str4;
                    if (str4 == null || str4.trim().equals("") || JavaUtils.isDecimal(wSpeed)) {
                        String str5 = strArrSplit[8];
                        wCourse = str5;
                        if (str5 == null || str5.trim().equals("") || JavaUtils.isDecimal(wCourse)) {
                            String str6 = strArrSplit[9];
                            wDate = str6;
                            if (str6 == null || str6.trim().equals("") || JavaUtils.isNumeric(wDate)) {
                                m_dwParsedGPSType |= GPRMC;
                                if (gPSSerialPortResultListener != null) {
                                    gPSSerialPortResultListener.onSuccessResult();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void ParseGNGGA(String str) {
        String[] strArrSplit = str.split(",");
        if (strArrSplit.length != 15) {
            return;
        }
        if (!JavaUtils.isEmpty(strArrSplit[7])) {
            wUsedSatellites_gps = Integer.parseInt(strArrSplit[7]);
        }
        wAltitude = strArrSplit[9];
    }

    /* JADX WARN: Removed duplicated region for block: B:78:0x0117  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void ParseGPGSV(java.lang.String r10) {
        /*
            Method dump skipped, instruction units count: 426
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.gps.GPSMonitor.ParseGPGSV(java.lang.String):void");
    }

    public static void ClearArray(byte[] bArr, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            bArr[i2] = 0;
        }
    }

    static void ParseGPGSA(String str) {
        if (strncmp(str, "$GPGSA", 6)) {
            wUsedSatellites_gps = 0;
        }
        if (strncmp(str, "$BDGSA", 6)) {
            wUsedSatellites_bd = 0;
        }
        String[] strArrSplit = str.split(",");
        if (strArrSplit.length < 17) {
            return;
        }
        for (int i = 0; i < 12; i++) {
            int i2 = i + 3;
            if (strArrSplit[i2] != null && strArrSplit[i2].length() > 0) {
                if (strncmp(str, "$GPGSA", 6) && Function.isNumeric(strArrSplit[i2]) && strArrSplit[i2].length() != 0) {
                    int[] iArr = m_wUsedSatellitesID_gps;
                    int i3 = wUsedSatellites_gps;
                    wUsedSatellites_gps = i3 + 1;
                    iArr[i3] = Integer.valueOf(strArrSplit[i2]).intValue();
                }
                if (strncmp(str, "$BDGSA", 6) && Function.isNumeric(strArrSplit[i2]) && strArrSplit[i2].length() != 0) {
                    int[] iArr2 = m_wUsedSatellitesID_bd;
                    int i4 = wUsedSatellites_bd;
                    wUsedSatellites_bd = i4 + 1;
                    iArr2[i4] = Integer.valueOf(strArrSplit[i2]).intValue();
                }
                if (Function.isNumeric(strArrSplit[i2]) && strArrSplit[i2].length() != 0 && Integer.valueOf(strArrSplit[i2]).intValue() > 100) {
                    Log.e("error id > 100", str);
                }
            }
        }
        m_dwParsedGPSType |= GPGSA;
    }

    private static boolean strncmp(String str, String str2, int i) {
        return str != null && str.length() > 0 && str2 != null && str2.length() > 0 && str2.length() <= str.length() && str.substring(0, str2.length()).equals(str2);
    }

    private static void ClearStarInfo_gps() {
        for (int i = 0; i < MAX_STAR_COUNT; i++) {
            wID_gps[i] = 0;
            bViewed_gps[i] = false;
            bValid_gps[i] = false;
            wSNR_gps[i] = 0;
        }
    }

    private static void ClearStarInfo_bd() {
        for (int i = 0; i < MAX_STAR_COUNT; i++) {
            wID_bd[i] = 0;
            bViewed_bd[i] = false;
            bValid_bd[i] = false;
            wSNR_bd[i] = 0;
        }
    }
}
