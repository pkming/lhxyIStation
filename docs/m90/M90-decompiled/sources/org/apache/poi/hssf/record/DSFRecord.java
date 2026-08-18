package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DSFRecord extends Record {
    public static final short sid = 353;
    private short field_1_dsf;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public DSFRecord() {
    }

    public DSFRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DSFRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 353) {
            throw new RecordFormatException("NOT A DSF RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_dsf = LittleEndian.getShort(bArr, i + 0);
    }

    public void setDsf(short s) {
        this.field_1_dsf = s;
    }

    public short getDsf() {
        return this.field_1_dsf;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DSF]\n");
        stringBuffer.append("    .isDSF           = ").append(Integer.toHexString(getDsf())).append("\n");
        stringBuffer.append("[/DSF]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getDsf());
        return getRecordSize();
    }
}
