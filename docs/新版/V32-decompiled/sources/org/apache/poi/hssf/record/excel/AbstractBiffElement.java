package org.apache.poi.hssf.record.excel;

import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractBiffElement implements BiffElement {
    private int length;
    private int type;

    @Override // org.apache.poi.hssf.record.excel.BiffElement
    public abstract byte[] toBinary() throws IOException;

    public AbstractBiffElement(int i, int i2) {
        this.type = i;
        this.length = i2;
    }

    @Override // org.apache.poi.hssf.record.excel.BiffElement
    public void setType(int i) {
        this.type = i;
    }

    @Override // org.apache.poi.hssf.record.excel.BiffElement
    public void setLength(int i) {
        this.length = i;
    }

    @Override // org.apache.poi.hssf.record.excel.BiffElement
    public int getType() {
        return this.type;
    }

    @Override // org.apache.poi.hssf.record.excel.BiffElement
    public int getBinaryLength() {
        return getLength() + 4;
    }

    protected int getLength() {
        return this.length;
    }
}
