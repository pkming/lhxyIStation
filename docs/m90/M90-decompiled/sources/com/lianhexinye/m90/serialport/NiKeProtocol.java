package com.lianhexinye.m90.serialport;

import com.lianhexinye.m90.greendao.gen.BusLineModel;
import org.apache.poi.hssf.record.formula.ParenthesisPtg;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;

/* JADX INFO: loaded from: classes2.dex */
public class NiKeProtocol {
    public byte[] createLineState(BusLineModel busLineModel) {
        byte[] bArr = new byte[17];
        bArr[0] = -69;
        bArr[1] = 16;
        bArr[2] = HSSFErrorConstants.ERROR_VALUE;
        bArr[3] = 1;
        bArr[4] = 0;
        bArr[5] = 9;
        bArr[6] = ParenthesisPtg.sid;
        bArr[7] = 5;
        bArr[8] = 7;
        bArr[9] = 17;
        if (busLineModel.getDirection() == 1) {
            bArr[10] = 0;
            bArr[11] = 0;
            bArr[12] = 0;
            bArr[13] = 0;
            bArr[14] = 91;
        } else {
            bArr[10] = -128;
            bArr[11] = 0;
            bArr[12] = 0;
            bArr[13] = 0;
            bArr[14] = -37;
        }
        bArr[15] = 0;
        bArr[16] = 85;
        return bArr;
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x0169 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:58:? A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.List<com.lianhexinye.m90.serialport.ProtocolResult> createLEDFileData(java.io.File r23) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 371
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lianhexinye.m90.serialport.NiKeProtocol.createLEDFileData(java.io.File):java.util.List");
    }
}
