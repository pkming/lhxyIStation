package com.lianhexinye.m90.serialport;

import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.MaintenanceModel;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class LHXYProtocol implements ProtocolGenerate {
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
        return new byte[]{-86, 117, 5, 1, b, GreaterThanPtg.sid, 10};
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createStopVol() {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createNewspaperStation(BusLineModel busLineModel) {
        try {
            byte[] bytes = busLineModel.getBusName().getBytes("gb2312");
            int length = bytes.length + 32;
            byte[] bArr = new byte[length];
            bArr[0] = 126;
            bArr[1] = -1;
            bArr[2] = 1;
            bArr[3] = 3;
            bArr[4] = 1;
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(bytes.length + 23);
            bArr[5] = bArrIntToBytesBig[2];
            bArr[6] = bArrIntToBytesBig[3];
            bArr[7] = 2;
            bArr[8] = 0;
            bArr[9] = 1;
            if (busLineModel.getDirection() == 1) {
                bArr[10] = 1;
            } else {
                bArr[10] = 2;
            }
            bArr[11] = 3;
            bArr[12] = 0;
            bArr[13] = 1;
            bArr[14] = 5;
            bArr[15] = 4;
            bArr[16] = 0;
            bArr[17] = 1;
            if (busLineModel.getStationType() == 0) {
                bArr[18] = 1;
            } else {
                bArr[18] = 2;
            }
            bArr[19] = 5;
            bArr[20] = 0;
            bArr[21] = 1;
            bArr[22] = (byte) Integer.parseInt(Integer.toHexString(busLineModel.getBusNo() + 1), 16);
            bArr[23] = 8;
            bArr[24] = 0;
            bArr[25] = (byte) Integer.parseInt(Integer.toHexString(bytes.length), 16);
            int i = 0;
            for (int i2 = 26; i2 < bytes.length + 26; i2++) {
                bArr[i2] = bytes[i];
                i++;
            }
            bArr[bytes.length + 26] = -96;
            bArr[bytes.length + 27] = 0;
            bArr[bytes.length + 28] = 1;
            bArr[bytes.length + 29] = 3;
            bArr[bytes.length + 30] = 0;
            bArr[bytes.length + 31] = 127;
            LogUtils.d("LHXYProtocol", "createNewspaperStation send:" + JavaUtils.bytesToHexString(bArr, length));
            return bArr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLineName(BusLineModel busLineModel) {
        try {
            byte[] bytes = busLineModel.getBusLineName().getBytes("gb2312");
            byte[] bytes2 = busLineModel.getStartBusName().getBytes("gb2312");
            byte[] bytes3 = busLineModel.getEndBusName().getBytes("gb2312");
            int length = bytes.length + bytes2.length + bytes3.length + bytes2.length + bytes3.length;
            int i = length + 32;
            byte[] bArr = new byte[i];
            bArr[0] = 126;
            bArr[1] = -1;
            bArr[2] = 1;
            bArr[3] = 4;
            bArr[4] = 1;
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(length + 23);
            bArr[5] = bArrIntToBytesBig[2];
            bArr[6] = bArrIntToBytesBig[3];
            bArr[7] = TarConstants.LF_LINK;
            bArr[8] = 0;
            bArr[9] = (byte) Integer.parseInt(Integer.toHexString(bytes.length), 16);
            int i2 = 0;
            for (int i3 = 10; i3 < bytes.length + 10; i3++) {
                bArr[i3] = bytes[i2];
                i2++;
            }
            bArr[bytes.length + 10] = TarConstants.LF_SYMLINK;
            bArr[bytes.length + 11] = 0;
            bArr[bytes.length + 12] = (byte) Integer.parseInt(Integer.toHexString(bytes2.length), 16);
            int i4 = 0;
            for (int length2 = bytes.length + 13; length2 < bytes2.length + 13 + bytes.length; length2++) {
                bArr[length2] = bytes2[i4];
                i4++;
            }
            bArr[bytes2.length + 13 + bytes.length] = TarConstants.LF_CHR;
            bArr[bytes2.length + 14 + bytes.length] = 0;
            bArr[bytes2.length + 15 + bytes.length] = (byte) Integer.parseInt(Integer.toHexString(bytes2.length), 16);
            int i5 = 0;
            for (int length3 = bytes2.length + 16 + bytes.length; length3 < bytes2.length + 16 + bytes2.length + bytes.length; length3++) {
                bArr[length3] = bytes2[i5];
                i5++;
            }
            bArr[bytes2.length + 16 + bytes2.length + bytes.length] = TarConstants.LF_BLK;
            bArr[bytes2.length + 17 + bytes2.length + bytes.length] = 0;
            bArr[bytes2.length + 18 + bytes2.length + bytes.length] = (byte) Integer.parseInt(Integer.toHexString(bytes3.length), 16);
            int i6 = 0;
            for (int length4 = bytes2.length + 19 + bytes2.length + bytes.length; length4 < bytes3.length + 19 + bytes2.length + bytes2.length + bytes.length; length4++) {
                bArr[length4] = bytes3[i6];
                i6++;
            }
            bArr[bytes3.length + 19 + bytes2.length + bytes2.length + bytes.length] = TarConstants.LF_DIR;
            bArr[bytes3.length + 20 + bytes2.length + bytes2.length + bytes.length] = 0;
            bArr[bytes3.length + 21 + bytes2.length + bytes2.length + bytes.length] = (byte) Integer.parseInt(Integer.toHexString(bytes3.length), 16);
            int i7 = 0;
            for (int length5 = bytes3.length + 22 + bytes2.length + bytes2.length + bytes.length; length5 < bytes3.length + 22 + bytes3.length + bytes2.length + bytes2.length + bytes.length; length5++) {
                bArr[length5] = bytes3[i7];
                i7++;
            }
            bArr[bytes3.length + 22 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 2;
            bArr[bytes3.length + 23 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 0;
            bArr[bytes3.length + 24 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 1;
            if (busLineModel.getDirection() == 1) {
                bArr[bytes3.length + 25 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 1;
            } else {
                bArr[bytes3.length + 25 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 2;
            }
            bArr[bytes3.length + 26 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = -71;
            bArr[bytes3.length + 27 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 0;
            bArr[bytes3.length + 28 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 1;
            bArr[bytes3.length + 29 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 3;
            bArr[bytes3.length + 30 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 0;
            bArr[bytes3.length + 31 + bytes3.length + bytes2.length + bytes2.length + bytes.length] = 127;
            LogUtils.d("LHXYProtocol", "createLineName send:" + JavaUtils.bytesToHexString(bArr, i));
            return bArr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public ProtocolResult createSiteInfo(List<BusLineModel> list) {
        ProtocolResult protocolResult = new ProtocolResult();
        byte[] bArr = new byte[65535];
        int length = 0;
        for (int i = 0; i < list.size(); i++) {
            try {
                BusLineModel busLineModel = list.get(i);
                bArr[length] = (byte) Integer.parseInt(Integer.toHexString(busLineModel.getBusNo() + 1), 16);
                byte[] bytes = busLineModel.getBusName().getBytes("gb2312");
                bArr[length + 1] = (byte) Integer.parseInt(Integer.toHexString(bytes.length), 16);
                for (int i2 = 0; i2 < bytes.length; i2++) {
                    bArr[length + 2 + i2] = bytes[i2];
                }
                bArr[bytes.length + length + 2] = 0;
                length += bytes.length + 3;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int i3 = length + 12;
        byte[] bArr2 = new byte[i3];
        bArr2[0] = 126;
        bArr2[1] = -1;
        bArr2[2] = 1;
        bArr2[3] = 16;
        bArr2[4] = 1;
        byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(length + 3);
        bArr2[5] = bArrIntToBytesBig[2];
        bArr2[6] = bArrIntToBytesBig[3];
        if (list.get(0).getDirection() == 1) {
            bArr2[7] = 33;
        } else {
            bArr2[7] = 34;
        }
        byte[] bArrIntToBytesBig2 = JavaUtils.intToBytesBig(length);
        bArr2[8] = bArrIntToBytesBig2[2];
        bArr2[9] = bArrIntToBytesBig2[3];
        for (int i4 = 0; i4 < length; i4++) {
            bArr2[i4 + 10] = bArr[i4];
        }
        bArr2[length + 10] = 0;
        bArr2[length + 11] = 127;
        LogUtils.d("LHXYProtocol", "createSiteInfo send:" + JavaUtils.bytesToHexString(bArr2, i3));
        protocolResult.setmBufferBreak(bArr2);
        return protocolResult;
    }
}
