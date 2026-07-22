package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes3.dex */
public interface CellValueRecordInterface {
    Object clone();

    short getColumn();

    int getRow();

    short getXFIndex();

    boolean isAfter(CellValueRecordInterface cellValueRecordInterface);

    boolean isBefore(CellValueRecordInterface cellValueRecordInterface);

    boolean isEqual(CellValueRecordInterface cellValueRecordInterface);

    void setColumn(short s);

    void setRow(int i);

    void setXFIndex(short s);
}
