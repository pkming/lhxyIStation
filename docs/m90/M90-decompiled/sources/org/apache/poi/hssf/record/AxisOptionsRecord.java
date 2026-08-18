package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class AxisOptionsRecord extends Record {
    public static final short sid = 4194;
    private BitField defaultBase;
    private BitField defaultCross;
    private BitField defaultDateSettings;
    private BitField defaultMajor;
    private BitField defaultMaximum;
    private BitField defaultMinimum;
    private BitField defaultMinorUnit;
    private short field_1_minimumCategory;
    private short field_2_maximumCategory;
    private short field_3_majorUnitValue;
    private short field_4_majorUnit;
    private short field_5_minorUnitValue;
    private short field_6_minorUnit;
    private short field_7_baseUnit;
    private short field_8_crossingPoint;
    private short field_9_options;
    private BitField isDate;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 22;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public AxisOptionsRecord() {
        this.defaultMinimum = new BitField(1);
        this.defaultMaximum = new BitField(2);
        this.defaultMajor = new BitField(4);
        this.defaultMinorUnit = new BitField(8);
        this.isDate = new BitField(16);
        this.defaultBase = new BitField(32);
        this.defaultCross = new BitField(64);
        this.defaultDateSettings = new BitField(128);
    }

    public AxisOptionsRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.defaultMinimum = new BitField(1);
        this.defaultMaximum = new BitField(2);
        this.defaultMajor = new BitField(4);
        this.defaultMinorUnit = new BitField(8);
        this.isDate = new BitField(16);
        this.defaultBase = new BitField(32);
        this.defaultCross = new BitField(64);
        this.defaultDateSettings = new BitField(128);
    }

    public AxisOptionsRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.defaultMinimum = new BitField(1);
        this.defaultMaximum = new BitField(2);
        this.defaultMajor = new BitField(4);
        this.defaultMinorUnit = new BitField(8);
        this.isDate = new BitField(16);
        this.defaultBase = new BitField(32);
        this.defaultCross = new BitField(64);
        this.defaultDateSettings = new BitField(128);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4194) {
            throw new RecordFormatException("Not a AxisOptions record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_minimumCategory = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_maximumCategory = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_majorUnitValue = LittleEndian.getShort(bArr, 4 + i);
        this.field_4_majorUnit = LittleEndian.getShort(bArr, 6 + i);
        this.field_5_minorUnitValue = LittleEndian.getShort(bArr, 8 + i);
        this.field_6_minorUnit = LittleEndian.getShort(bArr, 10 + i);
        this.field_7_baseUnit = LittleEndian.getShort(bArr, 12 + i);
        this.field_8_crossingPoint = LittleEndian.getShort(bArr, 14 + i);
        this.field_9_options = LittleEndian.getShort(bArr, 16 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[AXCEXT]\n");
        stringBuffer.append("    .minimumCategory      = ").append("0x").append(HexDump.toHex(getMinimumCategory())).append(" (").append((int) getMinimumCategory()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .maximumCategory      = ").append("0x").append(HexDump.toHex(getMaximumCategory())).append(" (").append((int) getMaximumCategory()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .majorUnitValue       = ").append("0x").append(HexDump.toHex(getMajorUnitValue())).append(" (").append((int) getMajorUnitValue()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .majorUnit            = ").append("0x").append(HexDump.toHex(getMajorUnit())).append(" (").append((int) getMajorUnit()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .minorUnitValue       = ").append("0x").append(HexDump.toHex(getMinorUnitValue())).append(" (").append((int) getMinorUnitValue()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .minorUnit            = ").append("0x").append(HexDump.toHex(getMinorUnit())).append(" (").append((int) getMinorUnit()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .baseUnit             = ").append("0x").append(HexDump.toHex(getBaseUnit())).append(" (").append((int) getBaseUnit()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .crossingPoint        = ").append("0x").append(HexDump.toHex(getCrossingPoint())).append(" (").append((int) getCrossingPoint()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .defaultMinimum           = ").append(isDefaultMinimum()).append('\n');
        stringBuffer.append("         .defaultMaximum           = ").append(isDefaultMaximum()).append('\n');
        stringBuffer.append("         .defaultMajor             = ").append(isDefaultMajor()).append('\n');
        stringBuffer.append("         .defaultMinorUnit         = ").append(isDefaultMinorUnit()).append('\n');
        stringBuffer.append("         .isDate                   = ").append(isIsDate()).append('\n');
        stringBuffer.append("         .defaultBase              = ").append(isDefaultBase()).append('\n');
        stringBuffer.append("         .defaultCross             = ").append(isDefaultCross()).append('\n');
        stringBuffer.append("         .defaultDateSettings      = ").append(isDefaultDateSettings()).append('\n');
        stringBuffer.append("[/AXCEXT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_minimumCategory);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_maximumCategory);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_majorUnitValue);
        LittleEndian.putShort(bArr, i + 10 + 0, this.field_4_majorUnit);
        LittleEndian.putShort(bArr, i + 12 + 0, this.field_5_minorUnitValue);
        LittleEndian.putShort(bArr, i + 14 + 0, this.field_6_minorUnit);
        LittleEndian.putShort(bArr, i + 16 + 0, this.field_7_baseUnit);
        LittleEndian.putShort(bArr, i + 18 + 0, this.field_8_crossingPoint);
        LittleEndian.putShort(bArr, i + 20 + 0, this.field_9_options);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        AxisOptionsRecord axisOptionsRecord = new AxisOptionsRecord();
        axisOptionsRecord.field_1_minimumCategory = this.field_1_minimumCategory;
        axisOptionsRecord.field_2_maximumCategory = this.field_2_maximumCategory;
        axisOptionsRecord.field_3_majorUnitValue = this.field_3_majorUnitValue;
        axisOptionsRecord.field_4_majorUnit = this.field_4_majorUnit;
        axisOptionsRecord.field_5_minorUnitValue = this.field_5_minorUnitValue;
        axisOptionsRecord.field_6_minorUnit = this.field_6_minorUnit;
        axisOptionsRecord.field_7_baseUnit = this.field_7_baseUnit;
        axisOptionsRecord.field_8_crossingPoint = this.field_8_crossingPoint;
        axisOptionsRecord.field_9_options = this.field_9_options;
        return axisOptionsRecord;
    }

    public short getMinimumCategory() {
        return this.field_1_minimumCategory;
    }

    public void setMinimumCategory(short s) {
        this.field_1_minimumCategory = s;
    }

    public short getMaximumCategory() {
        return this.field_2_maximumCategory;
    }

    public void setMaximumCategory(short s) {
        this.field_2_maximumCategory = s;
    }

    public short getMajorUnitValue() {
        return this.field_3_majorUnitValue;
    }

    public void setMajorUnitValue(short s) {
        this.field_3_majorUnitValue = s;
    }

    public short getMajorUnit() {
        return this.field_4_majorUnit;
    }

    public void setMajorUnit(short s) {
        this.field_4_majorUnit = s;
    }

    public short getMinorUnitValue() {
        return this.field_5_minorUnitValue;
    }

    public void setMinorUnitValue(short s) {
        this.field_5_minorUnitValue = s;
    }

    public short getMinorUnit() {
        return this.field_6_minorUnit;
    }

    public void setMinorUnit(short s) {
        this.field_6_minorUnit = s;
    }

    public short getBaseUnit() {
        return this.field_7_baseUnit;
    }

    public void setBaseUnit(short s) {
        this.field_7_baseUnit = s;
    }

    public short getCrossingPoint() {
        return this.field_8_crossingPoint;
    }

    public void setCrossingPoint(short s) {
        this.field_8_crossingPoint = s;
    }

    public short getOptions() {
        return this.field_9_options;
    }

    public void setOptions(short s) {
        this.field_9_options = s;
    }

    public void setDefaultMinimum(boolean z) {
        this.field_9_options = this.defaultMinimum.setShortBoolean(this.field_9_options, z);
    }

    public boolean isDefaultMinimum() {
        return this.defaultMinimum.isSet(this.field_9_options);
    }

    public void setDefaultMaximum(boolean z) {
        this.field_9_options = this.defaultMaximum.setShortBoolean(this.field_9_options, z);
    }

    public boolean isDefaultMaximum() {
        return this.defaultMaximum.isSet(this.field_9_options);
    }

    public void setDefaultMajor(boolean z) {
        this.field_9_options = this.defaultMajor.setShortBoolean(this.field_9_options, z);
    }

    public boolean isDefaultMajor() {
        return this.defaultMajor.isSet(this.field_9_options);
    }

    public void setDefaultMinorUnit(boolean z) {
        this.field_9_options = this.defaultMinorUnit.setShortBoolean(this.field_9_options, z);
    }

    public boolean isDefaultMinorUnit() {
        return this.defaultMinorUnit.isSet(this.field_9_options);
    }

    public void setIsDate(boolean z) {
        this.field_9_options = this.isDate.setShortBoolean(this.field_9_options, z);
    }

    public boolean isIsDate() {
        return this.isDate.isSet(this.field_9_options);
    }

    public void setDefaultBase(boolean z) {
        this.field_9_options = this.defaultBase.setShortBoolean(this.field_9_options, z);
    }

    public boolean isDefaultBase() {
        return this.defaultBase.isSet(this.field_9_options);
    }

    public void setDefaultCross(boolean z) {
        this.field_9_options = this.defaultCross.setShortBoolean(this.field_9_options, z);
    }

    public boolean isDefaultCross() {
        return this.defaultCross.isSet(this.field_9_options);
    }

    public void setDefaultDateSettings(boolean z) {
        this.field_9_options = this.defaultDateSettings.setShortBoolean(this.field_9_options, z);
    }

    public boolean isDefaultDateSettings() {
        return this.defaultDateSettings.isSet(this.field_9_options);
    }
}
