package org.apache.poi.hssf.record.formula;

/* JADX INFO: loaded from: classes3.dex */
public abstract class OperationPtg extends Ptg {
    public static final int TYPE_BINARY = 1;
    public static final int TYPE_FUNCTION = 2;
    public static final int TYPE_UNARY = 0;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    public abstract int getNumberOfOperands();

    public abstract int getType();

    public abstract String toFormulaString(String[] strArr);
}
