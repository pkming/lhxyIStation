package org.apache.poi.hssf.usermodel;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFClientAnchor extends HSSFAnchor {
    short col1;
    short col2;
    int row1;
    int row2;

    public HSSFClientAnchor() {
    }

    public HSSFClientAnchor(int i, int i2, int i3, int i4, short s, int i5, short s2, int i6) {
        super(i, i2, i3, i4);
        checkRange(i, 0, 1023, "dx1");
        checkRange(i3, 0, 1023, "dx2");
        checkRange(i2, 0, 255, "dy1");
        checkRange(i4, 0, 255, "dy2");
        checkRange(s, 0, 255, "col1");
        checkRange(s2, 0, 255, "col2");
        checkRange(i5, 0, 65280, "row1");
        checkRange(i6, 0, 65280, "row2");
        this.col1 = s;
        this.row1 = i5;
        this.col2 = s2;
        this.row2 = i6;
    }

    public float getAnchorHeightInPoints(HSSFSheet hSSFSheet) {
        int iMin = Math.min(getDy1(), getDy2());
        int iMax = Math.max(getDy1(), getDy2());
        int iMin2 = Math.min(getRow1(), getRow2());
        int iMax2 = Math.max(getRow1(), getRow2());
        if (iMin2 == iMax2) {
            return ((iMax - iMin) / 256.0f) * getRowHeightInPoints(hSSFSheet, iMax2);
        }
        float rowHeightInPoints = ((256.0f - iMin) / 256.0f) * getRowHeightInPoints(hSSFSheet, iMin2);
        float rowHeightInPoints2 = 0.0f;
        while (true) {
            rowHeightInPoints += rowHeightInPoints2;
            iMin2++;
            if (iMin2 < iMax2) {
                rowHeightInPoints2 = getRowHeightInPoints(hSSFSheet, iMin2);
            } else {
                return rowHeightInPoints + ((iMax / 256.0f) * getRowHeightInPoints(hSSFSheet, iMax2));
            }
        }
    }

    private float getRowHeightInPoints(HSSFSheet hSSFSheet, int i) {
        HSSFRow row = hSSFSheet.getRow(i);
        if (row == null) {
            return hSSFSheet.getDefaultRowHeightInPoints();
        }
        return row.getHeightInPoints();
    }

    public short getCol1() {
        return this.col1;
    }

    public void setCol1(short s) {
        checkRange(s, 0, 255, "col1");
        this.col1 = s;
    }

    public short getCol2() {
        return this.col2;
    }

    public void setCol2(short s) {
        checkRange(s, 0, 255, "col2");
        this.col2 = s;
    }

    public int getRow1() {
        return this.row1;
    }

    public void setRow1(int i) {
        checkRange(i, 0, 65536, "row1");
        this.row1 = i;
    }

    public int getRow2() {
        return this.row2;
    }

    public void setRow2(int i) {
        checkRange(i, 0, 65536, "row2");
        this.row2 = i;
    }

    public void setAnchor(short s, int i, int i2, int i3, short s2, int i4, int i5, int i6) {
        checkRange(this.dx1, 0, 1023, "dx1");
        checkRange(this.dx2, 0, 1023, "dx2");
        checkRange(this.dy1, 0, 255, "dy1");
        checkRange(this.dy2, 0, 255, "dy2");
        checkRange(s, 0, 255, "col1");
        checkRange(s2, 0, 255, "col2");
        checkRange(i, 0, 65280, "row1");
        checkRange(i4, 0, 65280, "row2");
        this.col1 = s;
        this.row1 = i;
        this.dx1 = i2;
        this.dy1 = i3;
        this.col2 = s2;
        this.row2 = i4;
        this.dx2 = i5;
        this.dy2 = i6;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFAnchor
    public boolean isHorizontallyFlipped() {
        short s = this.col1;
        short s2 = this.col2;
        return s == s2 ? this.dx1 > this.dx2 : s > s2;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFAnchor
    public boolean isVerticallyFlipped() {
        int i = this.row1;
        int i2 = this.row2;
        return i == i2 ? this.dy1 > this.dy2 : i > i2;
    }

    private void checkRange(int i, int i2, int i3, String str) {
        if (i < i2 || i > i3) {
            throw new IllegalArgumentException(new StringBuffer().append(str).append(" must be between ").append(i2).append(" and ").append(i3).toString());
        }
    }
}
