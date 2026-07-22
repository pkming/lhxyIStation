package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class AxisParentRecord extends Record {
    public static final short AXIS_TYPE_MAIN = 0;
    public static final short AXIS_TYPE_SECONDARY = 1;
    public static final short sid = 4161;
    private short field_1_axisType;
    private int field_2_x;
    private int field_3_y;
    private int field_4_width;
    private int field_5_height;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 22;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public AxisParentRecord() {
    }

    public AxisParentRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public AxisParentRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4161) {
            throw new RecordFormatException("Not a AxisParent record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_axisType = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_x = LittleEndian.getInt(bArr, 2 + i);
        this.field_3_y = LittleEndian.getInt(bArr, 6 + i);
        this.field_4_width = LittleEndian.getInt(bArr, 10 + i);
        this.field_5_height = LittleEndian.getInt(bArr, 14 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[AXISPARENT]\n");
        stringBuffer.append("    .axisType             = ").append("0x").append(HexDump.toHex(getAxisType())).append(" (").append((int) getAxisType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .x                    = ").append("0x").append(HexDump.toHex(getX())).append(" (").append(getX()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .y                    = ").append("0x").append(HexDump.toHex(getY())).append(" (").append(getY()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .width                = ").append("0x").append(HexDump.toHex(getWidth())).append(" (").append(getWidth()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .height               = ").append("0x").append(HexDump.toHex(getHeight())).append(" (").append(getHeight()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/AXISPARENT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_axisType);
        LittleEndian.putInt(bArr, i + 6 + 0, this.field_2_x);
        LittleEndian.putInt(bArr, i + 10 + 0, this.field_3_y);
        LittleEndian.putInt(bArr, i + 14 + 0, this.field_4_width);
        LittleEndian.putInt(bArr, i + 18 + 0, this.field_5_height);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        AxisParentRecord axisParentRecord = new AxisParentRecord();
        axisParentRecord.field_1_axisType = this.field_1_axisType;
        axisParentRecord.field_2_x = this.field_2_x;
        axisParentRecord.field_3_y = this.field_3_y;
        axisParentRecord.field_4_width = this.field_4_width;
        axisParentRecord.field_5_height = this.field_5_height;
        return axisParentRecord;
    }

    public short getAxisType() {
        return this.field_1_axisType;
    }

    public void setAxisType(short s) {
        this.field_1_axisType = s;
    }

    public int getX() {
        return this.field_2_x;
    }

    public void setX(int i) {
        this.field_2_x = i;
    }

    public int getY() {
        return this.field_3_y;
    }

    public void setY(int i) {
        this.field_3_y = i;
    }

    public int getWidth() {
        return this.field_4_width;
    }

    public void setWidth(int i) {
        this.field_4_width = i;
    }

    public int getHeight() {
        return this.field_5_height;
    }

    public void setHeight(int i) {
        this.field_5_height = i;
    }
}
