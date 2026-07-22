package org.apache.poi.ddf;

import java.io.ByteArrayOutputStream;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherClientAnchorRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtClientAnchor";
    public static final short RECORD_ID = -4080;
    private short field_1_flag;
    private short field_2_col1;
    private short field_3_dx1;
    private short field_4_row1;
    private short field_5_dy1;
    private short field_6_col2;
    private short field_7_dx2;
    private short field_8_row2;
    private short field_9_dy2;
    private byte[] remainingData;

    @Override // org.apache.poi.ddf.EscherRecord
    public short getRecordId() {
        return RECORD_ID;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "ClientAnchor";
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] bArr, int i, EscherRecordFactory escherRecordFactory) {
        int header = readHeader(bArr, i);
        int i2 = i + 8;
        this.field_1_flag = LittleEndian.getShort(bArr, i2 + 0);
        this.field_2_col1 = LittleEndian.getShort(bArr, i2 + 2);
        this.field_3_dx1 = LittleEndian.getShort(bArr, i2 + 4);
        this.field_4_row1 = LittleEndian.getShort(bArr, i2 + 6);
        this.field_5_dy1 = LittleEndian.getShort(bArr, i2 + 8);
        this.field_6_col2 = LittleEndian.getShort(bArr, i2 + 10);
        this.field_7_dx2 = LittleEndian.getShort(bArr, i2 + 12);
        this.field_8_row2 = LittleEndian.getShort(bArr, i2 + 14);
        this.field_9_dy2 = LittleEndian.getShort(bArr, i2 + 16);
        int i3 = header - 18;
        byte[] bArr2 = new byte[i3];
        this.remainingData = bArr2;
        System.arraycopy(bArr, i2 + 18, bArr2, 0, i3);
        return 26 + i3;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int i, byte[] bArr, EscherSerializationListener escherSerializationListener) {
        escherSerializationListener.beforeRecordSerialize(i, getRecordId(), this);
        if (this.remainingData == null) {
            this.remainingData = new byte[0];
        }
        LittleEndian.putShort(bArr, i, getOptions());
        LittleEndian.putShort(bArr, i + 2, getRecordId());
        LittleEndian.putInt(bArr, i + 4, this.remainingData.length + 18);
        int i2 = i + 8;
        LittleEndian.putShort(bArr, i2, this.field_1_flag);
        LittleEndian.putShort(bArr, i + 10, this.field_2_col1);
        LittleEndian.putShort(bArr, i + 12, this.field_3_dx1);
        LittleEndian.putShort(bArr, i + 14, this.field_4_row1);
        LittleEndian.putShort(bArr, i + 16, this.field_5_dy1);
        LittleEndian.putShort(bArr, i + 18, this.field_6_col2);
        LittleEndian.putShort(bArr, i + 20, this.field_7_dx2);
        LittleEndian.putShort(bArr, i + 22, this.field_8_row2);
        LittleEndian.putShort(bArr, i + 24, this.field_9_dy2);
        byte[] bArr2 = this.remainingData;
        System.arraycopy(bArr2, 0, bArr, i + 26, bArr2.length);
        int length = i2 + 18 + this.remainingData.length;
        int i3 = length - i;
        escherSerializationListener.afterRecordSerialize(length, getRecordId(), i3, this);
        return i3;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        byte[] bArr = this.remainingData;
        return (bArr == null ? 0 : bArr.length) + 26;
    }

    public String toString() {
        String string;
        String property = System.getProperty("line.separator");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            HexDump.dump(this.remainingData, 0L, byteArrayOutputStream, 0);
            string = byteArrayOutputStream.toString();
        } catch (Exception unused) {
            string = "error";
        }
        return new StringBuffer().append(getClass().getName()).append(":").append(property).append("  RecordId: 0x").append(HexDump.toHex(RECORD_ID)).append(property).append("  Options: 0x").append(HexDump.toHex(getOptions())).append(property).append("  Flag: ").append((int) this.field_1_flag).append(property).append("  Col1: ").append((int) this.field_2_col1).append(property).append("  DX1: ").append((int) this.field_3_dx1).append(property).append("  Row1: ").append((int) this.field_4_row1).append(property).append("  DY1: ").append((int) this.field_5_dy1).append(property).append("  Col2: ").append((int) this.field_6_col2).append(property).append("  DX2: ").append((int) this.field_7_dx2).append(property).append("  Row2: ").append((int) this.field_8_row2).append(property).append("  DY2: ").append((int) this.field_9_dy2).append(property).append("  Extra Data:").append(property).append(string).toString();
    }

    public short getFlag() {
        return this.field_1_flag;
    }

    public void setFlag(short s) {
        this.field_1_flag = s;
    }

    public short getCol1() {
        return this.field_2_col1;
    }

    public void setCol1(short s) {
        this.field_2_col1 = s;
    }

    public short getDx1() {
        return this.field_3_dx1;
    }

    public void setDx1(short s) {
        this.field_3_dx1 = s;
    }

    public short getRow1() {
        return this.field_4_row1;
    }

    public void setRow1(short s) {
        this.field_4_row1 = s;
    }

    public short getDy1() {
        return this.field_5_dy1;
    }

    public void setDy1(short s) {
        this.field_5_dy1 = s;
    }

    public short getCol2() {
        return this.field_6_col2;
    }

    public void setCol2(short s) {
        this.field_6_col2 = s;
    }

    public short getDx2() {
        return this.field_7_dx2;
    }

    public void setDx2(short s) {
        this.field_7_dx2 = s;
    }

    public short getRow2() {
        return this.field_8_row2;
    }

    public void setRow2(short s) {
        this.field_8_row2 = s;
    }

    public short getDy2() {
        return this.field_9_dy2;
    }

    public void setDy2(short s) {
        this.field_9_dy2 = s;
    }

    public byte[] getRemainingData() {
        return this.remainingData;
    }

    public void setRemainingData(byte[] bArr) {
        this.remainingData = bArr;
    }
}
