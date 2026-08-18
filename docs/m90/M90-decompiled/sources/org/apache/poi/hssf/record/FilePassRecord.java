package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.PwdUtils;

/* JADX INFO: loaded from: classes3.dex */
public class FilePassRecord extends Record {
    public static final short sid = 47;
    private short field_1_key;
    private short field_2_hash;
    private String pwdString;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 10;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 47;
    }

    public FilePassRecord() {
        this.pwdString = null;
    }

    public FilePassRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.pwdString = null;
    }

    public FilePassRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.pwdString = null;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 47) {
            throw new RecordFormatException("NOT A FILEPASS RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_key = LittleEndian.getShort(bArr, i + 2);
        this.field_2_hash = LittleEndian.getShort(bArr, i + 4);
    }

    public short getKey() {
        return this.field_1_key;
    }

    public void setKey(short s) {
        this.field_1_key = s;
    }

    public short getHash() {
        return this.field_2_hash;
    }

    public void setHash(short s) {
        this.field_2_hash = s;
    }

    public void setPassword(String str) {
        this.pwdString = str;
        byte[] bytes = str.getBytes();
        setKey(PwdUtils.getEncryptionKey(bytes));
        setHash(PwdUtils.getPasswordHash(bytes));
    }

    public String getPassword() {
        return this.pwdString;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[FILEPASS]\n");
        stringBuffer.append("    .key            = ").append(Integer.toHexString(getKey())).append("\n");
        stringBuffer.append("    .hash           = ").append(Integer.toHexString(getHash())).append("\n");
        stringBuffer.append("[/FILEPASS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 47);
        LittleEndian.putShort(bArr, i + 2, (short) 6);
        LittleEndian.putShort(bArr, i + 4, (short) 0);
        LittleEndian.putShort(bArr, i + 6, getKey());
        LittleEndian.putShort(bArr, i + 8, getHash());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        return super.clone();
    }
}
