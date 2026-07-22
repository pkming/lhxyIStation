package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SeriesChartGroupIndexRecord extends Record {
    public static final short sid = 4165;
    private short field_1_chartGroupIndex;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 4165;
    }

    public SeriesChartGroupIndexRecord() {
    }

    public SeriesChartGroupIndexRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SeriesChartGroupIndexRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4165) {
            throw new RecordFormatException("Not a SeriesChartGroupIndex record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_chartGroupIndex = LittleEndian.getShort(bArr, 0 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SERTOCRT]\n");
        stringBuffer.append("    .chartGroupIndex      = ").append("0x").append(HexDump.toHex(getChartGroupIndex())).append(" (").append((int) getChartGroupIndex()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/SERTOCRT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 4165);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_chartGroupIndex);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SeriesChartGroupIndexRecord seriesChartGroupIndexRecord = new SeriesChartGroupIndexRecord();
        seriesChartGroupIndexRecord.field_1_chartGroupIndex = this.field_1_chartGroupIndex;
        return seriesChartGroupIndexRecord;
    }

    public short getChartGroupIndex() {
        return this.field_1_chartGroupIndex;
    }

    public void setChartGroupIndex(short s) {
        this.field_1_chartGroupIndex = s;
    }
}
