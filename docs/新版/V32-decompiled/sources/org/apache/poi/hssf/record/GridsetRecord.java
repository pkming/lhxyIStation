package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class GridsetRecord extends Record {
    public static final short sid = 130;
    public short field_1_gridset_flag;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 130;
    }

    public GridsetRecord() {
    }

    public GridsetRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public GridsetRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 130) {
            throw new RecordFormatException("NOT A Gridset RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_gridset_flag = LittleEndian.getShort(bArr, i + 0);
    }

    public void setGridset(boolean z) {
        if (z) {
            this.field_1_gridset_flag = (short) 1;
        } else {
            this.field_1_gridset_flag = (short) 0;
        }
    }

    public boolean getGridset() {
        return this.field_1_gridset_flag == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[GRIDSET]\n");
        stringBuffer.append("    .gridset        = ").append(getGridset()).append("\n");
        stringBuffer.append("[/GRIDSET]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 130);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_gridset_flag);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        GridsetRecord gridsetRecord = new GridsetRecord();
        gridsetRecord.field_1_gridset_flag = this.field_1_gridset_flag;
        return gridsetRecord;
    }
}
