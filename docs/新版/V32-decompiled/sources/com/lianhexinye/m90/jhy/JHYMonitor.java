package com.lianhexinye.m90.jhy;

import com.lianhexinye.m90.common.utils.JavaUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class JHYMonitor {
    public static String allString = null;
    public static int cheneiNumber = 0;
    public static int countJHY = 0;
    public static int hc = 0;
    public static int hcPrevious = 0;
    public static String hcString = null;
    public static int hctmp = 0;
    public static int hj = 0;
    public static int hjPrevious = 0;
    public static String hjString = null;
    public static int hjtmp = 0;
    public static boolean isJHY = false;
    public static int nPortIndex;
    public static int qc;
    public static int qcPrevious;
    public static String qcString;
    public static int qctmp;
    public static int qj;
    public static int qjPrevious;
    public static String qjString;
    public static int qjtmp;
    public static int shangcheNumber;
    public static int xiacheNumber;

    public static void getJHYPortData(byte[] bArr, int i, JHYSerialPortResultListener jHYSerialPortResultListener) {
        ProcessjhyData(JavaUtils.bytesToHexString(bArr, i).toLowerCase(), jHYSerialPortResultListener);
    }

    private static void ProcessjhyData(String str, JHYSerialPortResultListener jHYSerialPortResultListener) {
        if (str != null && str.length() > 0) {
            LogUtils.d("ProcessjhyData", "数据内容 = " + str);
            if (str.startsWith("63") && str.startsWith("63001128") && str.length() >= 44) {
                int iByteToInt_HL = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.substring(8, 16)), 0);
                qjtmp = iByteToInt_HL;
                qj = Math.max(iByteToInt_HL - qjPrevious, 0);
                qjPrevious = qjtmp;
                int iByteToInt_HL2 = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.substring(16, 24)), 0);
                qctmp = iByteToInt_HL2;
                qc = Math.max(iByteToInt_HL2 - qcPrevious, 0);
                qcPrevious = qctmp;
                int iByteToInt_HL3 = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.substring(24, 32)), 0);
                hjtmp = iByteToInt_HL3;
                hj = Math.max(iByteToInt_HL3 - hjPrevious, 0);
                hjPrevious = hjtmp;
                int iByteToInt_HL4 = JavaUtils.byteToInt_HL(JavaUtils.hexToBytes(str.substring(32, 40)), 0);
                hctmp = iByteToInt_HL4;
                hc = Math.max(iByteToInt_HL4 - hcPrevious, 0);
                int i = hctmp;
                hcPrevious = i;
                int i2 = qjtmp + hjtmp;
                shangcheNumber = i2;
                int i3 = qctmp + i;
                xiacheNumber = i3;
                cheneiNumber = Math.max(i2 - i3, 0);
                qjString = String.valueOf(qjtmp);
                qcString = String.valueOf(qctmp);
                hjString = String.valueOf(hjtmp);
                hcString = String.valueOf(hctmp);
                allString = String.valueOf(cheneiNumber);
                LogUtils.d("JHYData", "qjtmp:" + qjtmp + "，qj:" + qj + ",qctmp:" + qctmp + ",qc:" + qc + ",hjtmp:" + hjtmp + ",hj:" + hj + ",hctmp:" + hctmp + ",hc:" + hc + ",shangcheNumber:" + shangcheNumber + ",xiacheNumber:" + xiacheNumber + ",cheneiNumber:" + cheneiNumber);
            }
        }
        String str2 = "FIN" + qjtmp + "FOUT" + qctmp + "BIN" + hjtmp + "BOUT" + hctmp + "ALL" + cheneiNumber;
        if (jHYSerialPortResultListener != null) {
            jHYSerialPortResultListener.onSuccessResult(str2);
        }
    }
}
