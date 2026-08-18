package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ColumnInfoRecord extends Record {
    public static final short sid = 125;
    private short field_1_first_col;
    private short field_2_last_col;
    private short field_3_col_width;
    private short field_4_xf_index;
    private short field_5_options;
    private short field_6_reserved;
    private static final BitField hidden = new BitField(1);
    private static final BitField outlevel = new BitField(1792);
    private static final BitField collapsed = new BitField(4096);

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 16;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 125;
    }

    public ColumnInfoRecord() {
    }

    public ColumnInfoRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public ColumnInfoRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_first_col = LittleEndian.getShort(bArr, i + 0);
        this.field_2_last_col = LittleEndian.getShort(bArr, i + 2);
        this.field_3_col_width = LittleEndian.getShort(bArr, i + 4);
        this.field_4_xf_index = LittleEndian.getShort(bArr, i + 6);
        this.field_5_options = LittleEndian.getShort(bArr, i + 8);
        this.field_6_reserved = bArr[i + 10];
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 125) {
            throw new RecordFormatException("NOT A COLINFO RECORD!!");
        }
    }

    public void setFirstColumn(short s) {
        this.field_1_first_col = s;
    }

    public void setLastColumn(short s) {
        this.field_2_last_col = s;
    }

    public void setColumnWidth(short s) {
        this.field_3_col_width = s;
    }

    public void setXFIndex(short s) {
        this.field_4_xf_index = s;
    }

    public void setOptions(short s) {
        this.field_5_options = s;
    }

    public void setHidden(boolean z) {
        this.field_5_options = hidden.setShortBoolean(this.field_5_options, z);
    }

    public void setOutlineLevel(short s) {
        this.field_5_options = outlevel.setShortValue(this.field_5_options, s);
    }

    public void setCollapsed(boolean z) {
        this.field_5_options = collapsed.setShortBoolean(this.field_5_options, z);
    }

    public short getFirstColumn() {
        return this.field_1_first_col;
    }

    public short getLastColumn() {
        return this.field_2_last_col;
    }

    public short getColumnWidth() {
        return this.field_3_col_width;
    }

    public short getXFIndex() {
        return this.field_4_xf_index;
    }

    public short getOptions() {
        return this.field_5_options;
    }

    public boolean getHidden() {
        return hidden.isSet(this.field_5_options);
    }

    public short getOutlineLevel() {
        return outlevel.getShortValue(this.field_5_options);
    }

    public boolean getCollapsed() {
        return collapsed.isSet(this.field_5_options);
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 125);
        LittleEndian.putShort(bArr, i + 2, (short) 12);
        LittleEndian.putShort(bArr, i + 4, getFirstColumn());
        LittleEndian.putShort(bArr, i + 6, getLastColumn());
        LittleEndian.putShort(bArr, i + 8, getColumnWidth());
        LittleEndian.putShort(bArr, i + 10, getXFIndex());
        LittleEndian.putShort(bArr, i + 12, getOptions());
        LittleEndian.putShort(bArr, i + 14, (short) 0);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[COLINFO]\n");
        stringBuffer.append("colfirst       = ").append((int) getFirstColumn()).append("\n");
        stringBuffer.append("collast        = ").append((int) getLastColumn()).append("\n");
        stringBuffer.append("colwidth       = ").append((int) getColumnWidth()).append("\n");
        stringBuffer.append("xfindex        = ").append((int) getXFIndex()).append("\n");
        stringBuffer.append("options        = ").append((int) getOptions()).append("\n");
        stringBuffer.append("  hidden       = ").append(getHidden()).append("\n");
        stringBuffer.append("  olevel       = ").append((int) getOutlineLevel()).append("\n");
        stringBuffer.append("  collapsed    = ").append(getCollapsed()).append("\n");
        stringBuffer.append("[/COLINFO]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        ColumnInfoRecord columnInfoRecord = new ColumnInfoRecord();
        columnInfoRecord.field_1_first_col = this.field_1_first_col;
        columnInfoRecord.field_2_last_col = this.field_2_last_col;
        columnInfoRecord.field_3_col_width = this.field_3_col_width;
        columnInfoRecord.field_4_xf_index = this.field_4_xf_index;
        columnInfoRecord.field_5_options = this.field_5_options;
        columnInfoRecord.field_6_reserved = this.field_6_reserved;
        return columnInfoRecord;
    }
}
