package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class BoolErrRecord extends Record implements CellValueRecordInterface, Comparable {
    public static final short sid = 517;
    private int field_1_row;
    private short field_2_column;
    private short field_3_xf_index;
    private byte field_4_bBoolErr;
    private byte field_5_fError;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 12;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 517;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isValue() {
        return true;
    }

    public BoolErrRecord() {
    }

    public BoolErrRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public BoolErrRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_row = LittleEndian.getUShort(bArr, i + 0);
        this.field_2_column = LittleEndian.getShort(bArr, i + 2);
        this.field_3_xf_index = LittleEndian.getShort(bArr, i + 4);
        this.field_4_bBoolErr = bArr[i + 6];
        this.field_5_fError = bArr[i + 7];
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

    public void setValue(boolean z) {
        this.field_4_bBoolErr = z ? (byte) 1 : (byte) 0;
        this.field_5_fError = (byte) 0;
    }

    public void setValue(byte b) {
        if (b == 0 || b == 7 || b == 15 || b == 23 || b == 29 || b == 36 || b == 42) {
            this.field_4_bBoolErr = b;
            this.field_5_fError = (byte) 1;
            return;
        }
        throw new RuntimeException(new StringBuffer().append("Error Value can only be 0,7,15,23,29,36 or 42. It cannot be ").append((int) b).toString());
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

    public boolean getBooleanValue() {
        return this.field_4_bBoolErr != 0;
    }

    public byte getErrorValue() {
        return this.field_4_bBoolErr;
    }

    public boolean isBoolean() {
        return this.field_5_fError == 0;
    }

    public void setError(boolean z) {
        this.field_5_fError = z ? (byte) 1 : (byte) 0;
    }

    public boolean isError() {
        return this.field_5_fError != 0;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[BOOLERR]\n");
        stringBuffer.append("    .row            = ").append(Integer.toHexString(getRow())).append("\n");
        stringBuffer.append("    .col            = ").append(Integer.toHexString(getColumn())).append("\n");
        stringBuffer.append("    .xfindex        = ").append(Integer.toHexString(getXFIndex())).append("\n");
        if (isBoolean()) {
            stringBuffer.append("    .booleanValue   = ").append(getBooleanValue()).append("\n");
        } else {
            stringBuffer.append("    .errorValue     = ").append((int) getErrorValue()).append("\n");
        }
        stringBuffer.append("[/BOOLERR]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 517);
        LittleEndian.putShort(bArr, i + 2, (short) 8);
        LittleEndian.putShort(bArr, i + 4, (short) getRow());
        LittleEndian.putShort(bArr, i + 6, getColumn());
        LittleEndian.putShort(bArr, i + 8, getXFIndex());
        bArr[i + 10] = this.field_4_bBoolErr;
        bArr[i + 11] = this.field_5_fError;
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 517) {
            throw new RecordFormatException("Not a valid BoolErrRecord");
        }
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
        BoolErrRecord boolErrRecord = new BoolErrRecord();
        boolErrRecord.field_1_row = this.field_1_row;
        boolErrRecord.field_2_column = this.field_2_column;
        boolErrRecord.field_3_xf_index = this.field_3_xf_index;
        boolErrRecord.field_4_bBoolErr = this.field_4_bBoolErr;
        boolErrRecord.field_5_fError = this.field_5_fError;
        return boolErrRecord;
    }
}
