package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ChartFormatRecord extends Record {
    public static final short sid = 4116;
    private int field1_x_position;
    private int field2_y_position;
    private int field3_width;
    private int field4_height;
    private short field5_grbit;
    private BitField varyDisplayPattern;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 22;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public ChartFormatRecord() {
        this.varyDisplayPattern = new BitField(1);
    }

    public ChartFormatRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.varyDisplayPattern = new BitField(1);
    }

    public ChartFormatRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.varyDisplayPattern = new BitField(1);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4116) {
            throw new RecordFormatException("NOT A CHARTFORMAT RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field1_x_position = LittleEndian.getInt(bArr, i + 0);
        this.field2_y_position = LittleEndian.getInt(bArr, i + 4);
        this.field3_width = LittleEndian.getInt(bArr, i + 8);
        this.field4_height = LittleEndian.getInt(bArr, i + 12);
        this.field5_grbit = LittleEndian.getShort(bArr, i + 16);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[CHARTFORMAT]\n");
        stringBuffer.append("    .xPosition       = ").append(getXPosition()).append("\n");
        stringBuffer.append("    .yPosition       = ").append(getYPosition()).append("\n");
        stringBuffer.append("    .width           = ").append(getWidth()).append("\n");
        stringBuffer.append("    .height          = ").append(getHeight()).append("\n");
        stringBuffer.append("    .grBit           = ").append(Integer.toHexString(this.field5_grbit)).append("\n");
        stringBuffer.append("[/CHARTFORMAT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) 22);
        LittleEndian.putInt(bArr, i + 4, getXPosition());
        LittleEndian.putInt(bArr, i + 8, getYPosition());
        LittleEndian.putInt(bArr, i + 12, getWidth());
        LittleEndian.putInt(bArr, i + 16, getHeight());
        LittleEndian.putShort(bArr, i + 20, this.field5_grbit);
        return getRecordSize();
    }

    public int getXPosition() {
        return this.field1_x_position;
    }

    public void setXPosition(int i) {
        this.field1_x_position = i;
    }

    public int getYPosition() {
        return this.field2_y_position;
    }

    public void setYPosition(int i) {
        this.field2_y_position = i;
    }

    public int getWidth() {
        return this.field3_width;
    }

    public void setWidth(int i) {
        this.field3_width = i;
    }

    public int getHeight() {
        return this.field4_height;
    }

    public void setHeight(int i) {
        this.field4_height = i;
    }

    public boolean getVaryDisplayPattern() {
        return this.varyDisplayPattern.isSet(this.field5_grbit);
    }

    public void setVaryDisplayPattern(boolean z) {
        this.field5_grbit = this.varyDisplayPattern.setShortBoolean(this.field5_grbit, z);
    }
}
