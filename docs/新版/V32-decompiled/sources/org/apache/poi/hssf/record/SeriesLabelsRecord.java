package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SeriesLabelsRecord extends Record {
    public static final short sid = 4108;
    private short field_1_formatFlags;
    private BitField labelAsPercentage;
    private BitField showActual;
    private BitField showBubbleSizes;
    private BitField showLabel;
    private BitField showPercent;
    private BitField smoothedLine;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public SeriesLabelsRecord() {
        this.showActual = new BitField(1);
        this.showPercent = new BitField(2);
        this.labelAsPercentage = new BitField(4);
        this.smoothedLine = new BitField(8);
        this.showLabel = new BitField(16);
        this.showBubbleSizes = new BitField(32);
    }

    public SeriesLabelsRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.showActual = new BitField(1);
        this.showPercent = new BitField(2);
        this.labelAsPercentage = new BitField(4);
        this.smoothedLine = new BitField(8);
        this.showLabel = new BitField(16);
        this.showBubbleSizes = new BitField(32);
    }

    public SeriesLabelsRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.showActual = new BitField(1);
        this.showPercent = new BitField(2);
        this.labelAsPercentage = new BitField(4);
        this.smoothedLine = new BitField(8);
        this.showLabel = new BitField(16);
        this.showBubbleSizes = new BitField(32);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4108) {
            throw new RecordFormatException("Not a SeriesLabels record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_formatFlags = LittleEndian.getShort(bArr, 0 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ATTACHEDLABEL]\n");
        stringBuffer.append("    .formatFlags          = ").append("0x").append(HexDump.toHex(getFormatFlags())).append(" (").append((int) getFormatFlags()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .showActual               = ").append(isShowActual()).append('\n');
        stringBuffer.append("         .showPercent              = ").append(isShowPercent()).append('\n');
        stringBuffer.append("         .labelAsPercentage        = ").append(isLabelAsPercentage()).append('\n');
        stringBuffer.append("         .smoothedLine             = ").append(isSmoothedLine()).append('\n');
        stringBuffer.append("         .showLabel                = ").append(isShowLabel()).append('\n');
        stringBuffer.append("         .showBubbleSizes          = ").append(isShowBubbleSizes()).append('\n');
        stringBuffer.append("[/ATTACHEDLABEL]\n");
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
        SeriesLabelsRecord seriesLabelsRecord = new SeriesLabelsRecord();
        seriesLabelsRecord.field_1_formatFlags = this.field_1_formatFlags;
        return seriesLabelsRecord;
    }

    public short getFormatFlags() {
        return this.field_1_formatFlags;
    }

    public void setFormatFlags(short s) {
        this.field_1_formatFlags = s;
    }

    public void setShowActual(boolean z) {
        this.field_1_formatFlags = this.showActual.setShortBoolean(this.field_1_formatFlags, z);
    }

    public boolean isShowActual() {
        return this.showActual.isSet(this.field_1_formatFlags);
    }

    public void setShowPercent(boolean z) {
        this.field_1_formatFlags = this.showPercent.setShortBoolean(this.field_1_formatFlags, z);
    }

    public boolean isShowPercent() {
        return this.showPercent.isSet(this.field_1_formatFlags);
    }

    public void setLabelAsPercentage(boolean z) {
        this.field_1_formatFlags = this.labelAsPercentage.setShortBoolean(this.field_1_formatFlags, z);
    }

    public boolean isLabelAsPercentage() {
        return this.labelAsPercentage.isSet(this.field_1_formatFlags);
    }

    public void setSmoothedLine(boolean z) {
        this.field_1_formatFlags = this.smoothedLine.setShortBoolean(this.field_1_formatFlags, z);
    }

    public boolean isSmoothedLine() {
        return this.smoothedLine.isSet(this.field_1_formatFlags);
    }

    public void setShowLabel(boolean z) {
        this.field_1_formatFlags = this.showLabel.setShortBoolean(this.field_1_formatFlags, z);
    }

    public boolean isShowLabel() {
        return this.showLabel.isSet(this.field_1_formatFlags);
    }

    public void setShowBubbleSizes(boolean z) {
        this.field_1_formatFlags = this.showBubbleSizes.setShortBoolean(this.field_1_formatFlags, z);
    }

    public boolean isShowBubbleSizes() {
        return this.showBubbleSizes.isSet(this.field_1_formatFlags);
    }
}
