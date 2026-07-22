package com.lianhexinye.m90.socket.request;

import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import java.io.UnsupportedEncodingException;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class GenerateReqPackage {
    private static long newsSerialNumber;

    private static byte[] pkgHead(byte b, byte[] bArr, byte b2, byte[] bArr2) {
        byte[] bArr3 = new byte[16];
        bArr3[0] = 1;
        bArr3[1] = 0;
        bArr3[2] = b;
        bArr3[3] = 0;
        bArr3[4] = bArr[0];
        bArr3[5] = bArr[1];
        bArr3[6] = b2;
        bArr3[7] = 0;
        if (bArr2 == null || bArr2.length <= 0) {
            bArr3[8] = 1;
            bArr3[9] = 0;
            bArr3[10] = 0;
            bArr3[11] = 0;
        } else {
            int i = 0;
            for (int i2 = 8; i2 < 12; i2++) {
                bArr3[i2] = bArr2[i];
                i++;
            }
        }
        for (int i3 = 12; i3 < 16; i3++) {
            bArr3[i3] = 0;
        }
        return bArr3;
    }

    public static byte[] generateHeartbeat(byte[] bArr) {
        byte[] bArr2 = new byte[32];
        try {
            newsSerialNumber++;
            byte[] bArrPkgHead = pkgHead((byte) 16, new byte[]{2, 16}, (byte) 4, bArr);
            for (int i = 0; i < bArrPkgHead.length; i++) {
                bArr2[i] = bArrPkgHead[i];
            }
            for (int i2 = 16; i2 < 32; i2++) {
                bArr2[i2] = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d("GenerateReqPackage", "心跳包send:" + JavaUtils.bytesToHexString(bArr2, 32));
        return bArr2;
    }

    public static byte[] registerDevice() {
        byte[] bArr = new byte[96];
        try {
            long j = newsSerialNumber + 1;
            newsSerialNumber = j;
            byte[] bArrPkgHead = pkgHead((byte) 80, new byte[]{3, 16}, (byte) 2, JavaUtils.longToBytes_Little(j));
            for (int i = 0; i < bArrPkgHead.length; i++) {
                bArr[i] = bArrPkgHead[i];
            }
            byte[] bytes = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", AndroidUtils.getMacAddress())).getBytes("gb2312");
            int i2 = 0;
            while (i2 < 20 - bytes.length) {
                bArr[i2 + 16] = TarConstants.LF_NORMAL;
                i2++;
            }
            for (byte b : bytes) {
                bArr[i2 + 16] = b;
                i2++;
            }
            byte[] bytes2 = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", AndroidUtils.getMacAddress())).getBytes("gb2312");
            int i3 = 0;
            for (int i4 = 36; i4 < bytes2.length + 36; i4++) {
                bArr[i4] = bytes2[i3];
                i3++;
            }
            while (i3 < 20) {
                bArr[i3 + 36] = 0;
                i3++;
            }
            byte[] bytes3 = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchPwd", "888888")).getBytes("gb2312");
            int i5 = 0;
            for (int i6 = 56; i6 < bytes3.length + 56; i6++) {
                bArr[i6] = bytes3[i5];
                i5++;
            }
            while (i5 < 20) {
                bArr[i5 + 56] = 0;
                i5++;
            }
            for (int i7 = 76; i7 < 80; i7++) {
                bArr[i7] = 0;
            }
            for (int i8 = 80; i8 < 96; i8++) {
                bArr[i8] = 0;
            }
            LogUtils.d("GenerateReqPackage", "注册包send:" + JavaUtils.bytesToHexString(bArr, 96));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bArr;
    }

    public static byte[] siteInfo(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        byte[] bytes2;
        byte[] bArr = new byte[100];
        try {
            long j = newsSerialNumber + 1;
            newsSerialNumber = j;
            byte[] bArrPkgHead = pkgHead((byte) 84, new byte[]{-81, 16}, (byte) 2, JavaUtils.longToBytes_Little(j));
            for (int i = 0; i < bArrPkgHead.length; i++) {
                bArr[i] = bArrPkgHead[i];
            }
            byte[] bytes3 = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", AndroidUtils.getMacAddress())).getBytes("gb2312");
            int i2 = 0;
            while (i2 < 20 - bytes3.length) {
                bArr[i2 + 16] = TarConstants.LF_NORMAL;
                i2++;
            }
            for (byte b : bytes3) {
                bArr[i2 + 16] = b;
                i2++;
            }
            if (reportInfoModel.getBusLineName().trim().length() > 18) {
                bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
            } else {
                bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
            }
            int i3 = 0;
            for (int i4 = 36; i4 < bytes.length + 36; i4++) {
                bArr[i4] = bytes[i3];
                i3++;
            }
            while (i3 < 20) {
                bArr[i3 + 36] = 0;
                i3++;
            }
            if (reportInfoModel.getDirection() == 1) {
                bArr[56] = 0;
            } else {
                bArr[56] = 1;
            }
            for (int i5 = 0; i5 < 3; i5++) {
                bArr[i5 + 57] = 0;
            }
            bArr[60] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getStatus()), 16);
            for (int i6 = 0; i6 < 3; i6++) {
                bArr[i6 + 61] = 0;
            }
            if (reportInfoModel.getBusName().trim().length() > 14) {
                bytes2 = (reportInfoModel.getBusName().trim().substring(0, 12) + ".").getBytes("gb2312");
            } else {
                bytes2 = reportInfoModel.getBusName().trim().getBytes("gb2312");
            }
            int i7 = 0;
            for (int i8 = 64; i8 < bytes2.length + 64; i8++) {
                bArr[i8] = bytes2[i7];
                i7++;
            }
            while (i7 < 16) {
                bArr[i7 + 64] = 0;
                i7++;
            }
            bArr[80] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getSpeedLimit()), 16);
            for (int i9 = 0; i9 < 3; i9++) {
                bArr[i9 + 81] = 0;
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
            int i10 = 0;
            for (int i11 = 84; i11 < 88; i11++) {
                bArr[i11] = bArrLongToBytes_Little[i10];
                i10++;
            }
            bArr[88] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getBusNo()), 16);
            for (int i12 = 0; i12 < 3; i12++) {
                bArr[i12 + 89] = 0;
            }
            byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getSpeed());
            bArr[92] = bArrLongToBytes_Little2[0];
            bArr[93] = bArrLongToBytes_Little2[1];
            bArr[94] = bArrLongToBytes_Little2[2];
            bArr[95] = bArrLongToBytes_Little2[3];
            bArr[96] = 0;
            bArr[97] = 0;
            bArr[98] = 0;
            bArr[99] = 0;
            LogUtils.d("GenerateReqPackage", "站点信息包send:" + JavaUtils.bytesToHexString(bArr, 100));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bArr;
    }

    public static byte[] gpsReport(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        byte[] bArr = new byte[124];
        try {
            long j = newsSerialNumber + 1;
            newsSerialNumber = j;
            byte[] bArrPkgHead = pkgHead((byte) 108, new byte[]{-82, 16}, (byte) 8, JavaUtils.longToBytes_Little(j));
            for (int i = 0; i < bArrPkgHead.length; i++) {
                bArr[i] = bArrPkgHead[i];
            }
            byte[] bytes2 = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", AndroidUtils.getMacAddress())).getBytes("gb2312");
            int i2 = 0;
            while (i2 < 20 - bytes2.length) {
                bArr[i2 + 16] = TarConstants.LF_NORMAL;
                i2++;
            }
            for (byte b : bytes2) {
                bArr[i2 + 16] = b;
                i2++;
            }
            if (reportInfoModel.getBusLineName().trim().length() > 18) {
                bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
            } else {
                bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
            }
            int i3 = 0;
            for (int i4 = 36; i4 < bytes.length + 36; i4++) {
                bArr[i4] = bytes[i3];
                i3++;
            }
            while (i3 < 20) {
                bArr[i3 + 36] = 0;
                i3++;
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
            int i5 = 0;
            for (int i6 = 56; i6 < 60; i6++) {
                bArr[i6] = bArrLongToBytes_Little[i5];
                i5++;
            }
            if (reportInfoModel.getLongitudeE().trim().toLowerCase().equals("e")) {
                bArr[60] = TarConstants.LF_LINK;
            } else {
                bArr[60] = TarConstants.LF_NORMAL;
            }
            byte[] bytes3 = reportInfoModel.getLongitude().trim().getBytes("gb2312");
            int i7 = 0;
            for (int i8 = 61; i8 < bytes3.length + 61; i8++) {
                bArr[i8] = bytes3[i7];
                i7++;
            }
            while (i7 < 23) {
                bArr[i7 + 61] = 0;
                i7++;
            }
            if (reportInfoModel.getLatitudeN().trim().toLowerCase().equals("n")) {
                bArr[84] = TarConstants.LF_LINK;
            } else {
                bArr[84] = TarConstants.LF_NORMAL;
            }
            byte[] bytes4 = reportInfoModel.getLatitude().trim().getBytes("gb2312");
            int i9 = 0;
            for (int i10 = 85; i10 < bytes4.length + 85; i10++) {
                bArr[i10] = bytes4[i9];
                i9++;
            }
            while (i9 < 23) {
                bArr[i9 + 85] = 0;
                i9++;
            }
            byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getSpeed());
            bArr[108] = bArrLongToBytes_Little2[0];
            bArr[109] = bArrLongToBytes_Little2[1];
            bArr[110] = bArrLongToBytes_Little2[2];
            bArr[111] = bArrLongToBytes_Little2[3];
            byte[] bArrLongToBytes_Little3 = JavaUtils.longToBytes_Little(reportInfoModel.getAngle());
            bArr[112] = bArrLongToBytes_Little3[0];
            bArr[113] = bArrLongToBytes_Little3[1];
            bArr[114] = bArrLongToBytes_Little3[2];
            bArr[115] = bArrLongToBytes_Little3[3];
            if (reportInfoModel.getDirection() == 1) {
                bArr[116] = 0;
            } else {
                bArr[116] = 1;
            }
            bArr[117] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getBusNo()), 16);
            bArr[118] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getStatus()), 16);
            bArr[119] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getOverSpeed()), 16);
            bArr[120] = 0;
            bArr[121] = 0;
            bArr[122] = 0;
            bArr[123] = 0;
            LogUtils.d("GenerateReqPackage", "上报消息send:" + JavaUtils.bytesToHexString(bArr, 124));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bArr;
    }

    public static byte[] speedAlarm(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        byte[] bytes2;
        byte[] bArr = new byte[124];
        try {
            long j = newsSerialNumber + 1;
            newsSerialNumber = j;
            byte[] bArrPkgHead = pkgHead((byte) 108, new byte[]{-80, 16}, (byte) 2, JavaUtils.longToBytes_Little(j));
            for (int i = 0; i < bArrPkgHead.length; i++) {
                bArr[i] = bArrPkgHead[i];
            }
            byte[] bytes3 = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", AndroidUtils.getMacAddress())).getBytes("gb2312");
            int i2 = 0;
            while (i2 < 20 - bytes3.length) {
                bArr[i2 + 16] = TarConstants.LF_NORMAL;
                i2++;
            }
            for (byte b : bytes3) {
                bArr[i2 + 16] = b;
                i2++;
            }
            if (reportInfoModel.getBusLineName().trim().length() > 18) {
                bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
            } else {
                bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
            }
            int i3 = 0;
            for (int i4 = 36; i4 < bytes.length + 36; i4++) {
                bArr[i4] = bytes[i3];
                i3++;
            }
            while (i3 < 20) {
                bArr[i3 + 36] = 0;
                i3++;
            }
            if (reportInfoModel.getDriverName() == null || reportInfoModel.getDriverName().trim().equals("")) {
                for (int i5 = 56; i5 < 72; i5++) {
                    bArr[i5] = 0;
                }
            } else {
                byte[] bytes4 = reportInfoModel.getDriverName().trim().getBytes("gb2312");
                int i6 = 0;
                for (int i7 = 56; i7 < bytes4.length + 56; i7++) {
                    bArr[i7] = bytes4[i6];
                    i6++;
                }
                while (i6 < 16) {
                    bArr[i6 + 56] = 0;
                    i6++;
                }
            }
            if (reportInfoModel.getDirection() == 1) {
                bArr[72] = 0;
            } else {
                bArr[72] = 1;
            }
            for (int i8 = 0; i8 < 3; i8++) {
                bArr[i8 + 73] = 0;
            }
            bArr[76] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getStatus()), 16);
            for (int i9 = 0; i9 < 3; i9++) {
                bArr[i9 + 77] = 0;
            }
            if (reportInfoModel.getBusName().trim().length() > 14) {
                bytes2 = (reportInfoModel.getBusName().trim().substring(0, 12) + ".").getBytes("gb2312");
            } else {
                bytes2 = reportInfoModel.getBusName().trim().getBytes("gb2312");
            }
            int i10 = 0;
            for (int i11 = 80; i11 < bytes2.length + 80; i11++) {
                bArr[i11] = bytes2[i10];
                i10++;
            }
            while (i10 < 16) {
                bArr[i10 + 80] = 0;
                i10++;
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
            int i12 = 0;
            for (int i13 = 96; i13 < 100; i13++) {
                bArr[i13] = bArrLongToBytes_Little[i12];
                i12++;
            }
            byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getSpeedLimit());
            bArr[100] = bArrLongToBytes_Little2[0];
            bArr[101] = bArrLongToBytes_Little2[1];
            bArr[102] = bArrLongToBytes_Little2[2];
            bArr[103] = bArrLongToBytes_Little2[3];
            byte[] bArrLongToBytes_Little3 = JavaUtils.longToBytes_Little(reportInfoModel.getSpeed());
            bArr[104] = bArrLongToBytes_Little3[0];
            bArr[105] = bArrLongToBytes_Little3[1];
            bArr[106] = bArrLongToBytes_Little3[2];
            bArr[107] = bArrLongToBytes_Little3[3];
            byte[] bArrLongToBytes_Little4 = JavaUtils.longToBytes_Little(reportInfoModel.getStartTime());
            int i14 = 0;
            for (int i15 = 108; i15 < 112; i15++) {
                bArr[i15] = bArrLongToBytes_Little4[i14];
                i14++;
            }
            byte[] bArrLongToBytes_Little5 = JavaUtils.longToBytes_Little(reportInfoModel.getContinueTime());
            bArr[112] = bArrLongToBytes_Little5[0];
            bArr[113] = bArrLongToBytes_Little5[1];
            bArr[114] = bArrLongToBytes_Little5[2];
            bArr[115] = bArrLongToBytes_Little5[3];
            for (int i16 = 116; i16 < 124; i16++) {
                bArr[i16] = 0;
            }
            LogUtils.d("GenerateReqPackage", "报警消息send:" + JavaUtils.bytesToHexString(bArr, 124));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bArr;
    }

    public static byte[] dispatchReply(ReportInfoModel reportInfoModel) {
        byte[] bArr = new byte[64];
        try {
            byte[] bArrPkgHead = pkgHead(TarConstants.LF_NORMAL, new byte[]{2, 32}, (byte) 4, JavaUtils.hexToBytes(reportInfoModel.getSerialNumber()));
            for (int i = 0; i < bArrPkgHead.length; i++) {
                bArr[i] = bArrPkgHead[i];
            }
            byte[] bytes = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", AndroidUtils.getMacAddress())).getBytes("gb2312");
            int i2 = 0;
            while (i2 < 20 - bytes.length) {
                bArr[i2 + 16] = TarConstants.LF_NORMAL;
                i2++;
            }
            for (byte b : bytes) {
                bArr[i2 + 16] = b;
                i2++;
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(reportInfoModel.getLineGuid());
            int i3 = 0;
            for (int i4 = 36; i4 < 40; i4++) {
                bArr[i4] = bArrLongToBytes_Little[i3];
                i3++;
            }
            bArr[40] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getDirection()), 16);
            bArr[41] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getScheduleNo()), 16);
            bArr[42] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getTimesNo()), 16);
            bArr[43] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getResult()), 16);
            for (int i5 = 44; i5 < 64; i5++) {
                bArr[i5] = 0;
            }
            LogUtils.d("GenerateReqPackage", "回复消息send:" + JavaUtils.bytesToHexString(bArr, 64));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bArr;
    }

    public static byte[] lowerReply(ReportInfoModel reportInfoModel) {
        byte[] bArr = new byte[40];
        try {
            int i = 0;
            byte[] bArrPkgHead = pkgHead((byte) 24, new byte[]{4, 32}, (byte) 4, JavaUtils.hexToBytes(reportInfoModel.getSerialNumber()));
            for (int i2 = 0; i2 < bArrPkgHead.length; i2++) {
                bArr[i2] = bArrPkgHead[i2];
            }
            byte[] bytes = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", AndroidUtils.getMacAddress())).getBytes("gb2312");
            int i3 = 0;
            while (i3 < 20 - bytes.length) {
                bArr[i3 + 16] = TarConstants.LF_NORMAL;
                i3++;
            }
            for (byte b : bytes) {
                bArr[i3 + 16] = b;
                i3++;
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(reportInfoModel.getResult());
            for (int i4 = 36; i4 < 40; i4++) {
                bArr[i4] = bArrLongToBytes_Little[i];
                i++;
            }
            LogUtils.d("GenerateReqPackage", "下发回复send:" + JavaUtils.bytesToHexString(bArr, 40));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bArr;
    }

    public static byte[] driversSignInOut(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        byte[] bArr = new byte[72];
        try {
            long j = newsSerialNumber + 1;
            newsSerialNumber = j;
            byte[] bArrPkgHead = pkgHead(PaletteRecord.STANDARD_PALETTE_SIZE, new byte[]{-76, 16}, (byte) 2, JavaUtils.longToBytes_Little(j));
            for (int i = 0; i < bArrPkgHead.length; i++) {
                bArr[i] = bArrPkgHead[i];
            }
            byte[] bytes2 = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", AndroidUtils.getMacAddress())).getBytes("gb2312");
            int i2 = 0;
            while (i2 < 20 - bytes2.length) {
                bArr[i2 + 16] = TarConstants.LF_NORMAL;
                i2++;
            }
            for (byte b : bytes2) {
                bArr[i2 + 16] = b;
                i2++;
            }
            if (reportInfoModel.getBusLineName().trim().length() > 18) {
                bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
            } else {
                bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
            }
            int i3 = 0;
            for (int i4 = 36; i4 < bytes.length + 36; i4++) {
                bArr[i4] = bytes[i3];
                i3++;
            }
            while (i3 < 20) {
                bArr[i3 + 36] = 0;
                i3++;
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
            int i5 = 0;
            for (int i6 = 56; i6 < 60; i6++) {
                bArr[i6] = bArrLongToBytes_Little[i5];
                i5++;
            }
            byte[] bytes3 = reportInfoModel.getCardNo().trim().getBytes("gb2312");
            int i7 = 0;
            for (int i8 = 60; i8 < bytes3.length + 60; i8++) {
                bArr[i8] = bytes3[i7];
                i7++;
            }
            while (i7 < 4) {
                bArr[i7 + 60] = 0;
                i7++;
            }
            byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getDriverId());
            int i9 = 0;
            for (int i10 = 64; i10 < 68; i10++) {
                bArr[i10] = bArrLongToBytes_Little2[i9];
                i9++;
            }
            bArr[68] = String.valueOf(reportInfoModel.getdStatus()).getBytes("gb2312")[0];
            bArr[69] = 0;
            bArr[70] = 0;
            bArr[71] = 0;
            LogUtils.d("GenerateReqPackage", "司机签到签退send:" + JavaUtils.bytesToHexString(bArr, 72));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bArr;
    }

    public static byte[] startBusReport(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        byte[] bArr = new byte[104];
        try {
            long j = newsSerialNumber + 1;
            newsSerialNumber = j;
            byte[] bArrPkgHead = pkgHead(TarConstants.LF_PAX_EXTENDED_HEADER_UC, new byte[]{17, 32}, (byte) 8, JavaUtils.longToBytes_Little(j));
            for (int i = 0; i < bArrPkgHead.length; i++) {
                bArr[i] = bArrPkgHead[i];
            }
            byte[] bytes2 = ((String) SPUserInfoUtils.get(AppApplication.getContext(), "NetDispatchID", AndroidUtils.getMacAddress())).getBytes("gb2312");
            int i2 = 0;
            while (i2 < 20 - bytes2.length) {
                bArr[i2 + 16] = TarConstants.LF_NORMAL;
                i2++;
            }
            for (byte b : bytes2) {
                bArr[i2 + 16] = b;
                i2++;
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
            int i3 = 0;
            for (int i4 = 36; i4 < 40; i4++) {
                bArr[i4] = bArrLongToBytes_Little[i3];
                i3++;
            }
            if (reportInfoModel.getBusLineName().trim().length() > 18) {
                bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
            } else {
                bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
            }
            int i5 = 0;
            for (int i6 = 40; i6 < bytes.length + 40; i6++) {
                bArr[i6] = bytes[i5];
                i5++;
            }
            while (i5 < 20) {
                bArr[i5 + 40] = 0;
                i5++;
            }
            byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getLineGuid());
            int i7 = 0;
            for (int i8 = 60; i8 < 64; i8++) {
                bArr[i8] = bArrLongToBytes_Little2[i7];
                i7++;
            }
            bArr[64] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getDirection()), 16);
            bArr[65] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getScheduleNo()), 16);
            bArr[66] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getTimesNo()), 16);
            byte[] bytes3 = reportInfoModel.getBusName().trim().getBytes("gb2312");
            int i9 = 0;
            for (int i10 = 67; i10 < bytes3.length + 67; i10++) {
                bArr[i10] = bytes3[i9];
                i9++;
            }
            while (i9 < 37) {
                bArr[i9 + 67] = 0;
                i9++;
            }
            LogUtils.d("GenerateReqPackage", "发车上报信息send:" + JavaUtils.bytesToHexString(bArr, 104));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bArr;
    }
}
