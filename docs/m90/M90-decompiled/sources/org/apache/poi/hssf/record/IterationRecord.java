package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class IterationRecord extends Record {
    public static final short sid = 17;
    private short field_1_iteration;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 17;
    }

    public IterationRecord() {
    }

    public IterationRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public IterationRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 17) {
            throw new RecordFormatException("NOT An ITERATION RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_iteration = LittleEndian.getShort(bArr, i + 0);
    }

    public void setIteration(boolean z) {
        if (z) {
            this.field_1_iteration = (short) 1;
        } else {
            this.field_1_iteration = (short) 0;
        }
    }

    public boolean getIteration() {
        return this.field_1_iteration == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ITERATION]\n");
        stringBuffer.append("    .iteration      = ").append(getIteration()).append("\n");
        stringBuffer.append("[/ITERATION]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 17);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_iteration);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        IterationRecord iterationRecord = new IterationRecord();
        iterationRecord.field_1_iteration = this.field_1_iteration;
        return iterationRecord;
    }
}
