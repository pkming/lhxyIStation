package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultRowHeightRecord extends Record {
    public static final short sid = 549;
    private short field_1_option_flags;
    private short field_2_row_height;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 8;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public DefaultRowHeightRecord() {
    }

    public DefaultRowHeightRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DefaultRowHeightRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 549) {
            throw new RecordFormatException("NOT A DefaultRowHeight RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_option_flags = LittleEndian.getShort(bArr, i + 0);
        this.field_2_row_height = LittleEndian.getShort(bArr, i + 2);
    }

    public void setOptionFlags(short s) {
        this.field_1_option_flags = s;
    }

    public void setRowHeight(short s) {
        this.field_2_row_height = s;
    }

    public short getOptionFlags() {
        return this.field_1_option_flags;
    }

    public short getRowHeight() {
        return this.field_2_row_height;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DEFAULTROWHEIGHT]\n");
        stringBuffer.append("    .optionflags    = ").append(Integer.toHexString(getOptionFlags())).append("\n");
        stringBuffer.append("    .rowheight      = ").append(Integer.toHexString(getRowHeight())).append("\n");
        stringBuffer.append("[/DEFAULTROWHEIGHT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) 4);
        LittleEndian.putShort(bArr, i + 4, getOptionFlags());
        LittleEndian.putShort(bArr, i + 6, getRowHeight());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        DefaultRowHeightRecord defaultRowHeightRecord = new DefaultRowHeightRecord();
        defaultRowHeightRecord.field_1_option_flags = this.field_1_option_flags;
        defaultRowHeightRecord.field_2_row_height = this.field_2_row_height;
        return defaultRowHeightRecord;
    }
}
