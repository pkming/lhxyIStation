package org.apache.poi.hssf.record.aggregates;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;

/* JADX INFO: loaded from: classes3.dex */
public class RowRecordsAggregate extends Record {
    Map records;
    int firstrow = -1;
    int lastrow = -1;
    int size = 0;

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return ValueRecordsAggregate.sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
    }

    public RowRecordsAggregate() {
        this.records = null;
        this.records = new TreeMap();
    }

    public void insertRow(RowRecord rowRecord) {
        this.size += rowRecord.getRecordSize();
        this.records.put(rowRecord, rowRecord);
        int rowNumber = rowRecord.getRowNumber();
        int i = this.firstrow;
        if (rowNumber < i || i == -1) {
            this.firstrow = rowRecord.getRowNumber();
        }
        int rowNumber2 = rowRecord.getRowNumber();
        int i2 = this.lastrow;
        if (rowNumber2 > i2 || i2 == -1) {
            this.lastrow = rowRecord.getRowNumber();
        }
    }

    public void removeRow(RowRecord rowRecord) {
        this.size -= rowRecord.getRecordSize();
        this.records.remove(rowRecord);
    }

    public RowRecord getRow(int i) {
        RowRecord rowRecord = new RowRecord();
        rowRecord.setRowNumber((short) i);
        return (RowRecord) this.records.get(rowRecord);
    }

    public int getPhysicalNumberOfRows() {
        return this.records.size();
    }

    public int getFirstRowNum() {
        return this.firstrow;
    }

    public int getLastRowNum() {
        return this.lastrow;
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
        return this.size;
    }

    public Iterator getIterator() {
        return this.records.values().iterator();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        RowRecordsAggregate rowRecordsAggregate = new RowRecordsAggregate();
        Iterator iterator = getIterator();
        while (iterator.hasNext()) {
            rowRecordsAggregate.insertRow((RowRecord) ((RowRecord) iterator.next()).clone());
        }
        return rowRecordsAggregate;
    }

    public int findStartOfRowOutlineGroup(int i) {
        short outlineLevel = getRow(i).getOutlineLevel();
        while (getRow(i) != null && getRow(i).getOutlineLevel() >= outlineLevel) {
            i--;
        }
        return i + 1;
    }

    public int findEndOfRowOutlineGroup(int i) {
        short outlineLevel = getRow(i).getOutlineLevel();
        while (i < getLastRowNum() && getRow(i) != null && getRow(i).getOutlineLevel() >= outlineLevel) {
            i++;
        }
        return i - 1;
    }

    public int writeHidden(RowRecord rowRecord, int i, boolean z) {
        short outlineLevel = rowRecord.getOutlineLevel();
        while (rowRecord != null && getRow(i).getOutlineLevel() >= outlineLevel) {
            rowRecord.setZeroHeight(z);
            i++;
            rowRecord = getRow(i);
        }
        return i - 1;
    }

    public void collapseRow(int i) {
        int iFindStartOfRowOutlineGroup = findStartOfRowOutlineGroup(i);
        int iWriteHidden = writeHidden(getRow(iFindStartOfRowOutlineGroup), iFindStartOfRowOutlineGroup, true) + 1;
        if (getRow(iWriteHidden) != null) {
            getRow(iWriteHidden).setColapsed(true);
            return;
        }
        RowRecord rowRecordCreateRow = createRow(iWriteHidden);
        rowRecordCreateRow.setColapsed(true);
        insertRow(rowRecordCreateRow);
    }

    public static RowRecord createRow(int i) {
        RowRecord rowRecord = new RowRecord();
        rowRecord.setRowNumber(i);
        rowRecord.setHeight((short) 255);
        rowRecord.setOptimize((short) 0);
        rowRecord.setOptionFlags((short) 256);
        rowRecord.setXFIndex((short) 15);
        return rowRecord;
    }

    public boolean isRowGroupCollapsed(int i) {
        int iFindEndOfRowOutlineGroup = findEndOfRowOutlineGroup(i) + 1;
        if (getRow(iFindEndOfRowOutlineGroup) == null) {
            return false;
        }
        return getRow(iFindEndOfRowOutlineGroup).getColapsed();
    }

    public void expandRow(int i) {
        if (i != -1 && isRowGroupCollapsed(i)) {
            int iFindStartOfRowOutlineGroup = findStartOfRowOutlineGroup(i);
            RowRecord row = getRow(iFindStartOfRowOutlineGroup);
            int iFindEndOfRowOutlineGroup = findEndOfRowOutlineGroup(i);
            if (!isRowGroupHiddenByParent(i)) {
                while (iFindStartOfRowOutlineGroup <= iFindEndOfRowOutlineGroup) {
                    if (row.getOutlineLevel() == getRow(iFindStartOfRowOutlineGroup).getOutlineLevel()) {
                        getRow(iFindStartOfRowOutlineGroup).setZeroHeight(false);
                    } else if (!isRowGroupCollapsed(iFindStartOfRowOutlineGroup)) {
                        getRow(iFindStartOfRowOutlineGroup).setZeroHeight(false);
                    }
                    iFindStartOfRowOutlineGroup++;
                }
            }
            getRow(iFindEndOfRowOutlineGroup + 1).setColapsed(false);
        }
    }

    public boolean isRowGroupHiddenByParent(int i) {
        short outlineLevel;
        boolean zeroHeight;
        boolean zeroHeight2;
        int iFindEndOfRowOutlineGroup = findEndOfRowOutlineGroup(i) + 1;
        short outlineLevel2 = 0;
        if (getRow(iFindEndOfRowOutlineGroup) == null) {
            zeroHeight = false;
            outlineLevel = 0;
        } else {
            outlineLevel = getRow(iFindEndOfRowOutlineGroup).getOutlineLevel();
            zeroHeight = getRow(iFindEndOfRowOutlineGroup).getZeroHeight();
        }
        int iFindStartOfRowOutlineGroup = findStartOfRowOutlineGroup(i) - 1;
        if (iFindStartOfRowOutlineGroup < 0 || getRow(iFindStartOfRowOutlineGroup) == null) {
            zeroHeight2 = false;
        } else {
            outlineLevel2 = getRow(iFindStartOfRowOutlineGroup).getOutlineLevel();
            zeroHeight2 = getRow(iFindStartOfRowOutlineGroup).getZeroHeight();
        }
        return outlineLevel > outlineLevel2 ? zeroHeight : zeroHeight2;
    }
}
