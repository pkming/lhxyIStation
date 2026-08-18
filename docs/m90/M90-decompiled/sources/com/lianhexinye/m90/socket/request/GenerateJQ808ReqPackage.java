package com.lianhexinye.m90.socket.request;

import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import java.util.Random;

/* JADX INFO: loaded from: classes2.dex */
public class GenerateJQ808ReqPackage {
    private static long newsSerialNumber;
    private static Random rand = new Random();

    private static byte[] pkgHead(byte[] bArr, byte[] bArr2, byte[] bArr3, ReportInfoModel reportInfoModel) {
        byte[] bArr4 = new byte[12];
        bArr4[0] = bArr2[0];
        bArr4[1] = bArr2[1];
        bArr4[2] = bArr[0];
        bArr4[3] = bArr[1];
        byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalID());
        if (bArrStr2Bcd.length <= 6) {
            int i = 0;
            while (i < bArrStr2Bcd.length) {
                bArr4[i + 4] = bArrStr2Bcd[i];
                i++;
            }
            while (i < 6) {
                bArr4[i + 4] = 0;
                i++;
            }
        } else {
            for (int i2 = 0; i2 < 6; i2++) {
                bArr4[i2 + 4] = bArrStr2Bcd[i2];
            }
        }
        bArr4[10] = bArr3[0];
        bArr4[11] = bArr3[1];
        return bArr4;
    }

    public static byte[] generateReportStation(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[35];
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(Integer.parseInt(reportInfoModel.getLineNumber()));
            for (int i = 0; i < bArrIntToBytesBig.length; i++) {
                bArr[i] = bArrIntToBytesBig[i];
            }
            bArr[4] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getStatus()), 16);
            bArr[5] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getDirection()), 16);
            bArr[6] = 0;
            bArr[7] = 0;
            bArr[8] = 0;
            bArr[9] = 0;
            bArr[10] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getBusNo()), 16);
            bArr[11] = 0;
            String[] strArrSplit = reportInfoModel.getLatitude().trim().split("\\.");
            if (strArrSplit.length > 1) {
                byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0] + strArrSplit[1]));
                for (int i2 = 0; i2 < bArrIntToBytesBig2.length; i2++) {
                    bArr[i2 + 12] = bArrIntToBytesBig2[i2];
                }
            } else {
                byte[] bArrIntToBytesBig3 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0]));
                for (int i3 = 0; i3 < bArrIntToBytesBig3.length; i3++) {
                    bArr[i3 + 12] = bArrIntToBytesBig3[i3];
                }
            }
            String[] strArrSplit2 = reportInfoModel.getLongitude().trim().split("\\.");
            if (strArrSplit2.length > 1) {
                byte[] bArrIntToBytesBig4 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0] + strArrSplit2[1]));
                for (int i4 = 0; i4 < bArrIntToBytesBig4.length; i4++) {
                    bArr[i4 + 16] = bArrIntToBytesBig4[i4];
                }
            } else {
                byte[] bArrIntToBytesBig5 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0]));
                for (int i5 = 0; i5 < bArrIntToBytesBig5.length; i5++) {
                    bArr[i5 + 16] = bArrIntToBytesBig5[i5];
                }
            }
            bArr[20] = 0;
            bArr[21] = 0;
            byte[] bArrIntToBytesBig6 = JavaUtils.intToBytesBig(reportInfoModel.getSpeed());
            bArr[22] = bArrIntToBytesBig6[2];
            bArr[23] = bArrIntToBytesBig6[3];
            byte[] bArrIntToBytesBig7 = JavaUtils.intToBytesBig(reportInfoModel.getAngle());
            bArr[24] = bArrIntToBytesBig7[2];
            bArr[25] = bArrIntToBytesBig7[3];
            byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
            if (bArrStr2Bcd.length <= 5) {
                int i6 = 0;
                while (i6 < bArrStr2Bcd.length) {
                    bArr[i6 + 26] = bArrStr2Bcd[i6];
                    i6++;
                }
                while (i6 < 6) {
                    bArr[i6 + 26] = 0;
                    i6++;
                }
            } else {
                for (int i7 = 0; i7 < 6; i7++) {
                    bArr[i7 + 26] = bArrStr2Bcd[i7];
                }
            }
            bArr[32] = 0;
            bArr[33] = 0;
            bArr[34] = 0;
            byte[] bArrIntToBytesBig8 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(35), 16)}, new byte[]{11, 2}, new byte[]{bArrIntToBytesBig8[2], bArrIntToBytesBig8[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 35 + 3;
            byte[] bArr2 = new byte[length];
            int i8 = 0;
            char c = 0;
            int i9 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i8 < bArrPkgHead.length + 35 + 3) {
                if (c == 0) {
                    bArr2[i8] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i9];
                    bArr2[i8] = b2;
                    i9++;
                    if (i9 >= bArrPkgHead.length) {
                        i9 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i9];
                    bArr2[i8] = b2;
                    i9++;
                    if (i9 >= 35) {
                        i9 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i8] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i8] = 126;
                    c = 5;
                }
                b = i8 > 1 ? (byte) (b ^ b2) : b2;
                i8++;
            }
            LogUtils.d("GenerateJQ808ReqPackage", "生成报站send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private static byte[] escape(byte[] bArr) {
        int i;
        byte[] bArr2 = new byte[255];
        int length = bArr.length - 1;
        int i2 = 0;
        for (int i3 = 0; i3 < bArr.length; i3++) {
            if (i3 < length && i3 > 0 && bArr[i3] == 126) {
                bArr2[i2] = 125;
                i = i2 + 1;
                bArr2[i] = 2;
            } else if (i3 < length && i3 > 0 && bArr[i3] == 125) {
                bArr2[i2] = 125;
                i = i2 + 1;
                bArr2[i] = 1;
            } else {
                bArr2[i2] = bArr[i3];
                i2++;
            }
            i2 = i + 1;
        }
        byte[] bArr3 = new byte[i2];
        for (int i4 = 0; i4 < i2; i4++) {
            bArr3[i4] = bArr2[i4];
        }
        LogUtils.d("GenerateJQ808ReqPackage", "转义后的应答send:" + JavaUtils.bytesToHexString(bArr3, i2));
        return bArr3;
    }
}
