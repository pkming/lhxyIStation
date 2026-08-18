package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class TopMarginRecord extends Record implements Margin {
    public static final short sid = 40;
    private double field_1_margin;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 12;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 40;
    }

    public TopMarginRecord() {
    }

    public TopMarginRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public TopMarginRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 40) {
            throw new RecordFormatException("Not a TopMargin record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_margin = LittleEndian.getDouble(bArr, i + 0);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[TopMargin]\n");
        stringBuffer.append("    .margin               = ").append(" (").append(getMargin()).append(" )\n");
        stringBuffer.append("[/TopMargin]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 40);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putDouble(bArr, i + 4, this.field_1_margin);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Margin
    public double getMargin() {
        return this.field_1_margin;
    }

    @Override // org.apache.poi.hssf.record.Margin
    public void setMargin(double d) {
        this.field_1_margin = d;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        TopMarginRecord topMarginRecord = new TopMarginRecord();
        topMarginRecord.field_1_margin = this.field_1_margin;
        return topMarginRecord;
    }
}
