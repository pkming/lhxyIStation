package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class LegendRecord extends Record {
    public static final byte SPACING_CLOSE = 0;
    public static final byte SPACING_MEDIUM = 1;
    public static final byte SPACING_OPEN = 2;
    public static final byte TYPE_BOTTOM = 0;
    public static final byte TYPE_CORNER = 1;
    public static final byte TYPE_LEFT = 4;
    public static final byte TYPE_RIGHT = 3;
    public static final byte TYPE_TOP = 2;
    public static final byte TYPE_UNDOCKED = 7;
    public static final short sid = 4117;
    private BitField autoPosition;
    private BitField autoSeries;
    private BitField autoXPositioning;
    private BitField autoYPositioning;
    private BitField dataTable;
    private int field_1_xAxisUpperLeft;
    private int field_2_yAxisUpperLeft;
    private int field_3_xSize;
    private int field_4_ySize;
    private byte field_5_type;
    private byte field_6_spacing;
    private short field_7_options;
    private BitField vertical;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 24;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public LegendRecord() {
        this.autoPosition = new BitField(1);
        this.autoSeries = new BitField(2);
        this.autoXPositioning = new BitField(4);
        this.autoYPositioning = new BitField(8);
        this.vertical = new BitField(16);
        this.dataTable = new BitField(32);
    }

    public LegendRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.autoPosition = new BitField(1);
        this.autoSeries = new BitField(2);
        this.autoXPositioning = new BitField(4);
        this.autoYPositioning = new BitField(8);
        this.vertical = new BitField(16);
        this.dataTable = new BitField(32);
    }

    public LegendRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.autoPosition = new BitField(1);
        this.autoSeries = new BitField(2);
        this.autoXPositioning = new BitField(4);
        this.autoYPositioning = new BitField(8);
        this.vertical = new BitField(16);
        this.dataTable = new BitField(32);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4117) {
            throw new RecordFormatException("Not a Legend record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_xAxisUpperLeft = LittleEndian.getInt(bArr, 0 + i);
        this.field_2_yAxisUpperLeft = LittleEndian.getInt(bArr, 4 + i);
        this.field_3_xSize = LittleEndian.getInt(bArr, 8 + i);
        this.field_4_ySize = LittleEndian.getInt(bArr, 12 + i);
        this.field_5_type = bArr[16 + i];
        this.field_6_spacing = bArr[17 + i];
        this.field_7_options = LittleEndian.getShort(bArr, 18 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[LEGEND]\n");
        stringBuffer.append("    .xAxisUpperLeft       = ").append("0x").append(HexDump.toHex(getXAxisUpperLeft())).append(" (").append(getXAxisUpperLeft()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .yAxisUpperLeft       = ").append("0x").append(HexDump.toHex(getYAxisUpperLeft())).append(" (").append(getYAxisUpperLeft()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .xSize                = ").append("0x").append(HexDump.toHex(getXSize())).append(" (").append(getXSize()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .ySize                = ").append("0x").append(HexDump.toHex(getYSize())).append(" (").append(getYSize()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .type                 = ").append("0x").append(HexDump.toHex(getType())).append(" (").append((int) getType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .spacing              = ").append("0x").append(HexDump.toHex(getSpacing())).append(" (").append((int) getSpacing()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .autoPosition             = ").append(isAutoPosition()).append('\n');
        stringBuffer.append("         .autoSeries               = ").append(isAutoSeries()).append('\n');
        stringBuffer.append("         .autoXPositioning         = ").append(isAutoXPositioning()).append('\n');
        stringBuffer.append("         .autoYPositioning         = ").append(isAutoYPositioning()).append('\n');
        stringBuffer.append("         .vertical                 = ").append(isVertical()).append('\n');
        stringBuffer.append("         .dataTable                = ").append(isDataTable()).append('\n');
        stringBuffer.append("[/LEGEND]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putInt(bArr, i + 4 + 0, this.field_1_xAxisUpperLeft);
        LittleEndian.putInt(bArr, i + 8 + 0, this.field_2_yAxisUpperLeft);
        LittleEndian.putInt(bArr, i + 12 + 0, this.field_3_xSize);
        LittleEndian.putInt(bArr, i + 16 + 0, this.field_4_ySize);
        bArr[i + 20 + 0] = this.field_5_type;
        bArr[i + 21 + 0] = this.field_6_spacing;
        LittleEndian.putShort(bArr, i + 22 + 0, this.field_7_options);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        LegendRecord legendRecord = new LegendRecord();
        legendRecord.field_1_xAxisUpperLeft = this.field_1_xAxisUpperLeft;
        legendRecord.field_2_yAxisUpperLeft = this.field_2_yAxisUpperLeft;
        legendRecord.field_3_xSize = this.field_3_xSize;
        legendRecord.field_4_ySize = this.field_4_ySize;
        legendRecord.field_5_type = this.field_5_type;
        legendRecord.field_6_spacing = this.field_6_spacing;
        legendRecord.field_7_options = this.field_7_options;
        return legendRecord;
    }

    public int getXAxisUpperLeft() {
        return this.field_1_xAxisUpperLeft;
    }

    public void setXAxisUpperLeft(int i) {
        this.field_1_xAxisUpperLeft = i;
    }

    public int getYAxisUpperLeft() {
        return this.field_2_yAxisUpperLeft;
    }

    public void setYAxisUpperLeft(int i) {
        this.field_2_yAxisUpperLeft = i;
    }

    public int getXSize() {
        return this.field_3_xSize;
    }

    public void setXSize(int i) {
        this.field_3_xSize = i;
    }

    public int getYSize() {
        return this.field_4_ySize;
    }

    public void setYSize(int i) {
        this.field_4_ySize = i;
    }

    public byte getType() {
        return this.field_5_type;
    }

    public void setType(byte b) {
        this.field_5_type = b;
    }

    public byte getSpacing() {
        return this.field_6_spacing;
    }

    public void setSpacing(byte b) {
        this.field_6_spacing = b;
    }

    public short getOptions() {
        return this.field_7_options;
    }

    public void setOptions(short s) {
        this.field_7_options = s;
    }

    public void setAutoPosition(boolean z) {
        this.field_7_options = this.autoPosition.setShortBoolean(this.field_7_options, z);
    }

    public boolean isAutoPosition() {
        return this.autoPosition.isSet(this.field_7_options);
    }

    public void setAutoSeries(boolean z) {
        this.field_7_options = this.autoSeries.setShortBoolean(this.field_7_options, z);
    }

    public boolean isAutoSeries() {
        return this.autoSeries.isSet(this.field_7_options);
    }

    public void setAutoXPositioning(boolean z) {
        this.field_7_options = this.autoXPositioning.setShortBoolean(this.field_7_options, z);
    }

    public boolean isAutoXPositioning() {
        return this.autoXPositioning.isSet(this.field_7_options);
    }

    public void setAutoYPositioning(boolean z) {
        this.field_7_options = this.autoYPositioning.setShortBoolean(this.field_7_options, z);
    }

    public boolean isAutoYPositioning() {
        return this.autoYPositioning.isSet(this.field_7_options);
    }

    public void setVertical(boolean z) {
        this.field_7_options = this.vertical.setShortBoolean(this.field_7_options, z);
    }

    public boolean isVertical() {
        return this.vertical.isSet(this.field_7_options);
    }

    public void setDataTable(boolean z) {
        this.field_7_options = this.dataTable.setShortBoolean(this.field_7_options, z);
    }

    public boolean isDataTable() {
        return this.dataTable.isSet(this.field_7_options);
    }
}
