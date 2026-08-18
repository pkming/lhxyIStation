package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SeriesIndexRecord extends Record {
    public static final short sid = 4197;
    private short field_1_index;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public SeriesIndexRecord() {
    }

    public SeriesIndexRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SeriesIndexRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4197) {
            throw new RecordFormatException("Not a SeriesIndex record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_index = LittleEndian.getShort(bArr, 0 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SINDEX]\n");
        stringBuffer.append("    .index                = ").append("0x").append(HexDump.toHex(getIndex())).append(" (").append((int) getIndex()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/SINDEX]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_index);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SeriesIndexRecord seriesIndexRecord = new SeriesIndexRecord();
        seriesIndexRecord.field_1_index = this.field_1_index;
        return seriesIndexRecord;
    }

    public short getIndex() {
        return this.field_1_index;
    }

    public void setIndex(short s) {
        this.field_1_index = s;
    }
}
