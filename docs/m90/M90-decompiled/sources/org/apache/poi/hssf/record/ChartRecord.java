package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ChartRecord extends Record {
    public static final short sid = 4098;
    private int field_1_x;
    private int field_2_y;
    private int field_3_width;
    private int field_4_height;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 20;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public ChartRecord() {
    }

    public ChartRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public ChartRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4098) {
            throw new RecordFormatException("Not a Chart record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_x = LittleEndian.getInt(bArr, 0 + i);
        this.field_2_y = LittleEndian.getInt(bArr, 4 + i);
        this.field_3_width = LittleEndian.getInt(bArr, 8 + i);
        this.field_4_height = LittleEndian.getInt(bArr, 12 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[CHART]\n");
        stringBuffer.append("    .x                    = ").append("0x").append(HexDump.toHex(getX())).append(" (").append(getX()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .y                    = ").append("0x").append(HexDump.toHex(getY())).append(" (").append(getY()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .width                = ").append("0x").append(HexDump.toHex(getWidth())).append(" (").append(getWidth()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .height               = ").append("0x").append(HexDump.toHex(getHeight())).append(" (").append(getHeight()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/CHART]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putInt(bArr, i + 4 + 0, this.field_1_x);
        LittleEndian.putInt(bArr, i + 8 + 0, this.field_2_y);
        LittleEndian.putInt(bArr, i + 12 + 0, this.field_3_width);
        LittleEndian.putInt(bArr, i + 16 + 0, this.field_4_height);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        ChartRecord chartRecord = new ChartRecord();
        chartRecord.field_1_x = this.field_1_x;
        chartRecord.field_2_y = this.field_2_y;
        chartRecord.field_3_width = this.field_3_width;
        chartRecord.field_4_height = this.field_4_height;
        return chartRecord;
    }

    public int getX() {
        return this.field_1_x;
    }

    public void setX(int i) {
        this.field_1_x = i;
    }

    public int getY() {
        return this.field_2_y;
    }

    public void setY(int i) {
        this.field_2_y = i;
    }

    public int getWidth() {
        return this.field_3_width;
    }

    public void setWidth(int i) {
        this.field_3_width = i;
    }

    public int getHeight() {
        return this.field_4_height;
    }

    public void setHeight(int i) {
        this.field_4_height = i;
    }
}
