package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;

/* JADX INFO: loaded from: classes3.dex */
public class MultiplyPtg extends OperationPtg {
    private static final String MULTIPLY = "*";
    public static final int SIZE = 1;
    public static final byte sid = 5;

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public int getNumberOfOperands() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 1;
    }

    public int getStringLength() {
        return 1;
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public int getType() {
        return 1;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return MULTIPLY;
    }

    public MultiplyPtg() {
    }

    public MultiplyPtg(byte[] bArr, int i) {
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = 5;
    }

    public String toFormulaString(Ptg[] ptgArr) {
        StringBuffer stringBuffer = new StringBuffer();
        Workbook workbook = (Workbook) null;
        stringBuffer.append(ptgArr[0].toFormulaString(workbook));
        stringBuffer.append(MULTIPLY);
        stringBuffer.append(ptgArr[1].toFormulaString(workbook));
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public String toFormulaString(String[] strArr) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(strArr[0]);
        stringBuffer.append(toFormulaString((Workbook) null));
        stringBuffer.append(strArr[1]);
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        return new MultiplyPtg();
    }
}
