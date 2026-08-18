package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class LineFormatRecord extends Record {
    public static final short LINE_PATTERN_DARK_GRAY_PATTERN = 6;
    public static final short LINE_PATTERN_DASH = 1;
    public static final short LINE_PATTERN_DASH_DOT = 3;
    public static final short LINE_PATTERN_DASH_DOT_DOT = 4;
    public static final short LINE_PATTERN_DOT = 2;
    public static final short LINE_PATTERN_LIGHT_GRAY_PATTERN = 8;
    public static final short LINE_PATTERN_MEDIUM_GRAY_PATTERN = 7;
    public static final short LINE_PATTERN_NONE = 5;
    public static final short LINE_PATTERN_SOLID = 0;
    public static final short WEIGHT_HAIRLINE = -1;
    public static final short WEIGHT_MEDIUM = 1;
    public static final short WEIGHT_NARROW = 0;
    public static final short WEIGHT_WIDE = 2;
    public static final short sid = 4103;
    private BitField auto;
    private BitField drawTicks;
    private int field_1_lineColor;
    private short field_2_linePattern;
    private short field_3_weight;
    private short field_4_format;
    private short field_5_colourPaletteIndex;
    private BitField unknown;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 16;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public LineFormatRecord() {
        this.auto = new BitField(1);
        this.drawTicks = new BitField(4);
        this.unknown = new BitField(4);
    }

    public LineFormatRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.auto = new BitField(1);
        this.drawTicks = new BitField(4);
        this.unknown = new BitField(4);
    }

    public LineFormatRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.auto = new BitField(1);
        this.drawTicks = new BitField(4);
        this.unknown = new BitField(4);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4103) {
            throw new RecordFormatException("Not a LineFormat record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_lineColor = LittleEndian.getInt(bArr, 0 + i);
        this.field_2_linePattern = LittleEndian.getShort(bArr, 4 + i);
        this.field_3_weight = LittleEndian.getShort(bArr, 6 + i);
        this.field_4_format = LittleEndian.getShort(bArr, 8 + i);
        this.field_5_colourPaletteIndex = LittleEndian.getShort(bArr, 10 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[LINEFORMAT]\n");
        stringBuffer.append("    .lineColor            = ").append("0x").append(HexDump.toHex(getLineColor())).append(" (").append(getLineColor()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .linePattern          = ").append("0x").append(HexDump.toHex(getLinePattern())).append(" (").append((int) getLinePattern()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .weight               = ").append("0x").append(HexDump.toHex(getWeight())).append(" (").append((int) getWeight()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .format               = ").append("0x").append(HexDump.toHex(getFormat())).append(" (").append((int) getFormat()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .auto                     = ").append(isAuto()).append('\n');
        stringBuffer.append("         .drawTicks                = ").append(isDrawTicks()).append('\n');
        stringBuffer.append("         .unknown                  = ").append(isUnknown()).append('\n');
        stringBuffer.append("    .colourPaletteIndex   = ").append("0x").append(HexDump.toHex(getColourPaletteIndex())).append(" (").append((int) getColourPaletteIndex()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/LINEFORMAT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putInt(bArr, i + 4 + 0, this.field_1_lineColor);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_2_linePattern);
        LittleEndian.putShort(bArr, i + 10 + 0, this.field_3_weight);
        LittleEndian.putShort(bArr, i + 12 + 0, this.field_4_format);
        LittleEndian.putShort(bArr, i + 14 + 0, this.field_5_colourPaletteIndex);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        LineFormatRecord lineFormatRecord = new LineFormatRecord();
        lineFormatRecord.field_1_lineColor = this.field_1_lineColor;
        lineFormatRecord.field_2_linePattern = this.field_2_linePattern;
        lineFormatRecord.field_3_weight = this.field_3_weight;
        lineFormatRecord.field_4_format = this.field_4_format;
        lineFormatRecord.field_5_colourPaletteIndex = this.field_5_colourPaletteIndex;
        return lineFormatRecord;
    }

    public int getLineColor() {
        return this.field_1_lineColor;
    }

    public void setLineColor(int i) {
        this.field_1_lineColor = i;
    }

    public short getLinePattern() {
        return this.field_2_linePattern;
    }

    public void setLinePattern(short s) {
        this.field_2_linePattern = s;
    }

    public short getWeight() {
        return this.field_3_weight;
    }

    public void setWeight(short s) {
        this.field_3_weight = s;
    }

    public short getFormat() {
        return this.field_4_format;
    }

    public void setFormat(short s) {
        this.field_4_format = s;
    }

    public short getColourPaletteIndex() {
        return this.field_5_colourPaletteIndex;
    }

    public void setColourPaletteIndex(short s) {
        this.field_5_colourPaletteIndex = s;
    }

    public void setAuto(boolean z) {
        this.field_4_format = this.auto.setShortBoolean(this.field_4_format, z);
    }

    public boolean isAuto() {
        return this.auto.isSet(this.field_4_format);
    }

    public void setDrawTicks(boolean z) {
        this.field_4_format = this.drawTicks.setShortBoolean(this.field_4_format, z);
    }

    public boolean isDrawTicks() {
        return this.drawTicks.isSet(this.field_4_format);
    }

    public void setUnknown(boolean z) {
        this.field_4_format = this.unknown.setShortBoolean(this.field_4_format, z);
    }

    public boolean isUnknown() {
        return this.unknown.isSet(this.field_4_format);
    }
}
