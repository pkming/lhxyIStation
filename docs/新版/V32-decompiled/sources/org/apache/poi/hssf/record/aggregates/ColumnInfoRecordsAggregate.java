package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.record.ColumnInfoRecord;
import org.apache.poi.hssf.record.Record;

/* JADX INFO: loaded from: classes3.dex */
public class ColumnInfoRecordsAggregate extends Record {
    List records;
    int size = 0;

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) -1012;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
    }

    public ColumnInfoRecordsAggregate() {
        this.records = null;
        this.records = new ArrayList();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return this.size;
    }

    public Iterator getIterator() {
        return this.records.iterator();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        ColumnInfoRecordsAggregate columnInfoRecordsAggregate = new ColumnInfoRecordsAggregate();
        Iterator iterator = getIterator();
        while (iterator.hasNext()) {
            columnInfoRecordsAggregate.insertColumn((ColumnInfoRecord) ((ColumnInfoRecord) iterator.next()).clone());
        }
        return columnInfoRecordsAggregate;
    }

    public void insertColumn(ColumnInfoRecord columnInfoRecord) {
        this.size += columnInfoRecord.getRecordSize();
        this.records.add(columnInfoRecord);
    }

    public void insertColumn(int i, ColumnInfoRecord columnInfoRecord) {
        this.size += columnInfoRecord.getRecordSize();
        this.records.add(i, columnInfoRecord);
    }

    public int getNumColumns() {
        return this.records.size();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        Iterator it = this.records.iterator();
        int iSerialize = i;
        while (it.hasNext()) {
            iSerialize += ((Record) it.next()).serialize(iSerialize, bArr);
        }
        return iSerialize - i;
    }

    public int findStartOfColumnOutlineGroup(int i) {
        ColumnInfoRecord columnInfoRecord = (ColumnInfoRecord) this.records.get(i);
        short outlineLevel = columnInfoRecord.getOutlineLevel();
        while (i != 0) {
            ColumnInfoRecord columnInfoRecord2 = (ColumnInfoRecord) this.records.get(i - 1);
            if (columnInfoRecord.getFirstColumn() - 1 != columnInfoRecord2.getLastColumn() || columnInfoRecord2.getOutlineLevel() < outlineLevel) {
                break;
            }
            i--;
            columnInfoRecord = columnInfoRecord2;
        }
        return i;
    }

    public int findEndOfColumnOutlineGroup(int i) {
        ColumnInfoRecord columnInfoRecord = (ColumnInfoRecord) this.records.get(i);
        short outlineLevel = columnInfoRecord.getOutlineLevel();
        while (i < this.records.size() - 1) {
            int i2 = i + 1;
            ColumnInfoRecord columnInfoRecord2 = (ColumnInfoRecord) this.records.get(i2);
            if (columnInfoRecord.getLastColumn() + 1 != columnInfoRecord2.getFirstColumn() || columnInfoRecord2.getOutlineLevel() < outlineLevel) {
                break;
            }
            columnInfoRecord = columnInfoRecord2;
            i = i2;
        }
        return i;
    }

    public ColumnInfoRecord getColInfo(int i) {
        return (ColumnInfoRecord) this.records.get(i);
    }

    public ColumnInfoRecord writeHidden(ColumnInfoRecord columnInfoRecord, int i, boolean z) {
        short outlineLevel = columnInfoRecord.getOutlineLevel();
        while (i < this.records.size()) {
            columnInfoRecord.setHidden(z);
            i++;
            if (i < this.records.size()) {
                ColumnInfoRecord columnInfoRecord2 = (ColumnInfoRecord) this.records.get(i);
                if (columnInfoRecord.getLastColumn() + 1 != columnInfoRecord2.getFirstColumn() || columnInfoRecord2.getOutlineLevel() < outlineLevel) {
                    break;
                }
                columnInfoRecord = columnInfoRecord2;
            }
        }
        return columnInfoRecord;
    }

    public boolean isColumnGroupCollapsed(int i) {
        int iFindEndOfColumnOutlineGroup = findEndOfColumnOutlineGroup(i);
        if (iFindEndOfColumnOutlineGroup >= this.records.size()) {
            return false;
        }
        int lastColumn = getColInfo(iFindEndOfColumnOutlineGroup).getLastColumn() + 1;
        int i2 = iFindEndOfColumnOutlineGroup + 1;
        if (lastColumn != getColInfo(i2).getFirstColumn()) {
            return false;
        }
        return getColInfo(i2).getCollapsed();
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x003d  */
    /* JADX WARN: Removed duplicated region for block: B:4:0x000d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean isColumnGroupHiddenByParent(int r6) {
        /*
            r5 = this;
            int r0 = r5.findEndOfColumnOutlineGroup(r6)
            java.util.List r1 = r5.records
            int r1 = r1.size()
            r2 = 0
            if (r0 < r1) goto L10
        Ld:
            r0 = r2
            r1 = r0
            goto L37
        L10:
            org.apache.poi.hssf.record.ColumnInfoRecord r1 = r5.getColInfo(r0)
            short r1 = r1.getLastColumn()
            int r1 = r1 + 1
            int r0 = r0 + 1
            org.apache.poi.hssf.record.ColumnInfoRecord r3 = r5.getColInfo(r0)
            short r3 = r3.getFirstColumn()
            if (r1 == r3) goto L27
            goto Ld
        L27:
            org.apache.poi.hssf.record.ColumnInfoRecord r1 = r5.getColInfo(r0)
            short r1 = r1.getOutlineLevel()
            org.apache.poi.hssf.record.ColumnInfoRecord r0 = r5.getColInfo(r0)
            boolean r0 = r0.getHidden()
        L37:
            int r6 = r5.findStartOfColumnOutlineGroup(r6)
            if (r6 > 0) goto L3f
        L3d:
            r6 = r2
            goto L66
        L3f:
            org.apache.poi.hssf.record.ColumnInfoRecord r3 = r5.getColInfo(r6)
            short r3 = r3.getFirstColumn()
            int r3 = r3 + (-1)
            int r6 = r6 + (-1)
            org.apache.poi.hssf.record.ColumnInfoRecord r4 = r5.getColInfo(r6)
            short r4 = r4.getLastColumn()
            if (r3 == r4) goto L56
            goto L3d
        L56:
            org.apache.poi.hssf.record.ColumnInfoRecord r2 = r5.getColInfo(r6)
            short r2 = r2.getOutlineLevel()
            org.apache.poi.hssf.record.ColumnInfoRecord r6 = r5.getColInfo(r6)
            boolean r6 = r6.getHidden()
        L66:
            if (r1 <= r2) goto L69
            return r0
        L69:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.record.aggregates.ColumnInfoRecordsAggregate.isColumnGroupHiddenByParent(int):boolean");
    }

    public void collapseColumn(short s) {
        int iFindColumnIdx = findColumnIdx(s, 0);
        if (iFindColumnIdx == -1) {
            return;
        }
        setColumn((short) (writeHidden((ColumnInfoRecord) this.records.get(findStartOfColumnOutlineGroup(iFindColumnIdx)), iFindColumnIdx, true).getLastColumn() + 1), null, null, null, Boolean.TRUE);
    }

    public void expandColumn(short s) {
        int iFindColumnIdx = findColumnIdx(s, 0);
        if (iFindColumnIdx != -1 && isColumnGroupCollapsed(iFindColumnIdx)) {
            int iFindStartOfColumnOutlineGroup = findStartOfColumnOutlineGroup(iFindColumnIdx);
            ColumnInfoRecord colInfo = getColInfo(iFindStartOfColumnOutlineGroup);
            int iFindEndOfColumnOutlineGroup = findEndOfColumnOutlineGroup(iFindColumnIdx);
            getColInfo(iFindEndOfColumnOutlineGroup);
            if (!isColumnGroupHiddenByParent(iFindColumnIdx)) {
                while (iFindStartOfColumnOutlineGroup <= iFindEndOfColumnOutlineGroup) {
                    if (colInfo.getOutlineLevel() == getColInfo(iFindStartOfColumnOutlineGroup).getOutlineLevel()) {
                        getColInfo(iFindStartOfColumnOutlineGroup).setHidden(false);
                    }
                    iFindStartOfColumnOutlineGroup++;
                }
            }
            setColumn((short) (colInfo.getLastColumn() + 1), null, null, null, Boolean.FALSE);
        }
    }

    public static Record createColInfo() {
        ColumnInfoRecord columnInfoRecord = new ColumnInfoRecord();
        columnInfoRecord.setColumnWidth((short) 2275);
        columnInfoRecord.setOptions((short) 2);
        columnInfoRecord.setXFIndex((short) 15);
        return columnInfoRecord;
    }

    public void setColumn(short s, Short sh, Integer num, Boolean bool, Boolean bool2) {
        ColumnInfoRecord columnInfoRecord;
        int i = 0;
        while (true) {
            columnInfoRecord = null;
            if (i < this.records.size()) {
                columnInfoRecord = (ColumnInfoRecord) this.records.get(i);
                if (columnInfoRecord.getFirstColumn() <= s && s <= columnInfoRecord.getLastColumn()) {
                    break;
                } else {
                    i++;
                }
            } else {
                break;
            }
        }
        ColumnInfoRecord columnInfoRecord2 = columnInfoRecord;
        if (columnInfoRecord2 == null) {
            ColumnInfoRecord columnInfoRecord3 = (ColumnInfoRecord) createColInfo();
            columnInfoRecord3.setFirstColumn(s);
            columnInfoRecord3.setLastColumn(s);
            setColumnInfoFields(columnInfoRecord3, sh, num, bool, bool2);
            insertColumn(i, columnInfoRecord3);
            return;
        }
        if ((sh != null && columnInfoRecord2.getColumnWidth() != sh.shortValue()) || (num != null && columnInfoRecord2.getOutlineLevel() != num.intValue()) || (bool != null && columnInfoRecord2.getHidden() != bool.booleanValue()) || (bool2 != null && columnInfoRecord2.getCollapsed() != bool2.booleanValue())) {
            if (columnInfoRecord2.getFirstColumn() == s && columnInfoRecord2.getLastColumn() == s) {
                setColumnInfoFields(columnInfoRecord2, sh, num, bool, bool2);
                return;
            }
            if (columnInfoRecord2.getFirstColumn() == s || columnInfoRecord2.getLastColumn() == s) {
                if (columnInfoRecord2.getFirstColumn() == s) {
                    columnInfoRecord2.setFirstColumn((short) (s + 1));
                } else {
                    columnInfoRecord2.setLastColumn((short) (s - 1));
                }
                ColumnInfoRecord columnInfoRecord4 = (ColumnInfoRecord) createColInfo();
                columnInfoRecord4.setFirstColumn(s);
                columnInfoRecord4.setLastColumn(s);
                columnInfoRecord4.setOptions(columnInfoRecord2.getOptions());
                columnInfoRecord4.setXFIndex(columnInfoRecord2.getXFIndex());
                setColumnInfoFields(columnInfoRecord4, sh, num, bool, bool2);
                insertColumn(i, columnInfoRecord4);
                return;
            }
            short lastColumn = columnInfoRecord2.getLastColumn();
            columnInfoRecord2.setLastColumn((short) (s - 1));
            ColumnInfoRecord columnInfoRecord5 = (ColumnInfoRecord) createColInfo();
            columnInfoRecord5.setFirstColumn(s);
            columnInfoRecord5.setLastColumn(s);
            columnInfoRecord5.setOptions(columnInfoRecord2.getOptions());
            columnInfoRecord5.setXFIndex(columnInfoRecord2.getXFIndex());
            setColumnInfoFields(columnInfoRecord5, sh, num, bool, bool2);
            int i2 = i + 1;
            insertColumn(i2, columnInfoRecord5);
            ColumnInfoRecord columnInfoRecord6 = (ColumnInfoRecord) createColInfo();
            columnInfoRecord6.setFirstColumn((short) (s + 1));
            columnInfoRecord6.setLastColumn(lastColumn);
            columnInfoRecord6.setOptions(columnInfoRecord2.getOptions());
            columnInfoRecord6.setXFIndex(columnInfoRecord2.getXFIndex());
            columnInfoRecord6.setColumnWidth(columnInfoRecord2.getColumnWidth());
            insertColumn(i2 + 1, columnInfoRecord6);
        }
    }

    private void setColumnInfoFields(ColumnInfoRecord columnInfoRecord, Short sh, Integer num, Boolean bool, Boolean bool2) {
        if (sh != null) {
            columnInfoRecord.setColumnWidth(sh.shortValue());
        }
        if (num != null) {
            columnInfoRecord.setOutlineLevel(num.shortValue());
        }
        if (bool != null) {
            columnInfoRecord.setHidden(bool.booleanValue());
        }
        if (bool2 != null) {
            columnInfoRecord.setCollapsed(bool2.booleanValue());
        }
    }

    public int findColumnIdx(int i, int i2) {
        if (i < 0) {
            throw new IllegalArgumentException(new StringBuffer().append("column parameter out of range: ").append(i).toString());
        }
        if (i2 < 0) {
            throw new IllegalArgumentException(new StringBuffer().append("fromIdx parameter out of range: ").append(i2).toString());
        }
        while (i2 < this.records.size()) {
            ColumnInfoRecord columnInfoRecord = (ColumnInfoRecord) this.records.get(i2);
            if (columnInfoRecord.getFirstColumn() <= i && i <= columnInfoRecord.getLastColumn()) {
                return i2;
            }
            i2++;
        }
        return -1;
    }

    public void collapseColInfoRecords(int i) {
        if (i == 0) {
            return;
        }
        ColumnInfoRecord columnInfoRecord = (ColumnInfoRecord) this.records.get(i - 1);
        ColumnInfoRecord columnInfoRecord2 = (ColumnInfoRecord) this.records.get(i);
        if (columnInfoRecord.getLastColumn() == columnInfoRecord2.getFirstColumn() - 1) {
            if (columnInfoRecord.getXFIndex() == columnInfoRecord2.getXFIndex() && columnInfoRecord.getOptions() == columnInfoRecord2.getOptions() && columnInfoRecord.getColumnWidth() == columnInfoRecord2.getColumnWidth()) {
                columnInfoRecord.setLastColumn(columnInfoRecord2.getLastColumn());
                this.records.remove(i);
            }
        }
    }

    public void groupColumnRange(short s, short s2, boolean z) {
        int i;
        int iMin;
        int i2 = 0;
        for (int i3 = s; i3 <= s2; i3++) {
            int iFindColumnIdx = findColumnIdx(i3, Math.max(0, i2));
            if (iFindColumnIdx != -1) {
                short outlineLevel = ((ColumnInfoRecord) this.records.get(iFindColumnIdx)).getOutlineLevel();
                iMin = Math.min(7, Math.max(0, z ? outlineLevel + 1 : outlineLevel - 1));
                i = iFindColumnIdx - 1;
            } else {
                i = i2;
                iMin = 1;
            }
            setColumn((short) i3, null, new Integer(iMin), null, null);
            collapseColInfoRecords(findColumnIdx(i3, Math.max(0, i)));
            i2 = i;
        }
    }
}
