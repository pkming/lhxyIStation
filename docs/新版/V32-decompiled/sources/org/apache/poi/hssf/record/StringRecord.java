package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class StringRecord extends Record {
    public static final short sid = 519;
    private int field_1_string_length;
    private byte field_2_unicode_flag;
    private String field_3_string;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 519;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    public StringRecord() {
    }

    public StringRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public StringRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 519) {
            throw new RecordFormatException("Not a valid StringRecord");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_string_length = LittleEndian.getUShort(bArr, i + 0);
        this.field_2_unicode_flag = bArr[i + 2];
        if (isUnCompressedUnicode()) {
            this.field_3_string = StringUtil.getFromUnicodeHigh(bArr, i + 3, this.field_1_string_length);
        } else {
            this.field_3_string = StringUtil.getFromCompressedUnicode(bArr, i + 3, this.field_1_string_length);
        }
    }

    private int getStringLength() {
        return this.field_1_string_length;
    }

    private int getStringByteLength() {
        return isUnCompressedUnicode() ? this.field_1_string_length * 2 : this.field_1_string_length;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return getStringByteLength() + 7;
    }

    public boolean isUnCompressedUnicode() {
        return this.field_2_unicode_flag == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 519);
        LittleEndian.putShort(bArr, i + 2, (short) (getStringByteLength() + 3));
        LittleEndian.putUShort(bArr, i + 4, this.field_1_string_length);
        bArr[i + 6] = this.field_2_unicode_flag;
        if (isUnCompressedUnicode()) {
            StringUtil.putUncompressedUnicode(this.field_3_string, bArr, i + 7);
        } else {
            StringUtil.putCompressedUnicode(this.field_3_string, bArr, i + 7);
        }
        return getRecordSize();
    }

    public String getString() {
        return this.field_3_string;
    }

    public void setCompressedFlag(byte b) {
        this.field_2_unicode_flag = b;
    }

    public void setString(String str) {
        this.field_1_string_length = str.length();
        this.field_3_string = str;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[STRING]\n");
        stringBuffer.append("    .string            = ").append(this.field_3_string).append("\n");
        stringBuffer.append("[/STRING]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        StringRecord stringRecord = new StringRecord();
        stringRecord.field_1_string_length = this.field_1_string_length;
        stringRecord.field_2_unicode_flag = this.field_2_unicode_flag;
        stringRecord.field_3_string = this.field_3_string;
        return stringRecord;
    }
}
