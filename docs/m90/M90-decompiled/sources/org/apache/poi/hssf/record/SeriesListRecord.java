package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SeriesListRecord extends Record {
    public static final short sid = 4118;
    private short[] field_1_seriesNumbers;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public SeriesListRecord() {
    }

    public SeriesListRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SeriesListRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4118) {
            throw new RecordFormatException("Not a SeriesList record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_seriesNumbers = LittleEndian.getShortArray(bArr, 0 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SERIESLIST]\n");
        stringBuffer.append("    .seriesNumbers        = ").append(" (").append(getSeriesNumbers()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/SERIESLIST]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShortArray(bArr, i + 4 + 0, this.field_1_seriesNumbers);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (this.field_1_seriesNumbers.length * 2) + 4 + 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SeriesListRecord seriesListRecord = new SeriesListRecord();
        seriesListRecord.field_1_seriesNumbers = this.field_1_seriesNumbers;
        return seriesListRecord;
    }

    public short[] getSeriesNumbers() {
        return this.field_1_seriesNumbers;
    }

    public void setSeriesNumbers(short[] sArr) {
        this.field_1_seriesNumbers = sArr;
    }
}
