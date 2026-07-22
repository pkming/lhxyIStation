package com.lianhexinye.m90.socket.request;

import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import java.util.Random;
import org.apache.poi.hssf.record.formula.NotEqualPtg;
import org.apache.poi.hssf.record.formula.UnaryMinusPtg;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class Generate808ReqPackage {
    private static long newsSerialNumber;
    private static Random rand = new Random();
    private static int requestMsgSerialNumber = 0;
    public static int intercomBagSerialNumber = 0;

    private static byte[] pkgHead(byte[] bArr, byte[] bArr2, byte[] bArr3, ReportInfoModel reportInfoModel) {
        byte[] bArr4 = new byte[12];
        bArr4[0] = bArr2[0];
        bArr4[1] = bArr2[1];
        bArr4[2] = bArr[0];
        bArr4[3] = bArr[1];
        byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalID());
        if (bArrStr2Bcd.length <= 5) {
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

    public static byte[] generateHeartbeat(ReportInfoModel reportInfoModel) {
        byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(rand.nextInt(60000));
        byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(0), 16)}, new byte[]{0, 2}, new byte[]{bArrIntToBytesBig[2], bArrIntToBytesBig[3]}, reportInfoModel);
        int length = bArrPkgHead.length + 3;
        byte[] bArr = new byte[length];
        int i = 0;
        char c = 0;
        int i2 = 0;
        byte b = 0;
        byte b2 = 0;
        while (i < bArrPkgHead.length + 3) {
            if (c == 0) {
                bArr[i] = 126;
                c = 1;
            } else if (c == 1) {
                b2 = bArrPkgHead[i2];
                bArr[i] = b2;
                i2++;
                if (i2 >= bArrPkgHead.length) {
                    c = 2;
                    i2 = 0;
                }
            } else if (c == 2) {
                bArr[i] = b;
                c = 3;
            } else if (c == 3) {
                bArr[i] = 126;
                c = 4;
            }
            b = i > 1 ? (byte) (b ^ b2) : b2;
            i++;
        }
        LogUtils.d("Generate808ReqPackage", "生成心跳包send:" + JavaUtils.bytesToHexString(bArr, length));
        return escape(bArr);
    }

    public static byte[] registerDevice(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[46];
            bArr[0] = -85;
            bArr[1] = -32;
            bArr[2] = -83;
            bArr[3] = 12;
            bArr[4] = 67;
            bArr[5] = 84;
            bArr[6] = 84;
            bArr[7] = 73;
            bArr[8] = 84;
            bArr[9] = 77;
            bArr[10] = 68;
            bArr[11] = 86;
            bArr[12] = 82;
            bArr[13] = TarConstants.LF_BLK;
            bArr[14] = 71;
            bArr[15] = 0;
            bArr[16] = 0;
            bArr[17] = 0;
            bArr[18] = 0;
            bArr[19] = 0;
            bArr[20] = 0;
            bArr[21] = 0;
            bArr[22] = 0;
            bArr[23] = 0;
            bArr[24] = 0;
            bArr[25] = 0;
            bArr[26] = 0;
            bArr[27] = 0;
            bArr[28] = 0;
            byte[] bytes = reportInfoModel.getTerminalID().getBytes("gb2312");
            int i = 0;
            while (i < bytes.length) {
                bArr[i + 29] = bytes[i];
                i++;
            }
            while (i < 7) {
                bArr[i + 29] = 0;
                i++;
            }
            bArr[36] = 0;
            bArr[37] = -44;
            bArr[38] = -63;
            bArr[39] = 66;
            bArr[40] = TarConstants.LF_NORMAL;
            bArr[41] = TarConstants.LF_NORMAL;
            bArr[42] = TarConstants.LF_SYMLINK;
            bArr[43] = TarConstants.LF_CHR;
            bArr[44] = TarConstants.LF_DIR;
            bArr[45] = 0;
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(46), 16)}, new byte[]{1, 0}, new byte[]{bArrIntToBytesBig[2], bArrIntToBytesBig[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 46 + 3;
            byte[] bArr2 = new byte[length];
            int i2 = 0;
            char c = 0;
            int i3 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i2 < bArrPkgHead.length + 46 + 3) {
                if (c == 0) {
                    bArr2[i2] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i3];
                    bArr2[i2] = b2;
                    i3++;
                    if (i3 >= bArrPkgHead.length) {
                        i3 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i3];
                    bArr2[i2] = b2;
                    i3++;
                    if (i3 >= 46) {
                        i3 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i2] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i2] = 126;
                    c = 5;
                }
                b = i2 > 1 ? (byte) (b ^ b2) : b2;
                i2++;
            }
            LogUtils.d("Generate808ReqPackage", "设备注册send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateAuthority(ReportInfoModel reportInfoModel) {
        byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(reportInfoModel.getAuthorityData());
        byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(rand.nextInt(60000));
        byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(bArrHexStringToByteArray.length), 16)}, new byte[]{1, 2}, new byte[]{bArrIntToBytesBig[2], bArrIntToBytesBig[3]}, reportInfoModel);
        int length = bArrPkgHead.length + bArrHexStringToByteArray.length + 3;
        byte[] bArr = new byte[length];
        int i = 0;
        char c = 0;
        int i2 = 0;
        byte b = 0;
        byte b2 = 0;
        while (i < bArrHexStringToByteArray.length + bArrPkgHead.length + 3) {
            if (c == 0) {
                bArr[i] = 126;
                c = 1;
            } else if (c == 1) {
                b2 = bArrPkgHead[i2];
                bArr[i] = b2;
                i2++;
                if (i2 >= bArrPkgHead.length) {
                    c = 2;
                    i2 = 0;
                }
            } else if (c == 2) {
                b2 = bArrHexStringToByteArray[i2];
                bArr[i] = b2;
                i2++;
                if (i2 >= bArrHexStringToByteArray.length) {
                    i2 = 0;
                    c = 3;
                }
            } else if (c == 3) {
                bArr[i] = b;
                c = 4;
            } else if (c == 4) {
                bArr[i] = 126;
                c = 5;
            }
            b = i > 1 ? (byte) (b ^ b2) : b2;
            i++;
        }
        LogUtils.d("Generate808ReqPackage", "生成鉴权send:" + JavaUtils.bytesToHexString(bArr, length));
        return escape(bArr);
    }

    public static byte[] positionInfoReport(ReportInfoModel reportInfoModel) {
        int i;
        try {
            byte[] bArr = new byte[78];
            bArr[0] = 0;
            bArr[1] = 0;
            bArr[2] = 0;
            bArr[3] = 0;
            bArr[4] = 0;
            bArr[5] = 8;
            bArr[6] = 0;
            bArr[7] = UnaryMinusPtg.sid;
            String[] strArrSplit = reportInfoModel.getLatitude().trim().split("\\.");
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0] + strArrSplit[1]));
            for (int i2 = 0; i2 < bArrIntToBytesBig.length; i2++) {
                bArr[i2 + 8] = bArrIntToBytesBig[i2];
            }
            String[] strArrSplit2 = reportInfoModel.getLongitude().trim().split("\\.");
            byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0] + strArrSplit2[1]));
            for (int i3 = 0; i3 < bArrIntToBytesBig2.length; i3++) {
                bArr[i3 + 12] = bArrIntToBytesBig2[i3];
            }
            bArr[16] = 0;
            bArr[17] = 0;
            byte[] bArrIntToBytesBig3 = JavaUtils.intToBytesBig(reportInfoModel.getSpeed());
            bArr[18] = bArrIntToBytesBig3[2];
            bArr[19] = bArrIntToBytesBig3[3];
            byte[] bArrIntToBytesBig4 = JavaUtils.intToBytesBig(reportInfoModel.getAngle());
            bArr[20] = bArrIntToBytesBig4[2];
            bArr[21] = bArrIntToBytesBig4[3];
            byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
            if (bArrStr2Bcd.length <= 5) {
                int i4 = 0;
                while (i4 < bArrStr2Bcd.length) {
                    bArr[i4 + 22] = bArrStr2Bcd[i4];
                    i4++;
                }
                while (i4 < 6) {
                    bArr[i4 + 22] = 0;
                    i4++;
                }
            } else {
                for (int i5 = 0; i5 < 6; i5++) {
                    bArr[i5 + 22] = bArrStr2Bcd[i5];
                }
            }
            bArr[28] = 20;
            bArr[29] = TarConstants.LF_NORMAL;
            for (int i6 = 0; i6 < 4; i6++) {
                bArr[i6 + 30] = 0;
            }
            if (JavaUtils.isEmpty(reportInfoModel.getLineNumber())) {
                i = 0;
                while (i < 36) {
                    bArr[i + 34] = 0;
                    i++;
                }
            } else {
                i = 0;
                for (byte b : reportInfoModel.getLineNumber().getBytes("gb2312")) {
                    bArr[i + 34] = b;
                    i++;
                }
                while (i < 36) {
                    bArr[i + 34] = 0;
                    i++;
                }
            }
            bArr[i + 34] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getDirection() == 1 ? 0 : 1), 16);
            bArr[i + 35] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getBusNo()), 16);
            bArr[i + 36] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getVehicleStatus()), 16);
            bArr[i + 37] = 0;
            bArr[i + 38] = 0;
            bArr[i + 39] = 0;
            bArr[i + 40] = 0;
            bArr[i + 41] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getSatellites()), 16);
            byte[] bArrIntToBytesBig5 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(78), 16)}, new byte[]{2, 0}, new byte[]{bArrIntToBytesBig5[2], bArrIntToBytesBig5[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 78 + 3;
            byte[] bArr2 = new byte[length];
            int i7 = 0;
            char c = 0;
            int i8 = 0;
            byte b2 = 0;
            byte b3 = 0;
            while (i7 < bArrPkgHead.length + 78 + 3) {
                if (c == 0) {
                    bArr2[i7] = 126;
                    c = 1;
                } else if (c == 1) {
                    b3 = bArrPkgHead[i8];
                    bArr2[i7] = b3;
                    i8++;
                    if (i8 >= bArrPkgHead.length) {
                        i8 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b3 = bArr[i8];
                    bArr2[i7] = b3;
                    i8++;
                    if (i8 >= 78) {
                        i8 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i7] = b2;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i7] = 126;
                    c = 5;
                }
                b2 = i7 > 1 ? (byte) (b2 ^ b3) : b3;
                i7++;
            }
            LogUtils.d("Generate808ReqPackage", "生成位置信息汇报send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateReportStation(ReportInfoModel reportInfoModel) {
        byte[] bArr;
        try {
            if (reportInfoModel.getStatus() == 0) {
                bArr = new byte[104];
                bArr[0] = 6;
            } else {
                bArr = new byte[112];
                bArr[0] = 7;
            }
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(reportInfoModel.getVehicleStatus());
            int i = 0;
            while (i < bArrIntToBytesBig.length) {
                int i2 = i + 1;
                bArr[i2] = bArrIntToBytesBig[i];
                i = i2;
            }
            if (JavaUtils.isEmpty(reportInfoModel.getSiteCode())) {
                for (int i3 = 0; i3 < 36; i3++) {
                    bArr[i3 + 5] = 0;
                }
            } else {
                int i4 = 0;
                for (byte b : reportInfoModel.getSiteCode().getBytes("gb2312")) {
                    bArr[i4 + 5] = b;
                    i4++;
                }
                while (i4 < 36) {
                    bArr[i4 + 5] = 0;
                    i4++;
                }
            }
            bArr[41] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getPlannedTrips()), 16);
            bArr[42] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getBusNo()), 16);
            if (reportInfoModel.getDirection() == 1) {
                bArr[43] = 0;
            } else {
                bArr[43] = 1;
            }
            if (JavaUtils.isEmpty(reportInfoModel.getLineNumber())) {
                for (int i5 = 0; i5 < 36; i5++) {
                    bArr[i5 + 44] = 0;
                }
            } else {
                int i6 = 0;
                for (byte b2 : reportInfoModel.getLineNumber().getBytes("gb2312")) {
                    bArr[i6 + 44] = b2;
                    i6++;
                }
                while (i6 < 36) {
                    bArr[i6 + 44] = 0;
                    i6++;
                }
            }
            if (JavaUtils.isEmpty(reportInfoModel.getArrivalTime())) {
                for (int i7 = 0; i7 < 6; i7++) {
                    bArr[i7 + 80] = 0;
                }
            } else {
                byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getArrivalTime());
                int length = 6 - bArrStr2Bcd.length;
                int i8 = 0;
                while (i8 < length) {
                    bArr[i8 + 80] = 0;
                    i8++;
                }
                for (byte b3 : bArrStr2Bcd) {
                    bArr[i8 + 80] = b3;
                    i8++;
                }
            }
            if (JavaUtils.isEmpty(reportInfoModel.getOutboundTime())) {
                for (int i9 = 0; i9 < 6; i9++) {
                    bArr[i9 + 86] = 0;
                }
            } else {
                byte[] bArrStr2Bcd2 = JavaUtils.str2Bcd(reportInfoModel.getOutboundTime());
                int length2 = 6 - bArrStr2Bcd2.length;
                int i10 = 0;
                while (i10 < length2) {
                    bArr[i10 + 86] = 0;
                    i10++;
                }
                for (byte b4 : bArrStr2Bcd2) {
                    bArr[i10 + 86] = b4;
                    i10++;
                }
            }
            byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(reportInfoModel.getReportType());
            bArr[92] = bArrIntToBytesBig2[2];
            bArr[93] = bArrIntToBytesBig2[3];
            byte[] bArrIntToBytesBig3 = JavaUtils.intToBytesBig(reportInfoModel.getAngle());
            bArr[94] = bArrIntToBytesBig3[2];
            bArr[95] = bArrIntToBytesBig3[3];
            if (reportInfoModel.getStatus() == 0) {
                String[] strArrSplit = reportInfoModel.getLongitude().trim().split("\\.");
                if (strArrSplit.length > 1) {
                    byte[] bArrIntToBytesBig4 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0] + strArrSplit[1]));
                    for (int i11 = 0; i11 < bArrIntToBytesBig4.length; i11++) {
                        bArr[i11 + 96] = bArrIntToBytesBig4[i11];
                    }
                } else {
                    byte[] bArrIntToBytesBig5 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0]));
                    for (int i12 = 0; i12 < bArrIntToBytesBig5.length; i12++) {
                        bArr[i12 + 96] = bArrIntToBytesBig5[i12];
                    }
                }
                String[] strArrSplit2 = reportInfoModel.getLatitude().trim().split("\\.");
                if (strArrSplit2.length > 1) {
                    byte[] bArrIntToBytesBig6 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0] + strArrSplit2[1]));
                    for (int i13 = 0; i13 < bArrIntToBytesBig6.length; i13++) {
                        bArr[i13 + 100] = bArrIntToBytesBig6[i13];
                    }
                } else {
                    byte[] bArrIntToBytesBig7 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0]));
                    for (int i14 = 0; i14 < bArrIntToBytesBig7.length; i14++) {
                        bArr[i14 + 100] = bArrIntToBytesBig7[i14];
                    }
                }
            } else {
                for (int i15 = 0; i15 < 6; i15++) {
                    bArr[i15 + 96] = 0;
                }
                String[] strArrSplit3 = reportInfoModel.getLongitude().trim().split("\\.");
                if (strArrSplit3.length > 1) {
                    byte[] bArrIntToBytesBig8 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit3[0] + strArrSplit3[1]));
                    for (int i16 = 0; i16 < bArrIntToBytesBig8.length; i16++) {
                        bArr[i16 + 102] = bArrIntToBytesBig8[i16];
                    }
                } else {
                    byte[] bArrIntToBytesBig9 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit3[0]));
                    for (int i17 = 0; i17 < bArrIntToBytesBig9.length; i17++) {
                        bArr[i17 + 102] = bArrIntToBytesBig9[i17];
                    }
                }
                String[] strArrSplit4 = reportInfoModel.getLatitude().trim().split("\\.");
                if (strArrSplit4.length > 1) {
                    byte[] bArrIntToBytesBig10 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit4[0] + strArrSplit4[1]));
                    for (int i18 = 0; i18 < bArrIntToBytesBig10.length; i18++) {
                        bArr[i18 + 106] = bArrIntToBytesBig10[i18];
                    }
                } else {
                    byte[] bArrIntToBytesBig11 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit4[0]));
                    for (int i19 = 0; i19 < bArrIntToBytesBig11.length; i19++) {
                        bArr[i19 + 106] = bArrIntToBytesBig11[i19];
                    }
                }
                bArr[110] = 0;
                bArr[111] = 0;
            }
            byte[] bArrIntToBytesBig12 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(bArr.length), 16)}, new byte[]{9, 0}, new byte[]{bArrIntToBytesBig12[2], bArrIntToBytesBig12[3]}, reportInfoModel);
            int length3 = bArr.length + bArrPkgHead.length + 3;
            byte[] bArr2 = new byte[length3];
            int i20 = 0;
            char c = 0;
            int i21 = 0;
            byte b5 = 0;
            byte b6 = 0;
            while (i20 < bArr.length + bArrPkgHead.length + 3) {
                if (c == 0) {
                    bArr2[i20] = 126;
                    c = 1;
                } else if (c == 1) {
                    b6 = bArrPkgHead[i21];
                    bArr2[i20] = b6;
                    i21++;
                    if (i21 >= bArrPkgHead.length) {
                        i21 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b6 = bArr[i21];
                    bArr2[i20] = b6;
                    i21++;
                    if (i21 >= bArr.length) {
                        i21 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i20] = b5;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i20] = 126;
                    c = 5;
                }
                b5 = i20 > 1 ? (byte) (b5 ^ b6) : b6;
                i20++;
            }
            LogUtils.d("Generate808ReqPackage", "生成报站send:" + JavaUtils.bytesToHexString(bArr2, length3));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateTerminalSelfCheck(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[43];
            bArr[0] = JavaUtils.hexStringToByteArray(reportInfoModel.getTransmissionType())[0];
            bArr[1] = JavaUtils.hexStringToByteArray(reportInfoModel.getLEDInnerStatus())[0];
            bArr[2] = JavaUtils.hexStringToByteArray(reportInfoModel.getLEDOutStatus())[0];
            bArr[3] = JavaUtils.hexStringToByteArray(reportInfoModel.getGPSDeviceStatus())[0];
            bArr[4] = JavaUtils.hexStringToByteArray(reportInfoModel.getPOSStatus())[0];
            for (int i = 0; i < 32; i++) {
                bArr[i + 5] = -1;
            }
            if (JavaUtils.isEmpty(reportInfoModel.getTerminalDate())) {
                for (int i2 = 0; i2 < 6; i2++) {
                    bArr[i2 + 37] = 0;
                }
            } else {
                byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
                for (int i3 = 0; i3 < bArrStr2Bcd.length; i3++) {
                    bArr[i3 + 37] = bArrStr2Bcd[i3];
                }
            }
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(43), 16)}, new byte[]{9, 0}, new byte[]{bArrIntToBytesBig[2], bArrIntToBytesBig[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 43 + 3;
            byte[] bArr2 = new byte[length];
            int i4 = 0;
            char c = 0;
            int i5 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i4 < bArrPkgHead.length + 43 + 3) {
                if (c == 0) {
                    bArr2[i4] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i5];
                    bArr2[i4] = b2;
                    i5++;
                    if (i5 >= bArrPkgHead.length) {
                        i5 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i5];
                    bArr2[i4] = b2;
                    i5++;
                    if (i5 >= 43) {
                        i5 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i4] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i4] = 126;
                    c = 5;
                }
                b = i4 > 1 ? (byte) (b ^ b2) : b2;
                i4++;
            }
            LogUtils.d("Generate808ReqPackage", "生成终端自检数据send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateDriverLoginLogout(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[87];
            bArr[0] = JavaUtils.hexStringToByteArray(reportInfoModel.getTransmissionType())[0];
            byte[] bytes = reportInfoModel.getDeviceVersion().getBytes("gb2312");
            if (bytes.length <= 4) {
                int i = 0;
                while (i < bytes.length) {
                    int i2 = i + 1;
                    bArr[i2] = bytes[i];
                    i = i2;
                }
                while (i < 4) {
                    i++;
                    bArr[i] = 0;
                }
            } else {
                int i3 = 0;
                while (i3 < 4) {
                    int i4 = i3 + 1;
                    bArr[i4] = bytes[i3];
                    i3 = i4;
                }
            }
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(reportInfoModel.getCardNo());
            if (bArrHexStringToByteArray.length <= 36) {
                int i5 = 0;
                while (i5 < bArrHexStringToByteArray.length) {
                    bArr[i5 + 5] = bArrHexStringToByteArray[i5];
                    i5++;
                }
                while (i5 < 36) {
                    bArr[i5 + 5] = 0;
                    i5++;
                }
            } else {
                for (int i6 = 0; i6 < 36; i6++) {
                    bArr[i6 + 5] = bArrHexStringToByteArray[i6];
                }
            }
            byte[] bytes2 = reportInfoModel.getDevicePassword().getBytes("gb2312");
            if (bytes2.length <= 10) {
                int i7 = 0;
                while (i7 < bytes2.length) {
                    bArr[i7 + 41] = bytes2[i7];
                    i7++;
                }
                while (i7 < 10) {
                    bArr[i7 + 41] = 0;
                    i7++;
                }
            } else {
                for (int i8 = 0; i8 < 10; i8++) {
                    bArr[i8 + 41] = bytes2[i8];
                }
            }
            String[] strArrSplit = reportInfoModel.getLongitude().trim().split("\\.");
            if (strArrSplit.length > 1) {
                byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0] + strArrSplit[1]));
                for (int i9 = 0; i9 < bArrIntToBytesBig.length; i9++) {
                    bArr[i9 + 51] = bArrIntToBytesBig[i9];
                }
            } else {
                byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0]));
                for (int i10 = 0; i10 < bArrIntToBytesBig2.length; i10++) {
                    bArr[i10 + 51] = bArrIntToBytesBig2[i10];
                }
            }
            String[] strArrSplit2 = reportInfoModel.getLatitude().trim().split("\\.");
            if (strArrSplit2.length > 1) {
                byte[] bArrIntToBytesBig3 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0] + strArrSplit2[1]));
                for (int i11 = 0; i11 < bArrIntToBytesBig3.length; i11++) {
                    bArr[i11 + 55] = bArrIntToBytesBig3[i11];
                }
            } else {
                byte[] bArrIntToBytesBig4 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0]));
                for (int i12 = 0; i12 < bArrIntToBytesBig4.length; i12++) {
                    bArr[i12 + 55] = bArrIntToBytesBig4[i12];
                }
            }
            if (JavaUtils.isEmpty(reportInfoModel.getTerminalDate())) {
                for (int i13 = 0; i13 < 6; i13++) {
                    bArr[i13 + 59] = 0;
                }
            } else {
                byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
                for (int i14 = 0; i14 < bArrStr2Bcd.length; i14++) {
                    bArr[i14 + 59] = bArrStr2Bcd[i14];
                }
            }
            bArr[65] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getdStatus()), 16);
            bArr[66] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getDeviceSignMode()), 16);
            byte[] bytes3 = reportInfoModel.getSimCardIccid().getBytes("gb2312");
            if (bytes3.length <= 20) {
                int i15 = 0;
                while (i15 < bytes3.length) {
                    bArr[i15 + 67] = bytes3[i15];
                    i15++;
                }
                while (i15 < 20) {
                    bArr[i15 + 67] = 0;
                    i15++;
                }
            } else {
                for (int i16 = 0; i16 < 20; i16++) {
                    bArr[i16 + 67] = bytes3[i16];
                }
            }
            byte[] bArrIntToBytesBig5 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(87), 16)}, new byte[]{9, 0}, new byte[]{bArrIntToBytesBig5[2], bArrIntToBytesBig5[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 87 + 3;
            byte[] bArr2 = new byte[length];
            int i17 = 0;
            char c = 0;
            int i18 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i17 < bArrPkgHead.length + 87 + 3) {
                if (c == 0) {
                    bArr2[i17] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i18];
                    bArr2[i17] = b2;
                    i18++;
                    if (i18 >= bArrPkgHead.length) {
                        c = 2;
                        i18 = 0;
                    }
                } else if (c == 2) {
                    b2 = bArr[i18];
                    bArr2[i17] = b2;
                    i18++;
                    if (i18 >= 87) {
                        i18 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i17] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i17] = 126;
                    c = 5;
                }
                b = i17 > 1 ? (byte) (b ^ b2) : b2;
                i17++;
            }
            LogUtils.d("Generate808ReqPackage", "生成驾驶员登录注销数据send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateCrossInfo(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[95];
            bArr[0] = JavaUtils.hexStringToByteArray(reportInfoModel.getTransmissionType())[0];
            if (JavaUtils.isEmpty(reportInfoModel.getLineNumber())) {
                int i = 0;
                while (i < 36) {
                    i++;
                    bArr[i] = 0;
                }
            } else {
                int i2 = 0;
                for (byte b : reportInfoModel.getLineNumber().getBytes("gb2312")) {
                    i2++;
                    bArr[i2] = b;
                }
                while (i2 < 36) {
                    i2++;
                    bArr[i2] = 0;
                }
            }
            if (JavaUtils.isEmpty(reportInfoModel.getCrossNumber())) {
                for (int i3 = 0; i3 < 36; i3++) {
                    bArr[i3 + 37] = 0;
                }
            } else {
                int i4 = 0;
                for (byte b2 : reportInfoModel.getCrossNumber().getBytes("gb2312")) {
                    bArr[i4 + 37] = b2;
                    i4++;
                }
                while (i4 < 36) {
                    bArr[i4 + 37] = 0;
                    i4++;
                }
            }
            if (JavaUtils.isEmpty(reportInfoModel.getArrivalTime())) {
                for (int i5 = 0; i5 < 6; i5++) {
                    bArr[i5 + 73] = 0;
                }
            } else {
                byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getArrivalTime());
                int length = 6 - bArrStr2Bcd.length;
                int i6 = 0;
                while (i6 < length) {
                    bArr[i6 + 73] = 0;
                    i6++;
                }
                for (byte b3 : bArrStr2Bcd) {
                    bArr[i6 + 73] = b3;
                    i6++;
                }
            }
            if (JavaUtils.isEmpty(reportInfoModel.getOutboundTime())) {
                for (int i7 = 0; i7 < 6; i7++) {
                    bArr[i7 + 79] = 0;
                }
            } else {
                byte[] bArrStr2Bcd2 = JavaUtils.str2Bcd(reportInfoModel.getOutboundTime());
                int length2 = 6 - bArrStr2Bcd2.length;
                int i8 = 0;
                while (i8 < length2) {
                    bArr[i8 + 79] = 0;
                    i8++;
                }
                for (byte b4 : bArrStr2Bcd2) {
                    bArr[i8 + 79] = b4;
                    i8++;
                }
            }
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(reportInfoModel.getAngle());
            bArr[85] = bArrIntToBytesBig[2];
            bArr[86] = bArrIntToBytesBig[3];
            String[] strArrSplit = reportInfoModel.getLongitude().trim().split("\\.");
            if (strArrSplit.length > 1) {
                byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0] + strArrSplit[1]));
                for (int i9 = 0; i9 < bArrIntToBytesBig2.length; i9++) {
                    bArr[i9 + 87] = bArrIntToBytesBig2[i9];
                }
            } else {
                byte[] bArrIntToBytesBig3 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0]));
                for (int i10 = 0; i10 < bArrIntToBytesBig3.length; i10++) {
                    bArr[i10 + 87] = bArrIntToBytesBig3[i10];
                }
            }
            String[] strArrSplit2 = reportInfoModel.getLatitude().trim().split("\\.");
            if (strArrSplit2.length > 1) {
                byte[] bArrIntToBytesBig4 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0] + strArrSplit2[1]));
                for (int i11 = 0; i11 < bArrIntToBytesBig4.length; i11++) {
                    bArr[i11 + 91] = bArrIntToBytesBig4[i11];
                }
            } else {
                byte[] bArrIntToBytesBig5 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0]));
                for (int i12 = 0; i12 < bArrIntToBytesBig5.length; i12++) {
                    bArr[i12 + 91] = bArrIntToBytesBig5[i12];
                }
            }
            byte[] bArrIntToBytesBig6 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(95), 16)}, new byte[]{9, 0}, new byte[]{bArrIntToBytesBig6[2], bArrIntToBytesBig6[3]}, reportInfoModel);
            int length3 = bArrPkgHead.length + 95 + 3;
            byte[] bArr2 = new byte[length3];
            int i13 = 0;
            char c = 0;
            int i14 = 0;
            byte b5 = 0;
            byte b6 = 0;
            while (i13 < bArrPkgHead.length + 95 + 3) {
                if (c == 0) {
                    bArr2[i13] = 126;
                    c = 1;
                } else if (c == 1) {
                    b6 = bArrPkgHead[i14];
                    bArr2[i13] = b6;
                    i14++;
                    if (i14 >= bArrPkgHead.length) {
                        i14 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b6 = bArr[i14];
                    bArr2[i13] = b6;
                    i14++;
                    if (i14 >= 95) {
                        i14 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i13] = b5;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i13] = 126;
                    c = 5;
                }
                b5 = i13 > 1 ? (byte) (b5 ^ b6) : b6;
                i13++;
            }
            LogUtils.d("Generate808ReqPackage", "生成车辆进出路口信息send:" + JavaUtils.bytesToHexString(bArr2, length3));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateBasicPriceSetResponse(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = {JavaUtils.hexStringToByteArray(reportInfoModel.getTransmissionType())[0], (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getResult()), 16)};
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(2), 16)}, new byte[]{9, 0}, new byte[]{bArrIntToBytesBig[2], bArrIntToBytesBig[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 2 + 3;
            byte[] bArr2 = new byte[length];
            int i = 0;
            char c = 0;
            int i2 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i < bArrPkgHead.length + 2 + 3) {
                if (c == 0) {
                    bArr2[i] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i2];
                    bArr2[i] = b2;
                    i2++;
                    if (i2 >= bArrPkgHead.length) {
                        c = 2;
                        i2 = 0;
                    }
                } else if (c == 2) {
                    b2 = bArr[i2];
                    bArr2[i] = b2;
                    i2++;
                    if (i2 >= 2) {
                        i2 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i] = 126;
                    c = 5;
                }
                b = i > 1 ? (byte) (b ^ b2) : b2;
                i++;
            }
            LogUtils.d("Generate808ReqPackage", "基准票价设置应答send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateProfessionRequest(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[88];
            bArr[0] = JavaUtils.hexStringToByteArray(reportInfoModel.getTransmissionType())[0];
            if (JavaUtils.isEmpty(reportInfoModel.getLineNumber())) {
                int i = 0;
                while (i < 36) {
                    i++;
                    bArr[i] = 0;
                }
            } else {
                int i2 = 0;
                for (byte b : reportInfoModel.getLineNumber().getBytes("gb2312")) {
                    i2++;
                    bArr[i2] = b;
                }
                while (i2 < 36) {
                    i2++;
                    bArr[i2] = 0;
                }
            }
            if (JavaUtils.isEmpty(reportInfoModel.getCardNo())) {
                for (int i3 = 0; i3 < 36; i3++) {
                    bArr[i3 + 37] = 0;
                }
            } else {
                int i4 = 0;
                for (byte b2 : JavaUtils.hexStringToByteArray(reportInfoModel.getCardNo())) {
                    bArr[i4 + 37] = b2;
                    i4++;
                }
                while (i4 < 36) {
                    bArr[i4 + 37] = 0;
                    i4++;
                }
            }
            bArr[73] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getProfessionRequestType()), 16);
            if (JavaUtils.isEmpty(reportInfoModel.getTerminalDate())) {
                for (int i5 = 0; i5 < 6; i5++) {
                    bArr[i5 + 74] = 0;
                }
            } else {
                byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
                for (int i6 = 0; i6 < bArrStr2Bcd.length; i6++) {
                    bArr[i6 + 74] = bArrStr2Bcd[i6];
                }
            }
            String[] strArrSplit = reportInfoModel.getLongitude().trim().split("\\.");
            if (strArrSplit.length > 1) {
                byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0] + strArrSplit[1]));
                for (int i7 = 0; i7 < bArrIntToBytesBig.length; i7++) {
                    bArr[i7 + 80] = bArrIntToBytesBig[i7];
                }
            } else {
                byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0]));
                for (int i8 = 0; i8 < bArrIntToBytesBig2.length; i8++) {
                    bArr[i8 + 80] = bArrIntToBytesBig2[i8];
                }
            }
            String[] strArrSplit2 = reportInfoModel.getLatitude().trim().split("\\.");
            if (strArrSplit2.length > 1) {
                byte[] bArrIntToBytesBig3 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0] + strArrSplit2[1]));
                for (int i9 = 0; i9 < bArrIntToBytesBig3.length; i9++) {
                    bArr[i9 + 84] = bArrIntToBytesBig3[i9];
                }
            } else {
                byte[] bArrIntToBytesBig4 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0]));
                for (int i10 = 0; i10 < bArrIntToBytesBig4.length; i10++) {
                    bArr[i10 + 84] = bArrIntToBytesBig4[i10];
                }
            }
            byte[] bArrIntToBytesBig5 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(88), 16)}, new byte[]{9, 0}, new byte[]{bArrIntToBytesBig5[2], bArrIntToBytesBig5[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 88 + 3;
            byte[] bArr2 = new byte[length];
            int i11 = 0;
            char c = 0;
            int i12 = 0;
            byte b3 = 0;
            byte b4 = 0;
            while (i11 < bArrPkgHead.length + 88 + 3) {
                if (c == 0) {
                    bArr2[i11] = 126;
                    c = 1;
                } else if (c == 1) {
                    b4 = bArrPkgHead[i12];
                    bArr2[i11] = b4;
                    i12++;
                    if (i12 >= bArrPkgHead.length) {
                        i12 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b4 = bArr[i12];
                    bArr2[i11] = b4;
                    i12++;
                    if (i12 >= 88) {
                        i12 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i11] = b3;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i11] = 126;
                    c = 5;
                }
                b3 = i11 > 1 ? (byte) (b3 ^ b4) : b4;
                i11++;
            }
            LogUtils.d("Generate808ReqPackage", "生成业务请求send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateOverspeedInfo(ReportInfoModel reportInfoModel) {
        byte[] bArr = null;
        try {
            int i = Integer.parseInt(Integer.toHexString(reportInfoModel.getOverSpeedType()), 16);
            if (i == 20 || i == 21) {
                bArr = new byte[155];
            } else if (i == 22) {
                bArr = new byte[154];
            }
            bArr[0] = JavaUtils.hexStringToByteArray(reportInfoModel.getTransmissionType())[0];
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(reportInfoModel.getOverSpeedInfoType());
            bArr[1] = bArrIntToBytesBig[2];
            bArr[2] = bArrIntToBytesBig[3];
            bArr[3] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getOverSpeedType()), 16);
            byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(reportInfoModel.getDataLength());
            bArr[4] = bArrIntToBytesBig2[2];
            bArr[5] = bArrIntToBytesBig2[3];
            if (JavaUtils.isEmpty(reportInfoModel.getLineNumber())) {
                for (int i2 = 0; i2 < 36; i2++) {
                    bArr[i2 + 6] = 0;
                }
            } else {
                int i3 = 0;
                for (byte b : reportInfoModel.getLineNumber().getBytes("gb2312")) {
                    bArr[i3 + 6] = b;
                    i3++;
                }
                for (int i4 = i3; i4 < 36; i4++) {
                    bArr[i4 + 6] = 0;
                }
            }
            if (i == 20 || i == 21) {
                if (JavaUtils.isEmpty(reportInfoModel.getSiteCode())) {
                    for (int i5 = 0; i5 < 36; i5++) {
                        bArr[i5 + 42] = 0;
                    }
                } else {
                    int i6 = 0;
                    for (byte b2 : reportInfoModel.getSiteCode().getBytes("gb2312")) {
                        bArr[i6 + 42] = b2;
                        i6++;
                    }
                    while (i6 < 36) {
                        bArr[i6 + 42] = 0;
                        i6++;
                    }
                }
            } else if (i == 22) {
                if (JavaUtils.isEmpty(reportInfoModel.getCrossNumber())) {
                    for (int i7 = 0; i7 < 36; i7++) {
                        bArr[i7 + 42] = 0;
                    }
                } else {
                    int i8 = 0;
                    for (byte b3 : reportInfoModel.getCrossNumber().getBytes("gb2312")) {
                        bArr[i8 + 42] = b3;
                        i8++;
                    }
                    while (i8 < 36) {
                        bArr[i8 + 42] = 0;
                        i8++;
                    }
                }
            }
            if (JavaUtils.isEmpty(reportInfoModel.getTerminalDate())) {
                for (int i9 = 0; i9 < 6; i9++) {
                    bArr[i9 + 78] = 0;
                }
            } else {
                byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
                for (int i10 = 0; i10 < bArrStr2Bcd.length; i10++) {
                    bArr[i10 + 78] = bArrStr2Bcd[i10];
                }
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(reportInfoModel.getContinueTime());
            bArr[84] = bArrLongToBytes_Little[1];
            bArr[85] = bArrLongToBytes_Little[0];
            byte[] bArrIntToBytesBig3 = JavaUtils.intToBytesBig(reportInfoModel.getHighSpeed());
            bArr[86] = bArrIntToBytesBig3[2];
            bArr[87] = bArrIntToBytesBig3[3];
            String[] strArrSplit = reportInfoModel.getLongitude().trim().split("\\.");
            if (strArrSplit.length > 1) {
                byte[] bArrIntToBytesBig4 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0] + strArrSplit[1]));
                for (int i11 = 0; i11 < bArrIntToBytesBig4.length; i11++) {
                    bArr[i11 + 88] = bArrIntToBytesBig4[i11];
                }
            } else {
                byte[] bArrIntToBytesBig5 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0]));
                for (int i12 = 0; i12 < bArrIntToBytesBig5.length; i12++) {
                    bArr[i12 + 88] = bArrIntToBytesBig5[i12];
                }
            }
            String[] strArrSplit2 = reportInfoModel.getLatitude().trim().split("\\.");
            if (strArrSplit2.length > 1) {
                byte[] bArrIntToBytesBig6 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0] + strArrSplit2[1]));
                for (int i13 = 0; i13 < bArrIntToBytesBig6.length; i13++) {
                    bArr[i13 + 92] = bArrIntToBytesBig6[i13];
                }
            } else {
                byte[] bArrIntToBytesBig7 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0]));
                for (int i14 = 0; i14 < bArrIntToBytesBig7.length; i14++) {
                    bArr[i14 + 92] = bArrIntToBytesBig7[i14];
                }
            }
            if (bArr[3] == 20 || bArr[3] == 21) {
                byte[] bArrIntToBytesBig8 = JavaUtils.intToBytesBig(reportInfoModel.getInLimitSpeed());
                bArr[96] = bArrIntToBytesBig8[2];
                bArr[97] = bArrIntToBytesBig8[3];
                byte[] bArrIntToBytesBig9 = JavaUtils.intToBytesBig(reportInfoModel.getOutLimitSpeed());
                bArr[98] = bArrIntToBytesBig9[2];
                bArr[99] = bArrIntToBytesBig9[3];
                bArr[100] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getBusNo()), 16);
                byte[] bArrIntToBytesBig10 = JavaUtils.intToBytesBig(reportInfoModel.getAverageSpeed());
                bArr[101] = bArrIntToBytesBig10[2];
                bArr[102] = bArrIntToBytesBig10[3];
                byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(reportInfoModel.getCardNo());
                if (bArrHexStringToByteArray.length <= 36) {
                    int i15 = 0;
                    while (i15 < bArrHexStringToByteArray.length) {
                        bArr[i15 + 103] = bArrHexStringToByteArray[i15];
                        i15++;
                    }
                    while (i15 < 36) {
                        bArr[i15 + 103] = 0;
                        i15++;
                    }
                } else {
                    for (int i16 = 0; i16 < 36; i16++) {
                        bArr[i16 + 103] = bArrHexStringToByteArray[i16];
                    }
                }
                for (int i17 = 0; i17 < 16; i17++) {
                    bArr[i17 + 139] = 0;
                }
            } else if (bArr[3] == 22) {
                byte[] bArrIntToBytesBig11 = JavaUtils.intToBytesBig(reportInfoModel.getCrossLimitSpeed());
                bArr[96] = bArrIntToBytesBig11[2];
                bArr[97] = bArrIntToBytesBig11[3];
                bArr[98] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getCrossType()), 16);
                bArr[99] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getCrossNo()), 16);
                byte[] bArrIntToBytesBig12 = JavaUtils.intToBytesBig(reportInfoModel.getAverageSpeed());
                bArr[100] = bArrIntToBytesBig12[2];
                bArr[101] = bArrIntToBytesBig12[3];
                byte[] bArrHexStringToByteArray2 = JavaUtils.hexStringToByteArray(reportInfoModel.getCardNo());
                if (bArrHexStringToByteArray2.length <= 36) {
                    int i18 = 0;
                    while (i18 < bArrHexStringToByteArray2.length) {
                        bArr[i18 + 102] = bArrHexStringToByteArray2[i18];
                        i18++;
                    }
                    while (i18 < 36) {
                        bArr[i18 + 102] = 0;
                        i18++;
                    }
                } else {
                    for (int i19 = 0; i19 < 36; i19++) {
                        bArr[i19 + 102] = bArrHexStringToByteArray2[i19];
                    }
                }
                for (int i20 = 0; i20 < 16; i20++) {
                    bArr[i20 + 138] = 0;
                }
            }
            byte[] bArrIntToBytesBig13 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(bArr.length), 16)}, new byte[]{9, 0}, new byte[]{bArrIntToBytesBig13[2], bArrIntToBytesBig13[3]}, reportInfoModel);
            int length = bArr.length + bArrPkgHead.length + 3;
            byte[] bArr2 = new byte[length];
            int i21 = 0;
            char c = 0;
            int i22 = 0;
            byte b4 = 0;
            byte b5 = 0;
            while (i21 < bArr.length + bArrPkgHead.length + 3) {
                if (c == 0) {
                    bArr2[i21] = 126;
                    c = 1;
                } else if (c == 1) {
                    b5 = bArrPkgHead[i22];
                    bArr2[i21] = b5;
                    i22++;
                    if (i22 >= bArrPkgHead.length) {
                        i22 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b5 = bArr[i22];
                    bArr2[i21] = b5;
                    i22++;
                    if (i22 >= bArr.length) {
                        i22 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i21] = b4;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i21] = 126;
                    c = 5;
                }
                b4 = i21 > 1 ? (byte) (b4 ^ b5) : b5;
                i21++;
            }
            LogUtils.d("Generate808ReqPackage", "生成超速报警send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateGeneralResponse(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(reportInfoModel.getSerialNumber());
            byte[] bArrHexStringToByteArray2 = JavaUtils.hexStringToByteArray(reportInfoModel.getResponseID());
            byte[] bArr = {bArrHexStringToByteArray[0], bArrHexStringToByteArray[1], bArrHexStringToByteArray2[0], bArrHexStringToByteArray2[1], (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getResult()), 16)};
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(5), 16)}, new byte[]{0, 1}, new byte[]{bArrIntToBytesBig[2], bArrIntToBytesBig[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 5 + 3;
            byte[] bArr2 = new byte[length];
            int i = 0;
            char c = 0;
            int i2 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i < bArrPkgHead.length + 5 + 3) {
                if (c == 0) {
                    bArr2[i] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i2];
                    bArr2[i] = b2;
                    i2++;
                    if (i2 >= bArrPkgHead.length) {
                        i2 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i2];
                    bArr2[i] = b2;
                    i2++;
                    if (i2 >= 5) {
                        i2 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i] = 126;
                    c = 5;
                }
                b = i > 1 ? (byte) (b ^ b2) : b2;
                i++;
            }
            LogUtils.d("Generate808ReqPackage", "生成文本信息应答send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateDriverAttendance(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[16];
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(Integer.parseInt(reportInfoModel.getLineNumber()));
            for (int i = 0; i < bArrIntToBytesBig.length; i++) {
                bArr[i] = bArrIntToBytesBig[i];
            }
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(reportInfoModel.getCardNo());
            if (bArrHexStringToByteArray.length <= 3) {
                int i2 = 0;
                while (i2 < bArrHexStringToByteArray.length) {
                    bArr[i2 + 4] = bArrHexStringToByteArray[i2];
                    i2++;
                }
                while (i2 < 4) {
                    bArr[i2 + 4] = 0;
                    i2++;
                }
            } else {
                for (int i3 = 0; i3 < 4; i3++) {
                    bArr[i3 + 4] = bArrHexStringToByteArray[i3];
                }
            }
            byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
            if (bArrStr2Bcd.length <= 5) {
                int i4 = 0;
                while (i4 < bArrStr2Bcd.length) {
                    bArr[i4 + 8] = bArrStr2Bcd[i4];
                    i4++;
                }
                while (i4 < 6) {
                    bArr[i4 + 8] = 0;
                    i4++;
                }
            } else {
                for (int i5 = 0; i5 < 6; i5++) {
                    bArr[i5 + 8] = bArrStr2Bcd[i5];
                }
            }
            bArr[14] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getdStatus()), 16);
            bArr[15] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getType()), 16);
            byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(16), 16)}, new byte[]{11, 5}, new byte[]{bArrIntToBytesBig2[2], bArrIntToBytesBig2[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 16 + 3;
            byte[] bArr2 = new byte[length];
            int i6 = 0;
            char c = 0;
            int i7 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i6 < bArrPkgHead.length + 16 + 3) {
                if (c == 0) {
                    bArr2[i6] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i7];
                    bArr2[i6] = b2;
                    i7++;
                    if (i7 >= bArrPkgHead.length) {
                        i7 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i7];
                    bArr2[i6] = b2;
                    i7++;
                    if (i7 >= 16) {
                        i7 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i6] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i6] = 126;
                    c = 5;
                }
                b = i6 > 1 ? (byte) (b ^ b2) : b2;
                i6++;
            }
            LogUtils.d("Generate808ReqPackage", "生成司机考勤send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateCarOperationResponse(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[22];
            bArr[0] = JavaUtils.hexStringToByteArray(reportInfoModel.getTransmissionType())[0];
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(reportInfoModel.getSerialNumber());
            bArr[1] = 0;
            bArr[2] = 0;
            bArr[3] = bArrHexStringToByteArray[0];
            bArr[4] = bArrHexStringToByteArray[1];
            byte[] bArrHexStringToByteArray2 = JavaUtils.hexStringToByteArray(reportInfoModel.getCarNumber());
            for (int i = 0; i < bArrHexStringToByteArray2.length; i++) {
                bArr[i + 5] = bArrHexStringToByteArray2[i];
            }
            if (JavaUtils.isEmpty(reportInfoModel.getDepartureTime())) {
                for (int i2 = 0; i2 < 6; i2++) {
                    bArr[i2 + 15] = 0;
                }
            } else {
                byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getDepartureTime());
                for (int i3 = 0; i3 < bArrStr2Bcd.length; i3++) {
                    bArr[i3 + 15] = bArrStr2Bcd[i3];
                }
            }
            bArr[21] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getResult()), 16);
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(22), 16)}, new byte[]{9, 0}, new byte[]{bArrHexStringToByteArray[0], bArrHexStringToByteArray[1]}, reportInfoModel);
            int length = bArrPkgHead.length + 22 + 3;
            byte[] bArr2 = new byte[length];
            int i4 = 0;
            char c = 0;
            int i5 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i4 < bArrPkgHead.length + 22 + 3) {
                if (c == 0) {
                    bArr2[i4] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i5];
                    bArr2[i4] = b2;
                    i5++;
                    if (i5 >= bArrPkgHead.length) {
                        i5 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i5];
                    bArr2[i4] = b2;
                    i5++;
                    if (i5 >= 22) {
                        i5 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i4] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i4] = 126;
                    c = 5;
                }
                b = i4 > 1 ? (byte) (b ^ b2) : b2;
                i4++;
            }
            LogUtils.d("Generate808ReqPackage", "生成车辆运营应答send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateViolationInfo(ReportInfoModel reportInfoModel) {
        try {
            byte[] bytes = reportInfoModel.getPromptInfo().getBytes("gb2312");
            int length = bytes.length + 31;
            byte[] bArr = new byte[length];
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(Integer.parseInt(reportInfoModel.getLineNumber()));
            for (int i = 0; i < bArrIntToBytesBig.length; i++) {
                bArr[i] = bArrIntToBytesBig[i];
            }
            bArr[4] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getOverSpeed()), 16);
            byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(reportInfoModel.getSpeed());
            bArr[5] = bArrIntToBytesBig2[2];
            bArr[6] = bArrIntToBytesBig2[3];
            byte[] bArrIntToBytesBig3 = JavaUtils.intToBytesBig(reportInfoModel.getSpeedLimit());
            bArr[7] = bArrIntToBytesBig3[2];
            bArr[8] = bArrIntToBytesBig3[3];
            String[] strArrSplit = reportInfoModel.getLatitude().trim().split("\\.");
            byte[] bArrIntToBytesBig4 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit[0] + strArrSplit[1]));
            for (int i2 = 0; i2 < bArrIntToBytesBig4.length; i2++) {
                bArr[i2 + 9] = bArrIntToBytesBig4[i2];
            }
            String[] strArrSplit2 = reportInfoModel.getLongitude().trim().split("\\.");
            byte[] bArrIntToBytesBig5 = JavaUtils.intToBytesBig(Integer.parseInt(strArrSplit2[0] + strArrSplit2[1]));
            for (int i3 = 0; i3 < bArrIntToBytesBig5.length; i3++) {
                bArr[i3 + 13] = bArrIntToBytesBig5[i3];
            }
            bArr[17] = 0;
            bArr[18] = 0;
            bArr[19] = bArrIntToBytesBig2[2];
            bArr[20] = bArrIntToBytesBig2[3];
            byte[] bArrIntToBytesBig6 = JavaUtils.intToBytesBig(reportInfoModel.getAngle());
            bArr[21] = bArrIntToBytesBig6[2];
            bArr[22] = bArrIntToBytesBig6[3];
            byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
            if (bArrStr2Bcd.length <= 5) {
                int i4 = 0;
                while (i4 < bArrStr2Bcd.length) {
                    bArr[i4 + 23] = bArrStr2Bcd[i4];
                    i4++;
                }
                while (i4 < 6) {
                    bArr[i4 + 23] = 0;
                    i4++;
                }
            } else {
                for (int i5 = 0; i5 < 6; i5++) {
                    bArr[i5 + 23] = bArrStr2Bcd[i5];
                }
            }
            bArr[29] = 0;
            int i6 = 30;
            for (byte b : bytes) {
                bArr[i6] = b;
                i6++;
            }
            bArr[i6] = 0;
            byte[] bArrIntToBytesBig7 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(length), 16)}, new byte[]{11, 4}, new byte[]{bArrIntToBytesBig7[2], bArrIntToBytesBig7[3]}, reportInfoModel);
            int length2 = bArrPkgHead.length + length + 3;
            byte[] bArr2 = new byte[length2];
            int i7 = 0;
            char c = 0;
            int i8 = 0;
            byte b2 = 0;
            byte b3 = 0;
            while (i7 < bArrPkgHead.length + length + 3) {
                if (c == 0) {
                    bArr2[i7] = 126;
                    c = 1;
                } else if (c == 1) {
                    b3 = bArrPkgHead[i8];
                    bArr2[i7] = b3;
                    i8++;
                    if (i8 >= bArrPkgHead.length) {
                        i8 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b3 = bArr[i8];
                    bArr2[i7] = b3;
                    i8++;
                    if (i8 >= length) {
                        i8 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i7] = b2;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i7] = 126;
                    c = 5;
                }
                b2 = i7 > 1 ? (byte) (b2 ^ b3) : b3;
                i7++;
            }
            LogUtils.d("Generate808ReqPackage", "生成违规信息send:" + JavaUtils.bytesToHexString(bArr2, length2));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateLineSwitchInfo(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[20];
            bArr[0] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getType()), 16);
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(Integer.parseInt(reportInfoModel.getFirstLineNumber()));
            int i = 0;
            while (i < bArrIntToBytesBig.length) {
                int i2 = i + 1;
                bArr[i2] = bArrIntToBytesBig[i];
                i = i2;
            }
            bArr[5] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getFirstDirection()), 16);
            bArr[6] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getFirstBusNo()), 16);
            byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(Integer.parseInt(reportInfoModel.getLineNumber()));
            for (int i3 = 0; i3 < bArrIntToBytesBig2.length; i3++) {
                bArr[i3 + 7] = bArrIntToBytesBig2[i3];
            }
            bArr[11] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getDirection()), 16);
            bArr[12] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getBusNo()), 16);
            byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
            if (bArrStr2Bcd.length <= 5) {
                int i4 = 0;
                while (i4 < bArrStr2Bcd.length) {
                    bArr[i4 + 13] = bArrStr2Bcd[i4];
                    i4++;
                }
                while (i4 < 6) {
                    bArr[i4 + 13] = 0;
                    i4++;
                }
            } else {
                for (int i5 = 0; i5 < 6; i5++) {
                    bArr[i5 + 13] = bArrStr2Bcd[i5];
                }
            }
            bArr[19] = 0;
            byte[] bArrIntToBytesBig3 = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(20), 16)}, new byte[]{11, NotEqualPtg.sid}, new byte[]{bArrIntToBytesBig3[2], bArrIntToBytesBig3[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 20 + 3;
            byte[] bArr2 = new byte[length];
            int i6 = 0;
            char c = 0;
            int i7 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i6 < bArrPkgHead.length + 20 + 3) {
                if (c == 0) {
                    bArr2[i6] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i7];
                    bArr2[i6] = b2;
                    i7++;
                    if (i7 >= bArrPkgHead.length) {
                        i7 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i7];
                    bArr2[i6] = b2;
                    i7++;
                    if (i7 >= 20) {
                        i7 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i6] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i6] = 126;
                    c = 5;
                }
                b = i6 > 1 ? (byte) (b ^ b2) : b2;
                i6++;
            }
            LogUtils.d("Generate808ReqPackage", "生成线路切换信息send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateRequestMessage(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[15];
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(Integer.parseInt(reportInfoModel.getLineNumber()));
            for (int i = 0; i < bArrIntToBytesBig.length; i++) {
                bArr[i] = bArrIntToBytesBig[i];
            }
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(reportInfoModel.getCardNo());
            if (bArrHexStringToByteArray.length <= 3) {
                int i2 = 0;
                while (i2 < bArrHexStringToByteArray.length) {
                    bArr[i2 + 4] = bArrHexStringToByteArray[i2];
                    i2++;
                }
                while (i2 < 4) {
                    bArr[i2 + 4] = 0;
                    i2++;
                }
            } else {
                for (int i3 = 0; i3 < 4; i3++) {
                    bArr[i3 + 4] = bArrHexStringToByteArray[i3];
                }
            }
            bArr[8] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getType()), 16);
            byte[] bArrStr2Bcd = JavaUtils.str2Bcd(reportInfoModel.getTerminalDate());
            if (bArrStr2Bcd.length <= 5) {
                int i4 = 0;
                while (i4 < bArrStr2Bcd.length) {
                    bArr[i4 + 9] = bArrStr2Bcd[i4];
                    i4++;
                }
                while (i4 < 6) {
                    bArr[i4 + 9] = 0;
                    i4++;
                }
            } else {
                for (int i5 = 0; i5 < 6; i5++) {
                    bArr[i5 + 9] = bArrStr2Bcd[i5];
                }
            }
            int i6 = requestMsgSerialNumber + 1;
            requestMsgSerialNumber = i6;
            if (i6 >= 60000) {
                requestMsgSerialNumber = 1;
            }
            AppApplication.mapDispatch.put(String.valueOf(requestMsgSerialNumber), String.valueOf(reportInfoModel.getType()));
            byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(requestMsgSerialNumber);
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(15), 16)}, new byte[]{11, 9}, new byte[]{bArrIntToBytesBig2[2], bArrIntToBytesBig2[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 15 + 3;
            byte[] bArr2 = new byte[length];
            int i7 = 0;
            char c = 0;
            int i8 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i7 < bArrPkgHead.length + 15 + 3) {
                if (c == 0) {
                    bArr2[i7] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i8];
                    bArr2[i7] = b2;
                    i8++;
                    if (i8 >= bArrPkgHead.length) {
                        i8 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i8];
                    bArr2[i7] = b2;
                    i8++;
                    if (i8 >= 15) {
                        i8 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i7] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i7] = 126;
                    c = 5;
                }
                b = i7 > 1 ? (byte) (b ^ b2) : b2;
                i7++;
            }
            LogUtils.d("Generate808ReqPackage", "生成请求消息send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateUpgradeNotification(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(reportInfoModel.getSerialNumber());
            byte[] bArr = {bArrHexStringToByteArray[0], bArrHexStringToByteArray[1], (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getUpgradeStatus()), 16), (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getUpgradeProgress()), 16)};
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(4), 16)}, new byte[]{11, 10}, new byte[]{bArrIntToBytesBig[2], bArrIntToBytesBig[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 4 + 3;
            byte[] bArr2 = new byte[length];
            int i = 0;
            char c = 0;
            int i2 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i < bArrPkgHead.length + 4 + 3) {
                if (c == 0) {
                    bArr2[i] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i2];
                    bArr2[i] = b2;
                    i2++;
                    if (i2 >= bArrPkgHead.length) {
                        i2 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i2];
                    bArr2[i] = b2;
                    i2++;
                    if (i2 >= 4) {
                        i2 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i] = 126;
                    c = 5;
                }
                b = i > 1 ? (byte) (b ^ b2) : b2;
                i++;
            }
            LogUtils.d("Generate808ReqPackage", "生成升级通知消息send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateLogNotification(ReportInfoModel reportInfoModel) {
        try {
            byte[] bArr = new byte[17];
            LogUtils.d("generateLogNotification", "任务号:" + reportInfoModel.getLogTask() + ",上传状态" + reportInfoModel.getLogStatus() + ",上传进度:" + reportInfoModel.getLogProgress());
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(reportInfoModel.getLogTask());
            for (int i = 0; i < 8; i++) {
                bArr[i] = bArrHexStringToByteArray[i];
            }
            byte[] bArrHexStringToByteArray2 = JavaUtils.hexStringToByteArray(reportInfoModel.getLogTime());
            for (int i2 = 0; i2 < 3; i2++) {
                bArr[i2 + 8] = bArrHexStringToByteArray2[i2];
            }
            bArr[11] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getLogStatus()), 16);
            bArr[12] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getLogProgress()), 16);
            for (int i3 = 0; i3 < 4; i3++) {
                bArr[i3 + 13] = 0;
            }
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(rand.nextInt(60000));
            byte[] bArrPkgHead = pkgHead(new byte[]{0, (byte) Integer.parseInt(Integer.toHexString(17), 16)}, new byte[]{-22, 0}, new byte[]{bArrIntToBytesBig[2], bArrIntToBytesBig[3]}, reportInfoModel);
            int length = bArrPkgHead.length + 17 + 3;
            byte[] bArr2 = new byte[length];
            int i4 = 0;
            char c = 0;
            int i5 = 0;
            byte b = 0;
            byte b2 = 0;
            while (i4 < bArrPkgHead.length + 17 + 3) {
                if (c == 0) {
                    bArr2[i4] = 126;
                    c = 1;
                } else if (c == 1) {
                    b2 = bArrPkgHead[i5];
                    bArr2[i4] = b2;
                    i5++;
                    if (i5 >= bArrPkgHead.length) {
                        i5 = 0;
                        c = 2;
                    }
                } else if (c == 2) {
                    b2 = bArr[i5];
                    bArr2[i4] = b2;
                    i5++;
                    if (i5 >= 17) {
                        i5 = 0;
                        c = 3;
                    }
                } else if (c == 3) {
                    bArr2[i4] = b;
                    c = 4;
                } else if (c == 4) {
                    bArr2[i4] = 126;
                    c = 5;
                }
                b = i4 > 1 ? (byte) (b ^ b2) : b2;
                i4++;
            }
            LogUtils.d("Generate808ReqPackage", "生成上传log消息send:" + JavaUtils.bytesToHexString(bArr2, length));
            return escape(bArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] generateIntercomBag(byte[] bArr, String str, int i) {
        try {
            byte[] bArr2 = new byte[bArr.length + 26];
            bArr2[0] = TarConstants.LF_NORMAL;
            bArr2[1] = TarConstants.LF_LINK;
            bArr2[2] = 99;
            bArr2[3] = 100;
            bArr2[4] = -127;
            bArr2[5] = -122;
            int i2 = intercomBagSerialNumber + 1;
            intercomBagSerialNumber = i2;
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(i2);
            bArr2[6] = bArrIntToBytesBig[2];
            bArr2[7] = bArrIntToBytesBig[3];
            byte[] bArrStr2Bcd = JavaUtils.str2Bcd(str);
            if (bArrStr2Bcd.length <= 5) {
                int i3 = 0;
                while (i3 < bArrStr2Bcd.length) {
                    bArr2[i3 + 8] = bArrStr2Bcd[i3];
                    i3++;
                }
                while (i3 < 6) {
                    bArr2[i3 + 8] = 0;
                    i3++;
                }
            } else {
                for (int i4 = 0; i4 < 6; i4++) {
                    bArr2[i4 + 8] = bArrStr2Bcd[i4];
                }
            }
            bArr2[14] = (byte) Integer.parseInt(Integer.toHexString(i), 16);
            bArr2[15] = TarConstants.LF_NORMAL;
            byte[] bArrLongToBytesBig = JavaUtils.longToBytesBig(System.currentTimeMillis());
            for (int i5 = 0; i5 < bArrLongToBytesBig.length; i5++) {
                bArr2[i5 + 16] = bArrLongToBytesBig[i5];
            }
            byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(bArr.length);
            bArr2[24] = bArrIntToBytesBig2[2];
            bArr2[25] = bArrIntToBytesBig2[3];
            for (int i6 = 0; i6 < bArr.length; i6++) {
                bArr2[i6 + 26] = bArr[i6];
            }
            return bArr2;
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
        LogUtils.d("Generate808ReqPackage", "转义后的应答send:" + JavaUtils.bytesToHexString(bArr3, i2));
        return bArr3;
    }
}
