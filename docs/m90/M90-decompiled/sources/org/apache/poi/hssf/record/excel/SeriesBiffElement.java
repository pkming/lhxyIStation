package org.apache.poi.hssf.record.excel;

/* JADX INFO: loaded from: classes3.dex */
public abstract class SeriesBiffElement extends AbstractBiffElement {
    private int index;

    public SeriesBiffElement(int i, int i2) {
        super(i, i2);
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public int getIndex() {
        return this.index;
    }
}
