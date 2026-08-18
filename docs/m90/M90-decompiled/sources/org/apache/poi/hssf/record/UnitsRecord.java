package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class UnitsRecord extends Record {
    public static final short sid = 4097;
    private short field_1_units;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public UnitsRecord() {
    }

    public UnitsRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public UnitsRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4097) {
            throw new RecordFormatException("Not a Units record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_units = LittleEndian.getShort(bArr, 0 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[UNITS]\n");
        stringBuffer.append("    .units                = ").append("0x").append(HexDump.toHex(getUnits())).append(" (").append((int) getUnits()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/UNITS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_units);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        UnitsRecord unitsRecord = new UnitsRecord();
        unitsRecord.field_1_units = this.field_1_units;
        return unitsRecord;
    }

    public short getUnits() {
        return this.field_1_units;
    }

    public void setUnits(short s) {
        this.field_1_units = s;
    }
}
