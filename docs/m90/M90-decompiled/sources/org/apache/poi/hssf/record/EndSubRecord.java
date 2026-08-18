package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EndSubRecord extends SubRecord {
    public static final short sid = 0;

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 4;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 0;
    }

    public EndSubRecord() {
    }

    public EndSubRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public EndSubRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 0) {
            throw new RecordFormatException("Not a End record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ftEnd]\n");
        stringBuffer.append("[/ftEnd]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 0);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        return new EndSubRecord();
    }
}
