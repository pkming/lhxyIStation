package com.lianhexinye.m90.serialport;

import android.util.Log;
import com.lianhexinye.m90.common.utils.JavaUtils;
import java.util.Date;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;

/* JADX INFO: loaded from: classes2.dex */
public class JHYProtocol {
    public static byte[] createEmptyCount() {
        try {
            byte[] bArr = new byte[7];
            bArr[0] = 99;
            bArr[1] = 0;
            bArr[2] = 2;
            bArr[3] = 7;
            bArr[4] = 0;
            bArr[5] = (byte) Integer.parseInt(Integer.toHexString((~JavaUtils.sumCheckINT(bArr, 1, 5)) + 1), 16);
            bArr[6] = GreaterThanPtg.sid;
            Log.d("JHYProtocol", "createEmptyCount send:" + JavaUtils.bytesToHexString(bArr, 7));
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] createCurrentCount() {
        try {
            byte[] bArrHexToBytes = JavaUtils.hexToBytes(Integer.toHexString((~JavaUtils.sumCheckINT(bArr, 1, 4)) + 1));
            byte[] bArr = {99, 0, 1, 40, bArrHexToBytes[bArrHexToBytes.length - 1], GreaterThanPtg.sid};
            Log.d("JHYProtocol", "createCurrentCount send:" + JavaUtils.bytesToHexString(bArr, 6));
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] createCountState(int i) {
        try {
            byte[] bArr = new byte[6];
            bArr[0] = 99;
            if (i == 1) {
                bArr[1] = -96;
            } else {
                bArr[1] = -95;
            }
            bArr[2] = 1;
            bArr[3] = 6;
            bArr[4] = (byte) Integer.parseInt(Integer.toHexString((~JavaUtils.sumCheckINT(bArr, 1, 4)) + 1), 16);
            bArr[5] = GreaterThanPtg.sid;
            Log.d("JHYProtocol", "createCountState send:" + JavaUtils.bytesToHexString(bArr, 6));
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] createCountState() {
        try {
            String[] strArrSplit = JavaUtils.dateToString(new Date(), "yy:MM:dd:HH:mm:ss").split(":");
            byte[] bArr = new byte[12];
            bArr[0] = 99;
            bArr[1] = 0;
            bArr[2] = 7;
            bArr[3] = 5;
            bArr[4] = (byte) Integer.parseInt(Integer.toHexString(Integer.valueOf(strArrSplit[0]).intValue()), 16);
            bArr[5] = (byte) Integer.parseInt(Integer.toHexString(Integer.valueOf(strArrSplit[1]).intValue()), 16);
            bArr[6] = (byte) Integer.parseInt(Integer.toHexString(Integer.valueOf(strArrSplit[2]).intValue()), 16);
            bArr[7] = (byte) Integer.parseInt(Integer.toHexString(Integer.valueOf(strArrSplit[3]).intValue()), 16);
            bArr[8] = (byte) Integer.parseInt(Integer.toHexString(Integer.valueOf(strArrSplit[4]).intValue()), 16);
            bArr[9] = (byte) Integer.parseInt(Integer.toHexString(Integer.valueOf(strArrSplit[5]).intValue()), 16);
            bArr[10] = (byte) Integer.parseInt(Integer.toHexString((~JavaUtils.sumCheckINT(bArr, 1, 10)) + 1), 16);
            bArr[11] = GreaterThanPtg.sid;
            Log.d("JHYProtocol", "createCountState send:" + JavaUtils.bytesToHexString(bArr, 12));
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
