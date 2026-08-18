package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SeriesRecord extends Record {
    public static final short BUBBLE_SERIES_TYPE_DATES = 0;
    public static final short BUBBLE_SERIES_TYPE_NUMERIC = 1;
    public static final short BUBBLE_SERIES_TYPE_SEQUENCE = 2;
    public static final short BUBBLE_SERIES_TYPE_TEXT = 3;
    public static final short CATEGORY_DATA_TYPE_DATES = 0;
    public static final short CATEGORY_DATA_TYPE_NUMERIC = 1;
    public static final short CATEGORY_DATA_TYPE_SEQUENCE = 2;
    public static final short CATEGORY_DATA_TYPE_TEXT = 3;
    public static final short VALUES_DATA_TYPE_DATES = 0;
    public static final short VALUES_DATA_TYPE_NUMERIC = 1;
    public static final short VALUES_DATA_TYPE_SEQUENCE = 2;
    public static final short VALUES_DATA_TYPE_TEXT = 3;
    public static final short sid = 4099;
    private short field_1_categoryDataType;
    private short field_2_valuesDataType;
    private short field_3_numCategories;
    private short field_4_numValues;
    private short field_5_bubbleSeriesType;
    private short field_6_numBubbleValues;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 16;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public SeriesRecord() {
    }

    public SeriesRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SeriesRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4099) {
            throw new RecordFormatException("Not a Series record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_categoryDataType = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_valuesDataType = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_numCategories = LittleEndian.getShort(bArr, 4 + i);
        this.field_4_numValues = LittleEndian.getShort(bArr, 6 + i);
        this.field_5_bubbleSeriesType = LittleEndian.getShort(bArr, 8 + i);
        this.field_6_numBubbleValues = LittleEndian.getShort(bArr, 10 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SERIES]\n");
        stringBuffer.append("    .categoryDataType     = ").append("0x").append(HexDump.toHex(getCategoryDataType())).append(" (").append((int) getCategoryDataType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .valuesDataType       = ").append("0x").append(HexDump.toHex(getValuesDataType())).append(" (").append((int) getValuesDataType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .numCategories        = ").append("0x").append(HexDump.toHex(getNumCategories())).append(" (").append((int) getNumCategories()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .numValues            = ").append("0x").append(HexDump.toHex(getNumValues())).append(" (").append((int) getNumValues()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .bubbleSeriesType     = ").append("0x").append(HexDump.toHex(getBubbleSeriesType())).append(" (").append((int) getBubbleSeriesType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .numBubbleValues      = ").append("0x").append(HexDump.toHex(getNumBubbleValues())).append(" (").append((int) getNumBubbleValues()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/SERIES]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_categoryDataType);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_valuesDataType);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_numCategories);
        LittleEndian.putShort(bArr, i + 10 + 0, this.field_4_numValues);
        LittleEndian.putShort(bArr, i + 12 + 0, this.field_5_bubbleSeriesType);
        LittleEndian.putShort(bArr, i + 14 + 0, this.field_6_numBubbleValues);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SeriesRecord seriesRecord = new SeriesRecord();
        seriesRecord.field_1_categoryDataType = this.field_1_categoryDataType;
        seriesRecord.field_2_valuesDataType = this.field_2_valuesDataType;
        seriesRecord.field_3_numCategories = this.field_3_numCategories;
        seriesRecord.field_4_numValues = this.field_4_numValues;
        seriesRecord.field_5_bubbleSeriesType = this.field_5_bubbleSeriesType;
        seriesRecord.field_6_numBubbleValues = this.field_6_numBubbleValues;
        return seriesRecord;
    }

    public short getCategoryDataType() {
        return this.field_1_categoryDataType;
    }

    public void setCategoryDataType(short s) {
        this.field_1_categoryDataType = s;
    }

    public short getValuesDataType() {
        return this.field_2_valuesDataType;
    }

    public void setValuesDataType(short s) {
        this.field_2_valuesDataType = s;
    }

    public short getNumCategories() {
        return this.field_3_numCategories;
    }

    public void setNumCategories(short s) {
        this.field_3_numCategories = s;
    }

    public short getNumValues() {
        return this.field_4_numValues;
    }

    public void setNumValues(short s) {
        this.field_4_numValues = s;
    }

    public short getBubbleSeriesType() {
        return this.field_5_bubbleSeriesType;
    }

    public void setBubbleSeriesType(short s) {
        this.field_5_bubbleSeriesType = s;
    }

    public short getNumBubbleValues() {
        return this.field_6_numBubbleValues;
    }

    public void setNumBubbleValues(short s) {
        this.field_6_numBubbleValues = s;
    }
}
