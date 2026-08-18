package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DBCellRecord extends Record {
    public static final short sid = 215;
    private int field_1_row_offset;
    private short[] field_2_cell_offsets;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    public DBCellRecord() {
    }

    public DBCellRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DBCellRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 215) {
            throw new RecordFormatException("NOT A valid DBCell RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_row_offset = LittleEndian.getUShort(bArr, i + 0);
        int i2 = 4;
        this.field_2_cell_offsets = new short[(s - 4) / 2];
        int i3 = 0;
        while (i2 < bArr.length) {
            this.field_2_cell_offsets[i3] = LittleEndian.getShort(bArr, i2 + i);
            i2 += 2;
            i3++;
        }
    }

    public void setRowOffset(int i) {
        this.field_1_row_offset = i;
    }

    public void addCellOffset(short s) {
        short[] sArr = this.field_2_cell_offsets;
        if (sArr == null) {
            this.field_2_cell_offsets = new short[1];
        } else {
            short[] sArr2 = new short[sArr.length + 1];
            System.arraycopy(sArr, 0, sArr2, 0, sArr.length);
            this.field_2_cell_offsets = sArr2;
        }
        short[] sArr3 = this.field_2_cell_offsets;
        sArr3[sArr3.length - 1] = s;
    }

    public int getRowOffset() {
        return this.field_1_row_offset;
    }

    public short getCellOffsetAt(int i) {
        return this.field_2_cell_offsets[i];
    }

    public int getNumCellOffsets() {
        return this.field_2_cell_offsets.length;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DBCELL]\n");
        stringBuffer.append("    .rowoffset       = ").append(Integer.toHexString(getRowOffset())).append("\n");
        for (int i = 0; i < getNumCellOffsets(); i++) {
            stringBuffer.append(new StringBuffer().append("    .cell_").append(i).append("          = ").toString()).append(Integer.toHexString(getCellOffsetAt(i))).append("\n");
        }
        stringBuffer.append("[/DBCELL]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        if (this.field_2_cell_offsets == null) {
            this.field_2_cell_offsets = new short[0];
        }
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) ((getNumCellOffsets() * 2) + 4));
        LittleEndian.putInt(bArr, i + 4, getRowOffset());
        for (int i2 = 0; i2 < getNumCellOffsets(); i2++) {
            LittleEndian.putShort(bArr, i2 + 8 + i, getCellOffsetAt(i2));
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getNumCellOffsets() * 2) + 8;
    }
}
