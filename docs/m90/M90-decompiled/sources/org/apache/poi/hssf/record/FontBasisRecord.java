package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class FontBasisRecord extends Record {
    public static final short sid = 4192;
    private short field_1_xBasis;
    private short field_2_yBasis;
    private short field_3_heightBasis;
    private short field_4_scale;
    private short field_5_indexToFontTable;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 14;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public FontBasisRecord() {
    }

    public FontBasisRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public FontBasisRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4192) {
            throw new RecordFormatException("Not a FontBasis record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_xBasis = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_yBasis = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_heightBasis = LittleEndian.getShort(bArr, 4 + i);
        this.field_4_scale = LittleEndian.getShort(bArr, 6 + i);
        this.field_5_indexToFontTable = LittleEndian.getShort(bArr, 8 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[FBI]\n");
        stringBuffer.append("    .xBasis               = ").append("0x").append(HexDump.toHex(getXBasis())).append(" (").append((int) getXBasis()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .yBasis               = ").append("0x").append(HexDump.toHex(getYBasis())).append(" (").append((int) getYBasis()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .heightBasis          = ").append("0x").append(HexDump.toHex(getHeightBasis())).append(" (").append((int) getHeightBasis()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .scale                = ").append("0x").append(HexDump.toHex(getScale())).append(" (").append((int) getScale()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .indexToFontTable     = ").append("0x").append(HexDump.toHex(getIndexToFontTable())).append(" (").append((int) getIndexToFontTable()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/FBI]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_xBasis);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_yBasis);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_heightBasis);
        LittleEndian.putShort(bArr, i + 10 + 0, this.field_4_scale);
        LittleEndian.putShort(bArr, i + 12 + 0, this.field_5_indexToFontTable);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        FontBasisRecord fontBasisRecord = new FontBasisRecord();
        fontBasisRecord.field_1_xBasis = this.field_1_xBasis;
        fontBasisRecord.field_2_yBasis = this.field_2_yBasis;
        fontBasisRecord.field_3_heightBasis = this.field_3_heightBasis;
        fontBasisRecord.field_4_scale = this.field_4_scale;
        fontBasisRecord.field_5_indexToFontTable = this.field_5_indexToFontTable;
        return fontBasisRecord;
    }

    public short getXBasis() {
        return this.field_1_xBasis;
    }

    public void setXBasis(short s) {
        this.field_1_xBasis = s;
    }

    public short getYBasis() {
        return this.field_2_yBasis;
    }

    public void setYBasis(short s) {
        this.field_2_yBasis = s;
    }

    public short getHeightBasis() {
        return this.field_3_heightBasis;
    }

    public void setHeightBasis(short s) {
        this.field_3_heightBasis = s;
    }

    public short getScale() {
        return this.field_4_scale;
    }

    public void setScale(short s) {
        this.field_4_scale = s;
    }

    public short getIndexToFontTable() {
        return this.field_5_indexToFontTable;
    }

    public void setIndexToFontTable(short s) {
        this.field_5_indexToFontTable = s;
    }
}
