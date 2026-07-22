package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class BackupRecord extends Record {
    public static final short sid = 64;
    private short field_1_backup;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 64;
    }

    public BackupRecord() {
    }

    public BackupRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public BackupRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 64) {
            throw new RecordFormatException("NOT A BACKUP RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_backup = LittleEndian.getShort(bArr, i + 0);
    }

    public void setBackup(short s) {
        this.field_1_backup = s;
    }

    public short getBackup() {
        return this.field_1_backup;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[BACKUP]\n");
        stringBuffer.append("    .backup          = ").append(Integer.toHexString(getBackup())).append("\n");
        stringBuffer.append("[/BACKUP]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 64);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getBackup());
        return getRecordSize();
    }
}
