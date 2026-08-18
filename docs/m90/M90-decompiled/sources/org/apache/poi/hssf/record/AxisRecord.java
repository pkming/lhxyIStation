package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class AxisRecord extends Record {
    public static final short AXIS_TYPE_CATEGORY_OR_X_AXIS = 0;
    public static final short AXIS_TYPE_SERIES_AXIS = 2;
    public static final short AXIS_TYPE_VALUE_AXIS = 1;
    public static final short sid = 4125;
    private short field_1_axisType;
    private int field_2_reserved1;
    private int field_3_reserved2;
    private int field_4_reserved3;
    private int field_5_reserved4;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 22;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public AxisRecord() {
    }

    public AxisRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public AxisRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4125) {
            throw new RecordFormatException("Not a Axis record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_axisType = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_reserved1 = LittleEndian.getInt(bArr, 2 + i);
        this.field_3_reserved2 = LittleEndian.getInt(bArr, 6 + i);
        this.field_4_reserved3 = LittleEndian.getInt(bArr, 10 + i);
        this.field_5_reserved4 = LittleEndian.getInt(bArr, 14 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[AXIS]\n");
        stringBuffer.append("    .axisType             = ").append("0x").append(HexDump.toHex(getAxisType())).append(" (").append((int) getAxisType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved1            = ").append("0x").append(HexDump.toHex(getReserved1())).append(" (").append(getReserved1()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved2            = ").append("0x").append(HexDump.toHex(getReserved2())).append(" (").append(getReserved2()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved3            = ").append("0x").append(HexDump.toHex(getReserved3())).append(" (").append(getReserved3()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved4            = ").append("0x").append(HexDump.toHex(getReserved4())).append(" (").append(getReserved4()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/AXIS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_axisType);
        LittleEndian.putInt(bArr, i + 6 + 0, this.field_2_reserved1);
        LittleEndian.putInt(bArr, i + 10 + 0, this.field_3_reserved2);
        LittleEndian.putInt(bArr, i + 14 + 0, this.field_4_reserved3);
        LittleEndian.putInt(bArr, i + 18 + 0, this.field_5_reserved4);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        AxisRecord axisRecord = new AxisRecord();
        axisRecord.field_1_axisType = this.field_1_axisType;
        axisRecord.field_2_reserved1 = this.field_2_reserved1;
        axisRecord.field_3_reserved2 = this.field_3_reserved2;
        axisRecord.field_4_reserved3 = this.field_4_reserved3;
        axisRecord.field_5_reserved4 = this.field_5_reserved4;
        return axisRecord;
    }

    public short getAxisType() {
        return this.field_1_axisType;
    }

    public void setAxisType(short s) {
        this.field_1_axisType = s;
    }

    public int getReserved1() {
        return this.field_2_reserved1;
    }

    public void setReserved1(int i) {
        this.field_2_reserved1 = i;
    }

    public int getReserved2() {
        return this.field_3_reserved2;
    }

    public void setReserved2(int i) {
        this.field_3_reserved2 = i;
    }

    public int getReserved3() {
        return this.field_4_reserved3;
    }

    public void setReserved3(int i) {
        this.field_4_reserved3 = i;
    }

    public int getReserved4() {
        return this.field_5_reserved4;
    }

    public void setReserved4(int i) {
        this.field_5_reserved4 = i;
    }
}
