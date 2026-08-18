package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class TickRecord extends Record {
    public static final short sid = 4126;
    private BitField autoTextBackground;
    private BitField autoTextColor;
    private BitField autorotate;
    private short field_10_zero3;
    private byte field_1_majorTickType;
    private byte field_2_minorTickType;
    private byte field_3_labelPosition;
    private byte field_4_background;
    private int field_5_labelColorRgb;
    private short field_6_zero1;
    private short field_7_zero2;
    private short field_8_options;
    private short field_9_tickColor;
    private BitField rotation;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 34;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public TickRecord() {
        this.autoTextColor = new BitField(1);
        this.autoTextBackground = new BitField(2);
        this.rotation = new BitField(28);
        this.autorotate = new BitField(32);
    }

    public TickRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.autoTextColor = new BitField(1);
        this.autoTextBackground = new BitField(2);
        this.rotation = new BitField(28);
        this.autorotate = new BitField(32);
    }

    public TickRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.autoTextColor = new BitField(1);
        this.autoTextBackground = new BitField(2);
        this.rotation = new BitField(28);
        this.autorotate = new BitField(32);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4126) {
            throw new RecordFormatException("Not a Tick record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_majorTickType = bArr[0 + i];
        this.field_2_minorTickType = bArr[1 + i];
        this.field_3_labelPosition = bArr[2 + i];
        this.field_4_background = bArr[3 + i];
        this.field_5_labelColorRgb = LittleEndian.getInt(bArr, 4 + i);
        this.field_6_zero1 = LittleEndian.getShort(bArr, 8 + i);
        this.field_7_zero2 = LittleEndian.getShort(bArr, 16 + i);
        this.field_8_options = LittleEndian.getShort(bArr, 24 + i);
        this.field_9_tickColor = LittleEndian.getShort(bArr, 26 + i);
        this.field_10_zero3 = LittleEndian.getShort(bArr, 28 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[TICK]\n");
        stringBuffer.append("    .majorTickType        = ").append("0x").append(HexDump.toHex(getMajorTickType())).append(" (").append((int) getMajorTickType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .minorTickType        = ").append("0x").append(HexDump.toHex(getMinorTickType())).append(" (").append((int) getMinorTickType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .labelPosition        = ").append("0x").append(HexDump.toHex(getLabelPosition())).append(" (").append((int) getLabelPosition()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .background           = ").append("0x").append(HexDump.toHex(getBackground())).append(" (").append((int) getBackground()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .labelColorRgb        = ").append("0x").append(HexDump.toHex(getLabelColorRgb())).append(" (").append(getLabelColorRgb()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .zero1                = ").append("0x").append(HexDump.toHex(getZero1())).append(" (").append((int) getZero1()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .zero2                = ").append("0x").append(HexDump.toHex(getZero2())).append(" (").append((int) getZero2()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .autoTextColor            = ").append(isAutoTextColor()).append('\n');
        stringBuffer.append("         .autoTextBackground       = ").append(isAutoTextBackground()).append('\n');
        stringBuffer.append("         .rotation                 = ").append((int) getRotation()).append('\n');
        stringBuffer.append("         .autorotate               = ").append(isAutorotate()).append('\n');
        stringBuffer.append("    .tickColor            = ").append("0x").append(HexDump.toHex(getTickColor())).append(" (").append((int) getTickColor()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .zero3                = ").append("0x").append(HexDump.toHex(getZero3())).append(" (").append((int) getZero3()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/TICK]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        bArr[i + 4 + 0] = this.field_1_majorTickType;
        bArr[i + 5 + 0] = this.field_2_minorTickType;
        bArr[i + 6 + 0] = this.field_3_labelPosition;
        bArr[i + 7 + 0] = this.field_4_background;
        LittleEndian.putInt(bArr, i + 8 + 0, this.field_5_labelColorRgb);
        LittleEndian.putShort(bArr, i + 12 + 0, this.field_6_zero1);
        LittleEndian.putShort(bArr, i + 20 + 0, this.field_7_zero2);
        LittleEndian.putShort(bArr, i + 28 + 0, this.field_8_options);
        LittleEndian.putShort(bArr, i + 30 + 0, this.field_9_tickColor);
        LittleEndian.putShort(bArr, i + 32 + 0, this.field_10_zero3);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        TickRecord tickRecord = new TickRecord();
        tickRecord.field_1_majorTickType = this.field_1_majorTickType;
        tickRecord.field_2_minorTickType = this.field_2_minorTickType;
        tickRecord.field_3_labelPosition = this.field_3_labelPosition;
        tickRecord.field_4_background = this.field_4_background;
        tickRecord.field_5_labelColorRgb = this.field_5_labelColorRgb;
        tickRecord.field_6_zero1 = this.field_6_zero1;
        tickRecord.field_7_zero2 = this.field_7_zero2;
        tickRecord.field_8_options = this.field_8_options;
        tickRecord.field_9_tickColor = this.field_9_tickColor;
        tickRecord.field_10_zero3 = this.field_10_zero3;
        return tickRecord;
    }

    public byte getMajorTickType() {
        return this.field_1_majorTickType;
    }

    public void setMajorTickType(byte b) {
        this.field_1_majorTickType = b;
    }

    public byte getMinorTickType() {
        return this.field_2_minorTickType;
    }

    public void setMinorTickType(byte b) {
        this.field_2_minorTickType = b;
    }

    public byte getLabelPosition() {
        return this.field_3_labelPosition;
    }

    public void setLabelPosition(byte b) {
        this.field_3_labelPosition = b;
    }

    public byte getBackground() {
        return this.field_4_background;
    }

    public void setBackground(byte b) {
        this.field_4_background = b;
    }

    public int getLabelColorRgb() {
        return this.field_5_labelColorRgb;
    }

    public void setLabelColorRgb(int i) {
        this.field_5_labelColorRgb = i;
    }

    public short getZero1() {
        return this.field_6_zero1;
    }

    public void setZero1(short s) {
        this.field_6_zero1 = s;
    }

    public short getZero2() {
        return this.field_7_zero2;
    }

    public void setZero2(short s) {
        this.field_7_zero2 = s;
    }

    public short getOptions() {
        return this.field_8_options;
    }

    public void setOptions(short s) {
        this.field_8_options = s;
    }

    public short getTickColor() {
        return this.field_9_tickColor;
    }

    public void setTickColor(short s) {
        this.field_9_tickColor = s;
    }

    public short getZero3() {
        return this.field_10_zero3;
    }

    public void setZero3(short s) {
        this.field_10_zero3 = s;
    }

    public void setAutoTextColor(boolean z) {
        this.field_8_options = this.autoTextColor.setShortBoolean(this.field_8_options, z);
    }

    public boolean isAutoTextColor() {
        return this.autoTextColor.isSet(this.field_8_options);
    }

    public void setAutoTextBackground(boolean z) {
        this.field_8_options = this.autoTextBackground.setShortBoolean(this.field_8_options, z);
    }

    public boolean isAutoTextBackground() {
        return this.autoTextBackground.isSet(this.field_8_options);
    }

    public void setRotation(short s) {
        this.field_8_options = this.rotation.setShortValue(this.field_8_options, s);
    }

    public short getRotation() {
        return this.rotation.getShortValue(this.field_8_options);
    }

    public void setAutorotate(boolean z) {
        this.field_8_options = this.autorotate.setShortBoolean(this.field_8_options, z);
    }

    public boolean isAutorotate() {
        return this.autorotate.isSet(this.field_8_options);
    }
}
