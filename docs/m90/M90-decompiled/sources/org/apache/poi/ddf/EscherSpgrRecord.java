package org.apache.poi.ddf;

import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherSpgrRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtSpgr";
    public static final short RECORD_ID = -4087;
    private int field_1_rectX1;
    private int field_2_rectY1;
    private int field_3_rectX2;
    private int field_4_rectY2;

    @Override // org.apache.poi.ddf.EscherRecord
    public short getRecordId() {
        return RECORD_ID;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "Spgr";
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return 24;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] bArr, int i, EscherRecordFactory escherRecordFactory) {
        int header = readHeader(bArr, i);
        int i2 = i + 8;
        this.field_1_rectX1 = LittleEndian.getInt(bArr, i2 + 0);
        this.field_2_rectY1 = LittleEndian.getInt(bArr, i2 + 4);
        this.field_3_rectX2 = LittleEndian.getInt(bArr, i2 + 8);
        this.field_4_rectY2 = LittleEndian.getInt(bArr, i2 + 12);
        int i3 = header - 16;
        if (i3 == 0) {
            return 24 + i3;
        }
        throw new RecordFormatException(new StringBuffer().append("Expected no remaining bytes but got ").append(i3).toString());
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int i, byte[] bArr, EscherSerializationListener escherSerializationListener) {
        escherSerializationListener.beforeRecordSerialize(i, getRecordId(), this);
        LittleEndian.putShort(bArr, i, getOptions());
        LittleEndian.putShort(bArr, i + 2, getRecordId());
        LittleEndian.putInt(bArr, i + 4, 16);
        LittleEndian.putInt(bArr, i + 8, this.field_1_rectX1);
        LittleEndian.putInt(bArr, i + 12, this.field_2_rectY1);
        LittleEndian.putInt(bArr, i + 16, this.field_3_rectX2);
        LittleEndian.putInt(bArr, i + 20, this.field_4_rectY2);
        escherSerializationListener.afterRecordSerialize(getRecordSize() + i, getRecordId(), i + getRecordSize(), this);
        return 24;
    }

    public String toString() {
        String property = System.getProperty("line.separator");
        return new StringBuffer().append(getClass().getName()).append(":").append(property).append("  RecordId: 0x").append(HexDump.toHex(RECORD_ID)).append(property).append("  Options: 0x").append(HexDump.toHex(getOptions())).append(property).append("  RectX: ").append(this.field_1_rectX1).append(property).append("  RectY: ").append(this.field_2_rectY1).append(property).append("  RectWidth: ").append(this.field_3_rectX2).append(property).append("  RectHeight: ").append(this.field_4_rectY2).append(property).toString();
    }

    public int getRectX1() {
        return this.field_1_rectX1;
    }

    public void setRectX1(int i) {
        this.field_1_rectX1 = i;
    }

    public int getRectY1() {
        return this.field_2_rectY1;
    }

    public void setRectY1(int i) {
        this.field_2_rectY1 = i;
    }

    public int getRectX2() {
        return this.field_3_rectX2;
    }

    public void setRectX2(int i) {
        this.field_3_rectX2 = i;
    }

    public int getRectY2() {
        return this.field_4_rectY2;
    }

    public void setRectY2(int i) {
        this.field_4_rectY2 = i;
    }
}
