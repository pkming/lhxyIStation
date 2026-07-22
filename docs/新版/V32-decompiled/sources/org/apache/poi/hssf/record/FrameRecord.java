package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class FrameRecord extends Record {
    public static final short BORDER_TYPE_REGULAR = 0;
    public static final short BORDER_TYPE_SHADOW = 1;
    public static final short sid = 4146;
    private BitField autoPosition;
    private BitField autoSize;
    private short field_1_borderType;
    private short field_2_options;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 8;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public FrameRecord() {
        this.autoSize = new BitField(1);
        this.autoPosition = new BitField(2);
    }

    public FrameRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.autoSize = new BitField(1);
        this.autoPosition = new BitField(2);
    }

    public FrameRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.autoSize = new BitField(1);
        this.autoPosition = new BitField(2);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4146) {
            throw new RecordFormatException("Not a Frame record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_borderType = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_options = LittleEndian.getShort(bArr, 2 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[FRAME]\n");
        stringBuffer.append("    .borderType           = ").append("0x").append(HexDump.toHex(getBorderType())).append(" (").append((int) getBorderType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .autoSize                 = ").append(isAutoSize()).append('\n');
        stringBuffer.append("         .autoPosition             = ").append(isAutoPosition()).append('\n');
        stringBuffer.append("[/FRAME]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_borderType);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_options);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        FrameRecord frameRecord = new FrameRecord();
        frameRecord.field_1_borderType = this.field_1_borderType;
        frameRecord.field_2_options = this.field_2_options;
        return frameRecord;
    }

    public short getBorderType() {
        return this.field_1_borderType;
    }

    public void setBorderType(short s) {
        this.field_1_borderType = s;
    }

    public short getOptions() {
        return this.field_2_options;
    }

    public void setOptions(short s) {
        this.field_2_options = s;
    }

    public void setAutoSize(boolean z) {
        this.field_2_options = this.autoSize.setShortBoolean(this.field_2_options, z);
    }

    public boolean isAutoSize() {
        return this.autoSize.isSet(this.field_2_options);
    }

    public void setAutoPosition(boolean z) {
        this.field_2_options = this.autoPosition.setShortBoolean(this.field_2_options, z);
    }

    public boolean isAutoPosition() {
        return this.autoPosition.isSet(this.field_2_options);
    }
}
