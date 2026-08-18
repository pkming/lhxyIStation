package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class BlankRecord extends Record implements CellValueRecordInterface, Comparable {
    public static final short sid = 513;
    private int field_1_row;
    private short field_2_col;
    private short field_3_xf;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 10;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 513;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isValue() {
        return true;
    }

    public BlankRecord() {
    }

    public BlankRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public BlankRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_row = LittleEndian.getUShort(bArr, i + 0);
        this.field_2_col = LittleEndian.getShort(bArr, i + 2);
        this.field_3_xf = LittleEndian.getShort(bArr, i + 4);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 513) {
            throw new RecordFormatException("NOT A BLANKRECORD!");
        }
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setRow(int i) {
        this.field_1_row = i;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public int getRow() {
        return this.field_1_row;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getColumn() {
        return this.field_2_col;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setXFIndex(short s) {
        this.field_3_xf = s;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getXFIndex() {
        return this.field_3_xf;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setColumn(short s) {
        this.field_2_col = s;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isBefore(CellValueRecordInterface cellValueRecordInterface) {
        if (getRow() > cellValueRecordInterface.getRow()) {
            return false;
        }
        if (getRow() != cellValueRecordInterface.getRow() || getColumn() <= cellValueRecordInterface.getColumn()) {
            return (getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn()) ? false : true;
        }
        return false;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isAfter(CellValueRecordInterface cellValueRecordInterface) {
        if (getRow() < cellValueRecordInterface.getRow()) {
            return false;
        }
        if (getRow() != cellValueRecordInterface.getRow() || getColumn() >= cellValueRecordInterface.getColumn()) {
            return (getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn()) ? false : true;
        }
        return false;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isEqual(CellValueRecordInterface cellValueRecordInterface) {
        return getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[BLANK]\n");
        stringBuffer.append("row       = ").append(Integer.toHexString(getRow())).append("\n");
        stringBuffer.append("col       = ").append(Integer.toHexString(getColumn())).append("\n");
        stringBuffer.append("xf        = ").append(Integer.toHexString(getXFIndex())).append("\n");
        stringBuffer.append("[/BLANK]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 513);
        LittleEndian.putShort(bArr, i + 2, (short) 6);
        LittleEndian.putShort(bArr, i + 4, (short) getRow());
        LittleEndian.putShort(bArr, i + 6, getColumn());
        LittleEndian.putShort(bArr, i + 8, getXFIndex());
        return getRecordSize();
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        CellValueRecordInterface cellValueRecordInterface = (CellValueRecordInterface) obj;
        if (getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn()) {
            return 0;
        }
        if (getRow() < cellValueRecordInterface.getRow()) {
            return -1;
        }
        if (getRow() > cellValueRecordInterface.getRow()) {
            return 1;
        }
        return (getColumn() >= cellValueRecordInterface.getColumn() && getColumn() > cellValueRecordInterface.getColumn()) ? 1 : -1;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CellValueRecordInterface)) {
            return false;
        }
        CellValueRecordInterface cellValueRecordInterface = (CellValueRecordInterface) obj;
        return getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        BlankRecord blankRecord = new BlankRecord();
        blankRecord.field_1_row = this.field_1_row;
        blankRecord.field_2_col = this.field_2_col;
        blankRecord.field_3_xf = this.field_3_xf;
        return blankRecord;
    }
}
