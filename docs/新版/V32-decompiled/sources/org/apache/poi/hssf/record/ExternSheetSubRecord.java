package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ExternSheetSubRecord extends Record {
    public static final short sid = 4095;
    private short field_1_index_to_supbook;
    private short field_2_index_to_first_supbook_sheet;
    private short field_3_index_to_last_supbook_sheet;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 4095;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
    }

    public ExternSheetSubRecord() {
    }

    public ExternSheetSubRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public ExternSheetSubRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    public void setIndexToSupBook(short s) {
        this.field_1_index_to_supbook = s;
    }

    public short getIndexToSupBook() {
        return this.field_1_index_to_supbook;
    }

    public void setIndexToFirstSupBook(short s) {
        this.field_2_index_to_first_supbook_sheet = s;
    }

    public short getIndexToFirstSupBook() {
        return this.field_2_index_to_first_supbook_sheet;
    }

    public void setIndexToLastSupBook(short s) {
        this.field_3_index_to_last_supbook_sheet = s;
    }

    public short getIndexToLastSupBook() {
        return this.field_3_index_to_last_supbook_sheet;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_index_to_supbook = LittleEndian.getShort(bArr, i + 0);
        this.field_2_index_to_first_supbook_sheet = LittleEndian.getShort(bArr, i + 2);
        this.field_3_index_to_last_supbook_sheet = LittleEndian.getShort(bArr, i + 4);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("   supbookindex =").append((int) getIndexToSupBook()).append('\n');
        stringBuffer.append("   1stsbindex   =").append((int) getIndexToFirstSupBook()).append('\n');
        stringBuffer.append("   lastsbindex  =").append((int) getIndexToLastSupBook()).append('\n');
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, getIndexToSupBook());
        LittleEndian.putShort(bArr, i + 2, getIndexToFirstSupBook());
        LittleEndian.putShort(bArr, i + 4, getIndexToLastSupBook());
        return getRecordSize();
    }
}
