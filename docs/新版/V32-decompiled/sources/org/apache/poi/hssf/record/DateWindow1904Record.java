package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DateWindow1904Record extends Record {
    public static final short sid = 34;
    private short field_1_window;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 34;
    }

    public DateWindow1904Record() {
    }

    public DateWindow1904Record(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DateWindow1904Record(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 34) {
            throw new RecordFormatException("NOT A 1904 RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_window = LittleEndian.getShort(bArr, i + 0);
    }

    public void setWindowing(short s) {
        this.field_1_window = s;
    }

    public short getWindowing() {
        return this.field_1_window;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[1904]\n");
        stringBuffer.append("    .is1904          = ").append(Integer.toHexString(getWindowing())).append("\n");
        stringBuffer.append("[/1904]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 34);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getWindowing());
        return getRecordSize();
    }
}
