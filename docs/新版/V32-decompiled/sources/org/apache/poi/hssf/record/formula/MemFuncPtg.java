package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class MemFuncPtg extends ControlPtg {
    public static final byte sid = 41;
    private short field_1_len_ref_subexpression;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 0;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 3;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return "";
    }

    public MemFuncPtg() {
        this.field_1_len_ref_subexpression = (short) 0;
    }

    public MemFuncPtg(byte[] bArr, int i) {
        this.field_1_len_ref_subexpression = (short) 0;
        this.field_1_len_ref_subexpression = LittleEndian.getShort(bArr, i + 1 + 0);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = sid;
        LittleEndian.putShort(bArr, i + 1, this.field_1_len_ref_subexpression);
    }

    public int getNumberOfOperands() {
        return this.field_1_len_ref_subexpression;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        MemFuncPtg memFuncPtg = new MemFuncPtg();
        memFuncPtg.field_1_len_ref_subexpression = this.field_1_len_ref_subexpression;
        return memFuncPtg;
    }

    public int getLenRefSubexpression() {
        return this.field_1_len_ref_subexpression;
    }

    public void setLenRefSubexpression(int i) {
        this.field_1_len_ref_subexpression = (short) i;
    }
}
