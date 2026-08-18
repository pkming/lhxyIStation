package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.StringRecord;

/* JADX INFO: loaded from: classes3.dex */
public class FormulaRecordAggregate extends Record implements CellValueRecordInterface, Comparable {
    public static final short sid = -2000;
    private FormulaRecord formulaRecord;
    private SharedFormulaRecord sharedFormulaRecord;
    private StringRecord stringRecord;

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
    }

    public FormulaRecordAggregate(FormulaRecord formulaRecord, StringRecord stringRecord) {
        this.formulaRecord = formulaRecord;
        this.stringRecord = stringRecord;
    }

    public FormulaRecordAggregate(FormulaRecord formulaRecord, StringRecord stringRecord, SharedFormulaRecord sharedFormulaRecord) {
        this.formulaRecord = formulaRecord;
        this.stringRecord = stringRecord;
        this.sharedFormulaRecord = sharedFormulaRecord;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        int iSerialize = this.formulaRecord.serialize(i, bArr) + i;
        if (getSharedFormulaRecord() != null) {
            iSerialize += getSharedFormulaRecord().serialize(iSerialize, bArr);
        }
        StringRecord stringRecord = this.stringRecord;
        if (stringRecord != null) {
            iSerialize += stringRecord.serialize(iSerialize, bArr);
        }
        return iSerialize - i;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        int recordSize = this.formulaRecord.getRecordSize();
        StringRecord stringRecord = this.stringRecord;
        return recordSize + (stringRecord == null ? 0 : stringRecord.getRecordSize()) + (getSharedFormulaRecord() != null ? getSharedFormulaRecord().getRecordSize() : 0);
    }

    public void setStringRecord(StringRecord stringRecord) {
        this.stringRecord = stringRecord;
    }

    public void setFormulaRecord(FormulaRecord formulaRecord) {
        this.formulaRecord = formulaRecord;
    }

    public FormulaRecord getFormulaRecord() {
        return this.formulaRecord;
    }

    public StringRecord getStringRecord() {
        return this.stringRecord;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isEqual(CellValueRecordInterface cellValueRecordInterface) {
        return this.formulaRecord.isEqual(cellValueRecordInterface);
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isAfter(CellValueRecordInterface cellValueRecordInterface) {
        return this.formulaRecord.isAfter(cellValueRecordInterface);
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isBefore(CellValueRecordInterface cellValueRecordInterface) {
        return this.formulaRecord.isBefore(cellValueRecordInterface);
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getXFIndex() {
        return this.formulaRecord.getXFIndex();
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setXFIndex(short s) {
        this.formulaRecord.setXFIndex(s);
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setColumn(short s) {
        this.formulaRecord.setColumn(s);
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setRow(int i) {
        this.formulaRecord.setRow(i);
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getColumn() {
        return this.formulaRecord.getColumn();
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public int getRow() {
        return this.formulaRecord.getRow();
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        return this.formulaRecord.compareTo(obj);
    }

    public boolean equals(Object obj) {
        return this.formulaRecord.equals(obj);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        return this.formulaRecord.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        StringRecord stringRecord = this.stringRecord;
        StringRecord stringRecord2 = stringRecord == null ? null : (StringRecord) stringRecord.clone();
        SharedFormulaRecord sharedFormulaRecord = this.sharedFormulaRecord;
        return new FormulaRecordAggregate((FormulaRecord) this.formulaRecord.clone(), stringRecord2, sharedFormulaRecord != null ? (SharedFormulaRecord) sharedFormulaRecord.clone() : null);
    }

    public SharedFormulaRecord getSharedFormulaRecord() {
        return this.sharedFormulaRecord;
    }

    public void setSharedFormulaRecord(SharedFormulaRecord sharedFormulaRecord) {
        this.sharedFormulaRecord = sharedFormulaRecord;
    }

    public String getStringValue() {
        StringRecord stringRecord = this.stringRecord;
        if (stringRecord == null) {
            return null;
        }
        return stringRecord.getString();
    }
}
