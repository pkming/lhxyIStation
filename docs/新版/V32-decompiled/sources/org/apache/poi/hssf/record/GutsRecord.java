package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class GutsRecord extends Record {
    public static final short sid = 128;
    private short field_1_left_row_gutter;
    private short field_2_top_col_gutter;
    private short field_3_row_level_max;
    private short field_4_col_level_max;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 12;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 128;
    }

    public GutsRecord() {
    }

    public GutsRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public GutsRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 128) {
            throw new RecordFormatException("NOT A Guts RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_left_row_gutter = LittleEndian.getShort(bArr, i + 0);
        this.field_2_top_col_gutter = LittleEndian.getShort(bArr, i + 2);
        this.field_3_row_level_max = LittleEndian.getShort(bArr, i + 4);
        this.field_4_col_level_max = LittleEndian.getShort(bArr, i + 6);
    }

    public void setLeftRowGutter(short s) {
        this.field_1_left_row_gutter = s;
    }

    public void setTopColGutter(short s) {
        this.field_2_top_col_gutter = s;
    }

    public void setRowLevelMax(short s) {
        this.field_3_row_level_max = s;
    }

    public void setColLevelMax(short s) {
        this.field_4_col_level_max = s;
    }

    public short getLeftRowGutter() {
        return this.field_1_left_row_gutter;
    }

    public short getTopColGutter() {
        return this.field_2_top_col_gutter;
    }

    public short getRowLevelMax() {
        return this.field_3_row_level_max;
    }

    public short getColLevelMax() {
        return this.field_4_col_level_max;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[GUTS]\n");
        stringBuffer.append("    .leftgutter     = ").append(Integer.toHexString(getLeftRowGutter())).append("\n");
        stringBuffer.append("    .topgutter      = ").append(Integer.toHexString(getTopColGutter())).append("\n");
        stringBuffer.append("    .rowlevelmax    = ").append(Integer.toHexString(getRowLevelMax())).append("\n");
        stringBuffer.append("    .collevelmax    = ").append(Integer.toHexString(getColLevelMax())).append("\n");
        stringBuffer.append("[/GUTS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 128);
        LittleEndian.putShort(bArr, i + 2, (short) 8);
        LittleEndian.putShort(bArr, i + 4, getLeftRowGutter());
        LittleEndian.putShort(bArr, i + 6, getTopColGutter());
        LittleEndian.putShort(bArr, i + 8, getRowLevelMax());
        LittleEndian.putShort(bArr, i + 10, getColLevelMax());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        GutsRecord gutsRecord = new GutsRecord();
        gutsRecord.field_1_left_row_gutter = this.field_1_left_row_gutter;
        gutsRecord.field_2_top_col_gutter = this.field_2_top_col_gutter;
        gutsRecord.field_3_row_level_max = this.field_3_row_level_max;
        gutsRecord.field_4_col_level_max = this.field_4_col_level_max;
        return gutsRecord;
    }
}
