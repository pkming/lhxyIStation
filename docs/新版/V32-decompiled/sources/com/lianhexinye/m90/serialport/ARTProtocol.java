package com.lianhexinye.m90.serialport;

import android.util.Log;
import com.lianhexinye.m90.common.utils.JavaUtils;
import java.io.UnsupportedEncodingException;
import org.apache.poi.hssf.record.formula.UnaryPlusPtg;

/* JADX INFO: loaded from: classes2.dex */
public class ARTProtocol {
    public static byte[] createDriverAttendance(String str) {
        try {
            byte[] bytes = str.getBytes("gb2312");
            int length = bytes.length + 7;
            byte[] bArr = new byte[length];
            bArr[0] = -69;
            bArr[1] = 16;
            bArr[2] = (byte) Integer.parseInt(Integer.toHexString(bytes.length + 5), 16);
            bArr[3] = UnaryPlusPtg.sid;
            int i = 0;
            for (int i2 = 4; i2 < bytes.length + 4; i2++) {
                bArr[i2] = bytes[i];
                i++;
            }
            String strSubstring = String.format("0x%04x", Integer.valueOf(CRC16.calcCrc16(bArr, 1, bytes.length + 4))).substring(2);
            bArr[bytes.length + 4] = JavaUtils.hexStringToByteArray(strSubstring)[1];
            bArr[bytes.length + 5] = JavaUtils.hexStringToByteArray(strSubstring)[0];
            bArr[bytes.length + 6] = 85;
            Log.d("ARTProtocol", "createDriverAttendance send:" + JavaUtils.bytesToHexString(bArr, length));
            return bArr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
