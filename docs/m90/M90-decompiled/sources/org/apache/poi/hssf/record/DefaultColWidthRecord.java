package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultColWidthRecord extends Record {
    public static final short sid = 85;
    private short field_1_col_width;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 85;
    }

    public DefaultColWidthRecord() {
    }

    public DefaultColWidthRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DefaultColWidthRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 85) {
            throw new RecordFormatException("NOT A DefaultColWidth RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_col_width = LittleEndian.getShort(bArr, i + 0);
    }

    public void setColWidth(short s) {
        this.field_1_col_width = s;
    }

    public short getColWidth() {
        return this.field_1_col_width;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DEFAULTCOLWIDTH]\n");
        stringBuffer.append("    .colwidth      = ").append(Integer.toHexString(getColWidth())).append("\n");
        stringBuffer.append("[/DEFAULTCOLWIDTH]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 85);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getColWidth());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        DefaultColWidthRecord defaultColWidthRecord = new DefaultColWidthRecord();
        defaultColWidthRecord.field_1_col_width = this.field_1_col_width;
        return defaultColWidthRecord;
    }
}
