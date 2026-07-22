package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class CalcModeRecord extends Record {
    public static final short AUTOMATIC = 1;
    public static final short AUTOMATIC_EXCEPT_TABLES = -1;
    public static final short MANUAL = 0;
    public static final short sid = 13;
    private short field_1_calcmode;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 13;
    }

    public CalcModeRecord() {
    }

    public CalcModeRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public CalcModeRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 13) {
            throw new RecordFormatException("NOT An Calc Mode RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_calcmode = LittleEndian.getShort(bArr, i + 0);
    }

    public void setCalcMode(short s) {
        this.field_1_calcmode = s;
    }

    public short getCalcMode() {
        return this.field_1_calcmode;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[CALCMODE]\n");
        stringBuffer.append("    .calcmode       = ").append(Integer.toHexString(getCalcMode())).append("\n");
        stringBuffer.append("[/CALCMODE]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 13);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getCalcMode());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        CalcModeRecord calcModeRecord = new CalcModeRecord();
        calcModeRecord.field_1_calcmode = this.field_1_calcmode;
        return calcModeRecord;
    }
}
