package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultDataLabelTextPropertiesRecord extends Record {
    public static final short CATEGORY_DATA_TYPE_ALL_TEXT_CHARACTERISTIC = 2;
    public static final short CATEGORY_DATA_TYPE_SHOW_LABELS_CHARACTERISTIC = 0;
    public static final short CATEGORY_DATA_TYPE_VALUE_AND_PERCENTAGE_CHARACTERISTIC = 1;
    public static final short sid = 4132;
    private short field_1_categoryDataType;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public DefaultDataLabelTextPropertiesRecord() {
    }

    public DefaultDataLabelTextPropertiesRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DefaultDataLabelTextPropertiesRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4132) {
            throw new RecordFormatException("Not a DefaultDataLabelTextProperties record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_categoryDataType = LittleEndian.getShort(bArr, 0 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DEFAULTTEXT]\n");
        stringBuffer.append("    .categoryDataType     = ").append("0x").append(HexDump.toHex(getCategoryDataType())).append(" (").append((int) getCategoryDataType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/DEFAULTTEXT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_categoryDataType);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        DefaultDataLabelTextPropertiesRecord defaultDataLabelTextPropertiesRecord = new DefaultDataLabelTextPropertiesRecord();
        defaultDataLabelTextPropertiesRecord.field_1_categoryDataType = this.field_1_categoryDataType;
        return defaultDataLabelTextPropertiesRecord;
    }

    public short getCategoryDataType() {
        return this.field_1_categoryDataType;
    }

    public void setCategoryDataType(short s) {
        this.field_1_categoryDataType = s;
    }
}
