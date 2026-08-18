package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class BookBoolRecord extends Record {
    public static final short sid = 218;
    private short field_1_save_link_values;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public BookBoolRecord() {
    }

    public BookBoolRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public BookBoolRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 218) {
            throw new RecordFormatException("NOT A BOOKBOOL RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_save_link_values = LittleEndian.getShort(bArr, i + 0);
    }

    public void setSaveLinkValues(short s) {
        this.field_1_save_link_values = s;
    }

    public short getSaveLinkValues() {
        return this.field_1_save_link_values;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[BOOKBOOL]\n");
        stringBuffer.append("    .savelinkvalues  = ").append(Integer.toHexString(getSaveLinkValues())).append("\n");
        stringBuffer.append("[/BOOKBOOL]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_save_link_values);
        return getRecordSize();
    }
}
