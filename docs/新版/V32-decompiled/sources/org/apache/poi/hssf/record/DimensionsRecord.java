package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DimensionsRecord extends Record {
    public static final short sid = 512;
    private int field_1_first_row;
    private int field_2_last_row;
    private short field_3_first_col;
    private short field_4_last_col;
    private short field_5_zero;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 18;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 512;
    }

    public DimensionsRecord() {
    }

    public DimensionsRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DimensionsRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 512) {
            throw new RecordFormatException("NOT A valid DIMENSIONS RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_first_row = LittleEndian.getInt(bArr, i + 0);
        this.field_2_last_row = LittleEndian.getInt(bArr, i + 4);
        this.field_3_first_col = LittleEndian.getShort(bArr, i + 8);
        this.field_4_last_col = LittleEndian.getShort(bArr, i + 10);
        this.field_5_zero = LittleEndian.getShort(bArr, i + 12);
    }

    public void setFirstRow(int i) {
        this.field_1_first_row = i;
    }

    public void setLastRow(int i) {
        this.field_2_last_row = i;
    }

    public void setFirstCol(short s) {
        this.field_3_first_col = s;
    }

    public void setLastCol(short s) {
        this.field_4_last_col = s;
    }

    public int getFirstRow() {
        return this.field_1_first_row;
    }

    public int getLastRow() {
        return this.field_2_last_row;
    }

    public short getFirstCol() {
        return this.field_3_first_col;
    }

    public short getLastCol() {
        return this.field_4_last_col;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DIMENSIONS]\n");
        stringBuffer.append("    .firstrow       = ").append(Integer.toHexString(getFirstRow())).append("\n");
        stringBuffer.append("    .lastrow        = ").append(Integer.toHexString(getLastRow())).append("\n");
        stringBuffer.append("    .firstcol       = ").append(Integer.toHexString(getFirstCol())).append("\n");
        stringBuffer.append("    .lastcol        = ").append(Integer.toHexString(getLastCol())).append("\n");
        stringBuffer.append("    .zero           = ").append(Integer.toHexString(this.field_5_zero)).append("\n");
        stringBuffer.append("[/DIMENSIONS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 512);
        LittleEndian.putShort(bArr, i + 2, (short) 14);
        LittleEndian.putInt(bArr, i + 4, getFirstRow());
        LittleEndian.putInt(bArr, i + 8, getLastRow());
        LittleEndian.putShort(bArr, i + 12, getFirstCol());
        LittleEndian.putShort(bArr, i + 14, getLastCol());
        LittleEndian.putShort(bArr, i + 16, (short) 0);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        DimensionsRecord dimensionsRecord = new DimensionsRecord();
        dimensionsRecord.field_1_first_row = this.field_1_first_row;
        dimensionsRecord.field_2_last_row = this.field_2_last_row;
        dimensionsRecord.field_3_first_col = this.field_3_first_col;
        dimensionsRecord.field_4_last_col = this.field_4_last_col;
        dimensionsRecord.field_5_zero = this.field_5_zero;
        return dimensionsRecord;
    }
}
