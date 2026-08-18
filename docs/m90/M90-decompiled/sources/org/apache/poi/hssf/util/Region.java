package org.apache.poi.hssf.util;

import org.apache.poi.hssf.record.MergeCellsRecord;

/* JADX INFO: loaded from: classes3.dex */
public class Region implements Comparable {
    private short colFrom;
    private short colTo;
    private int rowFrom;
    private int rowTo;

    public Region() {
    }

    public Region(int i, short s, int i2, short s2) {
        this.rowFrom = i;
        this.rowTo = i2;
        this.colFrom = s;
        this.colTo = s2;
    }

    public Region(MergeCellsRecord.MergedRegion mergedRegion) {
        this(mergedRegion.row_from, mergedRegion.col_from, mergedRegion.row_to, mergedRegion.col_to);
    }

    public short getColumnFrom() {
        return this.colFrom;
    }

    public int getRowFrom() {
        return this.rowFrom;
    }

    public short getColumnTo() {
        return this.colTo;
    }

    public int getRowTo() {
        return this.rowTo;
    }

    public void setColumnFrom(short s) {
        this.colFrom = s;
    }

    public void setRowFrom(int i) {
        this.rowFrom = i;
    }

    public void setColumnTo(short s) {
        this.colTo = s;
    }

    public void setRowTo(int i) {
        this.rowTo = i;
    }

    public boolean contains(int i, short s) {
        return this.rowFrom <= i && this.rowTo >= i && this.colFrom <= s && this.colTo >= s;
    }

    public boolean equals(Region region) {
        return compareTo(region) == 0;
    }

    public int compareTo(Region region) {
        if (getRowFrom() == region.getRowFrom() && getColumnFrom() == region.getColumnFrom() && getRowTo() == region.getRowTo() && getColumnTo() == region.getColumnTo()) {
            return 0;
        }
        return (getRowFrom() < region.getRowFrom() || getColumnFrom() < region.getColumnFrom() || getRowTo() < region.getRowTo() || getColumnTo() < region.getColumnTo()) ? 1 : -1;
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        return compareTo((Region) obj);
    }

    public int getArea() {
        return ((getRowTo() - getRowFrom()) + 1) * ((getColumnTo() - getColumnFrom()) + 1);
    }
}
