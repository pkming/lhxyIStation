package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class InterfaceHdrRecord extends Record {
    public static final short CODEPAGE = 1200;
    public static final short sid = 225;
    private short field_1_codepage;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public InterfaceHdrRecord() {
    }

    public InterfaceHdrRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public InterfaceHdrRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 225) {
            throw new RecordFormatException("NOT A INTERFACEHDR RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_codepage = LittleEndian.getShort(bArr, i + 0);
    }

    public void setCodepage(short s) {
        this.field_1_codepage = s;
    }

    public short getCodepage() {
        return this.field_1_codepage;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[INTERFACEHDR]\n");
        stringBuffer.append("    .codepage        = ").append(Integer.toHexString(getCodepage())).append("\n");
        stringBuffer.append("[/INTERFACEHDR]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getCodepage());
        return getRecordSize();
    }
}
