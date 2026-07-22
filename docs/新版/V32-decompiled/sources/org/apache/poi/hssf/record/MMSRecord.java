package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class MMSRecord extends Record {
    public static final short sid = 193;
    private byte field_1_addMenuCount;
    private byte field_2_delMenuCount;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 193;
    }

    public MMSRecord() {
    }

    public MMSRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public MMSRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 193) {
            throw new RecordFormatException("NOT A MMS RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_addMenuCount = bArr[i + 0];
        this.field_2_delMenuCount = bArr[i + 1];
    }

    public void setAddMenuCount(byte b) {
        this.field_1_addMenuCount = b;
    }

    public void setDelMenuCount(byte b) {
        this.field_2_delMenuCount = b;
    }

    public byte getAddMenuCount() {
        return this.field_1_addMenuCount;
    }

    public byte getDelMenuCount() {
        return this.field_2_delMenuCount;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[MMS]\n");
        stringBuffer.append("    .addMenu        = ").append(Integer.toHexString(getAddMenuCount())).append("\n");
        stringBuffer.append("    .delMenu        = ").append(Integer.toHexString(getDelMenuCount())).append("\n");
        stringBuffer.append("[/MMS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 193);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        bArr[i + 4] = getAddMenuCount();
        bArr[i + 5] = getDelMenuCount();
        return getRecordSize();
    }
}
