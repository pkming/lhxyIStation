package com.lianhexinye.m90.serialport;

import java.math.BigInteger;
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
public class CRC16Util {
    static byte[] crc16_tab_h = {0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, -128, 65, 0, -63, -127, 64, 1, -64, -128, 65, 1, -64, -128, 65, 0, -63, -127, 64};
    static byte[] crc16_tab_l = {0, -64, -63, 1, -61, 3, 2, -62, -58, 6, 7, -57, 5, -59, -60, 4, -52, 12, GreaterThanPtg.sid, -51, HSSFErrorConstants.ERROR_VALUE, -49, -50, NotEqualPtg.sid, 10, -54, -53, 11, -55, 9, 8, -56, -40, 24, AttrPtg.sid, -39, 27, -37, -38, 26, IntPtg.sid, -34, -33, NumberPtg.sid, -35, 29, 28, -36, 20, -44, -43, ParenthesisPtg.sid, -41, 23, MissingArgPtg.sid, -42, -46, UnaryPlusPtg.sid, UnaryMinusPtg.sid, -45, 17, -47, -48, 16, -16, TarConstants.LF_NORMAL, TarConstants.LF_LINK, -15, TarConstants.LF_CHR, -13, -14, TarConstants.LF_SYMLINK, TarConstants.LF_FIFO, -10, -9, TarConstants.LF_CONTIG, -11, TarConstants.LF_DIR, TarConstants.LF_BLK, -12, 60, -4, -3, 61, -1, 63, 62, -2, -6, Ref3DPtg.sid, Area3DPtg.sid, -5, 57, -7, -8, PaletteRecord.STANDARD_PALETTE_SIZE, 40, -24, -23, MemFuncPtg.sid, -21, 43, HSSFErrorConstants.ERROR_NA, -22, -18, 46, 47, -17, 45, -19, -20, 44, -28, 36, 37, -27, 39, -25, -26, 38, 34, -30, -29, 35, -31, 33, 32, -32, -96, 96, 97, -95, 99, -93, -94, 98, 102, -90, -89, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, -91, 101, 100, -92, 108, -84, -83, 109, -81, 111, 110, -82, -86, 106, 107, -85, 105, -87, -88, 104, TarConstants.LF_PAX_EXTENDED_HEADER_LC, -72, -71, 121, -69, 123, 122, -70, -66, 126, 127, -65, 125, -67, -68, 124, -76, 116, 117, -75, 119, -73, -74, 118, 114, -78, -77, 115, -79, 113, 112, -80, 80, -112, -111, 81, -109, TarConstants.LF_GNUTYPE_SPARSE, 82, -110, -106, 86, 87, -105, 85, -107, -108, 84, -100, 92, 93, -99, 95, -97, -98, 94, 90, -102, -101, 91, -103, 89, TarConstants.LF_PAX_EXTENDED_HEADER_UC, -104, -120, 72, 73, -119, TarConstants.LF_GNUTYPE_LONGLINK, -117, -118, 74, 78, -114, -113, 79, -115, 77, TarConstants.LF_GNUTYPE_LONGNAME, -116, 68, -124, -123, 69, -121, 71, 70, -122, -126, 66, 67, -125, 65, -127, -128, 64};

    public static int calcCrc16(byte[] bArr) {
        return calcCrc16(bArr, 0, bArr.length);
    }

    public static int calcCrc16(byte[] bArr, int i, int i2) {
        return calcCrc16(bArr, i, i2, 65535);
    }

    public static int calcCrc16(byte[] bArr, int i, int i2, int i3) {
        int i4 = (65280 & i3) >> 8;
        int i5 = i3 & 255;
        int i6 = 0;
        while (i6 < i2) {
            int i7 = (i5 ^ bArr[i + i6]) & 255;
            int i8 = i4 ^ crc16_tab_h[i7];
            i6++;
            i4 = crc16_tab_l[i7];
            i5 = i8;
        }
        return ((i5 & 255) << 8) | (i4 & 255 & 65535);
    }

    public static void main(String[] strArr) {
        int iCalcCrc16 = calcCrc16(new byte[]{2, 5, 0, 3, -1, 0});
        int iCalcCrc162 = calcCrc16(new byte[]{1, 6, 32, 1, 0, 1});
        byte[] byteArray = new BigInteger("010600000001", 16).toByteArray();
        byte[] byteArray2 = new BigInteger("010620010001", 16).toByteArray();
        System.out.println(String.format("0x%04x", Integer.valueOf(calcCrc16(byteArray))));
        System.out.println(String.format("0x%04x", Integer.valueOf(calcCrc16(byteArray2))));
        System.out.println(String.format("%04x", Integer.valueOf(iCalcCrc16)));
        System.out.println(String.format("0x%04x", Integer.valueOf(iCalcCrc162)));
    }
}
