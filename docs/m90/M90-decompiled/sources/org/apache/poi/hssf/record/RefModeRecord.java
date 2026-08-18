package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class RefModeRecord extends Record {
    public static final short USE_A1_MODE = 1;
    public static final short USE_R1C1_MODE = 0;
    public static final short sid = 15;
    private short field_1_mode;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 15;
    }

    public RefModeRecord() {
    }

    public RefModeRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public RefModeRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 15) {
            throw new RecordFormatException("NOT An RefMode RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_mode = LittleEndian.getShort(bArr, i + 0);
    }

    public void setMode(short s) {
        this.field_1_mode = s;
    }

    public short getMode() {
        return this.field_1_mode;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[REFMODE]\n");
        stringBuffer.append("    .mode           = ").append(Integer.toHexString(getMode())).append("\n");
        stringBuffer.append("[/REFMODE]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 15);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getMode());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        RefModeRecord refModeRecord = new RefModeRecord();
        refModeRecord.field_1_mode = this.field_1_mode;
        return refModeRecord;
    }
}
