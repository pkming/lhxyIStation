package org.apache.poi.hssf.usermodel;

/* JADX INFO: loaded from: classes3.dex */
public abstract class HSSFShape {
    public static final int LINESTYLE_DASHDOTDOTSYS = 4;
    public static final int LINESTYLE_DASHDOTGEL = 8;
    public static final int LINESTYLE_DASHDOTSYS = 3;
    public static final int LINESTYLE_DASHGEL = 6;
    public static final int LINESTYLE_DASHSYS = 1;
    public static final int LINESTYLE_DOTGEL = 5;
    public static final int LINESTYLE_DOTSYS = 2;
    public static final int LINESTYLE_LONGDASHDOTDOTGEL = 10;
    public static final int LINESTYLE_LONGDASHDOTGEL = 9;
    public static final int LINESTYLE_LONGDASHGEL = 7;
    public static final int LINESTYLE_NONE = -1;
    public static final int LINESTYLE_SOLID = 0;
    public static final int LINEWIDTH_DEFAULT = 9525;
    public static final int LINEWIDTH_ONE_PT = 12700;
    HSSFAnchor anchor;
    HSSFShape parent;
    int lineStyleColor = 134217792;
    int fillColor = 134217737;
    int lineWidth = LINEWIDTH_DEFAULT;
    int lineStyle = 0;
    boolean noFill = false;

    public int countOfAllChildren() {
        return 1;
    }

    HSSFShape(HSSFShape hSSFShape, HSSFAnchor hSSFAnchor) {
        this.parent = hSSFShape;
        this.anchor = hSSFAnchor;
    }

    public HSSFShape getParent() {
        return this.parent;
    }

    public HSSFAnchor getAnchor() {
        return this.anchor;
    }

    public void setAnchor(HSSFAnchor hSSFAnchor) {
        if (this.parent == null) {
            if (hSSFAnchor instanceof HSSFChildAnchor) {
                throw new IllegalArgumentException("Must use client anchors for shapes directly attached to sheet.");
            }
        } else if (hSSFAnchor instanceof HSSFClientAnchor) {
            throw new IllegalArgumentException("Must use child anchors for shapes attached to groups.");
        }
        this.anchor = hSSFAnchor;
    }

    public int getLineStyleColor() {
        return this.lineStyleColor;
    }

    public void setLineStyleColor(int i) {
        this.lineStyleColor = i;
    }

    public void setLineStyleColor(int i, int i2, int i3) {
        this.lineStyleColor = i | (i2 << 8) | (i3 << 16);
    }

    public int getFillColor() {
        return this.fillColor;
    }

    public void setFillColor(int i) {
        this.fillColor = i;
    }

    public void setFillColor(int i, int i2, int i3) {
        this.fillColor = i | (i2 << 8) | (i3 << 16);
    }

    public int getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(int i) {
        this.lineWidth = i;
    }

    public int getLineStyle() {
        return this.lineStyle;
    }

    public void setLineStyle(int i) {
        this.lineStyle = i;
    }

    public boolean isNoFill() {
        return this.noFill;
    }

    public void setNoFill(boolean z) {
        this.noFill = z;
    }
}
