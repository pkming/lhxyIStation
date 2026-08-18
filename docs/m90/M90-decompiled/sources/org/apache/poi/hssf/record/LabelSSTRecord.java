package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class LabelSSTRecord extends Record implements CellValueRecordInterface, Comparable {
    public static final short sid = 253;
    private int field_1_row;
    private short field_2_column;
    private short field_3_xf_index;
    private int field_4_sst_index;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 14;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 253;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isValue() {
        return true;
    }

    public LabelSSTRecord() {
    }

    public LabelSSTRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public LabelSSTRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 253) {
            throw new RecordFormatException("NOT A valid LabelSST RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_row = LittleEndian.getUShort(bArr, i + 0);
        this.field_2_column = LittleEndian.getShort(bArr, i + 2);
        this.field_3_xf_index = LittleEndian.getShort(bArr, i + 4);
        this.field_4_sst_index = LittleEndian.getInt(bArr, i + 6);
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setRow(int i) {
        this.field_1_row = i;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setColumn(short s) {
        this.field_2_column = s;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setXFIndex(short s) {
        this.field_3_xf_index = s;
    }

    public void setSSTIndex(int i) {
        this.field_4_sst_index = i;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public int getRow() {
        return this.field_1_row;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getColumn() {
        return this.field_2_column;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getXFIndex() {
        return this.field_3_xf_index;
    }

    public int getSSTIndex() {
        return this.field_4_sst_index;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[LABELSST]\n");
        stringBuffer.append("    .row            = ").append(Integer.toHexString(getRow())).append("\n");
        stringBuffer.append("    .column         = ").append(Integer.toHexString(getColumn())).append("\n");
        stringBuffer.append("    .xfindex        = ").append(Integer.toHexString(getXFIndex())).append("\n");
        stringBuffer.append("    .sstindex       = ").append(Integer.toHexString(getSSTIndex())).append("\n");
        stringBuffer.append("[/LABELSST]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 253);
        LittleEndian.putShort(bArr, i + 2, (short) 10);
        LittleEndian.putShort(bArr, i + 4, (short) getRow());
        LittleEndian.putShort(bArr, i + 6, getColumn());
        LittleEndian.putShort(bArr, i + 8, getXFIndex());
        LittleEndian.putInt(bArr, i + 10, getSSTIndex());
        return getRecordSize();
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
        LabelSSTRecord labelSSTRecord = new LabelSSTRecord();
        labelSSTRecord.field_1_row = this.field_1_row;
        labelSSTRecord.field_2_column = this.field_2_column;
        labelSSTRecord.field_3_xf_index = this.field_3_xf_index;
        labelSSTRecord.field_4_sst_index = this.field_4_sst_index;
        return labelSSTRecord;
    }
}
