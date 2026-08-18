package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DeltaRecord extends Record {
    public static final double DEFAULT_VALUE = 0.001d;
    public static final short sid = 16;
    private double field_1_max_change;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 12;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 16;
    }

    public DeltaRecord() {
    }

    public DeltaRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DeltaRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 16) {
            throw new RecordFormatException("NOT A DELTA RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_max_change = LittleEndian.getDouble(bArr, i + 0);
    }

    public void setMaxChange(double d) {
        this.field_1_max_change = d;
    }

    public double getMaxChange() {
        return this.field_1_max_change;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DELTA]\n");
        stringBuffer.append("    .maxchange      = ").append(getMaxChange()).append("\n");
        stringBuffer.append("[/DELTA]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 16);
        LittleEndian.putShort(bArr, i + 2, (short) 8);
        LittleEndian.putDouble(bArr, i + 4, getMaxChange());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        DeltaRecord deltaRecord = new DeltaRecord();
        deltaRecord.field_1_max_change = this.field_1_max_change;
        return deltaRecord;
    }
}
