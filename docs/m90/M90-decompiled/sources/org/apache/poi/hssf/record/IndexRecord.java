package org.apache.poi.hssf.record;

import org.apache.poi.util.IntList;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class IndexRecord extends Record {
    public static final int DBCELL_CAPACITY = 30;
    public static final short sid = 523;
    public int field_1_zero;
    public int field_2_first_row;
    public int field_3_last_row_add1;
    public int field_4_zero;
    public IntList field_5_dbcells;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 523;
    }

    public IndexRecord() {
    }

    public IndexRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public IndexRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 523) {
            throw new RecordFormatException("NOT An Index RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_5_dbcells = new IntList(30);
        this.field_1_zero = LittleEndian.getInt(bArr, i + 0);
        this.field_2_first_row = LittleEndian.getInt(bArr, i + 4);
        this.field_3_last_row_add1 = LittleEndian.getInt(bArr, i + 8);
        this.field_4_zero = LittleEndian.getInt(bArr, i + 12);
        for (int i2 = 16; i2 < s; i2 += 4) {
            this.field_5_dbcells.add(LittleEndian.getInt(bArr, i2 + i));
        }
    }

    public void setFirstRow(int i) {
        this.field_2_first_row = i;
    }

    public void setLastRowAdd1(int i) {
        this.field_3_last_row_add1 = i;
    }

    public void addDbcell(int i) {
        if (this.field_5_dbcells == null) {
            this.field_5_dbcells = new IntList();
        }
        this.field_5_dbcells.add(i);
    }

    public void setDbcell(int i, int i2) {
        this.field_5_dbcells.set(i, i2);
    }

    public int getFirstRow() {
        return this.field_2_first_row;
    }

    public int getLastRowAdd1() {
        return this.field_3_last_row_add1;
    }

    public int getNumDbcells() {
        IntList intList = this.field_5_dbcells;
        if (intList == null) {
            return 0;
        }
        return intList.size();
    }

    public int getDbcellAt(int i) {
        return this.field_5_dbcells.get(i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[INDEX]\n");
        stringBuffer.append("    .firstrow       = ").append(Integer.toHexString(getFirstRow())).append("\n");
        stringBuffer.append("    .lastrowadd1    = ").append(Integer.toHexString(getLastRowAdd1())).append("\n");
        for (int i = 0; i < getNumDbcells(); i++) {
            stringBuffer.append(new StringBuffer().append("    .dbcell_").append(i).append("       = ").toString()).append(Integer.toHexString(getDbcellAt(i))).append("\n");
        }
        stringBuffer.append("[/INDEX]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 523);
        LittleEndian.putShort(bArr, i + 2, (short) ((getNumDbcells() * 4) + 16));
        LittleEndian.putInt(bArr, i + 4, 0);
        LittleEndian.putInt(bArr, i + 8, getFirstRow());
        LittleEndian.putInt(bArr, i + 12, getLastRowAdd1());
        LittleEndian.putInt(bArr, i + 16, 0);
        for (int i2 = 0; i2 < getNumDbcells(); i2++) {
            LittleEndian.putInt(bArr, (i2 * 4) + 20 + i, getDbcellAt(i2));
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getNumDbcells() * 4) + 20;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        IndexRecord indexRecord = new IndexRecord();
        indexRecord.field_1_zero = this.field_1_zero;
        indexRecord.field_2_first_row = this.field_2_first_row;
        indexRecord.field_3_last_row_add1 = this.field_3_last_row_add1;
        indexRecord.field_4_zero = this.field_4_zero;
        IntList intList = new IntList();
        indexRecord.field_5_dbcells = intList;
        intList.addAll(this.field_5_dbcells);
        return indexRecord;
    }
}
