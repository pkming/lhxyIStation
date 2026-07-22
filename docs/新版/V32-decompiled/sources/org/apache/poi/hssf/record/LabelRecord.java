package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class LabelRecord extends Record implements CellValueRecordInterface {
    public static final short sid = 516;
    private int field_1_row;
    private short field_2_column;
    private short field_3_xf_index;
    private short field_4_string_len;
    private byte field_5_unicode_flag;
    private String field_6_value;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 516;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isValue() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setColumn(short s) {
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setRow(int i) {
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setXFIndex(short s) {
    }

    public LabelRecord() {
    }

    public LabelRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public LabelRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 516) {
            throw new RecordFormatException("Not a valid LabelRecord");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_row = LittleEndian.getUShort(bArr, i + 0);
        this.field_2_column = LittleEndian.getShort(bArr, i + 2);
        this.field_3_xf_index = LittleEndian.getShort(bArr, i + 4);
        short s2 = LittleEndian.getShort(bArr, i + 6);
        this.field_4_string_len = s2;
        this.field_5_unicode_flag = bArr[i + 8];
        if (s2 > 0) {
            if (isUnCompressedUnicode()) {
                this.field_6_value = StringUtil.getFromUnicode(bArr, i + 9, this.field_4_string_len);
                return;
            } else {
                this.field_6_value = StringUtil.getFromCompressedUnicode(bArr, i + 9, getStringLength());
                return;
            }
        }
        this.field_6_value = null;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public int getRow() {
        return this.field_1_row;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getColumn() {
        return this.field_2_column;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getXFIndex() {
        return this.field_3_xf_index;
    }

    public short getStringLength() {
        return this.field_4_string_len;
    }

    public boolean isUnCompressedUnicode() {
        return this.field_5_unicode_flag == 1;
    }

    public String getValue() {
        return this.field_6_value;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[LABEL]\n");
        stringBuffer.append("    .row            = ").append(Integer.toHexString(getRow())).append("\n");
        stringBuffer.append("    .column         = ").append(Integer.toHexString(getColumn())).append("\n");
        stringBuffer.append("    .xfindex        = ").append(Integer.toHexString(getXFIndex())).append("\n");
        stringBuffer.append("    .string_len       = ").append(Integer.toHexString(this.field_4_string_len)).append("\n");
        stringBuffer.append("    .unicode_flag       = ").append(Integer.toHexString(this.field_5_unicode_flag)).append("\n");
        stringBuffer.append("    .value       = ").append(getValue()).append("\n");
        stringBuffer.append("[/LABEL]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isBefore(CellValueRecordInterface cellValueRecordInterface) {
        if (getRow() > cellValueRecordInterface.getRow()) {
            return false;
        }
        if (getRow() != cellValueRecordInterface.getRow() || getColumn() <= cellValueRecordInterface.getColumn()) {
            return (getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn()) ? false : true;
        }
        return false;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isAfter(CellValueRecordInterface cellValueRecordInterface) {
        if (getRow() < cellValueRecordInterface.getRow()) {
            return false;
        }
        if (getRow() != cellValueRecordInterface.getRow() || getColumn() >= cellValueRecordInterface.getColumn()) {
            return (getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn()) ? false : true;
        }
        return false;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isEqual(CellValueRecordInterface cellValueRecordInterface) {
        return getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        LabelRecord labelRecord = new LabelRecord();
        labelRecord.field_1_row = this.field_1_row;
        labelRecord.field_2_column = this.field_2_column;
        labelRecord.field_3_xf_index = this.field_3_xf_index;
        labelRecord.field_4_string_len = this.field_4_string_len;
        labelRecord.field_5_unicode_flag = this.field_5_unicode_flag;
        labelRecord.field_6_value = this.field_6_value;
        return labelRecord;
    }
}
