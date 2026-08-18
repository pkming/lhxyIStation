package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;

/* JADX INFO: loaded from: classes3.dex */
public class LessThanPtg extends OperationPtg {
    private static final String LESSTHAN = "<";
    public static final int SIZE = 1;
    public static final byte sid = 9;

    @Override // org.apache.poi.hssf.record.formula.OperationPtg, org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public int getNumberOfOperands() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 1;
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public int getType() {
        return 1;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return LESSTHAN;
    }

    public LessThanPtg() {
    }

    public LessThanPtg(byte[] bArr, int i) {
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = 9;
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public String toFormulaString(String[] strArr) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(strArr[0]);
        stringBuffer.append(LESSTHAN);
        stringBuffer.append(strArr[1]);
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        return new LessThanPtg();
    }
}
