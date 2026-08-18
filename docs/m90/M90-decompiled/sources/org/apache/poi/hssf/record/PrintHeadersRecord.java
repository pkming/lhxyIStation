package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class PrintHeadersRecord extends Record {
    public static final short sid = 42;
    private short field_1_print_headers;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 42;
    }

    public PrintHeadersRecord() {
    }

    public PrintHeadersRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public PrintHeadersRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 42) {
            throw new RecordFormatException("NOT A PrintHeaders RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_print_headers = LittleEndian.getShort(bArr, i + 0);
    }

    public void setPrintHeaders(boolean z) {
        if (z) {
            this.field_1_print_headers = (short) 1;
        } else {
            this.field_1_print_headers = (short) 0;
        }
    }

    public boolean getPrintHeaders() {
        return this.field_1_print_headers == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[PRINTHEADERS]\n");
        stringBuffer.append("    .printheaders   = ").append(getPrintHeaders()).append("\n");
        stringBuffer.append("[/PRINTHEADERS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 42);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_print_headers);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        PrintHeadersRecord printHeadersRecord = new PrintHeadersRecord();
        printHeadersRecord.field_1_print_headers = this.field_1_print_headers;
        return printHeadersRecord;
    }
}
