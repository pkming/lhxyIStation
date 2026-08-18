package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class NameXPtg extends Ptg {
    private static final int SIZE = 7;
    public static final short sid = 57;
    private short field_1_ixals;
    private short field_2_ilbl;
    private short field_3_reserved;

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
        return "NO IDEA - NAME";
    }

    private NameXPtg() {
    }

    public NameXPtg(String str) {
    }

    public NameXPtg(byte[] bArr, int i) {
        int i2 = i + 1;
        this.field_1_ixals = LittleEndian.getShort(bArr, i2);
        this.field_2_ilbl = LittleEndian.getShort(bArr, i2 + 2);
        this.field_3_reserved = LittleEndian.getShort(bArr, i2 + 4);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = (byte) (this.ptgClass + 57);
        LittleEndian.putShort(bArr, i + 1, this.field_1_ixals);
        LittleEndian.putShort(bArr, i + 3, this.field_2_ilbl);
        LittleEndian.putShort(bArr, i + 5, this.field_3_reserved);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        NameXPtg nameXPtg = new NameXPtg();
        nameXPtg.field_1_ixals = this.field_1_ixals;
        nameXPtg.field_3_reserved = this.field_3_reserved;
        nameXPtg.field_2_ilbl = this.field_2_ilbl;
        nameXPtg.setClass(this.ptgClass);
        return nameXPtg;
    }
}
