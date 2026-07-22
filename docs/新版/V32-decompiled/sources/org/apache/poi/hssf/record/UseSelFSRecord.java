package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class UseSelFSRecord extends Record {
    public static final short FALSE = 0;
    public static final short TRUE = 1;
    public static final short sid = 352;
    private short field_1_flag;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public UseSelFSRecord() {
    }

    public UseSelFSRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public UseSelFSRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 352) {
            throw new RecordFormatException("NOT A UseSelFS RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_flag = LittleEndian.getShort(bArr, i + 0);
    }

    public void setFlag(short s) {
        this.field_1_flag = s;
    }

    public short getFlag() {
        return this.field_1_flag;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[USESELFS]\n");
        stringBuffer.append("    .flag            = ").append(Integer.toHexString(getFlag())).append("\n");
        stringBuffer.append("[/USESELFS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getFlag());
        return getRecordSize();
    }
}
