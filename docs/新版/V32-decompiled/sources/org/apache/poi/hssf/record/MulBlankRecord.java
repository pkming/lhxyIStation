package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class MulBlankRecord extends Record {
    public static final short sid = 190;
    private int field_1_row;
    private short field_2_first_col;
    private short[] field_3_xfs;
    private short field_4_last_col;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 190;
    }

    public MulBlankRecord() {
    }

    public MulBlankRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public MulBlankRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    public int getRow() {
        return this.field_1_row;
    }

    public short getFirstColumn() {
        return this.field_2_first_col;
    }

    public short getLastColumn() {
        return this.field_4_last_col;
    }

    public int getNumColumns() {
        return (this.field_4_last_col - this.field_2_first_col) + 1;
    }

    public short getXFAt(int i) {
        return this.field_3_xfs[i];
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_row = LittleEndian.getUShort(bArr, i + 0);
        this.field_2_first_col = LittleEndian.getShort(bArr, i + 2);
        short[] xFs = parseXFs(bArr, 4, i, s);
        this.field_3_xfs = xFs;
        this.field_4_last_col = LittleEndian.getShort(bArr, (xFs.length * 2) + 4 + i);
    }

    private short[] parseXFs(byte[] bArr, int i, int i2, short s) {
        short[] sArr = new short[((s - i) - 2) / 2];
        int i3 = 0;
        while (i < s - 2) {
            short s2 = LittleEndian.getShort(bArr, i + i2);
            i += 2;
            sArr[i3] = s2;
            i3++;
        }
        return sArr;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[MULBLANK]\n");
        stringBuffer.append("row  = ").append(Integer.toHexString(getRow())).append("\n");
        stringBuffer.append("firstcol  = ").append(Integer.toHexString(getFirstColumn())).append("\n");
        stringBuffer.append(" lastcol  = ").append(Integer.toHexString(getLastColumn())).append("\n");
        for (int i = 0; i < getNumColumns(); i++) {
            stringBuffer.append("xf").append(i).append("        = ").append(Integer.toHexString(getXFAt(i))).append("\n");
        }
        stringBuffer.append("[/MULBLANK]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 190) {
            throw new RecordFormatException("Not a MulBlankRecord!");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        throw new RecordFormatException("Sorry, you can't serialize a MulBlank in this release");
    }
}
