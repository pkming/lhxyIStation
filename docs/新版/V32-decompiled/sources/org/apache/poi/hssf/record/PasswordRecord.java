package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.PwdUtils;

/* JADX INFO: loaded from: classes3.dex */
public class PasswordRecord extends Record {
    public static final short sid = 19;
    private short field_1_password;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 19;
    }

    public PasswordRecord() {
    }

    public PasswordRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public PasswordRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 19) {
            throw new RecordFormatException("NOT A PASSWORD RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_password = LittleEndian.getShort(bArr, i + 0);
    }

    public void setPassword(short s) {
        this.field_1_password = s;
    }

    public void setPassword(String str) {
        this.field_1_password = PwdUtils.getPasswordHash(str.getBytes());
    }

    public short getPassword() {
        return this.field_1_password;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[PASSWORD]\n");
        stringBuffer.append("    .password       = ").append(Integer.toHexString(getPassword())).append("\n");
        stringBuffer.append("[/PASSWORD]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 19);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getPassword());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        PasswordRecord passwordRecord = new PasswordRecord();
        passwordRecord.setPassword(this.field_1_password);
        return passwordRecord;
    }
}
