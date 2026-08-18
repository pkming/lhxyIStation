package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class CalcCountRecord extends Record {
    public static final short sid = 12;
    private short field_1_iterations;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 12;
    }

    public CalcCountRecord() {
    }

    public CalcCountRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public CalcCountRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 12) {
            throw new RecordFormatException("NOT An Calc Count RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_iterations = LittleEndian.getShort(bArr, i + 0);
    }

    public void setIterations(short s) {
        this.field_1_iterations = s;
    }

    public short getIterations() {
        return this.field_1_iterations;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[CALCCOUNT]\n");
        stringBuffer.append("    .iterations     = ").append(Integer.toHexString(getIterations())).append("\n");
        stringBuffer.append("[/CALCCOUNT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 12);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getIterations());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        CalcCountRecord calcCountRecord = new CalcCountRecord();
        calcCountRecord.field_1_iterations = this.field_1_iterations;
        return calcCountRecord;
    }
}
