package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ValueRangeRecord extends Record {
    public static final short sid = 4127;
    private BitField automaticCategoryCrossing;
    private BitField automaticMajor;
    private BitField automaticMaximum;
    private BitField automaticMinimum;
    private BitField automaticMinor;
    private BitField crossCategoryAxisAtMaximum;
    private double field_1_minimumAxisValue;
    private double field_2_maximumAxisValue;
    private double field_3_majorIncrement;
    private double field_4_minorIncrement;
    private double field_5_categoryAxisCross;
    private short field_6_options;
    private BitField logarithmicScale;
    private BitField reserved;
    private BitField valuesInReverse;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 46;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public ValueRangeRecord() {
        this.automaticMinimum = new BitField(1);
        this.automaticMaximum = new BitField(2);
        this.automaticMajor = new BitField(4);
        this.automaticMinor = new BitField(8);
        this.automaticCategoryCrossing = new BitField(16);
        this.logarithmicScale = new BitField(32);
        this.valuesInReverse = new BitField(64);
        this.crossCategoryAxisAtMaximum = new BitField(128);
        this.reserved = new BitField(256);
    }

    public ValueRangeRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.automaticMinimum = new BitField(1);
        this.automaticMaximum = new BitField(2);
        this.automaticMajor = new BitField(4);
        this.automaticMinor = new BitField(8);
        this.automaticCategoryCrossing = new BitField(16);
        this.logarithmicScale = new BitField(32);
        this.valuesInReverse = new BitField(64);
        this.crossCategoryAxisAtMaximum = new BitField(128);
        this.reserved = new BitField(256);
    }

    public ValueRangeRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.automaticMinimum = new BitField(1);
        this.automaticMaximum = new BitField(2);
        this.automaticMajor = new BitField(4);
        this.automaticMinor = new BitField(8);
        this.automaticCategoryCrossing = new BitField(16);
        this.logarithmicScale = new BitField(32);
        this.valuesInReverse = new BitField(64);
        this.crossCategoryAxisAtMaximum = new BitField(128);
        this.reserved = new BitField(256);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4127) {
            throw new RecordFormatException("Not a ValueRange record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_minimumAxisValue = LittleEndian.getDouble(bArr, 0 + i);
        this.field_2_maximumAxisValue = LittleEndian.getDouble(bArr, 8 + i);
        this.field_3_majorIncrement = LittleEndian.getDouble(bArr, 16 + i);
        this.field_4_minorIncrement = LittleEndian.getDouble(bArr, 24 + i);
        this.field_5_categoryAxisCross = LittleEndian.getDouble(bArr, 32 + i);
        this.field_6_options = LittleEndian.getShort(bArr, 40 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[VALUERANGE]\n");
        stringBuffer.append("    .minimumAxisValue     = ").append(" (").append(getMinimumAxisValue()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .maximumAxisValue     = ").append(" (").append(getMaximumAxisValue()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .majorIncrement       = ").append(" (").append(getMajorIncrement()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .minorIncrement       = ").append(" (").append(getMinorIncrement()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .categoryAxisCross    = ").append(" (").append(getCategoryAxisCross()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .automaticMinimum         = ").append(isAutomaticMinimum()).append('\n');
        stringBuffer.append("         .automaticMaximum         = ").append(isAutomaticMaximum()).append('\n');
        stringBuffer.append("         .automaticMajor           = ").append(isAutomaticMajor()).append('\n');
        stringBuffer.append("         .automaticMinor           = ").append(isAutomaticMinor()).append('\n');
        stringBuffer.append("         .automaticCategoryCrossing     = ").append(isAutomaticCategoryCrossing()).append('\n');
        stringBuffer.append("         .logarithmicScale         = ").append(isLogarithmicScale()).append('\n');
        stringBuffer.append("         .valuesInReverse          = ").append(isValuesInReverse()).append('\n');
        stringBuffer.append("         .crossCategoryAxisAtMaximum     = ").append(isCrossCategoryAxisAtMaximum()).append('\n');
        stringBuffer.append("         .reserved                 = ").append(isReserved()).append('\n');
        stringBuffer.append("[/VALUERANGE]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putDouble(bArr, i + 4 + 0, this.field_1_minimumAxisValue);
        LittleEndian.putDouble(bArr, i + 12 + 0, this.field_2_maximumAxisValue);
        LittleEndian.putDouble(bArr, i + 20 + 0, this.field_3_majorIncrement);
        LittleEndian.putDouble(bArr, i + 28 + 0, this.field_4_minorIncrement);
        LittleEndian.putDouble(bArr, i + 36 + 0, this.field_5_categoryAxisCross);
        LittleEndian.putShort(bArr, i + 44 + 0, this.field_6_options);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        ValueRangeRecord valueRangeRecord = new ValueRangeRecord();
        valueRangeRecord.field_1_minimumAxisValue = this.field_1_minimumAxisValue;
        valueRangeRecord.field_2_maximumAxisValue = this.field_2_maximumAxisValue;
        valueRangeRecord.field_3_majorIncrement = this.field_3_majorIncrement;
        valueRangeRecord.field_4_minorIncrement = this.field_4_minorIncrement;
        valueRangeRecord.field_5_categoryAxisCross = this.field_5_categoryAxisCross;
        valueRangeRecord.field_6_options = this.field_6_options;
        return valueRangeRecord;
    }

    public double getMinimumAxisValue() {
        return this.field_1_minimumAxisValue;
    }

    public void setMinimumAxisValue(double d) {
        this.field_1_minimumAxisValue = d;
    }

    public double getMaximumAxisValue() {
        return this.field_2_maximumAxisValue;
    }

    public void setMaximumAxisValue(double d) {
        this.field_2_maximumAxisValue = d;
    }

    public double getMajorIncrement() {
        return this.field_3_majorIncrement;
    }

    public void setMajorIncrement(double d) {
        this.field_3_majorIncrement = d;
    }

    public double getMinorIncrement() {
        return this.field_4_minorIncrement;
    }

    public void setMinorIncrement(double d) {
        this.field_4_minorIncrement = d;
    }

    public double getCategoryAxisCross() {
        return this.field_5_categoryAxisCross;
    }

    public void setCategoryAxisCross(double d) {
        this.field_5_categoryAxisCross = d;
    }

    public short getOptions() {
        return this.field_6_options;
    }

    public void setOptions(short s) {
        this.field_6_options = s;
    }

    public void setAutomaticMinimum(boolean z) {
        this.field_6_options = this.automaticMinimum.setShortBoolean(this.field_6_options, z);
    }

    public boolean isAutomaticMinimum() {
        return this.automaticMinimum.isSet(this.field_6_options);
    }

    public void setAutomaticMaximum(boolean z) {
        this.field_6_options = this.automaticMaximum.setShortBoolean(this.field_6_options, z);
    }

    public boolean isAutomaticMaximum() {
        return this.automaticMaximum.isSet(this.field_6_options);
    }

    public void setAutomaticMajor(boolean z) {
        this.field_6_options = this.automaticMajor.setShortBoolean(this.field_6_options, z);
    }

    public boolean isAutomaticMajor() {
        return this.automaticMajor.isSet(this.field_6_options);
    }

    public void setAutomaticMinor(boolean z) {
        this.field_6_options = this.automaticMinor.setShortBoolean(this.field_6_options, z);
    }

    public boolean isAutomaticMinor() {
        return this.automaticMinor.isSet(this.field_6_options);
    }

    public void setAutomaticCategoryCrossing(boolean z) {
        this.field_6_options = this.automaticCategoryCrossing.setShortBoolean(this.field_6_options, z);
    }

    public boolean isAutomaticCategoryCrossing() {
        return this.automaticCategoryCrossing.isSet(this.field_6_options);
    }

    public void setLogarithmicScale(boolean z) {
        this.field_6_options = this.logarithmicScale.setShortBoolean(this.field_6_options, z);
    }

    public boolean isLogarithmicScale() {
        return this.logarithmicScale.isSet(this.field_6_options);
    }

    public void setValuesInReverse(boolean z) {
        this.field_6_options = this.valuesInReverse.setShortBoolean(this.field_6_options, z);
    }

    public boolean isValuesInReverse() {
        return this.valuesInReverse.isSet(this.field_6_options);
    }

    public void setCrossCategoryAxisAtMaximum(boolean z) {
        this.field_6_options = this.crossCategoryAxisAtMaximum.setShortBoolean(this.field_6_options, z);
    }

    public boolean isCrossCategoryAxisAtMaximum() {
        return this.crossCategoryAxisAtMaximum.isSet(this.field_6_options);
    }

    public void setReserved(boolean z) {
        this.field_6_options = this.reserved.setShortBoolean(this.field_6_options, z);
    }

    public boolean isReserved() {
        return this.reserved.isSet(this.field_6_options);
    }
}
