package org.apache.poi.hssf.record.formula;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class FuncVarPtg extends AbstractFunctionPtg {
    private static final int SIZE = 4;
    public static final byte sid = 34;

    @Override // org.apache.poi.hssf.record.formula.AbstractFunctionPtg, org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 4;
    }

    private FuncVarPtg() {
    }

    public FuncVarPtg(byte[] bArr, int i) {
        int i2 = i + 1;
        this.field_1_num_args = bArr[i2 + 0];
        this.field_2_fnc_index = LittleEndian.getShort(bArr, i2 + 1);
    }

    public FuncVarPtg(String str, byte b) {
        this.field_1_num_args = b;
        this.field_2_fnc_index = lookupIndex(str);
        try {
            this.returnClass = ((Byte) AbstractFunctionPtg.functionData[this.field_2_fnc_index][0]).byteValue();
            this.paramClass = (byte[]) AbstractFunctionPtg.functionData[this.field_2_fnc_index][1];
        } catch (NullPointerException unused) {
            this.returnClass = (byte) 32;
            this.paramClass = new byte[]{32};
        }
    }

    @Override // org.apache.poi.hssf.record.formula.AbstractFunctionPtg, org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = (byte) (this.ptgClass + 34);
        bArr[i + 1] = this.field_1_num_args;
        LittleEndian.putShort(bArr, i + 2, this.field_2_fnc_index);
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public int getNumberOfOperands() {
        return this.field_1_num_args;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        FuncVarPtg funcVarPtg = new FuncVarPtg();
        funcVarPtg.field_1_num_args = this.field_1_num_args;
        funcVarPtg.field_2_fnc_index = this.field_2_fnc_index;
        funcVarPtg.setClass(this.ptgClass);
        return funcVarPtg;
    }

    @Override // org.apache.poi.hssf.record.formula.AbstractFunctionPtg, org.apache.poi.hssf.record.formula.Ptg
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<FunctionVarPtg>").append("\n").append("   field_1_num_args=").append((int) this.field_1_num_args).append("\n").append("      name         =").append(lookupName(this.field_2_fnc_index)).append("\n").append("   field_2_fnc_index=").append((int) this.field_2_fnc_index).append("\n").append("</FunctionPtg>");
        return stringBuffer.toString();
    }
}
