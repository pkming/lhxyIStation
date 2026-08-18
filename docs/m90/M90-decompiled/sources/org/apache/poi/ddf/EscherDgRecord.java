package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherDgRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtDg";
    public static final short RECORD_ID = -4088;
    private int field_1_numShapes;
    private int field_2_lastMSOSPID;

    @Override // org.apache.poi.ddf.EscherRecord
    public short getRecordId() {
        return RECORD_ID;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "Dg";
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return 16;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] bArr, int i, EscherRecordFactory escherRecordFactory) {
        readHeader(bArr, i);
        int i2 = i + 8;
        this.field_1_numShapes = LittleEndian.getInt(bArr, i2 + 0);
        this.field_2_lastMSOSPID = LittleEndian.getInt(bArr, i2 + 4);
        return getRecordSize();
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int i, byte[] bArr, EscherSerializationListener escherSerializationListener) {
        escherSerializationListener.beforeRecordSerialize(i, getRecordId(), this);
        LittleEndian.putShort(bArr, i, getOptions());
        LittleEndian.putShort(bArr, i + 2, getRecordId());
        LittleEndian.putInt(bArr, i + 4, 8);
        LittleEndian.putInt(bArr, i + 8, this.field_1_numShapes);
        LittleEndian.putInt(bArr, i + 12, this.field_2_lastMSOSPID);
        escherSerializationListener.afterRecordSerialize(i + 16, getRecordId(), getRecordSize(), this);
        return getRecordSize();
    }

    public String toString() {
        String property = System.getProperty("line.separator");
        return new StringBuffer().append(getClass().getName()).append(":").append(property).append("  RecordId: 0x").append(HexDump.toHex(RECORD_ID)).append(property).append("  Options: 0x").append(HexDump.toHex(getOptions())).append(property).append("  NumShapes: ").append(this.field_1_numShapes).append(property).append("  LastMSOSPID: ").append(this.field_2_lastMSOSPID).append(property).toString();
    }

    public int getNumShapes() {
        return this.field_1_numShapes;
    }

    public void setNumShapes(int i) {
        this.field_1_numShapes = i;
    }

    public int getLastMSOSPID() {
        return this.field_2_lastMSOSPID;
    }

    public void setLastMSOSPID(int i) {
        this.field_2_lastMSOSPID = i;
    }

    public short getDrawingGroupId() {
        return (short) (getOptions() >> 4);
    }

    public void incrementShapeCount() {
        this.field_1_numShapes++;
    }
}
