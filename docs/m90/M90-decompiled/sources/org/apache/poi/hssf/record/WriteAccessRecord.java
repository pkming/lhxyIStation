package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class WriteAccessRecord extends Record {
    public static final short sid = 92;
    private String field_1_username;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 116;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 92;
    }

    public WriteAccessRecord() {
    }

    public WriteAccessRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public WriteAccessRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 92) {
            throw new RecordFormatException("NOT A WRITEACCESS RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_username = StringUtil.getFromCompressedUnicode(bArr, i + 3, bArr.length - 4);
    }

    public void setUsername(String str) {
        this.field_1_username = str;
    }

    public String getUsername() {
        return this.field_1_username;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[WRITEACCESS]\n");
        stringBuffer.append("    .name            = ").append(this.field_1_username.toString()).append("\n");
        stringBuffer.append("[/WRITEACCESS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        String username = getUsername();
        StringBuffer stringBuffer = new StringBuffer(109);
        stringBuffer.append(username);
        while (stringBuffer.length() < 109) {
            stringBuffer.append(" ");
        }
        String string = stringBuffer.toString();
        UnicodeString unicodeString = new UnicodeString();
        unicodeString.setString(string);
        unicodeString.setOptionFlags((byte) 0);
        unicodeString.setCharCount((short) 4);
        byte[] bArrSerialize = unicodeString.serialize();
        LittleEndian.putShort(bArr, i + 0, (short) 92);
        LittleEndian.putShort(bArr, i + 2, (short) bArrSerialize.length);
        System.arraycopy(bArrSerialize, 0, bArr, i + 4, bArrSerialize.length);
        return getRecordSize();
    }
}
