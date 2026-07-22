package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class PrintGridlinesRecord extends Record {
    public static final short sid = 43;
    private short field_1_print_gridlines;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 43;
    }

    public PrintGridlinesRecord() {
    }

    public PrintGridlinesRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public PrintGridlinesRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 43) {
            throw new RecordFormatException("NOT A PrintGridlines RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_print_gridlines = LittleEndian.getShort(bArr, i + 0);
    }

    public void setPrintGridlines(boolean z) {
        if (z) {
            this.field_1_print_gridlines = (short) 1;
        } else {
            this.field_1_print_gridlines = (short) 0;
        }
    }

    public boolean getPrintGridlines() {
        return this.field_1_print_gridlines == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[PRINTGRIDLINES]\n");
        stringBuffer.append("    .printgridlines = ").append(getPrintGridlines()).append("\n");
        stringBuffer.append("[/PRINTGRIDLINES]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 43);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_print_gridlines);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        PrintGridlinesRecord printGridlinesRecord = new PrintGridlinesRecord();
        printGridlinesRecord.field_1_print_gridlines = this.field_1_print_gridlines;
        return printGridlinesRecord;
    }
}
