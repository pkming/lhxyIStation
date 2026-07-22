package org.apache.poi.hssf.usermodel;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFTextbox extends HSSFSimpleShape {
    public static final short OBJECT_TYPE_TEXT = 6;
    int marginBottom;
    int marginLeft;
    int marginRight;
    int marginTop;
    HSSFRichTextString string;

    public HSSFTextbox(HSSFShape hSSFShape, HSSFAnchor hSSFAnchor) {
        super(hSSFShape, hSSFAnchor);
        this.string = new HSSFRichTextString("");
        setShapeType(6);
    }

    public HSSFRichTextString getString() {
        return this.string;
    }

    public void setString(HSSFRichTextString hSSFRichTextString) {
        this.string = hSSFRichTextString;
    }

    public int getMarginLeft() {
        return this.marginLeft;
    }

    public void setMarginLeft(int i) {
        this.marginLeft = i;
    }

    public int getMarginRight() {
        return this.marginRight;
    }

    public void setMarginRight(int i) {
        this.marginRight = i;
    }

    public int getMarginTop() {
        return this.marginTop;
    }

    public void setMarginTop(int i) {
        this.marginTop = i;
    }

    public int getMarginBottom() {
        return this.marginBottom;
    }

    public void setMarginBottom(int i) {
        this.marginBottom = i;
    }
}
