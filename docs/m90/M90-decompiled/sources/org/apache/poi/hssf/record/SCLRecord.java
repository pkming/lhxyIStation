package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SCLRecord extends Record {
    public static final short sid = 160;
    private short field_1_numerator;
    private short field_2_denominator;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 8;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 160;
    }

    public SCLRecord() {
    }

    public SCLRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SCLRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 160) {
            throw new RecordFormatException("Not a SCL record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_numerator = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_denominator = LittleEndian.getShort(bArr, 2 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SCL]\n");
        stringBuffer.append("    .numerator            = ").append("0x").append(HexDump.toHex(getNumerator())).append(" (").append((int) getNumerator()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .denominator          = ").append("0x").append(HexDump.toHex(getDenominator())).append(" (").append((int) getDenominator()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/SCL]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 160);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_numerator);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_denominator);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SCLRecord sCLRecord = new SCLRecord();
        sCLRecord.field_1_numerator = this.field_1_numerator;
        sCLRecord.field_2_denominator = this.field_2_denominator;
        return sCLRecord;
    }

    public short getNumerator() {
        return this.field_1_numerator;
    }

    public void setNumerator(short s) {
        this.field_1_numerator = s;
    }

    public short getDenominator() {
        return this.field_2_denominator;
    }

    public void setDenominator(short s) {
        this.field_2_denominator = s;
    }
}
