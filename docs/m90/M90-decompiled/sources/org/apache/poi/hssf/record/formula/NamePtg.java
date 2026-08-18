package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class NamePtg extends Ptg {
    private static final int SIZE = 5;
    public static final short sid = 35;
    private short field_1_label_index;
    private short field_2_zero;
    boolean xtra = false;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 0;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 5;
    }

    private NamePtg() {
    }

    public NamePtg(String str, Workbook workbook) {
        short numNames = (short) (workbook.getNumNames() + 1);
        for (short s = 1; s < numNames; s = (short) (s + 1)) {
            if (str.equals(workbook.getNameRecord(s - 1).getNameText())) {
                this.field_1_label_index = s;
                return;
            }
        }
        NameRecord nameRecord = new NameRecord();
        nameRecord.setNameText(str);
        nameRecord.setNameTextLength((byte) str.length());
        workbook.addName(nameRecord);
        this.field_1_label_index = numNames;
    }

    public NamePtg(byte[] bArr, int i) {
        int i2 = i + 1;
        this.field_1_label_index = LittleEndian.getShort(bArr, i2);
        this.field_2_zero = LittleEndian.getShort(bArr, i2 + 2);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = (byte) (this.ptgClass + 35);
        LittleEndian.putShort(bArr, i + 1, this.field_1_label_index);
        LittleEndian.putShort(bArr, i + 3, this.field_2_zero);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return workbook.getNameRecord(this.field_1_label_index - 1).getNameText();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        NamePtg namePtg = new NamePtg();
        namePtg.field_1_label_index = this.field_1_label_index;
        namePtg.field_2_zero = this.field_2_zero;
        return namePtg;
    }
}
