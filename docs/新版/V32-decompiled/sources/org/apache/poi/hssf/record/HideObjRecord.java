package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class HideObjRecord extends Record {
    public static final short HIDE_ALL = 2;
    public static final short SHOW_ALL = 0;
    public static final short SHOW_PLACEHOLDERS = 1;
    public static final short sid = 141;
    private short field_1_hide_obj;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 141;
    }

    public HideObjRecord() {
    }

    public HideObjRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public HideObjRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 141) {
            throw new RecordFormatException("NOT A HIDEOBJ RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_hide_obj = LittleEndian.getShort(bArr, i + 0);
    }

    public void setHideObj(short s) {
        this.field_1_hide_obj = s;
    }

    public short getHideObj() {
        return this.field_1_hide_obj;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[HIDEOBJ]\n");
        stringBuffer.append("    .hideobj         = ").append(Integer.toHexString(getHideObj())).append("\n");
        stringBuffer.append("[/HIDEOBJ]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 141);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, getHideObj());
        return getRecordSize();
    }
}
