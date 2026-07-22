package com.lianhexinye.m90.serialport;

import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.MaintenanceModel;
import java.util.List;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class LEDProtocol implements ProtocolGenerate {
    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createInternalScreen(BusLineModel busLineModel) {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLedAdvInfo(List<MaintenanceModel> list) {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLineName(BusLineModel busLineModel) {
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
        byte[] bArr = new byte[6];
        bArr[0] = 126;
        bArr[1] = (byte) Integer.parseInt(Integer.toHexString(busLineModel.getBusNo()), 16);
        bArr[2] = (byte) Integer.parseInt(Integer.toHexString(busLineModel.getStationType()), 16);
        bArr[3] = (byte) Integer.parseInt(Integer.toHexString(busLineModel.getCountStation()), 16);
        if (busLineModel.getDirection() == 1) {
            bArr[4] = (byte) Integer.parseInt(Integer.toHexString(0), 16);
        } else {
            bArr[4] = (byte) Integer.parseInt(Integer.toHexString(1), 16);
        }
        bArr[5] = 127;
        return bArr;
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createMaintenanceMsg(MaintenanceModel maintenanceModel) {
        try {
            byte[] bytes = maintenanceModel.getContent().getBytes("gb2312");
            int length = bytes.length + 14;
            byte[] bArr = new byte[length];
            bArr[0] = 126;
            bArr[1] = 67;
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(bytes.length + 10);
            bArr[2] = bArrIntToBytesBig[2];
            bArr[3] = bArrIntToBytesBig[3];
            bArr[4] = 0;
            bArr[5] = 0;
            bArr[6] = TarConstants.LF_GNUTYPE_SPARSE;
            bArr[7] = 80;
            bArr[8] = 69;
            bArr[9] = 67;
            bArr[10] = (byte) Integer.parseInt(Integer.toHexString(bytes.length), 16);
            int i = 0;
            for (int i2 = 11; i2 < bytes.length + 11; i2++) {
                bArr[i2] = bytes[i];
                i++;
            }
            byte[] bArrHexStringToByteArray = JavaUtils.hexStringToByteArray(String.format("0x%04x", Integer.valueOf(JavaUtils.crcXModem(bArr, 1, bytes.length + 10))).substring(2));
            bArr[bytes.length + 11] = bArrHexStringToByteArray[0];
            bArr[bytes.length + 12] = bArrHexStringToByteArray[1];
            bArr[bytes.length + 13] = 126;
            LogUtils.d("TongDaProtocol", "createMaintenanceMsg send:" + JavaUtils.bytesToHexString(bArr, length));
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
