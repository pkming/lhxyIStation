package org.apache.poi.hssf.record.aggregates;

import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.UnknownRecord;

/* JADX INFO: loaded from: classes3.dex */
public class ValueRecordsAggregate extends Record {
    public static final short sid = -1000;
    int firstcell = -1;
    int lastcell = -1;
    TreeMap records;

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
    }

    public ValueRecordsAggregate() {
        this.records = null;
        this.records = new TreeMap();
    }

    public void insertCell(CellValueRecordInterface cellValueRecordInterface) {
        this.records.put(cellValueRecordInterface, cellValueRecordInterface);
        short column = cellValueRecordInterface.getColumn();
        int i = this.firstcell;
        if (column < i || i == -1) {
            this.firstcell = cellValueRecordInterface.getColumn();
        }
        short column2 = cellValueRecordInterface.getColumn();
        int i2 = this.lastcell;
        if (column2 > i2 || i2 == -1) {
            this.lastcell = cellValueRecordInterface.getColumn();
        }
    }

    public void removeCell(CellValueRecordInterface cellValueRecordInterface) {
        this.records.remove(cellValueRecordInterface);
    }

    public int getPhysicalNumberOfCells() {
        return this.records.size();
    }

    public int getFirstCellNum() {
        return this.firstcell;
    }

    public int getLastCellNum() {
        return this.lastcell;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int construct(int i, List list) {
        Record record;
        boolean z;
        FormulaRecordAggregate formulaRecordAggregate = null;
        while (i < list.size() && (((z = (record = (Record) list.get(i)) instanceof StringRecord)) || record.isInValueSection() || (record instanceof UnknownRecord))) {
            if (record instanceof FormulaRecord) {
                formulaRecordAggregate = new FormulaRecordAggregate((FormulaRecord) record, null);
                insertCell(formulaRecordAggregate);
            } else if (z) {
                formulaRecordAggregate.setStringRecord((StringRecord) record);
            } else if (record instanceof SharedFormulaRecord) {
                formulaRecordAggregate.setSharedFormulaRecord((SharedFormulaRecord) record);
            } else if (record.isValue()) {
                insertCell((CellValueRecordInterface) record);
            }
            i++;
        }
        return i;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        Iterator it = this.records.values().iterator();
        int iSerialize = i;
        while (it.hasNext()) {
            iSerialize += ((Record) it.next()).serialize(iSerialize, bArr);
        }
        return iSerialize - i;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        Iterator it = this.records.values().iterator();
        int recordSize = 0;
        while (it.hasNext()) {
            recordSize += ((Record) it.next()).getRecordSize();
        }
        return recordSize;
    }

    public Iterator getIterator() {
        return this.records.values().iterator();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        ValueRecordsAggregate valueRecordsAggregate = new ValueRecordsAggregate();
        Iterator iterator = getIterator();
        while (iterator.hasNext()) {
            valueRecordsAggregate.insertCell((CellValueRecordInterface) ((CellValueRecordInterface) iterator.next()).clone());
        }
        return valueRecordsAggregate;
    }
}
