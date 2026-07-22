package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class FontRecord extends Record {
    public static final short SS_NONE = 0;
    public static final short SS_SUB = 2;
    public static final short SS_SUPER = 1;
    public static final byte U_DOUBLE = 2;
    public static final byte U_DOUBLE_ACCOUNTING = 34;
    public static final byte U_NONE = 0;
    public static final byte U_SINGLE = 1;
    public static final byte U_SINGLE_ACCOUNTING = 33;
    public static final short sid = 49;
    private byte field_10_font_name_len;
    private String field_11_font_name;
    private short field_1_font_height;
    private short field_2_attributes;
    private short field_3_color_palette_index;
    private short field_4_bold_weight;
    private short field_5_super_sub_script;
    private byte field_6_underline;
    private byte field_7_family;
    private byte field_8_charset;
    private byte field_9_zero;
    private static final BitField italic = new BitField(2);
    private static final BitField strikeout = new BitField(8);
    private static final BitField macoutline = new BitField(16);
    private static final BitField macshadow = new BitField(32);

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 49;
    }

    public FontRecord() {
        this.field_9_zero = (byte) 0;
    }

    public FontRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.field_9_zero = (byte) 0;
    }

    public FontRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.field_9_zero = (byte) 0;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 49) {
            throw new RecordFormatException("NOT A FONT RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_font_height = LittleEndian.getShort(bArr, i + 0);
        this.field_2_attributes = LittleEndian.getShort(bArr, i + 2);
        this.field_3_color_palette_index = LittleEndian.getShort(bArr, i + 4);
        this.field_4_bold_weight = LittleEndian.getShort(bArr, i + 6);
        this.field_5_super_sub_script = LittleEndian.getShort(bArr, i + 8);
        this.field_6_underline = bArr[i + 10];
        this.field_7_family = bArr[i + 11];
        this.field_8_charset = bArr[i + 12];
        this.field_9_zero = bArr[i + 13];
        byte b = bArr[i + 14];
        this.field_10_font_name_len = b;
        if (b > 0) {
            if (bArr[15] == 0) {
                this.field_11_font_name = StringUtil.getFromCompressedUnicode(bArr, 16, LittleEndian.ubyteToInt(b));
            } else {
                this.field_11_font_name = StringUtil.getFromUnicodeHigh(bArr, 16, b);
            }
        }
    }

    public void setFontHeight(short s) {
        this.field_1_font_height = s;
    }

    public void setAttributes(short s) {
        this.field_2_attributes = s;
    }

    public void setItalic(boolean z) {
        this.field_2_attributes = italic.setShortBoolean(this.field_2_attributes, z);
    }

    public void setStrikeout(boolean z) {
        this.field_2_attributes = strikeout.setShortBoolean(this.field_2_attributes, z);
    }

    public void setMacoutline(boolean z) {
        this.field_2_attributes = macoutline.setShortBoolean(this.field_2_attributes, z);
    }

    public void setMacshadow(boolean z) {
        this.field_2_attributes = macshadow.setShortBoolean(this.field_2_attributes, z);
    }

    public void setColorPaletteIndex(short s) {
        this.field_3_color_palette_index = s;
    }

    public void setBoldWeight(short s) {
        this.field_4_bold_weight = s;
    }

    public void setSuperSubScript(short s) {
        this.field_5_super_sub_script = s;
    }

    public void setUnderline(byte b) {
        this.field_6_underline = b;
    }

    public void setFamily(byte b) {
        this.field_7_family = b;
    }

    public void setCharset(byte b) {
        this.field_8_charset = b;
    }

    public void setFontNameLength(byte b) {
        this.field_10_font_name_len = b;
    }

    public void setFontName(String str) {
        this.field_11_font_name = str;
    }

    public short getFontHeight() {
        return this.field_1_font_height;
    }

    public short getAttributes() {
        return this.field_2_attributes;
    }

    public boolean isItalic() {
        return italic.isSet(this.field_2_attributes);
    }

    public boolean isStruckout() {
        return strikeout.isSet(this.field_2_attributes);
    }

    public boolean isMacoutlined() {
        return macoutline.isSet(this.field_2_attributes);
    }

    public boolean isMacshadowed() {
        return macshadow.isSet(this.field_2_attributes);
    }

    public short getColorPaletteIndex() {
        return this.field_3_color_palette_index;
    }

    public short getBoldWeight() {
        return this.field_4_bold_weight;
    }

    public short getSuperSubScript() {
        return this.field_5_super_sub_script;
    }

    public byte getUnderline() {
        return this.field_6_underline;
    }

    public byte getFamily() {
        return this.field_7_family;
    }

    public byte getCharset() {
        return this.field_8_charset;
    }

    public byte getFontNameLength() {
        return this.field_10_font_name_len;
    }

    public String getFontName() {
        return this.field_11_font_name;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[FONT]\n");
        stringBuffer.append("    .fontheight      = ").append(Integer.toHexString(getFontHeight())).append("\n");
        stringBuffer.append("    .attributes      = ").append(Integer.toHexString(getAttributes())).append("\n");
        stringBuffer.append("         .italic     = ").append(isItalic()).append("\n");
        stringBuffer.append("         .strikout   = ").append(isStruckout()).append("\n");
        stringBuffer.append("         .macoutlined= ").append(isMacoutlined()).append("\n");
        stringBuffer.append("         .macshadowed= ").append(isMacshadowed()).append("\n");
        stringBuffer.append("    .colorpalette    = ").append(Integer.toHexString(getColorPaletteIndex())).append("\n");
        stringBuffer.append("    .boldweight      = ").append(Integer.toHexString(getBoldWeight())).append("\n");
        stringBuffer.append("    .supersubscript  = ").append(Integer.toHexString(getSuperSubScript())).append("\n");
        stringBuffer.append("    .underline       = ").append(Integer.toHexString(getUnderline())).append("\n");
        stringBuffer.append("    .family          = ").append(Integer.toHexString(getFamily())).append("\n");
        stringBuffer.append("    .charset         = ").append(Integer.toHexString(getCharset())).append("\n");
        stringBuffer.append("    .namelength      = ").append(Integer.toHexString(getFontNameLength())).append("\n");
        stringBuffer.append("    .fontname        = ").append(getFontName()).append("\n");
        stringBuffer.append("[/FONT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        int fontNameLength = getFontNameLength() * 2;
        LittleEndian.putShort(bArr, i + 0, (short) 49);
        LittleEndian.putShort(bArr, i + 2, (short) (fontNameLength + 15 + 1));
        LittleEndian.putShort(bArr, i + 4, getFontHeight());
        LittleEndian.putShort(bArr, i + 6, getAttributes());
        LittleEndian.putShort(bArr, i + 8, getColorPaletteIndex());
        LittleEndian.putShort(bArr, i + 10, getBoldWeight());
        LittleEndian.putShort(bArr, i + 12, getSuperSubScript());
        bArr[i + 14] = getUnderline();
        bArr[i + 15] = getFamily();
        bArr[i + 16] = getCharset();
        bArr[i + 17] = 0;
        bArr[i + 18] = getFontNameLength();
        bArr[i + 19] = 1;
        if (getFontName() != null) {
            StringUtil.putUncompressedUnicode(getFontName(), bArr, i + 20);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getFontNameLength() * 2) + 20;
    }
}
