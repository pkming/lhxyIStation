package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class AreaRecord extends Record {
    public static final short sid = 4122;
    private BitField displayAsPercentage;
    private short field_1_formatFlags;
    private BitField shadow;
    private BitField stacked;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public AreaRecord() {
        this.stacked = new BitField(1);
        this.displayAsPercentage = new BitField(2);
        this.shadow = new BitField(4);
    }

    public AreaRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.stacked = new BitField(1);
        this.displayAsPercentage = new BitField(2);
        this.shadow = new BitField(4);
    }

    public AreaRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.stacked = new BitField(1);
        this.displayAsPercentage = new BitField(2);
        this.shadow = new BitField(4);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4122) {
            throw new RecordFormatException("Not a Area record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_formatFlags = LittleEndian.getShort(bArr, 0 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[AREA]\n");
        stringBuffer.append("    .formatFlags          = ").append("0x").append(HexDump.toHex(getFormatFlags())).append(" (").append((int) getFormatFlags()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .stacked                  = ").append(isStacked()).append('\n');
        stringBuffer.append("         .displayAsPercentage      = ").append(isDisplayAsPercentage()).append('\n');
        stringBuffer.append("         .shadow                   = ").append(isShadow()).append('\n');
        stringBuffer.append("[/AREA]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_formatFlags);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        AreaRecord areaRecord = new AreaRecord();
        areaRecord.field_1_formatFlags = this.field_1_formatFlags;
        return areaRecord;
    }

    public short getFormatFlags() {
        return this.field_1_formatFlags;
    }

    public void setFormatFlags(short s) {
        this.field_1_formatFlags = s;
    }

    public void setStacked(boolean z) {
        this.field_1_formatFlags = this.stacked.setShortBoolean(this.field_1_formatFlags, z);
    }

    public boolean isStacked() {
        return this.stacked.isSet(this.field_1_formatFlags);
    }

    public void setDisplayAsPercentage(boolean z) {
        this.field_1_formatFlags = this.displayAsPercentage.setShortBoolean(this.field_1_formatFlags, z);
    }

    public boolean isDisplayAsPercentage() {
        return this.displayAsPercentage.isSet(this.field_1_formatFlags);
    }

    public void setShadow(boolean z) {
        this.field_1_formatFlags = this.shadow.setShortBoolean(this.field_1_formatFlags, z);
    }

    public boolean isShadow() {
        return this.shadow.isSet(this.field_1_formatFlags);
    }
}
