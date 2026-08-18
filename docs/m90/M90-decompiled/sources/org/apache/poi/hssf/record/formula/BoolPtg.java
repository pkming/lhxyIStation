package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;

/* JADX INFO: loaded from: classes3.dex */
public class BoolPtg extends Ptg {
    public static final int SIZE = 2;
    public static final byte sid = 29;
    private boolean field_1_value;
    private String val;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 2;
    }

    private BoolPtg() {
    }

    public BoolPtg(byte[] bArr, int i) {
        this.field_1_value = bArr[i + 1] == 1;
    }

    public BoolPtg(String str) {
        this.field_1_value = str.equals("TRUE");
    }

    public void setValue(boolean z) {
        this.field_1_value = z;
    }

    public boolean getValue() {
        return this.field_1_value;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = 29;
        bArr[i + 1] = this.field_1_value ? (byte) 1 : (byte) 0;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return this.field_1_value ? "TRUE" : "FALSE";
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        BoolPtg boolPtg = new BoolPtg();
        boolPtg.field_1_value = this.field_1_value;
        return boolPtg;
    }
}
