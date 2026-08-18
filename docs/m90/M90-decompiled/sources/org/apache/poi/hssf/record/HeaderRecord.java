package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class HeaderRecord extends Record {
    public static final short sid = 20;
    private byte field_1_header_len;
    private String field_2_header;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 20;
    }

    public HeaderRecord() {
    }

    public HeaderRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public HeaderRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 20) {
            throw new RecordFormatException("NOT A HEADERRECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        if (s > 0) {
            byte b = bArr[i + 0];
            this.field_1_header_len = b;
            this.field_2_header = StringUtil.getFromCompressedUnicode(bArr, i + 3, LittleEndian.ubyteToInt(b));
        }
    }

    public void setHeaderLength(byte b) {
        this.field_1_header_len = b;
    }

    public void setHeader(String str) {
        this.field_2_header = str;
    }

    public short getHeaderLength() {
        return (short) (this.field_1_header_len & 255);
    }

    public String getHeader() {
        return this.field_2_header;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[HEADER]\n");
        stringBuffer.append("    .length         = ").append((int) getHeaderLength()).append("\n");
        stringBuffer.append("    .header         = ").append(getHeader()).append("\n");
        stringBuffer.append("[/HEADER]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        int i2 = getHeaderLength() != 0 ? 7 : 4;
        LittleEndian.putShort(bArr, i + 0, (short) 20);
        LittleEndian.putShort(bArr, i + 2, (short) ((i2 - 4) + getHeaderLength()));
        if (getHeaderLength() > 0) {
            bArr[i + 4] = (byte) getHeaderLength();
            StringUtil.putCompressedUnicode(getHeader(), bArr, i + 7);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getHeaderLength() != 0 ? (short) 7 : (short) 4) + getHeaderLength();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        HeaderRecord headerRecord = new HeaderRecord();
        headerRecord.field_1_header_len = this.field_1_header_len;
        headerRecord.field_2_header = this.field_2_header;
        return headerRecord;
    }
}
