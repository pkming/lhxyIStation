package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class RowRecord extends Record implements Comparable {
    public static final short sid = 520;
    private BitField badFontHeight;
    private BitField colapsed;
    private int field_1_row_number;
    private short field_2_first_col;
    private short field_3_last_col;
    private short field_4_height;
    private short field_5_optimize;
    private short field_6_reserved;
    private short field_7_option_flags;
    private short field_8_xf_index;
    private BitField formatted;
    private BitField outlineLevel;
    private BitField zeroHeight;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 20;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 520;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    public RowRecord() {
        this.outlineLevel = new BitField(7);
        this.colapsed = new BitField(16);
        this.zeroHeight = new BitField(32);
        this.badFontHeight = new BitField(64);
        this.formatted = new BitField(128);
    }

    public RowRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.outlineLevel = new BitField(7);
        this.colapsed = new BitField(16);
        this.zeroHeight = new BitField(32);
        this.badFontHeight = new BitField(64);
        this.formatted = new BitField(128);
    }

    public RowRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.outlineLevel = new BitField(7);
        this.colapsed = new BitField(16);
        this.zeroHeight = new BitField(32);
        this.badFontHeight = new BitField(64);
        this.formatted = new BitField(128);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 520) {
            throw new RecordFormatException("NOT A valid ROW RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_row_number = LittleEndian.getUShort(bArr, i + 0);
        this.field_2_first_col = LittleEndian.getShort(bArr, i + 2);
        this.field_3_last_col = LittleEndian.getShort(bArr, i + 4);
        this.field_4_height = LittleEndian.getShort(bArr, i + 6);
        this.field_5_optimize = LittleEndian.getShort(bArr, i + 8);
        this.field_6_reserved = LittleEndian.getShort(bArr, i + 10);
        this.field_7_option_flags = LittleEndian.getShort(bArr, i + 12);
        this.field_8_xf_index = LittleEndian.getShort(bArr, i + 14);
    }

    public void setRowNumber(int i) {
        this.field_1_row_number = i;
    }

    public void setFirstCol(short s) {
        this.field_2_first_col = s;
    }

    public void setLastCol(short s) {
        this.field_3_last_col = s;
    }

    public void setHeight(short s) {
        this.field_4_height = s;
    }

    public void setOptimize(short s) {
        this.field_5_optimize = s;
    }

    public void setOptionFlags(short s) {
        this.field_7_option_flags = s;
    }

    public void setOutlineLevel(short s) {
        this.field_7_option_flags = this.outlineLevel.setShortValue(this.field_7_option_flags, s);
    }

    public void setColapsed(boolean z) {
        this.field_7_option_flags = this.colapsed.setShortBoolean(this.field_7_option_flags, z);
    }

    public void setZeroHeight(boolean z) {
        this.field_7_option_flags = this.zeroHeight.setShortBoolean(this.field_7_option_flags, z);
    }

    public void setBadFontHeight(boolean z) {
        this.field_7_option_flags = this.badFontHeight.setShortBoolean(this.field_7_option_flags, z);
    }

    public void setFormatted(boolean z) {
        this.field_7_option_flags = this.formatted.setShortBoolean(this.field_7_option_flags, z);
    }

    public void setXFIndex(short s) {
        this.field_8_xf_index = s;
    }

    public int getRowNumber() {
        return this.field_1_row_number;
    }

    public short getFirstCol() {
        return this.field_2_first_col;
    }

    public short getLastCol() {
        return this.field_3_last_col;
    }

    public short getHeight() {
        return this.field_4_height;
    }

    public short getOptimize() {
        return this.field_5_optimize;
    }

    public short getOptionFlags() {
        return this.field_7_option_flags;
    }

    public short getOutlineLevel() {
        return this.outlineLevel.getShortValue(this.field_7_option_flags);
    }

    public boolean getColapsed() {
        return this.colapsed.isSet(this.field_7_option_flags);
    }

    public boolean getZeroHeight() {
        return this.zeroHeight.isSet(this.field_7_option_flags);
    }

    public boolean getBadFontHeight() {
        return this.badFontHeight.isSet(this.field_7_option_flags);
    }

    public boolean getFormatted() {
        return this.formatted.isSet(this.field_7_option_flags);
    }

    public short getXFIndex() {
        return this.field_8_xf_index;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ROW]\n");
        stringBuffer.append("    .rownumber      = ").append(Integer.toHexString(getRowNumber())).append("\n");
        stringBuffer.append("    .firstcol       = ").append(Integer.toHexString(getFirstCol())).append("\n");
        stringBuffer.append("    .lastcol        = ").append(Integer.toHexString(getLastCol())).append("\n");
        stringBuffer.append("    .height         = ").append(Integer.toHexString(getHeight())).append("\n");
        stringBuffer.append("    .optimize       = ").append(Integer.toHexString(getOptimize())).append("\n");
        stringBuffer.append("    .reserved       = ").append(Integer.toHexString(this.field_6_reserved)).append("\n");
        stringBuffer.append("    .optionflags    = ").append(Integer.toHexString(getOptionFlags())).append("\n");
        stringBuffer.append("        .outlinelvl = ").append(Integer.toHexString(getOutlineLevel())).append("\n");
        stringBuffer.append("        .colapsed   = ").append(getColapsed()).append("\n");
        stringBuffer.append("        .zeroheight = ").append(getZeroHeight()).append("\n");
        stringBuffer.append("        .badfontheig= ").append(getBadFontHeight()).append("\n");
        stringBuffer.append("        .formatted  = ").append(getFormatted()).append("\n");
        stringBuffer.append("    .xfindex        = ").append(Integer.toHexString(getXFIndex())).append("\n");
        stringBuffer.append("[/ROW]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 520);
        LittleEndian.putShort(bArr, i + 2, (short) 16);
        LittleEndian.putShort(bArr, i + 4, (short) getRowNumber());
        LittleEndian.putShort(bArr, i + 6, getFirstCol() == -1 ? (short) 0 : getFirstCol());
        LittleEndian.putShort(bArr, i + 8, getLastCol() != -1 ? getLastCol() : (short) 0);
        LittleEndian.putShort(bArr, i + 10, getHeight());
        LittleEndian.putShort(bArr, i + 12, getOptimize());
        LittleEndian.putShort(bArr, i + 14, this.field_6_reserved);
        LittleEndian.putShort(bArr, i + 16, getOptionFlags());
        LittleEndian.putShort(bArr, i + 18, getXFIndex());
        return getRecordSize();
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        RowRecord rowRecord = (RowRecord) obj;
        if (getRowNumber() == rowRecord.getRowNumber()) {
            return 0;
        }
        return (getRowNumber() >= rowRecord.getRowNumber() && getRowNumber() > rowRecord.getRowNumber()) ? 1 : -1;
    }

    public boolean equals(Object obj) {
        return (obj instanceof RowRecord) && getRowNumber() == ((RowRecord) obj).getRowNumber();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        RowRecord rowRecord = new RowRecord();
        rowRecord.field_1_row_number = this.field_1_row_number;
        rowRecord.field_2_first_col = this.field_2_first_col;
        rowRecord.field_3_last_col = this.field_3_last_col;
        rowRecord.field_4_height = this.field_4_height;
        rowRecord.field_5_optimize = this.field_5_optimize;
        rowRecord.field_6_reserved = this.field_6_reserved;
        rowRecord.field_7_option_flags = this.field_7_option_flags;
        rowRecord.field_8_xf_index = this.field_8_xf_index;
        return rowRecord;
    }
}
