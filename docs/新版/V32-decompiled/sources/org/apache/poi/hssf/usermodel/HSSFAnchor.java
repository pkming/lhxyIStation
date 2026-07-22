package org.apache.poi.hssf.usermodel;

/* JADX INFO: loaded from: classes3.dex */
public abstract class HSSFAnchor {
    int dx1;
    int dx2;
    int dy1;
    int dy2;

    public abstract boolean isHorizontallyFlipped();

    public abstract boolean isVerticallyFlipped();

    public HSSFAnchor() {
    }

    public HSSFAnchor(int i, int i2, int i3, int i4) {
        this.dx1 = i;
        this.dy1 = i2;
        this.dx2 = i3;
        this.dy2 = i4;
    }

    public int getDx1() {
        return this.dx1;
    }

    public void setDx1(int i) {
        this.dx1 = i;
    }

    public int getDy1() {
        return this.dy1;
    }

    public void setDy1(int i) {
        this.dy1 = i;
    }

    public int getDy2() {
        return this.dy2;
    }

    public void setDy2(int i) {
        this.dy2 = i;
    }

    public int getDx2() {
        return this.dx2;
    }

    public void setDx2(int i) {
        this.dx2 = i;
    }
}
