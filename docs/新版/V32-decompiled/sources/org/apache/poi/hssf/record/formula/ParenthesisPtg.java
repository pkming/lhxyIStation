package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;

/* JADX INFO: loaded from: classes3.dex */
public class ParenthesisPtg extends OperationPtg {
    private static final int SIZE = 1;
    public static final byte sid = 21;

    @Override // org.apache.poi.hssf.record.formula.OperationPtg, org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public int getNumberOfOperands() {
        return 1;
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
        return "()";
    }

    public ParenthesisPtg() {
    }

    public ParenthesisPtg(byte[] bArr, int i) {
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = sid;
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public String toFormulaString(String[] strArr) {
        return new StringBuffer().append("(").append(strArr[0]).append(")").toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        return new ParenthesisPtg();
    }
}
