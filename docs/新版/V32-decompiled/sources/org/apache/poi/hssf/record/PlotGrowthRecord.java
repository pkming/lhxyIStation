package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class PlotGrowthRecord extends Record {
    public static final short sid = 4196;
    private int field_1_horizontalScale;
    private int field_2_verticalScale;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 12;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public PlotGrowthRecord() {
    }

    public PlotGrowthRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public PlotGrowthRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4196) {
            throw new RecordFormatException("Not a PlotGrowth record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_horizontalScale = LittleEndian.getInt(bArr, 0 + i);
        this.field_2_verticalScale = LittleEndian.getInt(bArr, 4 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[PLOTGROWTH]\n");
        stringBuffer.append("    .horizontalScale      = ").append("0x").append(HexDump.toHex(getHorizontalScale())).append(" (").append(getHorizontalScale()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .verticalScale        = ").append("0x").append(HexDump.toHex(getVerticalScale())).append(" (").append(getVerticalScale()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/PLOTGROWTH]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putInt(bArr, i + 4 + 0, this.field_1_horizontalScale);
        LittleEndian.putInt(bArr, i + 8 + 0, this.field_2_verticalScale);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        PlotGrowthRecord plotGrowthRecord = new PlotGrowthRecord();
        plotGrowthRecord.field_1_horizontalScale = this.field_1_horizontalScale;
        plotGrowthRecord.field_2_verticalScale = this.field_2_verticalScale;
        return plotGrowthRecord;
    }

    public int getHorizontalScale() {
        return this.field_1_horizontalScale;
    }

    public void setHorizontalScale(int i) {
        this.field_1_horizontalScale = i;
    }

    public int getVerticalScale() {
        return this.field_2_verticalScale;
    }

    public void setVerticalScale(int i) {
        this.field_2_verticalScale = i;
    }
}
