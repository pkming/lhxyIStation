package org.apache.poi.hssf.record.formula;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class FuncPtg extends AbstractFunctionPtg {
    public static final int SIZE = 3;
    public static final byte sid = 33;
    private int numParams;

    @Override // org.apache.poi.hssf.record.formula.AbstractFunctionPtg, org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 3;
    }

    private FuncPtg() {
        this.numParams = 0;
    }

    public FuncPtg(byte[] bArr, int i) {
        this.numParams = 0;
        this.field_2_fnc_index = LittleEndian.getShort(bArr, i + 1 + 0);
        try {
            this.numParams = ((Integer) AbstractFunctionPtg.functionData[this.field_2_fnc_index][2]).intValue();
        } catch (NullPointerException unused) {
            this.numParams = 0;
        }
    }

    @Override // org.apache.poi.hssf.record.formula.AbstractFunctionPtg, org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = (byte) (this.ptgClass + 33);
        LittleEndian.putShort(bArr, i + 1, this.field_2_fnc_index);
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public int getNumberOfOperands() {
        return this.numParams;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        FuncPtg funcPtg = new FuncPtg();
        funcPtg.field_2_fnc_index = this.field_2_fnc_index;
        funcPtg.setClass(this.ptgClass);
        return funcPtg;
    }

    @Override // org.apache.poi.hssf.record.formula.AbstractFunctionPtg, org.apache.poi.hssf.record.formula.Ptg
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<FunctionPtg>").append("\n").append("   numArgs(internal)=").append(this.numParams).append("\n").append("      name         =").append(lookupName(this.field_2_fnc_index)).append("\n").append("   field_2_fnc_index=").append((int) this.field_2_fnc_index).append("\n").append("</FunctionPtg>");
        return stringBuffer.toString();
    }
}
