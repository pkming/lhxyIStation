package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.ExtendedFormatRecord;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFCellStyle {
    public static final short ALIGN_CENTER = 2;
    public static final short ALIGN_CENTER_SELECTION = 6;
    public static final short ALIGN_FILL = 4;
    public static final short ALIGN_GENERAL = 0;
    public static final short ALIGN_JUSTIFY = 5;
    public static final short ALIGN_LEFT = 1;
    public static final short ALIGN_RIGHT = 3;
    public static final short ALT_BARS = 3;
    public static final short BIG_SPOTS = 9;
    public static final short BORDER_DASHED = 3;
    public static final short BORDER_DASH_DOT = 9;
    public static final short BORDER_DASH_DOT_DOT = 11;
    public static final short BORDER_DOTTED = 7;
    public static final short BORDER_DOUBLE = 6;
    public static final short BORDER_HAIR = 4;
    public static final short BORDER_MEDIUM = 2;
    public static final short BORDER_MEDIUM_DASHED = 8;
    public static final short BORDER_MEDIUM_DASH_DOT = 10;
    public static final short BORDER_MEDIUM_DASH_DOT_DOT = 12;
    public static final short BORDER_NONE = 0;
    public static final short BORDER_SLANTED_DASH_DOT = 13;
    public static final short BORDER_THICK = 5;
    public static final short BORDER_THIN = 1;
    public static final short BRICKS = 10;
    public static final short DIAMONDS = 16;
    public static final short FINE_DOTS = 2;
    public static final short NO_FILL = 0;
    public static final short SOLID_FOREGROUND = 1;
    public static final short SPARSE_DOTS = 4;
    public static final short SQUARES = 15;
    public static final short THICK_BACKWARD_DIAG = 7;
    public static final short THICK_FORWARD_DIAG = 8;
    public static final short THICK_HORZ_BANDS = 5;
    public static final short THICK_VERT_BANDS = 6;
    public static final short THIN_BACKWARD_DIAG = 13;
    public static final short THIN_FORWARD_DIAG = 14;
    public static final short THIN_HORZ_BANDS = 11;
    public static final short THIN_VERT_BANDS = 12;
    public static final short VERTICAL_BOTTOM = 2;
    public static final short VERTICAL_CENTER = 1;
    public static final short VERTICAL_JUSTIFY = 3;
    public static final short VERTICAL_TOP = 0;
    private short fontindex = 0;
    private ExtendedFormatRecord format;
    private short index;

    protected HSSFCellStyle(short s, ExtendedFormatRecord extendedFormatRecord) {
        this.format = null;
        this.index = (short) 0;
        this.index = s;
        this.format = extendedFormatRecord;
    }

    public short getIndex() {
        return this.index;
    }

    public void setDataFormat(short s) {
        this.format.setFormatIndex(s);
    }

    public short getDataFormat() {
        return this.format.getFormatIndex();
    }

    public void setFont(HSSFFont hSSFFont) {
        this.format.setIndentNotParentFont(true);
        short index = hSSFFont.getIndex();
        this.fontindex = index;
        this.format.setFontIndex(index);
    }

    public short getFontIndex() {
        return this.format.getFontIndex();
    }

    public void setHidden(boolean z) {
        this.format.setIndentNotParentCellOptions(true);
        this.format.setHidden(z);
    }

    public boolean getHidden() {
        return this.format.isHidden();
    }

    public void setLocked(boolean z) {
        this.format.setIndentNotParentCellOptions(true);
        this.format.setLocked(z);
    }

    public boolean getLocked() {
        return this.format.isLocked();
    }

    public void setAlignment(short s) {
        this.format.setIndentNotParentAlignment(true);
        this.format.setAlignment(s);
    }

    public short getAlignment() {
        return this.format.getAlignment();
    }

    public void setWrapText(boolean z) {
        this.format.setIndentNotParentAlignment(true);
        this.format.setWrapText(z);
    }

    public boolean getWrapText() {
        return this.format.getWrapText();
    }

    public void setVerticalAlignment(short s) {
        this.format.setVerticalAlignment(s);
    }

    public short getVerticalAlignment() {
        return this.format.getVerticalAlignment();
    }

    public void setRotation(short s) {
        if (s < 0 && s >= -90) {
            s = (short) (90 - s);
        } else if (s < -90 || s > 90) {
            throw new IllegalArgumentException("The rotation must be between -90 and 90 degrees");
        }
        this.format.setRotation(s);
    }

    public short getRotation() {
        short rotation = this.format.getRotation();
        return rotation > 90 ? (short) (90 - rotation) : rotation;
    }

    public void setIndention(short s) {
        this.format.setIndent(s);
    }

    public short getIndention() {
        return this.format.getIndent();
    }

    public void setBorderLeft(short s) {
        this.format.setIndentNotParentBorder(true);
        this.format.setBorderLeft(s);
    }

    public short getBorderLeft() {
        return this.format.getBorderLeft();
    }

    public void setBorderRight(short s) {
        this.format.setIndentNotParentBorder(true);
        this.format.setBorderRight(s);
    }

    public short getBorderRight() {
        return this.format.getBorderRight();
    }

    public void setBorderTop(short s) {
        this.format.setIndentNotParentBorder(true);
        this.format.setBorderTop(s);
    }

    public short getBorderTop() {
        return this.format.getBorderTop();
    }

    public void setBorderBottom(short s) {
        this.format.setIndentNotParentBorder(true);
        this.format.setBorderBottom(s);
    }

    public short getBorderBottom() {
        return this.format.getBorderBottom();
    }

    public void setLeftBorderColor(short s) {
        this.format.setLeftBorderPaletteIdx(s);
    }

    public short getLeftBorderColor() {
        return this.format.getLeftBorderPaletteIdx();
    }

    public void setRightBorderColor(short s) {
        this.format.setRightBorderPaletteIdx(s);
    }

    public short getRightBorderColor() {
        return this.format.getRightBorderPaletteIdx();
    }

    public void setTopBorderColor(short s) {
        this.format.setTopBorderPaletteIdx(s);
    }

    public short getTopBorderColor() {
        return this.format.getTopBorderPaletteIdx();
    }

    public void setBottomBorderColor(short s) {
        this.format.setBottomBorderPaletteIdx(s);
    }

    public short getBottomBorderColor() {
        return this.format.getBottomBorderPaletteIdx();
    }

    public void setFillPattern(short s) {
        this.format.setAdtlFillPattern(s);
    }

    public short getFillPattern() {
        return this.format.getAdtlFillPattern();
    }

    public void setFillBackgroundColor(short s) {
        this.format.setFillBackground(s);
    }

    public short getFillBackgroundColor() {
        return this.format.getFillBackground();
    }

    public void setFillForegroundColor(short s) {
        this.format.setFillForeground(s);
    }

    public short getFillForegroundColor() {
        return this.format.getFillForeground();
    }

    public boolean getShrinkToFit() {
        return this.format.getShrinkToFit();
    }

    public void setShrinkToFit(boolean z) {
        this.format.setShrinkToFit(z);
    }
}
