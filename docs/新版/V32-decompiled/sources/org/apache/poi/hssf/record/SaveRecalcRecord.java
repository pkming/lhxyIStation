package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SaveRecalcRecord extends Record {
    public static final short sid = 95;
    private short field_1_recalc;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 95;
    }

    public SaveRecalcRecord() {
    }

    public SaveRecalcRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SaveRecalcRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 95) {
            throw new RecordFormatException("NOT A Save Recalc RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_recalc = LittleEndian.getShort(bArr, i + 0);
    }

    public void setRecalc(boolean z) {
        this.field_1_recalc = (short) (!z ? 0 : 1);
    }

    public boolean getRecalc() {
        return this.field_1_recalc == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SAVERECALC]\n");
        stringBuffer.append("    .recalc         = ").append(getRecalc()).append("\n");
        stringBuffer.append("[/SAVERECALC]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 95);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_recalc);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SaveRecalcRecord saveRecalcRecord = new SaveRecalcRecord();
        saveRecalcRecord.field_1_recalc = this.field_1_recalc;
        return saveRecalcRecord;
    }
}
