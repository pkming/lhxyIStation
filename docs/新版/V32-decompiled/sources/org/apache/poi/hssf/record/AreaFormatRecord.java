package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class AreaFormatRecord extends Record {
    public static final short sid = 4106;
    private BitField automatic;
    private int field_1_foregroundColor;
    private int field_2_backgroundColor;
    private short field_3_pattern;
    private short field_4_formatFlags;
    private short field_5_forecolorIndex;
    private short field_6_backcolorIndex;
    private BitField invert;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 20;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public AreaFormatRecord() {
        this.automatic = new BitField(1);
        this.invert = new BitField(2);
    }

    public AreaFormatRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.automatic = new BitField(1);
        this.invert = new BitField(2);
    }

    public AreaFormatRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.automatic = new BitField(1);
        this.invert = new BitField(2);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4106) {
            throw new RecordFormatException("Not a AreaFormat record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_foregroundColor = LittleEndian.getInt(bArr, 0 + i);
        this.field_2_backgroundColor = LittleEndian.getInt(bArr, 4 + i);
        this.field_3_pattern = LittleEndian.getShort(bArr, 8 + i);
        this.field_4_formatFlags = LittleEndian.getShort(bArr, 10 + i);
        this.field_5_forecolorIndex = LittleEndian.getShort(bArr, 12 + i);
        this.field_6_backcolorIndex = LittleEndian.getShort(bArr, 14 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[AREAFORMAT]\n");
        stringBuffer.append("    .foregroundColor      = ").append("0x").append(HexDump.toHex(getForegroundColor())).append(" (").append(getForegroundColor()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .backgroundColor      = ").append("0x").append(HexDump.toHex(getBackgroundColor())).append(" (").append(getBackgroundColor()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .pattern              = ").append("0x").append(HexDump.toHex(getPattern())).append(" (").append((int) getPattern()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .formatFlags          = ").append("0x").append(HexDump.toHex(getFormatFlags())).append(" (").append((int) getFormatFlags()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .automatic                = ").append(isAutomatic()).append('\n');
        stringBuffer.append("         .invert                   = ").append(isInvert()).append('\n');
        stringBuffer.append("    .forecolorIndex       = ").append("0x").append(HexDump.toHex(getForecolorIndex())).append(" (").append((int) getForecolorIndex()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .backcolorIndex       = ").append("0x").append(HexDump.toHex(getBackcolorIndex())).append(" (").append((int) getBackcolorIndex()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/AREAFORMAT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putInt(bArr, i + 4 + 0, this.field_1_foregroundColor);
        LittleEndian.putInt(bArr, i + 8 + 0, this.field_2_backgroundColor);
        LittleEndian.putShort(bArr, i + 12 + 0, this.field_3_pattern);
        LittleEndian.putShort(bArr, i + 14 + 0, this.field_4_formatFlags);
        LittleEndian.putShort(bArr, i + 16 + 0, this.field_5_forecolorIndex);
        LittleEndian.putShort(bArr, i + 18 + 0, this.field_6_backcolorIndex);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        AreaFormatRecord areaFormatRecord = new AreaFormatRecord();
        areaFormatRecord.field_1_foregroundColor = this.field_1_foregroundColor;
        areaFormatRecord.field_2_backgroundColor = this.field_2_backgroundColor;
        areaFormatRecord.field_3_pattern = this.field_3_pattern;
        areaFormatRecord.field_4_formatFlags = this.field_4_formatFlags;
        areaFormatRecord.field_5_forecolorIndex = this.field_5_forecolorIndex;
        areaFormatRecord.field_6_backcolorIndex = this.field_6_backcolorIndex;
        return areaFormatRecord;
    }

    public int getForegroundColor() {
        return this.field_1_foregroundColor;
    }

    public void setForegroundColor(int i) {
        this.field_1_foregroundColor = i;
    }

    public int getBackgroundColor() {
        return this.field_2_backgroundColor;
    }

    public void setBackgroundColor(int i) {
        this.field_2_backgroundColor = i;
    }

    public short getPattern() {
        return this.field_3_pattern;
    }

    public void setPattern(short s) {
        this.field_3_pattern = s;
    }

    public short getFormatFlags() {
        return this.field_4_formatFlags;
    }

    public void setFormatFlags(short s) {
        this.field_4_formatFlags = s;
    }

    public short getForecolorIndex() {
        return this.field_5_forecolorIndex;
    }

    public void setForecolorIndex(short s) {
        this.field_5_forecolorIndex = s;
    }

    public short getBackcolorIndex() {
        return this.field_6_backcolorIndex;
    }

    public void setBackcolorIndex(short s) {
        this.field_6_backcolorIndex = s;
    }

    public void setAutomatic(boolean z) {
        this.field_4_formatFlags = this.automatic.setShortBoolean(this.field_4_formatFlags, z);
    }

    public boolean isAutomatic() {
        return this.automatic.isSet(this.field_4_formatFlags);
    }

    public void setInvert(boolean z) {
        this.field_4_formatFlags = this.invert.setShortBoolean(this.field_4_formatFlags, z);
    }

    public boolean isInvert() {
        return this.invert.isSet(this.field_4_formatFlags);
    }
}
