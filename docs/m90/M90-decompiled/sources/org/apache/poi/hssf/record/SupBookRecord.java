package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SupBookRecord extends Record {
    public static final short sid = 430;
    private short field_1_number_of_sheets;
    private short field_2_flag;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 8;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public SupBookRecord() {
        setFlag((short) 1025);
    }

    public SupBookRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SupBookRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 430) {
            throw new RecordFormatException("NOT An Supbook RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_number_of_sheets = LittleEndian.getShort(bArr, i + 0);
        this.field_2_flag = LittleEndian.getShort(bArr, i + 2);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SUPBOOK]\n");
        stringBuffer.append("numberosheets = ").append((int) getNumberOfSheets()).append('\n');
        stringBuffer.append("flag          = ").append((int) getFlag()).append('\n');
        stringBuffer.append("[/SUPBOOK]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) 4);
        LittleEndian.putShort(bArr, i + 4, this.field_1_number_of_sheets);
        LittleEndian.putShort(bArr, i + 6, this.field_2_flag);
        return getRecordSize();
    }

    public void setNumberOfSheets(short s) {
        this.field_1_number_of_sheets = s;
    }

    public short getNumberOfSheets() {
        return this.field_1_number_of_sheets;
    }

    public void setFlag(short s) {
        this.field_2_flag = s;
    }

    public short getFlag() {
        return this.field_2_flag;
    }
}
