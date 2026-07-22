package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.FontRecord;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFFont {
    public static final short BOLDWEIGHT_BOLD = 700;
    public static final short BOLDWEIGHT_NORMAL = 400;
    public static final short COLOR_NORMAL = Short.MAX_VALUE;
    public static final short COLOR_RED = 10;
    public static final String FONT_ARIAL = "Arial";
    public static final short SS_NONE = 0;
    public static final short SS_SUB = 2;
    public static final short SS_SUPER = 1;
    public static final byte U_DOUBLE = 2;
    public static final byte U_DOUBLE_ACCOUNTING = 34;
    public static final byte U_NONE = 0;
    public static final byte U_SINGLE = 1;
    public static final byte U_SINGLE_ACCOUNTING = 33;
    private FontRecord font;
    private short index;

    protected HSSFFont(short s, FontRecord fontRecord) {
        this.font = fontRecord;
        this.index = s;
    }

    public void setFontName(String str) {
        this.font.setFontName(str);
        this.font.setFontNameLength((byte) str.length());
    }

    public String getFontName() {
        return this.font.getFontName();
    }

    public short getIndex() {
        return this.index;
    }

    public void setFontHeight(short s) {
        this.font.setFontHeight(s);
    }

    public void setFontHeightInPoints(short s) {
        this.font.setFontHeight((short) (s * 20));
    }

    public short getFontHeight() {
        return this.font.getFontHeight();
    }

    public short getFontHeightInPoints() {
        return (short) (this.font.getFontHeight() / 20);
    }

    public void setItalic(boolean z) {
        this.font.setItalic(z);
    }

    public boolean getItalic() {
        return this.font.isItalic();
    }

    public void setStrikeout(boolean z) {
        this.font.setStrikeout(z);
    }

    public boolean getStrikeout() {
        return this.font.isStruckout();
    }

    public void setColor(short s) {
        this.font.setColorPaletteIndex(s);
    }

    public short getColor() {
        return this.font.getColorPaletteIndex();
    }

    public void setBoldweight(short s) {
        this.font.setBoldWeight(s);
    }

    public short getBoldweight() {
        return this.font.getBoldWeight();
    }

    public void setTypeOffset(short s) {
        this.font.setSuperSubScript(s);
    }

    public short getTypeOffset() {
        return this.font.getSuperSubScript();
    }

    public void setUnderline(byte b) {
        this.font.setUnderline(b);
    }

    public byte getUnderline() {
        return this.font.getUnderline();
    }

    public String toString() {
        return new StringBuffer().append("org.apache.poi.hssf.usermodel.HSSFFont{").append(this.font).append("}").toString();
    }
}
