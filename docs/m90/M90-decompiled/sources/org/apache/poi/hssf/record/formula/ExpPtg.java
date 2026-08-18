package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;

/* JADX INFO: loaded from: classes3.dex */
public class ExpPtg extends Ptg {
    private static final int SIZE = 5;
    public static final short sid = 1;
    private byte[] existing;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 5;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return "NO IDEA SHARED FORMULA EXP PTG";
    }

    public ExpPtg() {
        this.existing = null;
    }

    public ExpPtg(byte[] bArr, int i) {
        this.existing = null;
        byte[] bArr2 = new byte[getSize()];
        this.existing = bArr2;
        System.arraycopy(bArr, i, bArr2, 0, getSize());
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        byte[] bArr2 = this.existing;
        if (bArr2 != null) {
            System.arraycopy(bArr2, 0, bArr, i, bArr2.length);
        }
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        if (this.existing == null) {
            throw new RuntimeException("NO IDEA SHARED FORMULA EXP PTG");
        }
        return new ExpPtg(this.existing, 0);
    }
}
