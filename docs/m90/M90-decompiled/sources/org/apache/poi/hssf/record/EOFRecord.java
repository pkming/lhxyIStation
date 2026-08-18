package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EOFRecord extends Record {
    public static final short sid = 10;

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 4;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 10;
    }

    public EOFRecord() {
    }

    public EOFRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public EOFRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 10) {
            throw new RecordFormatException("NOT An EOF RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[EOF]\n");
        stringBuffer.append("[/EOF]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 10);
        LittleEndian.putShort(bArr, i + 2, (short) 0);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        return new EOFRecord();
    }
}
