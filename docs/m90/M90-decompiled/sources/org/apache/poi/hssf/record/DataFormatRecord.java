package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DataFormatRecord extends Record {
    public static final short sid = 4102;
    private short field_1_pointNumber;
    private short field_2_seriesIndex;
    private short field_3_seriesNumber;
    private short field_4_formatFlags;
    private BitField useExcel4Colors;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 12;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public DataFormatRecord() {
        this.useExcel4Colors = new BitField(1);
    }

    public DataFormatRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.useExcel4Colors = new BitField(1);
    }

    public DataFormatRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.useExcel4Colors = new BitField(1);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4102) {
            throw new RecordFormatException("Not a DataFormat record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_pointNumber = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_seriesIndex = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_seriesNumber = LittleEndian.getShort(bArr, 4 + i);
        this.field_4_formatFlags = LittleEndian.getShort(bArr, 6 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DATAFORMAT]\n");
        stringBuffer.append("    .pointNumber          = ").append("0x").append(HexDump.toHex(getPointNumber())).append(" (").append((int) getPointNumber()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .seriesIndex          = ").append("0x").append(HexDump.toHex(getSeriesIndex())).append(" (").append((int) getSeriesIndex()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .seriesNumber         = ").append("0x").append(HexDump.toHex(getSeriesNumber())).append(" (").append((int) getSeriesNumber()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .formatFlags          = ").append("0x").append(HexDump.toHex(getFormatFlags())).append(" (").append((int) getFormatFlags()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .useExcel4Colors          = ").append(isUseExcel4Colors()).append('\n');
        stringBuffer.append("[/DATAFORMAT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_pointNumber);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_seriesIndex);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_seriesNumber);
        LittleEndian.putShort(bArr, i + 10 + 0, this.field_4_formatFlags);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        DataFormatRecord dataFormatRecord = new DataFormatRecord();
        dataFormatRecord.field_1_pointNumber = this.field_1_pointNumber;
        dataFormatRecord.field_2_seriesIndex = this.field_2_seriesIndex;
        dataFormatRecord.field_3_seriesNumber = this.field_3_seriesNumber;
        dataFormatRecord.field_4_formatFlags = this.field_4_formatFlags;
        return dataFormatRecord;
    }

    public short getPointNumber() {
        return this.field_1_pointNumber;
    }

    public void setPointNumber(short s) {
        this.field_1_pointNumber = s;
    }

    public short getSeriesIndex() {
        return this.field_2_seriesIndex;
    }

    public void setSeriesIndex(short s) {
        this.field_2_seriesIndex = s;
    }

    public short getSeriesNumber() {
        return this.field_3_seriesNumber;
    }

    public void setSeriesNumber(short s) {
        this.field_3_seriesNumber = s;
    }

    public short getFormatFlags() {
        return this.field_4_formatFlags;
    }

    public void setFormatFlags(short s) {
        this.field_4_formatFlags = s;
    }

    public void setUseExcel4Colors(boolean z) {
        this.field_4_formatFlags = this.useExcel4Colors.setShortBoolean(this.field_4_formatFlags, z);
    }

    public boolean isUseExcel4Colors() {
        return this.useExcel4Colors.isSet(this.field_4_formatFlags);
    }
}
