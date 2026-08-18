package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class NumberPtg extends Ptg {
    public static final int SIZE = 9;
    public static final byte sid = 31;
    private double field_1_value;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 9;
    }

    private NumberPtg() {
    }

    public NumberPtg(byte[] bArr, int i) {
        setValue(LittleEndian.getDouble(bArr, i + 1));
    }

    public NumberPtg(String str) {
        setValue(Double.parseDouble(str));
    }

    public void setValue(double d) {
        this.field_1_value = d;
    }

    public double getValue() {
        return this.field_1_value;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = sid;
        LittleEndian.putDouble(bArr, i + 1, getValue());
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return new StringBuffer().append("").append(getValue()).toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        NumberPtg numberPtg = new NumberPtg();
        numberPtg.field_1_value = this.field_1_value;
        return numberPtg;
    }
}
