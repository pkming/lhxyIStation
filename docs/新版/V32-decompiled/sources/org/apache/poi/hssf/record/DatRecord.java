package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DatRecord extends Record {
    public static final short sid = 4195;
    private BitField border;
    private short field_1_options;
    private BitField horizontalBorder;
    private BitField showSeriesKey;
    private BitField verticalBorder;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public DatRecord() {
        this.horizontalBorder = new BitField(1);
        this.verticalBorder = new BitField(2);
        this.border = new BitField(4);
        this.showSeriesKey = new BitField(8);
    }

    public DatRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.horizontalBorder = new BitField(1);
        this.verticalBorder = new BitField(2);
        this.border = new BitField(4);
        this.showSeriesKey = new BitField(8);
    }

    public DatRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.horizontalBorder = new BitField(1);
        this.verticalBorder = new BitField(2);
        this.border = new BitField(4);
        this.showSeriesKey = new BitField(8);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4195) {
            throw new RecordFormatException("Not a Dat record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_options = LittleEndian.getShort(bArr, 0 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DAT]\n");
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .horizontalBorder         = ").append(isHorizontalBorder()).append('\n');
        stringBuffer.append("         .verticalBorder           = ").append(isVerticalBorder()).append('\n');
        stringBuffer.append("         .border                   = ").append(isBorder()).append('\n');
        stringBuffer.append("         .showSeriesKey            = ").append(isShowSeriesKey()).append('\n');
        stringBuffer.append("[/DAT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_options);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        DatRecord datRecord = new DatRecord();
        datRecord.field_1_options = this.field_1_options;
        return datRecord;
    }

    public short getOptions() {
        return this.field_1_options;
    }

    public void setOptions(short s) {
        this.field_1_options = s;
    }

    public void setHorizontalBorder(boolean z) {
        this.field_1_options = this.horizontalBorder.setShortBoolean(this.field_1_options, z);
    }

    public boolean isHorizontalBorder() {
        return this.horizontalBorder.isSet(this.field_1_options);
    }

    public void setVerticalBorder(boolean z) {
        this.field_1_options = this.verticalBorder.setShortBoolean(this.field_1_options, z);
    }

    public boolean isVerticalBorder() {
        return this.verticalBorder.isSet(this.field_1_options);
    }

    public void setBorder(boolean z) {
        this.field_1_options = this.border.setShortBoolean(this.field_1_options, z);
    }

    public boolean isBorder() {
        return this.border.isSet(this.field_1_options);
    }

    public void setShowSeriesKey(boolean z) {
        this.field_1_options = this.showSeriesKey.setShortBoolean(this.field_1_options, z);
    }

    public boolean isShowSeriesKey() {
        return this.showSeriesKey.isSet(this.field_1_options);
    }
}
