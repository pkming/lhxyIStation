package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class CategorySeriesAxisRecord extends Record {
    public static final short sid = 4128;
    private BitField crossesFarRight;
    private short field_1_crossingPoint;
    private short field_2_labelFrequency;
    private short field_3_tickMarkFrequency;
    private short field_4_options;
    private BitField reversed;
    private BitField valueAxisCrossing;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 12;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public CategorySeriesAxisRecord() {
        this.valueAxisCrossing = new BitField(1);
        this.crossesFarRight = new BitField(2);
        this.reversed = new BitField(4);
    }

    public CategorySeriesAxisRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.valueAxisCrossing = new BitField(1);
        this.crossesFarRight = new BitField(2);
        this.reversed = new BitField(4);
    }

    public CategorySeriesAxisRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.valueAxisCrossing = new BitField(1);
        this.crossesFarRight = new BitField(2);
        this.reversed = new BitField(4);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4128) {
            throw new RecordFormatException("Not a CategorySeriesAxis record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_crossingPoint = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_labelFrequency = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_tickMarkFrequency = LittleEndian.getShort(bArr, 4 + i);
        this.field_4_options = LittleEndian.getShort(bArr, 6 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[CATSERRANGE]\n");
        stringBuffer.append("    .crossingPoint        = ").append("0x").append(HexDump.toHex(getCrossingPoint())).append(" (").append((int) getCrossingPoint()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .labelFrequency       = ").append("0x").append(HexDump.toHex(getLabelFrequency())).append(" (").append((int) getLabelFrequency()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .tickMarkFrequency    = ").append("0x").append(HexDump.toHex(getTickMarkFrequency())).append(" (").append((int) getTickMarkFrequency()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .valueAxisCrossing        = ").append(isValueAxisCrossing()).append('\n');
        stringBuffer.append("         .crossesFarRight          = ").append(isCrossesFarRight()).append('\n');
        stringBuffer.append("         .reversed                 = ").append(isReversed()).append('\n');
        stringBuffer.append("[/CATSERRANGE]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_crossingPoint);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_labelFrequency);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_tickMarkFrequency);
        LittleEndian.putShort(bArr, i + 10 + 0, this.field_4_options);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        CategorySeriesAxisRecord categorySeriesAxisRecord = new CategorySeriesAxisRecord();
        categorySeriesAxisRecord.field_1_crossingPoint = this.field_1_crossingPoint;
        categorySeriesAxisRecord.field_2_labelFrequency = this.field_2_labelFrequency;
        categorySeriesAxisRecord.field_3_tickMarkFrequency = this.field_3_tickMarkFrequency;
        categorySeriesAxisRecord.field_4_options = this.field_4_options;
        return categorySeriesAxisRecord;
    }

    public short getCrossingPoint() {
        return this.field_1_crossingPoint;
    }

    public void setCrossingPoint(short s) {
        this.field_1_crossingPoint = s;
    }

    public short getLabelFrequency() {
        return this.field_2_labelFrequency;
    }

    public void setLabelFrequency(short s) {
        this.field_2_labelFrequency = s;
    }

    public short getTickMarkFrequency() {
        return this.field_3_tickMarkFrequency;
    }

    public void setTickMarkFrequency(short s) {
        this.field_3_tickMarkFrequency = s;
    }

    public short getOptions() {
        return this.field_4_options;
    }

    public void setOptions(short s) {
        this.field_4_options = s;
    }

    public void setValueAxisCrossing(boolean z) {
        this.field_4_options = this.valueAxisCrossing.setShortBoolean(this.field_4_options, z);
    }

    public boolean isValueAxisCrossing() {
        return this.valueAxisCrossing.isSet(this.field_4_options);
    }

    public void setCrossesFarRight(boolean z) {
        this.field_4_options = this.crossesFarRight.setShortBoolean(this.field_4_options, z);
    }

    public boolean isCrossesFarRight() {
        return this.crossesFarRight.isSet(this.field_4_options);
    }

    public void setReversed(boolean z) {
        this.field_4_options = this.reversed.setShortBoolean(this.field_4_options, z);
    }

    public boolean isReversed() {
        return this.reversed.isSet(this.field_4_options);
    }
}
