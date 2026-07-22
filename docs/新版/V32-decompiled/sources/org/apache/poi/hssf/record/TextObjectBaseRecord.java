package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class TextObjectBaseRecord extends Record {
    public static final short HORIZONTAL_TEXT_ALIGNMENT_CENTERED = 2;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_JUSTIFIED = 4;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_LEFT_ALIGNED = 1;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_RIGHT_ALIGNED = 3;
    public static final short TEXT_ORIENTATION_NONE = 0;
    public static final short TEXT_ORIENTATION_ROT_LEFT = 3;
    public static final short TEXT_ORIENTATION_ROT_RIGHT = 2;
    public static final short TEXT_ORIENTATION_TOP_TO_BOTTOM = 1;
    public static final short VERTICAL_TEXT_ALIGNMENT_BOTTOM = 3;
    public static final short VERTICAL_TEXT_ALIGNMENT_CENTER = 2;
    public static final short VERTICAL_TEXT_ALIGNMENT_JUSTIFY = 4;
    public static final short VERTICAL_TEXT_ALIGNMENT_TOP = 1;
    public static final short sid = 438;
    private BitField HorizontalTextAlignment;
    private BitField VerticalTextAlignment;
    private short field_1_options;
    private short field_2_textOrientation;
    private short field_3_reserved4;
    private short field_4_reserved5;
    private short field_5_reserved6;
    private short field_6_textLength;
    private short field_7_formattingRunLength;
    private int field_8_reserved7;
    private BitField reserved1;
    private BitField reserved2;
    private BitField reserved3;
    private BitField textLocked;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 22;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public TextObjectBaseRecord() {
        this.reserved1 = new BitField(1);
        this.HorizontalTextAlignment = new BitField(14);
        this.VerticalTextAlignment = new BitField(112);
        this.reserved2 = new BitField(384);
        this.textLocked = new BitField(512);
        this.reserved3 = new BitField(64512);
    }

    public TextObjectBaseRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.reserved1 = new BitField(1);
        this.HorizontalTextAlignment = new BitField(14);
        this.VerticalTextAlignment = new BitField(112);
        this.reserved2 = new BitField(384);
        this.textLocked = new BitField(512);
        this.reserved3 = new BitField(64512);
    }

    public TextObjectBaseRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.reserved1 = new BitField(1);
        this.HorizontalTextAlignment = new BitField(14);
        this.VerticalTextAlignment = new BitField(112);
        this.reserved2 = new BitField(384);
        this.textLocked = new BitField(512);
        this.reserved3 = new BitField(64512);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 438) {
            throw new RecordFormatException("Not a TextObjectBase record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_options = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_textOrientation = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_reserved4 = LittleEndian.getShort(bArr, 4 + i);
        this.field_4_reserved5 = LittleEndian.getShort(bArr, 6 + i);
        this.field_5_reserved6 = LittleEndian.getShort(bArr, 8 + i);
        this.field_6_textLength = LittleEndian.getShort(bArr, 10 + i);
        this.field_7_formattingRunLength = LittleEndian.getShort(bArr, 12 + i);
        this.field_8_reserved7 = LittleEndian.getInt(bArr, 14 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[TXO]\n");
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .reserved1                = ").append(isReserved1()).append('\n');
        stringBuffer.append("         .HorizontalTextAlignment     = ").append((int) getHorizontalTextAlignment()).append('\n');
        stringBuffer.append("         .VerticalTextAlignment     = ").append((int) getVerticalTextAlignment()).append('\n');
        stringBuffer.append("         .reserved2                = ").append((int) getReserved2()).append('\n');
        stringBuffer.append("         .textLocked               = ").append(isTextLocked()).append('\n');
        stringBuffer.append("         .reserved3                = ").append((int) getReserved3()).append('\n');
        stringBuffer.append("    .textOrientation      = ").append("0x").append(HexDump.toHex(getTextOrientation())).append(" (").append((int) getTextOrientation()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved4            = ").append("0x").append(HexDump.toHex(getReserved4())).append(" (").append((int) getReserved4()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved5            = ").append("0x").append(HexDump.toHex(getReserved5())).append(" (").append((int) getReserved5()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved6            = ").append("0x").append(HexDump.toHex(getReserved6())).append(" (").append((int) getReserved6()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .textLength           = ").append("0x").append(HexDump.toHex(getTextLength())).append(" (").append((int) getTextLength()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .formattingRunLength  = ").append("0x").append(HexDump.toHex(getFormattingRunLength())).append(" (").append((int) getFormattingRunLength()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved7            = ").append("0x").append(HexDump.toHex(getReserved7())).append(" (").append(getReserved7()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/TXO]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_options);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_textOrientation);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_reserved4);
        LittleEndian.putShort(bArr, i + 10 + 0, this.field_4_reserved5);
        LittleEndian.putShort(bArr, i + 12 + 0, this.field_5_reserved6);
        LittleEndian.putShort(bArr, i + 14 + 0, this.field_6_textLength);
        LittleEndian.putShort(bArr, i + 16 + 0, this.field_7_formattingRunLength);
        LittleEndian.putInt(bArr, i + 18 + 0, this.field_8_reserved7);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        TextObjectBaseRecord textObjectBaseRecord = new TextObjectBaseRecord();
        textObjectBaseRecord.field_1_options = this.field_1_options;
        textObjectBaseRecord.field_2_textOrientation = this.field_2_textOrientation;
        textObjectBaseRecord.field_3_reserved4 = this.field_3_reserved4;
        textObjectBaseRecord.field_4_reserved5 = this.field_4_reserved5;
        textObjectBaseRecord.field_5_reserved6 = this.field_5_reserved6;
        textObjectBaseRecord.field_6_textLength = this.field_6_textLength;
        textObjectBaseRecord.field_7_formattingRunLength = this.field_7_formattingRunLength;
        textObjectBaseRecord.field_8_reserved7 = this.field_8_reserved7;
        return textObjectBaseRecord;
    }

    public short getOptions() {
        return this.field_1_options;
    }

    public void setOptions(short s) {
        this.field_1_options = s;
    }

    public short getTextOrientation() {
        return this.field_2_textOrientation;
    }

    public void setTextOrientation(short s) {
        this.field_2_textOrientation = s;
    }

    public short getReserved4() {
        return this.field_3_reserved4;
    }

    public void setReserved4(short s) {
        this.field_3_reserved4 = s;
    }

    public short getReserved5() {
        return this.field_4_reserved5;
    }

    public void setReserved5(short s) {
        this.field_4_reserved5 = s;
    }

    public short getReserved6() {
        return this.field_5_reserved6;
    }

    public void setReserved6(short s) {
        this.field_5_reserved6 = s;
    }

    public short getTextLength() {
        return this.field_6_textLength;
    }

    public void setTextLength(short s) {
        this.field_6_textLength = s;
    }

    public short getFormattingRunLength() {
        return this.field_7_formattingRunLength;
    }

    public void setFormattingRunLength(short s) {
        this.field_7_formattingRunLength = s;
    }

    public int getReserved7() {
        return this.field_8_reserved7;
    }

    public void setReserved7(int i) {
        this.field_8_reserved7 = i;
    }

    public void setReserved1(boolean z) {
        this.field_1_options = this.reserved1.setShortBoolean(this.field_1_options, z);
    }

    public boolean isReserved1() {
        return this.reserved1.isSet(this.field_1_options);
    }

    public void setHorizontalTextAlignment(short s) {
        this.field_1_options = this.HorizontalTextAlignment.setShortValue(this.field_1_options, s);
    }

    public short getHorizontalTextAlignment() {
        return this.HorizontalTextAlignment.getShortValue(this.field_1_options);
    }

    public void setVerticalTextAlignment(short s) {
        this.field_1_options = this.VerticalTextAlignment.setShortValue(this.field_1_options, s);
    }

    public short getVerticalTextAlignment() {
        return this.VerticalTextAlignment.getShortValue(this.field_1_options);
    }

    public void setReserved2(short s) {
        this.field_1_options = this.reserved2.setShortValue(this.field_1_options, s);
    }

    public short getReserved2() {
        return this.reserved2.getShortValue(this.field_1_options);
    }

    public void setTextLocked(boolean z) {
        this.field_1_options = this.textLocked.setShortBoolean(this.field_1_options, z);
    }

    public boolean isTextLocked() {
        return this.textLocked.isSet(this.field_1_options);
    }

    public void setReserved3(short s) {
        this.field_1_options = this.reserved3.setShortValue(this.field_1_options, s);
    }

    public short getReserved3() {
        return this.reserved3.getShortValue(this.field_1_options);
    }
}
