package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class BarRecord extends Record {
    public static final short sid = 4119;
    private BitField displayAsPercentage;
    private short field_1_barSpace;
    private short field_2_categorySpace;
    private short field_3_formatFlags;
    private BitField horizontal;
    private BitField shadow;
    private BitField stacked;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 10;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public BarRecord() {
        this.horizontal = new BitField(1);
        this.stacked = new BitField(2);
        this.displayAsPercentage = new BitField(4);
        this.shadow = new BitField(8);
    }

    public BarRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.horizontal = new BitField(1);
        this.stacked = new BitField(2);
        this.displayAsPercentage = new BitField(4);
        this.shadow = new BitField(8);
    }

    public BarRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.horizontal = new BitField(1);
        this.stacked = new BitField(2);
        this.displayAsPercentage = new BitField(4);
        this.shadow = new BitField(8);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4119) {
            throw new RecordFormatException("Not a Bar record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_barSpace = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_categorySpace = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_formatFlags = LittleEndian.getShort(bArr, 4 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[BAR]\n");
        stringBuffer.append("    .barSpace             = ").append("0x").append(HexDump.toHex(getBarSpace())).append(" (").append((int) getBarSpace()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .categorySpace        = ").append("0x").append(HexDump.toHex(getCategorySpace())).append(" (").append((int) getCategorySpace()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .formatFlags          = ").append("0x").append(HexDump.toHex(getFormatFlags())).append(" (").append((int) getFormatFlags()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .horizontal               = ").append(isHorizontal()).append('\n');
        stringBuffer.append("         .stacked                  = ").append(isStacked()).append('\n');
        stringBuffer.append("         .displayAsPercentage      = ").append(isDisplayAsPercentage()).append('\n');
        stringBuffer.append("         .shadow                   = ").append(isShadow()).append('\n');
        stringBuffer.append("[/BAR]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_barSpace);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_categorySpace);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_formatFlags);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        BarRecord barRecord = new BarRecord();
        barRecord.field_1_barSpace = this.field_1_barSpace;
        barRecord.field_2_categorySpace = this.field_2_categorySpace;
        barRecord.field_3_formatFlags = this.field_3_formatFlags;
        return barRecord;
    }

    public short getBarSpace() {
        return this.field_1_barSpace;
    }

    public void setBarSpace(short s) {
        this.field_1_barSpace = s;
    }

    public short getCategorySpace() {
        return this.field_2_categorySpace;
    }

    public void setCategorySpace(short s) {
        this.field_2_categorySpace = s;
    }

    public short getFormatFlags() {
        return this.field_3_formatFlags;
    }

    public void setFormatFlags(short s) {
        this.field_3_formatFlags = s;
    }

    public void setHorizontal(boolean z) {
        this.field_3_formatFlags = this.horizontal.setShortBoolean(this.field_3_formatFlags, z);
    }

    public boolean isHorizontal() {
        return this.horizontal.isSet(this.field_3_formatFlags);
    }

    public void setStacked(boolean z) {
        this.field_3_formatFlags = this.stacked.setShortBoolean(this.field_3_formatFlags, z);
    }

    public boolean isStacked() {
        return this.stacked.isSet(this.field_3_formatFlags);
    }

    public void setDisplayAsPercentage(boolean z) {
        this.field_3_formatFlags = this.displayAsPercentage.setShortBoolean(this.field_3_formatFlags, z);
    }

    public boolean isDisplayAsPercentage() {
        return this.displayAsPercentage.isSet(this.field_3_formatFlags);
    }

    public void setShadow(boolean z) {
        this.field_3_formatFlags = this.shadow.setShortBoolean(this.field_3_formatFlags, z);
    }

    public boolean isShadow() {
        return this.shadow.isSet(this.field_3_formatFlags);
    }
}
