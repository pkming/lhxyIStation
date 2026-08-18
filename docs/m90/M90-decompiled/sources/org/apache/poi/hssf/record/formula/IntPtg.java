package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class IntPtg extends Ptg {
    public static final int SIZE = 3;
    public static final byte sid = 30;
    private short field_1_value;
    private int strlen = 0;
    private String val;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 3;
    }

    private IntPtg() {
    }

    public IntPtg(byte[] bArr, int i) {
        setValue(LittleEndian.getShort(bArr, i + 1));
    }

    public IntPtg(String str) {
        setValue(Short.parseShort(str));
    }

    public void setValue(short s) {
        this.field_1_value = s;
    }

    public short getValue() {
        return this.field_1_value;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = sid;
        LittleEndian.putShort(bArr, i + 1, getValue());
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return new StringBuffer().append("").append((int) getValue()).toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        IntPtg intPtg = new IntPtg();
        intPtg.field_1_value = this.field_1_value;
        return intPtg;
    }
}
