package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class FnGroupCountRecord extends Record {
    public static final short COUNT = 14;
    public static final short sid = 156;
    private short field_1_count;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 156;
    }

    public FnGroupCountRecord() {
    }

    public FnGroupCountRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public FnGroupCountRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 156) {
            throw new RecordFormatException("NOT A FNGROUPCOUNT RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_count = LittleEndian.getShort(bArr, i + 0);
    }

    public void setCount(short s) {
        this.field_1_count = s;
    }

    public short getCount() {
        return this.field_1_count;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[FNGROUPCOUNT]\n");
        stringBuffer.append("    .count            = ").append((int) getCount()).append("\n");
        stringBuffer.append("[/FNGROUPCOUNT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 156);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getCount());
        return getRecordSize();
    }
}
