package com.lianhexinye.m90.serialport;

import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.AttrPtg;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;
import org.apache.poi.hssf.record.formula.IntPtg;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.MissingArgPtg;
import org.apache.poi.hssf.record.formula.NotEqualPtg;
import org.apache.poi.hssf.record.formula.NumberPtg;
import org.apache.poi.hssf.record.formula.ParenthesisPtg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.record.formula.UnaryMinusPtg;
import org.apache.poi.hssf.record.formula.UnaryPlusPtg;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class CRC16 {
    static byte[] crc16_tab_h = {0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64};
    static byte[] crc16_tab_l = {0, -64, -63, 1, -61, 3, 2, -62, -58, 6, 7, -57, 5, -59, -60, 4, -52, 12, GreaterThanPtg.sid, -51, HSSFErrorConstants.ERROR_VALUE, -49, -50, NotEqualPtg.sid, 10, -54, -53, 11, -55, 9, 8, -56, -40, 24, AttrPtg.sid, -39, 27, -37, -38, 26, IntPtg.sid, -34, -33, NumberPtg.sid, -35, 29, 28, -36, 20, -44, -43, ParenthesisPtg.sid, -41, 23, MissingArgPtg.sid, -42, -46, UnaryPlusPtg.sid, UnaryMinusPtg.sid, -45, 17, -47, -48, 16, -16, TarConstants.LF_NORMAL, TarConstants.LF_LINK, -15, TarConstants.LF_CHR, -13, -14, TarConstants.LF_SYMLINK, TarConstants.LF_FIFO, -10, -9, TarConstants.LF_CONTIG, -11, TarConstants.LF_DIR, TarConstants.LF_BLK, -12, 60, -4, -3, 61, -1, 63, 62, -2, -6, Ref3DPtg.sid, Area3DPtg.sid, -5, 57, -7, -8, PaletteRecord.STANDARD_PALETTE_SIZE, 40, -24, -23, MemFuncPtg.sid, -21, 43, HSSFErrorConstants.ERROR_NA, -22, -18, 46, 47, -17, 45, -19, -20, 44, -28, 36, 37, -27, 39, -25, -26, 38, 34, -30, -29, 35, -31, 33, 32, -32, -96, 96, 97, -95, 99, -93, -94, 98, 102, -90, -89, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, -91, 101, 100, -92, 108, -84, -83, 109, -81, 111, 110, -82, -86, 106, 107, -85, 105, -87, -88, 104, TarConstants.LF_PAX_EXTENDED_HEADER_LC, -72, -71, 121, -69, 123, 122, -70, -66, 126, 127, -65, 125, -67, -68, 124, -76, 116, 117, -75, 119, -73, -74, 118, 114, -78, -77, 115, -79, 113, 112, -80, 80, -112, -111, 81, -109, TarConstants.LF_GNUTYPE_SPARSE, 82, -110, -106, 86, 87, -105, 85, -107, -108, 84, -100, 92, 93, -99, 95, -97, -98, 94, 90, -102, -101, 91, -103, 89, TarConstants.LF_PAX_EXTENDED_HEADER_UC, -104, -120, 72, 73, -119, TarConstants.LF_GNUTYPE_LONGLINK, -117, -118, 74, 78, -114, -113, 79, -115, 77, TarConstants.LF_GNUTYPE_LONGNAME, -116, 68, -124, -123, 69, -121, 71, 70, -122, -126, 66, 67, -125, 65, -127, -128, 64};

    public static void main(String[] strArr) {
    }

    public static int calcCrc16(byte[] bArr) {
        return calcCrc16(bArr, 0, bArr.length - 2);
    }

    public static int calcCrc16(byte[] bArr, int i, int i2) {
        return calcCrc16(bArr, i, i2, 65535);
    }

    public static int calcCrc16(byte[] bArr, int i, int i2, int i3) {
        int i4 = 0;
        byte b = 0;
        int i5 = 0;
        while (i4 < i2) {
            int i6 = (i5 ^ bArr[i + i4]) & 255;
            int i7 = b ^ crc16_tab_h[i6];
            byte b2 = crc16_tab_l[i6];
            i4++;
            i5 = i7;
            b = b2;
        }
        return ((b & 255) << 8) | (i5 & 255 & 65535);
    }

    public static String getCRC(String str) {
        String strReplace = str.replace(" ", "");
        int length = strReplace.length();
        if (length % 2 != 0) {
            return "0000";
        }
        int i = length / 2;
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 < i) {
            int i3 = i2 + 1;
            bArr[i2] = (byte) Integer.valueOf(strReplace.substring(i2 * 2, i3 * 2), 16).intValue();
            i2 = i3;
        }
        return getCRC(bArr, i);
    }

    public static String getCRC(byte[] bArr, int i) {
        int i2 = 65535;
        for (int i3 = 0; i3 < i; i3++) {
            i2 ^= bArr[i3] & 255;
            for (int i4 = 0; i4 < 8; i4++) {
                i2 = (i2 & 1) != 0 ? (i2 >> 1) ^ 40961 : i2 >> 1;
            }
        }
        String upperCase = Integer.toHexString(i2).toUpperCase();
        if (upperCase.length() != 4) {
            upperCase = new StringBuffer("0000").replace(4 - upperCase.length(), 4, upperCase).toString();
        }
        return upperCase.substring(2, 4) + upperCase.substring(0, 2);
    }
}
