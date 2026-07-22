package org.apache.poi.ddf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherBlipRecord extends EscherRecord {
    private static final int HEADER_SIZE = 8;
    public static final String RECORD_DESCRIPTION = "msofbtBlip";
    public static final short RECORD_ID_END = -3817;
    public static final short RECORD_ID_START = -4072;
    private byte field_10_compressionFlag;
    private byte field_11_filter;
    private byte[] field_12_data;
    private byte[] field_1_secondaryUID;
    private int field_2_cacheOfSize;
    private int field_3_boundaryTop;
    private int field_4_boundaryLeft;
    private int field_5_boundaryWidth;
    private int field_6_boundaryHeight;
    private int field_7_width;
    private int field_8_height;
    private int field_9_cacheOfSavedSize;

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "Blip";
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] bArr, int i, EscherRecordFactory escherRecordFactory) {
        int header = readHeader(bArr, i);
        int i2 = i + 8;
        byte[] bArr2 = new byte[16];
        this.field_1_secondaryUID = bArr2;
        System.arraycopy(bArr, i2 + 0, bArr2, 0, 16);
        this.field_2_cacheOfSize = LittleEndian.getInt(bArr, i2 + 16);
        this.field_3_boundaryTop = LittleEndian.getInt(bArr, i2 + 20);
        this.field_4_boundaryLeft = LittleEndian.getInt(bArr, i2 + 24);
        this.field_5_boundaryWidth = LittleEndian.getInt(bArr, i2 + 28);
        this.field_6_boundaryHeight = LittleEndian.getInt(bArr, i2 + 32);
        this.field_7_width = LittleEndian.getInt(bArr, i2 + 36);
        this.field_8_height = LittleEndian.getInt(bArr, i2 + 40);
        this.field_9_cacheOfSavedSize = LittleEndian.getInt(bArr, i2 + 44);
        this.field_10_compressionFlag = bArr[i2 + 48];
        this.field_11_filter = bArr[i2 + 49];
        int i3 = header - 50;
        byte[] bArr3 = new byte[i3];
        this.field_12_data = bArr3;
        System.arraycopy(bArr, i2 + 50, bArr3, 0, i3);
        return i3 + 8 + header;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int i, byte[] bArr, EscherSerializationListener escherSerializationListener) {
        escherSerializationListener.beforeRecordSerialize(i, getRecordId(), this);
        LittleEndian.putShort(bArr, i, getOptions());
        LittleEndian.putShort(bArr, i + 2, getRecordId());
        LittleEndian.putInt(bArr, i + 4, this.field_12_data.length + 36);
        int i2 = i + 8;
        System.arraycopy(this.field_1_secondaryUID, 0, bArr, i2, 16);
        int i3 = i2 + 16;
        LittleEndian.putInt(bArr, i3, this.field_2_cacheOfSize);
        int i4 = i3 + 4;
        LittleEndian.putInt(bArr, i4, this.field_3_boundaryTop);
        int i5 = i4 + 4;
        LittleEndian.putInt(bArr, i5, this.field_4_boundaryLeft);
        int i6 = i5 + 4;
        LittleEndian.putInt(bArr, i6, this.field_5_boundaryWidth);
        int i7 = i6 + 4;
        LittleEndian.putInt(bArr, i7, this.field_6_boundaryHeight);
        int i8 = i7 + 4;
        LittleEndian.putInt(bArr, i8, this.field_7_width);
        int i9 = i8 + 4;
        LittleEndian.putInt(bArr, i9, this.field_8_height);
        int i10 = i9 + 4;
        LittleEndian.putInt(bArr, i10, this.field_9_cacheOfSavedSize);
        int i11 = i10 + 4;
        int i12 = i11 + 1;
        bArr[i11] = this.field_10_compressionFlag;
        int i13 = i12 + 1;
        bArr[i12] = this.field_11_filter;
        byte[] bArr2 = this.field_12_data;
        System.arraycopy(bArr2, 0, bArr, i13, bArr2.length);
        int length = i13 + this.field_12_data.length;
        int i14 = length - i;
        escherSerializationListener.afterRecordSerialize(length, getRecordId(), i14, this);
        return i14;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return this.field_12_data.length + 58;
    }

    public byte[] getSecondaryUID() {
        return this.field_1_secondaryUID;
    }

    public void setSecondaryUID(byte[] bArr) {
        this.field_1_secondaryUID = bArr;
    }

    public int getCacheOfSize() {
        return this.field_2_cacheOfSize;
    }

    public void setCacheOfSize(int i) {
        this.field_2_cacheOfSize = i;
    }

    public int getBoundaryTop() {
        return this.field_3_boundaryTop;
    }

    public void setBoundaryTop(int i) {
        this.field_3_boundaryTop = i;
    }

    public int getBoundaryLeft() {
        return this.field_4_boundaryLeft;
    }

    public void setBoundaryLeft(int i) {
        this.field_4_boundaryLeft = i;
    }

    public int getBoundaryWidth() {
        return this.field_5_boundaryWidth;
    }

    public void setBoundaryWidth(int i) {
        this.field_5_boundaryWidth = i;
    }

    public int getBoundaryHeight() {
        return this.field_6_boundaryHeight;
    }

    public void setBoundaryHeight(int i) {
        this.field_6_boundaryHeight = i;
    }

    public int getWidth() {
        return this.field_7_width;
    }

    public void setWidth(int i) {
        this.field_7_width = i;
    }

    public int getHeight() {
        return this.field_8_height;
    }

    public void setHeight(int i) {
        this.field_8_height = i;
    }

    public int getCacheOfSavedSize() {
        return this.field_9_cacheOfSavedSize;
    }

    public void setCacheOfSavedSize(int i) {
        this.field_9_cacheOfSavedSize = i;
    }

    public byte getCompressionFlag() {
        return this.field_10_compressionFlag;
    }

    public void setCompressionFlag(byte b) {
        this.field_10_compressionFlag = b;
    }

    public byte getFilter() {
        return this.field_11_filter;
    }

    public void setFilter(byte b) {
        this.field_11_filter = b;
    }

    public byte[] getData() {
        return this.field_12_data;
    }

    public void setData(byte[] bArr) {
        this.field_12_data = bArr;
    }

    public String toString() {
        String string;
        String property = System.getProperty("line.separator");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            HexDump.dump(this.field_12_data, 0L, byteArrayOutputStream, 0);
            string = byteArrayOutputStream.toString();
        } catch (Exception e) {
            string = e.toString();
        }
        return new StringBuffer().append(getClass().getName()).append(":").append(property).append("  RecordId: 0x").append(HexDump.toHex(getRecordId())).append(property).append("  Options: 0x").append(HexDump.toHex(getOptions())).append(property).append("  Secondary UID: ").append(HexDump.toHex(this.field_1_secondaryUID)).append(property).append("  CacheOfSize: ").append(this.field_2_cacheOfSize).append(property).append("  BoundaryTop: ").append(this.field_3_boundaryTop).append(property).append("  BoundaryLeft: ").append(this.field_4_boundaryLeft).append(property).append("  BoundaryWidth: ").append(this.field_5_boundaryWidth).append(property).append("  BoundaryHeight: ").append(this.field_6_boundaryHeight).append(property).append("  X: ").append(this.field_7_width).append(property).append("  Y: ").append(this.field_8_height).append(property).append("  CacheOfSavedSize: ").append(this.field_9_cacheOfSavedSize).append(property).append("  CompressionFlag: ").append((int) this.field_10_compressionFlag).append(property).append("  Filter: ").append((int) this.field_11_filter).append(property).append("  Data:").append(property).append(string).toString();
    }

    public static byte[] compress(byte[] bArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
        for (byte b : bArr) {
            try {
                deflaterOutputStream.write(b);
            } catch (IOException e) {
                throw new RecordFormatException(e.toString());
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decompress(byte[] bArr, int i, int i2) {
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i + 50, bArr2, 0, i2);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(bArr2));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            try {
                int i3 = inflaterInputStream.read();
                if (i3 != -1) {
                    byteArrayOutputStream.write(i3);
                } else {
                    return byteArrayOutputStream.toByteArray();
                }
            } catch (IOException e) {
                throw new RecordFormatException(e.toString());
            }
        }
    }
}
