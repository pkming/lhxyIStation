package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class CommonObjectDataSubRecord extends SubRecord {
    public static final short OBJECT_TYPE_ARC = 4;
    public static final short OBJECT_TYPE_BUTTON = 7;
    public static final short OBJECT_TYPE_CHART = 5;
    public static final short OBJECT_TYPE_CHECKBOX = 11;
    public static final short OBJECT_TYPE_COMBO_BOX = 20;
    public static final short OBJECT_TYPE_COMMENT = 25;
    public static final short OBJECT_TYPE_DIALOG_BOX = 15;
    public static final short OBJECT_TYPE_EDIT_BOX = 13;
    public static final short OBJECT_TYPE_GROUP = 0;
    public static final short OBJECT_TYPE_GROUP_BOX = 19;
    public static final short OBJECT_TYPE_LABEL = 14;
    public static final short OBJECT_TYPE_LINE = 1;
    public static final short OBJECT_TYPE_LIST_BOX = 18;
    public static final short OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;
    public static final short OBJECT_TYPE_OPTION_BUTTON = 12;
    public static final short OBJECT_TYPE_OVAL = 3;
    public static final short OBJECT_TYPE_PICTURE = 8;
    public static final short OBJECT_TYPE_POLYGON = 9;
    public static final short OBJECT_TYPE_RECTANGLE = 2;
    public static final short OBJECT_TYPE_RESERVED1 = 10;
    public static final short OBJECT_TYPE_RESERVED2 = 21;
    public static final short OBJECT_TYPE_RESERVED3 = 22;
    public static final short OBJECT_TYPE_RESERVED4 = 23;
    public static final short OBJECT_TYPE_RESERVED5 = 24;
    public static final short OBJECT_TYPE_RESERVED6 = 26;
    public static final short OBJECT_TYPE_RESERVED7 = 27;
    public static final short OBJECT_TYPE_RESERVED8 = 28;
    public static final short OBJECT_TYPE_RESERVED9 = 29;
    public static final short OBJECT_TYPE_SCROLL_BAR = 17;
    public static final short OBJECT_TYPE_SPINNER = 16;
    public static final short OBJECT_TYPE_TEXT = 6;
    public static final short sid = 21;
    private BitField autofill;
    private BitField autoline;
    private short field_1_objectType;
    private short field_2_objectId;
    private short field_3_option;
    private int field_4_reserved1;
    private int field_5_reserved2;
    private int field_6_reserved3;
    private BitField locked;
    private BitField printable;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 22;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 21;
    }

    public CommonObjectDataSubRecord() {
        this.locked = new BitField(1);
        this.printable = new BitField(16);
        this.autofill = new BitField(8192);
        this.autoline = new BitField(16384);
    }

    public CommonObjectDataSubRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.locked = new BitField(1);
        this.printable = new BitField(16);
        this.autofill = new BitField(8192);
        this.autoline = new BitField(16384);
    }

    public CommonObjectDataSubRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.locked = new BitField(1);
        this.printable = new BitField(16);
        this.autofill = new BitField(8192);
        this.autoline = new BitField(16384);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 21) {
            throw new RecordFormatException("Not a CommonObjectData record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_objectType = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_objectId = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_option = LittleEndian.getShort(bArr, 4 + i);
        this.field_4_reserved1 = LittleEndian.getInt(bArr, 6 + i);
        this.field_5_reserved2 = LittleEndian.getInt(bArr, 10 + i);
        this.field_6_reserved3 = LittleEndian.getInt(bArr, 14 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ftCmo]\n");
        stringBuffer.append("    .objectType           = ").append("0x").append(HexDump.toHex(getObjectType())).append(" (").append((int) getObjectType()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .objectId             = ").append("0x").append(HexDump.toHex(getObjectId())).append(" (").append((int) getObjectId()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .option               = ").append("0x").append(HexDump.toHex(getOption())).append(" (").append((int) getOption()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .locked                   = ").append(isLocked()).append('\n');
        stringBuffer.append("         .printable                = ").append(isPrintable()).append('\n');
        stringBuffer.append("         .autofill                 = ").append(isAutofill()).append('\n');
        stringBuffer.append("         .autoline                 = ").append(isAutoline()).append('\n');
        stringBuffer.append("    .reserved1            = ").append("0x").append(HexDump.toHex(getReserved1())).append(" (").append(getReserved1()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved2            = ").append("0x").append(HexDump.toHex(getReserved2())).append(" (").append(getReserved2()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved3            = ").append("0x").append(HexDump.toHex(getReserved3())).append(" (").append(getReserved3()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/ftCmo]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 21);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_objectType);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_objectId);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_option);
        LittleEndian.putInt(bArr, i + 10 + 0, this.field_4_reserved1);
        LittleEndian.putInt(bArr, i + 14 + 0, this.field_5_reserved2);
        LittleEndian.putInt(bArr, i + 18 + 0, this.field_6_reserved3);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        CommonObjectDataSubRecord commonObjectDataSubRecord = new CommonObjectDataSubRecord();
        commonObjectDataSubRecord.field_1_objectType = this.field_1_objectType;
        commonObjectDataSubRecord.field_2_objectId = this.field_2_objectId;
        commonObjectDataSubRecord.field_3_option = this.field_3_option;
        commonObjectDataSubRecord.field_4_reserved1 = this.field_4_reserved1;
        commonObjectDataSubRecord.field_5_reserved2 = this.field_5_reserved2;
        commonObjectDataSubRecord.field_6_reserved3 = this.field_6_reserved3;
        return commonObjectDataSubRecord;
    }

    public short getObjectType() {
        return this.field_1_objectType;
    }

    public void setObjectType(short s) {
        this.field_1_objectType = s;
    }

    public short getObjectId() {
        return this.field_2_objectId;
    }

    public void setObjectId(short s) {
        this.field_2_objectId = s;
    }

    public short getOption() {
        return this.field_3_option;
    }

    public void setOption(short s) {
        this.field_3_option = s;
    }

    public int getReserved1() {
        return this.field_4_reserved1;
    }

    public void setReserved1(int i) {
        this.field_4_reserved1 = i;
    }

    public int getReserved2() {
        return this.field_5_reserved2;
    }

    public void setReserved2(int i) {
        this.field_5_reserved2 = i;
    }

    public int getReserved3() {
        return this.field_6_reserved3;
    }

    public void setReserved3(int i) {
        this.field_6_reserved3 = i;
    }

    public void setLocked(boolean z) {
        this.field_3_option = this.locked.setShortBoolean(this.field_3_option, z);
    }

    public boolean isLocked() {
        return this.locked.isSet(this.field_3_option);
    }

    public void setPrintable(boolean z) {
        this.field_3_option = this.printable.setShortBoolean(this.field_3_option, z);
    }

    public boolean isPrintable() {
        return this.printable.isSet(this.field_3_option);
    }

    public void setAutofill(boolean z) {
        this.field_3_option = this.autofill.setShortBoolean(this.field_3_option, z);
    }

    public boolean isAutofill() {
        return this.autofill.isSet(this.field_3_option);
    }

    public void setAutoline(boolean z) {
        this.field_3_option = this.autoline.setShortBoolean(this.field_3_option, z);
    }

    public boolean isAutoline() {
        return this.autoline.isSet(this.field_3_option);
    }
}
