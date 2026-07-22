package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.util.HexDump;

/* JADX INFO: loaded from: classes3.dex */
public abstract class Ptg {
    public static final byte CLASS_ARRAY = 64;
    public static final byte CLASS_REF = 0;
    public static final byte CLASS_VALUE = 32;
    protected byte ptgClass = 0;

    public abstract Object clone();

    public abstract byte getDefaultOperandClass();

    public abstract int getSize();

    public abstract String toFormulaString(Workbook workbook);

    public abstract void writeBytes(byte[] bArr, int i);

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x009f  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00a6  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00bb  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x014b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static org.apache.poi.hssf.record.formula.Ptg createPtg(byte[] r2, int r3) {
        /*
            Method dump skipped, instruction units count: 540
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.record.formula.Ptg.createPtg(byte[], int):org.apache.poi.hssf.record.formula.Ptg");
    }

    public final byte[] getBytes() {
        byte[] bArr = new byte[getSize()];
        writeBytes(bArr, 0);
        return bArr;
    }

    public String toDebugString() {
        byte[] bArr = new byte[getSize()];
        writeBytes(bArr, 0);
        try {
            return HexDump.dump(bArr, 0L, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return getClass().toString();
    }

    public void setClass(byte b) {
        this.ptgClass = b;
    }

    public byte getPtgClass() {
        return this.ptgClass;
    }
}
