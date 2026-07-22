package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SheetPropertiesRecord extends Record {
    public static final byte EMPTY_INTERPOLATED = 2;
    public static final byte EMPTY_NOT_PLOTTED = 0;
    public static final byte EMPTY_ZERO = 1;
    public static final short sid = 4164;
    private BitField autoPlotArea;
    private BitField chartTypeManuallyFormatted;
    private BitField defaultPlotDimensions;
    private BitField doNotSizeWithWindow;
    private short field_1_flags;
    private byte field_2_empty;
    private BitField plotVisibleOnly;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 7;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public SheetPropertiesRecord() {
        this.chartTypeManuallyFormatted = new BitField(1);
        this.plotVisibleOnly = new BitField(2);
        this.doNotSizeWithWindow = new BitField(4);
        this.defaultPlotDimensions = new BitField(8);
        this.autoPlotArea = new BitField(16);
    }

    public SheetPropertiesRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.chartTypeManuallyFormatted = new BitField(1);
        this.plotVisibleOnly = new BitField(2);
        this.doNotSizeWithWindow = new BitField(4);
        this.defaultPlotDimensions = new BitField(8);
        this.autoPlotArea = new BitField(16);
    }

    public SheetPropertiesRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.chartTypeManuallyFormatted = new BitField(1);
        this.plotVisibleOnly = new BitField(2);
        this.doNotSizeWithWindow = new BitField(4);
        this.defaultPlotDimensions = new BitField(8);
        this.autoPlotArea = new BitField(16);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4164) {
            throw new RecordFormatException("Not a SheetProperties record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_flags = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_empty = bArr[2 + i];
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SHTPROPS]\n");
        stringBuffer.append("    .flags                = ").append("0x").append(HexDump.toHex(getFlags())).append(" (").append((int) getFlags()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .chartTypeManuallyFormatted     = ").append(isChartTypeManuallyFormatted()).append('\n');
        stringBuffer.append("         .plotVisibleOnly          = ").append(isPlotVisibleOnly()).append('\n');
        stringBuffer.append("         .doNotSizeWithWindow      = ").append(isDoNotSizeWithWindow()).append('\n');
        stringBuffer.append("         .defaultPlotDimensions     = ").append(isDefaultPlotDimensions()).append('\n');
        stringBuffer.append("         .autoPlotArea             = ").append(isAutoPlotArea()).append('\n');
        stringBuffer.append("    .empty                = ").append("0x").append(HexDump.toHex(getEmpty())).append(" (").append((int) getEmpty()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/SHTPROPS]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_flags);
        bArr[i + 6 + 0] = this.field_2_empty;
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SheetPropertiesRecord sheetPropertiesRecord = new SheetPropertiesRecord();
        sheetPropertiesRecord.field_1_flags = this.field_1_flags;
        sheetPropertiesRecord.field_2_empty = this.field_2_empty;
        return sheetPropertiesRecord;
    }

    public short getFlags() {
        return this.field_1_flags;
    }

    public void setFlags(short s) {
        this.field_1_flags = s;
    }

    public byte getEmpty() {
        return this.field_2_empty;
    }

    public void setEmpty(byte b) {
        this.field_2_empty = b;
    }

    public void setChartTypeManuallyFormatted(boolean z) {
        this.field_1_flags = this.chartTypeManuallyFormatted.setShortBoolean(this.field_1_flags, z);
    }

    public boolean isChartTypeManuallyFormatted() {
        return this.chartTypeManuallyFormatted.isSet(this.field_1_flags);
    }

    public void setPlotVisibleOnly(boolean z) {
        this.field_1_flags = this.plotVisibleOnly.setShortBoolean(this.field_1_flags, z);
    }

    public boolean isPlotVisibleOnly() {
        return this.plotVisibleOnly.isSet(this.field_1_flags);
    }

    public void setDoNotSizeWithWindow(boolean z) {
        this.field_1_flags = this.doNotSizeWithWindow.setShortBoolean(this.field_1_flags, z);
    }

    public boolean isDoNotSizeWithWindow() {
        return this.doNotSizeWithWindow.isSet(this.field_1_flags);
    }

    public void setDefaultPlotDimensions(boolean z) {
        this.field_1_flags = this.defaultPlotDimensions.setShortBoolean(this.field_1_flags, z);
    }

    public boolean isDefaultPlotDimensions() {
        return this.defaultPlotDimensions.isSet(this.field_1_flags);
    }

    public void setAutoPlotArea(boolean z) {
        this.field_1_flags = this.autoPlotArea.setShortBoolean(this.field_1_flags, z);
    }

    public boolean isAutoPlotArea() {
        return this.autoPlotArea.isSet(this.field_1_flags);
    }
}
