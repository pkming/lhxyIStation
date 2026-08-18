package org.apache.poi.ddf;

import java.io.ByteArrayOutputStream;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherBSERecord extends EscherRecord {
    public static final byte BT_DIB = 7;
    public static final byte BT_EMF = 2;
    public static final byte BT_ERROR = 0;
    public static final byte BT_JPEG = 5;
    public static final byte BT_PICT = 4;
    public static final byte BT_PNG = 6;
    public static final byte BT_UNKNOWN = 1;
    public static final byte BT_WMF = 3;
    public static final String RECORD_DESCRIPTION = "MsofbtBSE";
    public static final short RECORD_ID = -4089;
    private byte field_10_unused2;
    private byte field_11_unused3;
    private byte field_1_blipTypeWin32;
    private byte field_2_blipTypeMacOS;
    private byte[] field_3_uid;
    private short field_4_tag;
    private int field_5_size;
    private int field_6_ref;
    private int field_7_offset;
    private byte field_8_usage;
    private byte field_9_name;
    private byte[] remainingData;

    public String getBlipType(byte b) {
        switch (b) {
            case 0:
                return " ERROR";
            case 1:
                return " UNKNOWN";
            case 2:
                return " EMF";
            case 3:
                return " WMF";
            case 4:
                return " PICT";
            case 5:
                return " JPEG";
            case 6:
                return " PNG";
            case 7:
                return " DIB";
            default:
                return b < 32 ? " NotKnown" : " Client";
        }
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "BSE";
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] bArr, int i, EscherRecordFactory escherRecordFactory) {
        int header = readHeader(bArr, i);
        int i2 = i + 8;
        this.field_1_blipTypeWin32 = bArr[i2];
        this.field_2_blipTypeMacOS = bArr[i2 + 1];
        byte[] bArr2 = new byte[16];
        this.field_3_uid = bArr2;
        System.arraycopy(bArr, i2 + 2, bArr2, 0, 16);
        this.field_4_tag = LittleEndian.getShort(bArr, i2 + 18);
        this.field_5_size = LittleEndian.getInt(bArr, i2 + 20);
        this.field_6_ref = LittleEndian.getInt(bArr, i2 + 24);
        this.field_7_offset = LittleEndian.getInt(bArr, i2 + 28);
        this.field_8_usage = bArr[i2 + 32];
        this.field_9_name = bArr[i2 + 33];
        this.field_10_unused2 = bArr[i2 + 34];
        this.field_11_unused3 = bArr[i2 + 35];
        int i3 = header - 36;
        byte[] bArr3 = new byte[i3];
        this.remainingData = bArr3;
        System.arraycopy(bArr, i2 + 36, bArr3, 0, i3);
        return i3 + 8 + 36;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int i, byte[] bArr, EscherSerializationListener escherSerializationListener) {
        escherSerializationListener.beforeRecordSerialize(i, getRecordId(), this);
        LittleEndian.putShort(bArr, i, getOptions());
        LittleEndian.putShort(bArr, i + 2, getRecordId());
        LittleEndian.putInt(bArr, i + 4, this.remainingData.length + 36);
        int i2 = i + 8;
        bArr[i2] = this.field_1_blipTypeWin32;
        bArr[i + 9] = this.field_2_blipTypeMacOS;
        for (int i3 = 0; i3 < 16; i3++) {
            bArr[i + 10 + i3] = this.field_3_uid[i3];
        }
        LittleEndian.putShort(bArr, i + 26, this.field_4_tag);
        LittleEndian.putInt(bArr, i + 28, this.field_5_size);
        LittleEndian.putInt(bArr, i + 32, this.field_6_ref);
        LittleEndian.putInt(bArr, i + 36, this.field_7_offset);
        bArr[i + 40] = this.field_8_usage;
        bArr[i + 41] = this.field_9_name;
        bArr[i + 42] = this.field_10_unused2;
        bArr[i + 43] = this.field_11_unused3;
        byte[] bArr2 = this.remainingData;
        System.arraycopy(bArr2, 0, bArr, i + 44, bArr2.length);
        int length = i2 + 36 + this.remainingData.length;
        int i4 = length - i;
        escherSerializationListener.afterRecordSerialize(length, getRecordId(), i4, this);
        return i4;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return this.remainingData.length + 44;
    }

    public byte getBlipTypeWin32() {
        return this.field_1_blipTypeWin32;
    }

    public void setBlipTypeWin32(byte b) {
        this.field_1_blipTypeWin32 = b;
    }

    public byte getBlipTypeMacOS() {
        return this.field_2_blipTypeMacOS;
    }

    public void setBlipTypeMacOS(byte b) {
        this.field_2_blipTypeMacOS = b;
    }

    public byte[] getUid() {
        return this.field_3_uid;
    }

    public void setUid(byte[] bArr) {
        this.field_3_uid = bArr;
    }

    public short getTag() {
        return this.field_4_tag;
    }

    public void setTag(short s) {
        this.field_4_tag = s;
    }

    public int getSize() {
        return this.field_5_size;
    }

    public void setSize(int i) {
        this.field_5_size = i;
    }

    public int getRef() {
        return this.field_6_ref;
    }

    public void setRef(int i) {
        this.field_6_ref = i;
    }

    public int getOffset() {
        return this.field_7_offset;
    }

    public void setOffset(int i) {
        this.field_7_offset = i;
    }

    public byte getUsage() {
        return this.field_8_usage;
    }

    public void setUsage(byte b) {
        this.field_8_usage = b;
    }

    public byte getName() {
        return this.field_9_name;
    }

    public void setName(byte b) {
        this.field_9_name = b;
    }

    public byte getUnused2() {
        return this.field_10_unused2;
    }

    public void setUnused2(byte b) {
        this.field_10_unused2 = b;
    }

    public byte getUnused3() {
        return this.field_11_unused3;
    }

    public void setUnused3(byte b) {
        this.field_11_unused3 = b;
    }

    public byte[] getRemainingData() {
        return this.remainingData;
    }

    public void setRemainingData(byte[] bArr) {
        this.remainingData = bArr;
    }

    public String toString() {
        String string;
        String property = System.getProperty("line.separator");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            HexDump.dump(this.remainingData, 0L, byteArrayOutputStream, 0);
            string = byteArrayOutputStream.toString();
        } catch (Exception e) {
            string = e.toString();
        }
        return new StringBuffer().append(getClass().getName()).append(":").append(property).append("  RecordId: 0x").append(HexDump.toHex(RECORD_ID)).append(property).append("  Options: 0x").append(HexDump.toHex(getOptions())).append(property).append("  BlipTypeWin32: ").append((int) this.field_1_blipTypeWin32).append(property).append("  BlipTypeMacOS: ").append((int) this.field_2_blipTypeMacOS).append(property).append("  SUID: ").append(HexDump.toHex(this.field_3_uid)).append(property).append("  Tag: ").append((int) this.field_4_tag).append(property).append("  Size: ").append(this.field_5_size).append(property).append("  Ref: ").append(this.field_6_ref).append(property).append("  Offset: ").append(this.field_7_offset).append(property).append("  Usage: ").append((int) this.field_8_usage).append(property).append("  Name: ").append((int) this.field_9_name).append(property).append("  Unused2: ").append((int) this.field_10_unused2).append(property).append("  Unused3: ").append((int) this.field_11_unused3).append(property).append("  Extra Data:").append(property).append(string).toString();
    }
}
