package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class CodepageRecord extends Record {
    public static final short CODEPAGE = 1200;
    public static final short sid = 66;
    private short field_1_codepage;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 66;
    }

    public CodepageRecord() {
    }

    public CodepageRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public CodepageRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 66) {
            throw new RecordFormatException("NOT A CODEPAGE RECORD");
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
        stringBuffer.append("[CODEPAGE]\n");
        stringBuffer.append("    .codepage        = ").append(Integer.toHexString(getCodepage())).append("\n");
        stringBuffer.append("[/CODEPAGE]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 66);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getCodepage());
        return getRecordSize();
    }
}
