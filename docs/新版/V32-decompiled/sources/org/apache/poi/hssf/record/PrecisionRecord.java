package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class PrecisionRecord extends Record {
    public static final short sid = 14;
    public short field_1_precision;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 14;
    }

    public PrecisionRecord() {
    }

    public PrecisionRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public PrecisionRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 14) {
            throw new RecordFormatException("NOT A PRECISION RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_precision = LittleEndian.getShort(bArr, i + 0);
    }

    public void setFullPrecision(boolean z) {
        if (z) {
            this.field_1_precision = (short) 1;
        } else {
            this.field_1_precision = (short) 0;
        }
    }

    public boolean getFullPrecision() {
        return this.field_1_precision == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[PRECISION]\n");
        stringBuffer.append("    .precision       = ").append(getFullPrecision()).append("\n");
        stringBuffer.append("[/PRECISION]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 14);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_precision);
        return getRecordSize();
    }
}
