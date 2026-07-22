package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.util.BitField;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class StringPtg extends Ptg {
    public static final int SIZE = 9;
    public static final byte sid = 23;
    BitField fHighByte;
    byte field_1_length;
    byte field_2_options;
    private String field_3_string;

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    private StringPtg() {
        this.fHighByte = new BitField(1);
    }

    public StringPtg(byte[] bArr, int i) {
        BitField bitField = new BitField(1);
        this.fHighByte = bitField;
        int i2 = i + 1;
        this.field_1_length = bArr[i2];
        byte b = bArr[i2 + 1];
        this.field_2_options = b;
        if (bitField.isSet(b)) {
            this.field_3_string = StringUtil.getFromUnicode(bArr, i2 + 2, this.field_1_length);
        } else {
            this.field_3_string = StringUtil.getFromCompressedUnicode(bArr, i2 + 2, this.field_1_length);
        }
    }

    public StringPtg(String str) {
        this.fHighByte = new BitField(1);
        if (str.length() > 255) {
            throw new IllegalArgumentException("String literals in formulas cant be bigger than 255 characters ASCII");
        }
        this.field_2_options = (byte) 0;
        this.fHighByte.setBoolean(0, false);
        this.field_3_string = str;
        this.field_1_length = (byte) str.length();
    }

    public String getValue() {
        return this.field_3_string;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = 23;
        bArr[i + 1] = this.field_1_length;
        byte b = this.field_2_options;
        bArr[i + 2] = b;
        if (this.fHighByte.isSet(b)) {
            StringUtil.putUncompressedUnicode(getValue(), bArr, i + 3);
        } else {
            StringUtil.putCompressedUnicode(getValue(), bArr, i + 3);
        }
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        int i;
        if (this.fHighByte.isSet(this.field_2_options)) {
            i = this.field_1_length * 2;
        } else {
            i = this.field_1_length;
        }
        return i + 3;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return new StringBuffer().append("\"").append(getValue()).append("\"").toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        StringPtg stringPtg = new StringPtg();
        stringPtg.field_1_length = this.field_1_length;
        stringPtg.field_2_options = this.field_2_options;
        stringPtg.field_3_string = this.field_3_string;
        return stringPtg;
    }
}
