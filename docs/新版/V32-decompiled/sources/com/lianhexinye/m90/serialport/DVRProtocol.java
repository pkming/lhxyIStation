package com.lianhexinye.m90.serialport;

import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.ReportInfoModel;
import com.lianhexinye.serialprot.ComBean;
import com.lianhexinye.serialprot.SerialHelper;
import java.io.IOException;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;
import org.apache.poi.hssf.record.formula.UnaryMinusPtg;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class DVRProtocol {
    private DVRCallback dvrCallback;
    public boolean dvrState;
    private HandshakeThread handshakeThread;
    private int handshakeTime;
    private boolean iHandshake;
    public long intervalTime;
    private Serial232Control serial232Control;

    public interface DVRCallback {
        void onResult(String str);
    }

    private DVRProtocol() {
        this.iHandshake = false;
        this.handshakeTime = 8000;
        this.handshakeThread = null;
        this.intervalTime = 0L;
        this.dvrState = false;
    }

    private static class SingletonHolder {
        private static final DVRProtocol instance = new DVRProtocol();

        private SingletonHolder() {
        }
    }

    public static DVRProtocol getInstance() {
        return SingletonHolder.instance;
    }

    public void open232SerialPort() {
        try {
            Serial232Control serial232Control = this.serial232Control;
            if (serial232Control == null || !serial232Control.isOpen()) {
                Serial232Control serial232Control2 = new Serial232Control((String) SPUserInfoUtils.get(AppApplication.getContext(), "RS232-1Baud", "9600"));
                this.serial232Control = serial232Control2;
                serial232Control2.open();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SecurityException e2) {
            e2.printStackTrace();
        }
    }

    public void sendKey(byte b) {
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control == null || !serial232Control.isOpen()) {
            return;
        }
        byte[] bArr = new byte[10];
        bArr[0] = 85;
        bArr[1] = 20;
        bArr[2] = 4;
        bArr[3] = 0;
        bArr[4] = b;
        bArr[5] = 0;
        bArr[6] = 0;
        bArr[7] = 0;
        bArr[8] = JavaUtils.sumCheck(bArr, 8);
        bArr[9] = -86;
        LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 10));
        this.serial232Control.send(bArr);
    }

    public void sendTouchKey(int i, int i2, byte b) {
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control == null || !serial232Control.isOpen()) {
            return;
        }
        byte[] bArr = new byte[9];
        bArr[0] = 85;
        bArr[1] = -86;
        bArr[2] = 5;
        byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(i);
        bArr[3] = bArrIntToBytesBig[2];
        bArr[4] = bArrIntToBytesBig[3];
        byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(i2);
        bArr[5] = bArrIntToBytesBig2[2];
        bArr[6] = bArrIntToBytesBig2[3];
        bArr[7] = b;
        bArr[8] = JavaUtils.sumCheck(bArr, 8);
        LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 9));
        this.serial232Control.send(bArr);
    }

    public void sendGPSReport(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control != null && serial232Control.isOpen() && this.dvrState) {
            byte[] bArr = new byte[94];
            try {
                bArr[0] = 85;
                bArr[1] = 5;
                bArr[2] = TarConstants.LF_PAX_EXTENDED_HEADER_UC;
                bArr[3] = 0;
                if (reportInfoModel.getBusLineName().trim().length() > 18) {
                    bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
                } else {
                    bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
                }
                int i = 0;
                for (int i2 = 4; i2 < bytes.length + 4; i2++) {
                    bArr[i2] = bytes[i];
                    i++;
                }
                while (i < 20) {
                    bArr[i + 4] = 0;
                    i++;
                }
                byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
                int i3 = 0;
                for (int i4 = 24; i4 < 28; i4++) {
                    bArr[i4] = bArrLongToBytes_Little[i3];
                    i3++;
                }
                if (reportInfoModel.getLongitudeE().trim().toLowerCase().equals("e")) {
                    bArr[28] = TarConstants.LF_LINK;
                } else {
                    bArr[28] = TarConstants.LF_NORMAL;
                }
                byte[] bytes2 = reportInfoModel.getLongitude().trim().getBytes("gb2312");
                int i5 = 0;
                for (int i6 = 29; i6 < bytes2.length + 29; i6++) {
                    bArr[i6] = bytes2[i5];
                    i5++;
                }
                for (int i7 = i5; i7 < 23; i7++) {
                    bArr[i7 + 29] = 0;
                }
                if (reportInfoModel.getLatitudeN().trim().toLowerCase().equals("n")) {
                    bArr[52] = TarConstants.LF_LINK;
                } else {
                    bArr[52] = TarConstants.LF_NORMAL;
                }
                byte[] bytes3 = reportInfoModel.getLatitude().trim().getBytes("gb2312");
                int i8 = 0;
                for (int i9 = 53; i9 < bytes3.length + 53; i9++) {
                    bArr[i9] = bytes3[i8];
                    i8++;
                }
                while (i8 < 23) {
                    bArr[i8 + 53] = 0;
                    i8++;
                }
                byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getSpeed());
                bArr[76] = bArrLongToBytes_Little2[0];
                bArr[77] = bArrLongToBytes_Little2[1];
                bArr[78] = bArrLongToBytes_Little2[2];
                bArr[79] = bArrLongToBytes_Little2[3];
                byte[] bArrLongToBytes_Little3 = JavaUtils.longToBytes_Little(reportInfoModel.getAngle());
                bArr[80] = bArrLongToBytes_Little3[0];
                bArr[81] = bArrLongToBytes_Little3[1];
                bArr[82] = bArrLongToBytes_Little3[2];
                bArr[83] = bArrLongToBytes_Little3[3];
                if (reportInfoModel.getDirection() == 1) {
                    bArr[84] = 0;
                } else {
                    bArr[84] = 1;
                }
                bArr[85] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getBusNo()), 16);
                bArr[86] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getStatus()), 16);
                bArr[87] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getOverSpeed()), 16);
                bArr[88] = 0;
                bArr[89] = 0;
                bArr[90] = 0;
                bArr[91] = 0;
                bArr[92] = JavaUtils.sumCheck(bArr, 92);
                bArr[93] = -86;
                LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 94));
                this.serial232Control.send(bArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendSiteInfo(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        byte[] bytes2;
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control != null && serial232Control.isOpen() && this.dvrState) {
            byte[] bArr = new byte[70];
            try {
                bArr[0] = 85;
                bArr[1] = 6;
                bArr[2] = 64;
                bArr[3] = 0;
                if (reportInfoModel.getBusLineName().trim().length() > 18) {
                    bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
                } else {
                    bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
                }
                int i = 0;
                for (int i2 = 4; i2 < bytes.length + 4; i2++) {
                    bArr[i2] = bytes[i];
                    i++;
                }
                while (i < 20) {
                    bArr[i + 4] = 0;
                    i++;
                }
                if (reportInfoModel.getDirection() == 1) {
                    bArr[24] = 0;
                } else {
                    bArr[24] = 1;
                }
                for (int i3 = 0; i3 < 3; i3++) {
                    bArr[i3 + 25] = 0;
                }
                bArr[28] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getStatus()), 16);
                for (int i4 = 0; i4 < 3; i4++) {
                    bArr[i4 + 29] = 0;
                }
                if (reportInfoModel.getBusName().trim().length() > 18) {
                    bytes2 = (reportInfoModel.getBusName().trim().substring(0, 16) + ".").getBytes("gb2312");
                } else {
                    bytes2 = reportInfoModel.getBusName().trim().getBytes("gb2312");
                }
                int i5 = 0;
                for (int i6 = 32; i6 < bytes2.length + 32; i6++) {
                    bArr[i6] = bytes2[i5];
                    i5++;
                }
                while (i5 < 16) {
                    bArr[i5 + 32] = 0;
                    i5++;
                }
                bArr[48] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getSpeedLimit()), 16);
                for (int i7 = 0; i7 < 3; i7++) {
                    bArr[i7 + 49] = 0;
                }
                byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
                int i8 = 0;
                for (int i9 = 52; i9 < 56; i9++) {
                    bArr[i9] = bArrLongToBytes_Little[i8];
                    i8++;
                }
                bArr[56] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getBusNo()), 16);
                for (int i10 = 0; i10 < 3; i10++) {
                    bArr[i10 + 57] = 0;
                }
                byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getSpeed());
                bArr[60] = bArrLongToBytes_Little2[0];
                bArr[61] = bArrLongToBytes_Little2[1];
                bArr[62] = bArrLongToBytes_Little2[2];
                bArr[63] = bArrLongToBytes_Little2[3];
                bArr[64] = 0;
                bArr[65] = 0;
                bArr[66] = 0;
                bArr[67] = 0;
                bArr[68] = JavaUtils.sumCheck(bArr, 68);
                bArr[69] = -86;
                LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 70));
                this.serial232Control.send(bArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendSpeedAlarm(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        byte[] bytes2;
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control != null && serial232Control.isOpen() && this.dvrState) {
            byte[] bArr = new byte[94];
            try {
                bArr[0] = 85;
                bArr[1] = 11;
                bArr[2] = TarConstants.LF_PAX_EXTENDED_HEADER_UC;
                bArr[3] = 0;
                if (reportInfoModel.getBusLineName().trim().length() > 18) {
                    bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
                } else {
                    bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
                }
                int i = 0;
                for (int i2 = 4; i2 < bytes.length + 4; i2++) {
                    bArr[i2] = bytes[i];
                    i++;
                }
                while (i < 20) {
                    bArr[i + 4] = 0;
                    i++;
                }
                if (reportInfoModel.getDriverName() == null || reportInfoModel.getDriverName().trim().equals("")) {
                    for (int i3 = 24; i3 < 40; i3++) {
                        bArr[i3] = 0;
                    }
                } else {
                    byte[] bytes3 = reportInfoModel.getDriverName().trim().getBytes("gb2312");
                    int i4 = 0;
                    for (int i5 = 24; i5 < bytes3.length + 24; i5++) {
                        bArr[i5] = bytes3[i4];
                        i4++;
                    }
                    while (i4 < 16) {
                        bArr[i4 + 24] = 0;
                        i4++;
                    }
                }
                if (reportInfoModel.getDirection() == 1) {
                    bArr[40] = 0;
                } else {
                    bArr[40] = 1;
                }
                for (int i6 = 0; i6 < 3; i6++) {
                    bArr[i6 + 41] = 0;
                }
                bArr[44] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getStatus()), 16);
                for (int i7 = 0; i7 < 3; i7++) {
                    bArr[i7 + 45] = 0;
                }
                if (reportInfoModel.getBusName().trim().length() > 18) {
                    bytes2 = (reportInfoModel.getBusName().trim().substring(0, 16) + ".").getBytes("gb2312");
                } else {
                    bytes2 = reportInfoModel.getBusName().trim().getBytes("gb2312");
                }
                int i8 = 0;
                for (int i9 = 48; i9 < bytes2.length + 48; i9++) {
                    bArr[i9] = bytes2[i8];
                    i8++;
                }
                while (i8 < 16) {
                    bArr[i8 + 48] = 0;
                    i8++;
                }
                byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
                int i10 = 0;
                for (int i11 = 64; i11 < 68; i11++) {
                    bArr[i11] = bArrLongToBytes_Little[i10];
                    i10++;
                }
                byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getSpeedLimit());
                bArr[68] = bArrLongToBytes_Little2[0];
                bArr[69] = bArrLongToBytes_Little2[1];
                bArr[70] = bArrLongToBytes_Little2[2];
                bArr[71] = bArrLongToBytes_Little2[3];
                byte[] bArrLongToBytes_Little3 = JavaUtils.longToBytes_Little(reportInfoModel.getSpeed());
                bArr[72] = bArrLongToBytes_Little3[0];
                bArr[73] = bArrLongToBytes_Little3[1];
                bArr[74] = bArrLongToBytes_Little3[2];
                bArr[75] = bArrLongToBytes_Little3[3];
                byte[] bArrLongToBytes_Little4 = JavaUtils.longToBytes_Little(reportInfoModel.getStartTime());
                int i12 = 0;
                for (int i13 = 76; i13 < 80; i13++) {
                    bArr[i13] = bArrLongToBytes_Little4[i12];
                    i12++;
                }
                byte[] bArrLongToBytes_Little5 = JavaUtils.longToBytes_Little(reportInfoModel.getContinueTime());
                bArr[80] = bArrLongToBytes_Little5[0];
                bArr[81] = bArrLongToBytes_Little5[1];
                bArr[82] = bArrLongToBytes_Little5[2];
                bArr[83] = bArrLongToBytes_Little5[3];
                for (int i14 = 84; i14 < 92; i14++) {
                    bArr[i14] = 0;
                }
                bArr[92] = JavaUtils.sumCheck(bArr, 92);
                bArr[93] = -86;
                LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 94));
                this.serial232Control.send(bArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendDriversSignInOut(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        int i;
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control != null && serial232Control.isOpen() && this.dvrState) {
            byte[] bArr = new byte[42];
            try {
                bArr[0] = 85;
                bArr[1] = GreaterThanPtg.sid;
                bArr[2] = 36;
                bArr[3] = 0;
                if (reportInfoModel.getBusLineName().trim().length() > 18) {
                    bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
                } else {
                    bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
                }
                int i2 = 0;
                for (int i3 = 4; i3 < bytes.length + 4; i3++) {
                    bArr[i3] = bytes[i2];
                    i2++;
                }
                while (i2 < 20) {
                    bArr[i2 + 4] = 0;
                    i2++;
                }
                byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
                int i4 = 24;
                int i5 = 0;
                while (true) {
                    if (i4 >= 28) {
                        break;
                    }
                    bArr[i4] = bArrLongToBytes_Little[i5];
                    i5++;
                    i4++;
                }
                if (reportInfoModel.getCardNo() == null || reportInfoModel.getCardNo().trim().equals("")) {
                    for (i = 28; i < 32; i++) {
                        bArr[i] = 0;
                    }
                } else {
                    byte[] bytes2 = reportInfoModel.getCardNo().trim().getBytes("gb2312");
                    int i6 = 0;
                    for (int i7 = 28; i7 < bytes2.length + 28; i7++) {
                        bArr[i7] = bytes2[i6];
                        i6++;
                    }
                    while (i6 < 4) {
                        bArr[i6 + 28] = 0;
                        i6++;
                    }
                }
                byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getDriverId());
                int i8 = 0;
                for (int i9 = 32; i9 < 36; i9++) {
                    bArr[i9] = bArrLongToBytes_Little2[i8];
                    i8++;
                }
                bArr[36] = String.valueOf(reportInfoModel.getdStatus()).getBytes("gb2312")[0];
                bArr[37] = 0;
                bArr[38] = 0;
                bArr[39] = 0;
                bArr[40] = JavaUtils.sumCheck(bArr, 40);
                bArr[41] = -86;
                LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 42));
                this.serial232Control.send(bArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendDispatchReply(ReportInfoModel reportInfoModel) {
        int i;
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control != null && serial232Control.isOpen() && this.dvrState) {
            byte[] bArr = new byte[38];
            try {
                bArr[0] = 85;
                bArr[1] = 16;
                bArr[2] = 32;
                bArr[3] = 0;
                byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(reportInfoModel.getMsgSn());
                int i2 = 4;
                int i3 = 0;
                while (true) {
                    if (i2 >= 8) {
                        break;
                    }
                    bArr[i2] = bArrLongToBytes_Little[i3];
                    i3++;
                    i2++;
                }
                byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getLineGuid());
                int i4 = 0;
                for (i = 8; i < 12; i++) {
                    bArr[i] = bArrLongToBytes_Little2[i4];
                    i4++;
                }
                bArr[12] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getDirection()), 16);
                bArr[13] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getScheduleNo()), 16);
                bArr[14] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getTimesNo()), 16);
                bArr[15] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getResult()), 16);
                for (int i5 = 16; i5 < 36; i5++) {
                    bArr[i5] = 0;
                }
                bArr[36] = JavaUtils.sumCheck(bArr, 36);
                bArr[37] = -86;
                LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 38));
                this.serial232Control.send(bArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendStartBusReport(ReportInfoModel reportInfoModel) {
        byte[] bytes;
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control != null && serial232Control.isOpen() && this.dvrState) {
            byte[] bArr = new byte[74];
            try {
                bArr[0] = 85;
                bArr[1] = 17;
                bArr[2] = 68;
                bArr[3] = 0;
                byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(System.currentTimeMillis() / 1000);
                int i = 0;
                for (int i2 = 4; i2 < 8; i2++) {
                    bArr[i2] = bArrLongToBytes_Little[i];
                    i++;
                }
                if (reportInfoModel.getBusLineName().trim().length() > 18) {
                    bytes = (reportInfoModel.getBusLineName().trim().substring(0, 16) + ".").getBytes("gb2312");
                } else {
                    bytes = reportInfoModel.getBusLineName().trim().getBytes("gb2312");
                }
                int i3 = 0;
                for (int i4 = 8; i4 < bytes.length + 8; i4++) {
                    bArr[i4] = bytes[i3];
                    i3++;
                }
                while (i3 < 20) {
                    bArr[i3 + 8] = 0;
                    i3++;
                }
                byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getLineGuid());
                int i5 = 0;
                for (int i6 = 28; i6 < 32; i6++) {
                    bArr[i6] = bArrLongToBytes_Little2[i5];
                    i5++;
                }
                bArr[32] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getDirection()), 16);
                bArr[33] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getScheduleNo()), 16);
                bArr[34] = (byte) Integer.parseInt(Integer.toHexString(reportInfoModel.getTimesNo()), 16);
                for (int i7 = 35; i7 < 72; i7++) {
                    bArr[i7] = 0;
                }
                bArr[72] = JavaUtils.sumCheck(bArr, 72);
                bArr[73] = -86;
                LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 74));
                this.serial232Control.send(bArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendLowerReply(ReportInfoModel reportInfoModel) {
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control != null && serial232Control.isOpen() && this.dvrState) {
            byte[] bArr = new byte[14];
            int i = 0;
            try {
                bArr[0] = 85;
                bArr[1] = UnaryMinusPtg.sid;
                bArr[2] = 8;
                bArr[3] = 0;
                byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(reportInfoModel.getMsgSn());
                int i2 = 0;
                for (int i3 = 4; i3 < 8; i3++) {
                    bArr[i3] = bArrLongToBytes_Little[i2];
                    i2++;
                }
                byte[] bArrLongToBytes_Little2 = JavaUtils.longToBytes_Little(reportInfoModel.getResult());
                for (int i4 = 8; i4 < 12; i4++) {
                    bArr[i4] = bArrLongToBytes_Little2[i];
                    i++;
                }
                bArr[12] = JavaUtils.sumCheck(bArr, 12);
                bArr[13] = -86;
                LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 14));
                this.serial232Control.send(bArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendHandshake(byte b) {
        byte[] bArr = new byte[6];
        bArr[0] = 85;
        bArr[1] = b;
        bArr[2] = 0;
        bArr[3] = 0;
        bArr[4] = JavaUtils.sumCheck(bArr, 4);
        bArr[5] = -86;
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control == null || !serial232Control.isOpen()) {
            return;
        }
        LogUtils.d("DVRProtocol", "send:" + JavaUtils.bytesToHexString(bArr, 6));
        this.serial232Control.send(bArr);
    }

    private class HandshakeThread extends Thread {
        private boolean suspend = false;

        private HandshakeThread() {
        }

        public void setSuspend(boolean z) {
            this.suspend = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            while (!this.suspend) {
                DVRProtocol.this.sendHandshake((byte) 1);
                if (System.currentTimeMillis() - DVRProtocol.this.intervalTime > 42000) {
                    if (DVRProtocol.this.dvrCallback != null) {
                        DVRProtocol.this.dvrState = false;
                        DVRProtocol.this.dvrCallback.onResult("无效");
                    }
                    DVRProtocol.this.handshakeTime = 8000;
                }
                try {
                    Thread.sleep(DVRProtocol.this.handshakeTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setDvrCallback(DVRCallback dVRCallback) {
        this.dvrCallback = dVRCallback;
    }

    private class Serial232Control extends SerialHelper {
        private StringBuffer receivedBuffer;

        public Serial232Control(String str) {
            super("/dev/ttyS3", str);
            this.receivedBuffer = new StringBuffer();
        }

        @Override // com.lianhexinye.serialprot.SerialHelper
        public void onDataReceived(ComBean comBean) {
            this.receivedBuffer.append(JavaUtils.bytesToHexString(comBean.bRec, comBean.bRec.length));
            if (this.receivedBuffer.length() >= 8) {
                short sByteToShort_HL = JavaUtils.byteToShort_HL(JavaUtils.hexToBytes(this.receivedBuffer.substring(4, 8)), 0);
                if (sByteToShort_HL >= 0) {
                    int i = (sByteToShort_HL + 6) * 2;
                    if (this.receivedBuffer.length() >= i) {
                        if (!this.receivedBuffer.toString().toLowerCase().substring(0, 8).trim().equals("55020000")) {
                            if (DVRProtocol.this.dvrCallback != null) {
                                DVRProtocol.this.dvrCallback.onResult(this.receivedBuffer.substring(0, i));
                            }
                        } else {
                            DVRProtocol.this.intervalTime = System.currentTimeMillis();
                            DVRProtocol.this.handshakeTime = 20000;
                            if (DVRProtocol.this.dvrCallback != null) {
                                DVRProtocol.this.dvrCallback.onResult("有效");
                            }
                            DVRProtocol.this.dvrState = true;
                        }
                        String strSubstring = this.receivedBuffer.substring(i);
                        StringBuffer stringBuffer = new StringBuffer();
                        this.receivedBuffer = stringBuffer;
                        stringBuffer.append(strSubstring);
                        return;
                    }
                    if (this.receivedBuffer.substring(0, 2).equals("55")) {
                        return;
                    }
                    this.receivedBuffer = new StringBuffer();
                    return;
                }
                this.receivedBuffer = new StringBuffer();
            }
        }
    }

    public boolean isOpen() {
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control == null) {
            return false;
        }
        serial232Control.isOpen();
        return false;
    }

    public void close232SerialPort() {
        HandshakeThread handshakeThread = this.handshakeThread;
        if (handshakeThread != null) {
            handshakeThread.setSuspend(true);
        }
        Serial232Control serial232Control = this.serial232Control;
        if (serial232Control == null || !serial232Control.isOpen()) {
            return;
        }
        this.serial232Control.close();
    }
}
