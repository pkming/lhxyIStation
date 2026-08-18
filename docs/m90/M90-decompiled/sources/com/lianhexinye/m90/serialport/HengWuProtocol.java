package com.lianhexinye.m90.serialport;

import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.language.SPUtil;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.MaintenanceModel;
import java.io.UnsupportedEncodingException;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class HengWuProtocol implements ProtocolGenerate {
    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createInternalScreen(BusLineModel busLineModel) {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLedAdvInfo(List<MaintenanceModel> list) {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLineState() {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLineState(BusLineModel busLineModel) {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createMaintenanceMsg(MaintenanceModel maintenanceModel) {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createOpenVol() {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createServiceTone(byte b) {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public ProtocolResult createSiteInfo(List<BusLineModel> list) {
        return null;
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createStopVol() {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createNewspaperStation(BusLineModel busLineModel) {
        byte[] bytes;
        try {
            int selectLanguage = SPUtil.getInstance(AppApplication.getContext()).getSelectLanguage();
            if (busLineModel.getStationType() == 0) {
                if (selectLanguage == 5) {
                    bytes = ("Llegar:" + busLineModel.getBusName()).getBytes("gb2312");
                } else if (selectLanguage == 4 || selectLanguage == 3) {
                    bytes = ("ARRIVING:" + busLineModel.getBusName()).getBytes("gb2312");
                } else {
                    bytes = ("本站:" + busLineModel.getBusName()).getBytes("gb2312");
                }
            } else if (selectLanguage == 5) {
                bytes = ("Siguiente parada:" + busLineModel.getBusName()).getBytes("gb2312");
            } else if (selectLanguage == 4 || selectLanguage == 3) {
                bytes = ("NEXT STOP:" + busLineModel.getBusName()).getBytes("gb2312");
            } else {
                bytes = ("下一站:" + busLineModel.getBusName()).getBytes("gb2312");
            }
            byte[] bArr = new byte[100];
            int i = 0;
            for (byte b : bytes) {
                if ("7e".equals(JavaUtils.bytesToHexString(new byte[]{b}, 1).toLowerCase())) {
                    bArr[i] = 125;
                    i++;
                    bArr[i] = 1;
                } else if ("7d".equals(JavaUtils.bytesToHexString(new byte[]{b}, 1).toLowerCase())) {
                    bArr[i] = 125;
                    i++;
                    bArr[i] = 0;
                } else {
                    bArr[i] = b;
                }
                i++;
            }
            byte[] bArr2 = new byte[i];
            for (int i2 = 0; i2 < i; i2++) {
                bArr2[i2] = bArr[i2];
            }
            int i3 = i + 40;
            byte[] bArr3 = new byte[i3];
            bArr3[0] = 126;
            bArr3[1] = 67;
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(i + 36);
            bArr3[2] = bArrIntToBytesBig[2];
            bArr3[3] = bArrIntToBytesBig[3];
            bArr3[4] = 0;
            bArr3[5] = 0;
            byte[] bytes2 = "SHOW".getBytes("gb2312");
            int i4 = 0;
            for (int i5 = 6; i5 < bytes2.length + 6; i5++) {
                bArr3[i5] = bytes2[i4];
                i4++;
            }
            bArr3[bytes2.length + 6] = 1;
            bArr3[bytes2.length + 7] = 3;
            bArr3[bytes2.length + 8] = 0;
            bArr3[bytes2.length + 9] = 0;
            bArr3[bytes2.length + 10] = 0;
            bArr3[bytes2.length + 11] = 0;
            bArr3[bytes2.length + 12] = 0;
            bArr3[bytes2.length + 13] = 0;
            bArr3[bytes2.length + 14] = 3;
            bArr3[bytes2.length + 15] = 1;
            bArr3[bytes2.length + 16] = 0;
            bArr3[bytes2.length + 17] = 0;
            bArr3[bytes2.length + 18] = 0;
            bArr3[bytes2.length + 19] = 0;
            bArr3[bytes2.length + 20] = 0;
            bArr3[bytes2.length + 21] = 16;
            bArr3[bytes2.length + 22] = 2;
            bArr3[bytes2.length + 23] = 0;
            bArr3[bytes2.length + 24] = 0;
            bArr3[bytes2.length + 25] = 1;
            bArr3[bytes2.length + 26] = 0;
            bArr3[bytes2.length + 27] = 5;
            bArr3[bytes2.length + 28] = 1;
            bArr3[bytes2.length + 29] = 0;
            bArr3[bytes2.length + 30] = 0;
            bArr3[bytes2.length + 31] = 0;
            bArr3[bytes2.length + 32] = (byte) Integer.parseInt(Integer.toHexString(i), 16);
            int i6 = 0;
            for (int length = bytes2.length + 33; length < bytes2.length + 33 + i; length++) {
                bArr3[length] = bArr2[i6];
                i6++;
            }
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(String.format("0x%04x", Integer.valueOf(JavaUtils.crcXModem(bArr3, 1, bytes2.length + 32 + i))).substring(2));
            bArr3[bytes2.length + 33 + i] = bArrHexStringToByteArray[0];
            bArr3[bytes2.length + 34 + i] = bArrHexStringToByteArray[1];
            bArr3[bytes2.length + 35 + i] = 126;
            LogUtils.d("HengWuProtocol", "createNewspaperStation send:" + JavaUtils.bytesToHexString(bArr3, i3));
            return bArr3;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLineName(BusLineModel busLineModel) {
        try {
            byte[] bArr = new byte[100];
            int i = 0;
            for (byte b : busLineModel.getBusLineName().getBytes("gb2312")) {
                if ("7e".equals(JavaUtils.bytesToHexString(new byte[]{b}, 1).toLowerCase())) {
                    bArr[i] = 125;
                    i++;
                    bArr[i] = 1;
                } else if ("7d".equals(JavaUtils.bytesToHexString(new byte[]{b}, 1).toLowerCase())) {
                    bArr[i] = 125;
                    i++;
                    bArr[i] = 0;
                } else {
                    bArr[i] = b;
                }
                i++;
            }
            byte[] bArr2 = new byte[i];
            for (int i2 = 0; i2 < i; i2++) {
                bArr2[i2] = bArr[i2];
            }
            byte[] bArr3 = new byte[100];
            int i3 = 0;
            for (byte b2 : busLineModel.getStartBusName().getBytes("gb2312")) {
                if ("7e".equals(JavaUtils.bytesToHexString(new byte[]{b2}, 1).toLowerCase())) {
                    bArr3[i3] = 125;
                    i3++;
                    bArr3[i3] = 1;
                } else if ("7d".equals(JavaUtils.bytesToHexString(new byte[]{b2}, 1).toLowerCase())) {
                    bArr3[i3] = 125;
                    i3++;
                    bArr3[i3] = 0;
                } else {
                    bArr3[i3] = b2;
                }
                i3++;
            }
            byte[] bArr4 = new byte[i3];
            for (int i4 = 0; i4 < i3; i4++) {
                bArr4[i4] = bArr3[i4];
            }
            byte[] bArr5 = new byte[100];
            int i5 = 0;
            for (byte b3 : busLineModel.getEndBusName().getBytes("gb2312")) {
                if ("7e".equals(JavaUtils.bytesToHexString(new byte[]{b3}, 1).toLowerCase())) {
                    bArr5[i5] = 125;
                    int i6 = i5 + 1;
                    bArr5[i6] = 1;
                    i5 = i6 + 1;
                } else {
                    if ("7d".equals(JavaUtils.bytesToHexString(new byte[]{b3}, 1).toLowerCase())) {
                        bArr5[i5] = 125;
                        i5++;
                        bArr5[i5] = 0;
                    } else {
                        bArr5[i5] = b3;
                    }
                    i5++;
                }
            }
            byte[] bArr6 = new byte[i5];
            for (int i7 = 0; i7 < i5; i7++) {
                bArr6[i7] = bArr5[i7];
            }
            int i8 = i + 19 + i3 + i5;
            byte[] bArr7 = new byte[i8];
            bArr7[0] = 126;
            bArr7[1] = 67;
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(i + 15 + i3 + i5);
            bArr7[2] = bArrIntToBytesBig[2];
            bArr7[3] = bArrIntToBytesBig[3];
            bArr7[4] = 0;
            bArr7[5] = 0;
            byte[] bytes = "LIN1".getBytes("gb2312");
            int i9 = 0;
            for (int i10 = 6; i10 < bytes.length + 6; i10++) {
                bArr7[i10] = bytes[i9];
                i9++;
            }
            bArr7[bytes.length + 6] = -128;
            bArr7[bytes.length + 7] = (byte) Integer.parseInt(Integer.toHexString(i), 16);
            int i11 = 0;
            for (int length = bytes.length + 8; length < bytes.length + 8 + i; length++) {
                bArr7[length] = bArr2[i11];
                i11++;
            }
            bArr7[bytes.length + 8 + i] = (byte) Integer.parseInt(Integer.toHexString(i3), 16);
            int i12 = 0;
            for (int length2 = bytes.length + 9 + i; length2 < bytes.length + 9 + i + i3; length2++) {
                bArr7[length2] = bArr4[i12];
                i12++;
            }
            bArr7[bytes.length + 9 + i + i3] = 0;
            bArr7[bytes.length + 10 + i + i3] = (byte) Integer.parseInt(Integer.toHexString(i5), 16);
            int i13 = 0;
            for (int length3 = bytes.length + 11 + i + i3; length3 < bytes.length + 11 + i + i3 + i5; length3++) {
                bArr7[length3] = bArr6[i13];
                i13++;
            }
            bArr7[bytes.length + 11 + i + i3 + i5] = 0;
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(String.format("0x%04x", Integer.valueOf(JavaUtils.crcXModem(bArr7, 1, bytes.length + 11 + i + i3 + i5))).substring(2));
            bArr7[bytes.length + 12 + i + i3 + i5] = bArrHexStringToByteArray[0];
            bArr7[bytes.length + 13 + i + i3 + i5] = bArrHexStringToByteArray[1];
            bArr7[bytes.length + 14 + i + i3 + i5] = 126;
            LogUtils.d("HengWuProtocol", "createLineName send:" + JavaUtils.bytesToHexString(bArr7, i8));
            return bArr7;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
