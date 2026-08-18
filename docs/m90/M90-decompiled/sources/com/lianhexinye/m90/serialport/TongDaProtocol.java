package com.lianhexinye.m90.serialport;

import android.util.Log;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.language.SPUtil;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import com.lianhexinye.m90.greendao.gen.MaintenanceModel;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class TongDaProtocol implements ProtocolGenerate {
    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createMaintenanceMsg(MaintenanceModel maintenanceModel) {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createServiceTone(byte b) {
        return new byte[0];
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createNewspaperStation(BusLineModel busLineModel) {
        int busNo;
        byte[] bytes;
        int countStation;
        try {
            int selectLanguage = SPUtil.getInstance(AppApplication.getContext()).getSelectLanguage();
            if (busLineModel.getStationType() == 0) {
                busNo = busLineModel.getBusNo() | 128;
                if (selectLanguage == 3) {
                    bytes = ("ARRIVING:" + busLineModel.getBusName()).getBytes("gb2312");
                } else {
                    bytes = ("本站:" + busLineModel.getBusName()).getBytes("gb2312");
                }
            } else {
                busNo = busLineModel.getBusNo() | 0;
                if (selectLanguage == 3) {
                    bytes = ("NEXT STOP:" + busLineModel.getBusName()).getBytes("gb2312");
                } else {
                    bytes = ("下一站:" + busLineModel.getBusName()).getBytes("gb2312");
                }
            }
            int length = bytes.length + 10;
            int length2 = bytes.length + 12;
            byte[] bArr = new byte[length2];
            bArr[0] = -69;
            bArr[1] = 16;
            bArr[2] = (byte) Integer.parseInt(Integer.toHexString(length), 16);
            bArr[3] = 8;
            bArr[4] = (byte) Integer.parseInt(Integer.toHexString(busNo), 16);
            if (busLineModel.getDirection() == 1) {
                countStation = busLineModel.getCountStation() | 0;
            } else {
                countStation = busLineModel.getCountStation() | 128;
            }
            bArr[5] = (byte) Integer.parseInt(Integer.toHexString(countStation), 16);
            bArr[6] = 32;
            int i = 0;
            for (int i2 = 7; i2 < bytes.length + 7; i2++) {
                bArr[i2] = bytes[i];
                i++;
            }
            bArr[bytes.length + 7] = 32;
            bArr[bytes.length + 8] = 20;
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(JavaUtils.sumCheckINT(bArr, 1, bytes.length + 9));
            bArr[bytes.length + 9] = bArrLongToBytes_Little[0];
            bArr[bytes.length + 10] = bArrLongToBytes_Little[1];
            bArr[bytes.length + 11] = 85;
            Log.d("TongDaProtocol", "createNewspaperStation send:" + JavaUtils.bytesToHexString(bArr, length2));
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
            int length = bytes.length + 8 + bytes2.length + bytes3.length;
            int length2 = bytes.length + 10 + bytes2.length + bytes3.length;
            byte[] bArr = new byte[length2];
            bArr[0] = -69;
            bArr[1] = 16;
            bArr[2] = (byte) Integer.parseInt(Integer.toHexString(length), 16);
            bArr[3] = 2;
            bArr[4] = (byte) Integer.parseInt(Integer.toHexString(bytes.length), 16);
            int i = 0;
            for (int i2 = 5; i2 < bytes.length + 5; i2++) {
                bArr[i2] = bytes[i];
                i++;
            }
            bArr[bytes.length + 5] = (byte) Integer.parseInt(Integer.toHexString(bytes2.length), 16);
            int i3 = 0;
            for (int length3 = bytes.length + 6; length3 < bytes2.length + 6 + bytes.length; length3++) {
                bArr[length3] = bytes2[i3];
                i3++;
            }
            bArr[bytes2.length + 6 + bytes.length] = (byte) Integer.parseInt(Integer.toHexString(bytes3.length), 16);
            int i4 = 0;
            for (int length4 = bytes2.length + 7 + bytes.length; length4 < bytes3.length + 7 + bytes2.length + bytes.length; length4++) {
                bArr[length4] = bytes3[i4];
                i4++;
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(JavaUtils.sumCheckINT(bArr, 1, bytes3.length + 7 + bytes2.length + bytes.length));
            bArr[bytes3.length + 7 + bytes2.length + bytes.length] = bArrLongToBytes_Little[0];
            bArr[bytes3.length + 8 + bytes2.length + bytes.length] = bArrLongToBytes_Little[1];
            bArr[bytes3.length + 9 + bytes2.length + bytes.length] = 85;
            LogUtils.d("TongDaProtocol", "createLineName send:" + JavaUtils.bytesToHexString(bArr, length2));
            return bArr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLineState(BusLineModel busLineModel) {
        int busNo;
        int i = Calendar.getInstance().get(7);
        String strDateToString = JavaUtils.dateToString(new Date(), "yy/MM/dd/HH/mm/ss");
        byte[] bArr = new byte[17];
        bArr[0] = -69;
        bArr[1] = 16;
        bArr[2] = (byte) Integer.parseInt(Integer.toHexString(15), 16);
        bArr[3] = 1;
        bArr[4] = (byte) Integer.parseInt(strDateToString.split("/")[4], 16);
        bArr[5] = (byte) Integer.parseInt(strDateToString.split("/")[3], 16);
        bArr[6] = (byte) Integer.parseInt(strDateToString.split("/")[2], 16);
        bArr[7] = JavaUtils.getBytes(i - 1)[0];
        bArr[8] = (byte) Integer.parseInt(strDateToString.split("/")[1], 16);
        bArr[9] = (byte) Integer.parseInt(strDateToString.split("/")[0], 16);
        bArr[10] = (byte) Integer.parseInt(String.valueOf(busLineModel.getDirection() == 1 ? 0 : 80), 16);
        if (busLineModel.getStationType() == 0) {
            busNo = busLineModel.getBusNo() | 128;
        } else {
            busNo = busLineModel.getBusNo() | 0;
        }
        bArr[11] = (byte) Integer.parseInt(Integer.toHexString(busNo), 16);
        bArr[12] = 0;
        bArr[13] = 0;
        byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(JavaUtils.sumCheckINT(bArr, 1, 13));
        bArr[14] = bArrLongToBytes_Little[0];
        bArr[15] = bArrLongToBytes_Little[1];
        bArr[16] = 85;
        LogUtils.d("TongDaProtocol", "createLineState send:" + JavaUtils.bytesToHexString(bArr, 17));
        return bArr;
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLineState() {
        int i = Calendar.getInstance().get(7);
        String strDateToString = JavaUtils.dateToString(new Date(), "yy/MM/dd/HH/mm/ss");
        byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(JavaUtils.sumCheckINT(bArr, 1, 13));
        byte[] bArr = {-69, 16, (byte) Integer.parseInt(Integer.toHexString(15), 16), 1, (byte) Integer.parseInt(strDateToString.split("/")[4], 16), (byte) Integer.parseInt(strDateToString.split("/")[3], 16), (byte) Integer.parseInt(strDateToString.split("/")[2], 16), JavaUtils.getBytes(i - 1)[0], (byte) Integer.parseInt(strDateToString.split("/")[1], 16), (byte) Integer.parseInt(strDateToString.split("/")[0], 16), 0, 0, 0, 0, bArrLongToBytes_Little[0], bArrLongToBytes_Little[1], 85};
        LogUtils.d("TongDaProtocol", "createLineState 2 send:" + JavaUtils.bytesToHexString(bArr, 17));
        return bArr;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0059, code lost:
    
        r0.setBusLineModel(r8);
        r1[r4] = -1;
     */
    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.lianhexinye.m90.serialport.ProtocolResult createSiteInfo(java.util.List<com.lianhexinye.m90.greendao.gen.BusLineModel> r14) {
        /*
            Method dump skipped, instruction units count: 233
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.serialport.TongDaProtocol.createSiteInfo(java.util.List):com.lianhexinye.m90.serialport.ProtocolResult");
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createInternalScreen(BusLineModel busLineModel) {
        byte[] bytes;
        try {
            int selectLanguage = SPUtil.getInstance(AppApplication.getContext()).getSelectLanguage();
            if (busLineModel.getStationType() == 0) {
                busLineModel.getBusNo();
                if (selectLanguage == 3) {
                    bytes = ("ARRIVING:" + busLineModel.getBusName()).getBytes("gb2312");
                } else {
                    bytes = ("本站:" + busLineModel.getBusName()).getBytes("gb2312");
                }
            } else {
                busLineModel.getBusNo();
                if (selectLanguage == 3) {
                    bytes = ("NEXT STOP:" + busLineModel.getBusName()).getBytes("gb2312");
                } else {
                    bytes = ("下一站:" + busLineModel.getBusName()).getBytes("gb2312");
                }
            }
            int length = bytes.length + 6;
            int length2 = bytes.length + 8;
            byte[] bArr = new byte[length2];
            bArr[0] = -69;
            bArr[1] = 16;
            bArr[2] = (byte) Integer.parseInt(Integer.toHexString(length), 16);
            bArr[3] = 3;
            int i = 0;
            for (int i2 = 4; i2 < bytes.length + 4; i2++) {
                bArr[i2] = bytes[i];
                i++;
            }
            bArr[bytes.length + 4] = 20;
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(JavaUtils.sumCheckINT(bArr, 1, bytes.length + 5));
            bArr[bytes.length + 5] = bArrLongToBytes_Little[0];
            bArr[bytes.length + 6] = bArrLongToBytes_Little[1];
            bArr[bytes.length + 7] = 85;
            LogUtils.d("TongDaProtocol", "createInternalScreen send:" + JavaUtils.bytesToHexString(bArr, length2));
            return bArr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createStopVol() {
        return new byte[]{-69, 16, 7, 65, TarConstants.LF_NORMAL, -1, -121, 1, 85};
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createOpenVol() {
        return new byte[]{-69, 16, 7, 65, TarConstants.LF_LINK, -1, -120, 1, 85};
    }

    @Override // com.lianhexinye.m90.serialport.ProtocolGenerate
    public byte[] createLedAdvInfo(List<MaintenanceModel> list) {
        try {
            byte[] bArr = new byte[2048];
            int i = 0;
            for (int i2 = 0; i2 < list.size(); i2++) {
                byte[] bytes = list.get(i2).getContent().getBytes("gb2312");
                bArr[i] = (byte) Integer.parseInt(Integer.toHexString(bytes.length), 16);
                int i3 = i + 1;
                for (int i4 = 0; i4 < bytes.length; i4++) {
                    bArr[i3 + i4] = bytes[i4];
                }
                int length = i3 + bytes.length;
                bArr[length] = 0;
                i = length + 1;
            }
            int i5 = i + 8;
            byte[] bArr2 = new byte[i5];
            bArr2[0] = -69;
            bArr2[1] = 16;
            int i6 = i + 6;
            byte[] bArrIntToBytesBig = JavaUtils.intToBytesBig(i6);
            bArr2[2] = bArrIntToBytesBig[2];
            bArr2[3] = bArrIntToBytesBig[3];
            bArr2[4] = 4;
            for (int i7 = 0; i7 < i; i7++) {
                bArr2[i7 + 5] = bArr[i7];
            }
            byte[] bArrLongToBytes_Little = JavaUtils.longToBytes_Little(JavaUtils.sumCheckINT(bArr2, 1, i + 4));
            bArr2[i + 5] = bArrLongToBytes_Little[0];
            bArr2[i6] = bArrLongToBytes_Little[1];
            bArr2[i + 7] = 85;
            LogUtils.d("TongDaProtocol", "createLedAdvInfo send:" + JavaUtils.bytesToHexString(bArr2, i5));
            return bArr2;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
