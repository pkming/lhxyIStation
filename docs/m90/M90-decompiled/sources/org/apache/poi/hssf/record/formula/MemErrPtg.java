package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class MemErrPtg extends Ptg {
    private static final int SIZE = 7;
    public static final short sid = 39;
    private int field_1_reserved;
    private short field_2_subex_len;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 7;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return "ERR#";
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
    }

    public MemErrPtg() {
    }

    public MemErrPtg(byte[] bArr, int i) {
        this.field_1_reserved = LittleEndian.getInt(bArr, 0);
        this.field_2_subex_len = LittleEndian.getShort(bArr, 4);
    }

    public void setReserved(int i) {
        this.field_1_reserved = i;
    }

    public int getReserved() {
        return this.field_1_reserved;
    }

    public void setSubexpressionLength(short s) {
        this.field_2_subex_len = s;
    }

    public short getSubexpressionLength() {
        return this.field_2_subex_len;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        MemErrPtg memErrPtg = new MemErrPtg();
        memErrPtg.field_1_reserved = this.field_1_reserved;
        memErrPtg.field_2_subex_len = this.field_2_subex_len;
        return memErrPtg;
    }
}
