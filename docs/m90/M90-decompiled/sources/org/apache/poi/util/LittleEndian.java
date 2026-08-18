package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public class LittleEndian implements LittleEndianConsts {
    public static int ubyteToInt(byte b) {
        return (b & 128) == 0 ? b : (b & 127) + 128;
    }

    private LittleEndian() {
    }

    public static short getShort(byte[] bArr, int i) {
        return (short) getNumber(bArr, i, 2);
    }

    public static int getUShort(byte[] bArr, int i) {
        short number = (short) getNumber(bArr, i, 2);
        return number < 0 ? number + 65536 : number;
    }

    public static short[] getSimpleShortArray(byte[] bArr, int i, int i2) {
        short[] sArr = new short[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            sArr[i3] = getShort(bArr, i + 2 + (i3 * 2));
        }
        return sArr;
    }

    public static short[] getShortArray(byte[] bArr, int i) {
        return getSimpleShortArray(bArr, i, (short) getNumber(bArr, i, 2));
    }

    public static short getShort(byte[] bArr) {
        return getShort(bArr, 0);
    }

    public static int getUShort(byte[] bArr) {
        return getUShort(bArr, 0);
    }

    public static int getInt(byte[] bArr, int i) {
        return (int) getNumber(bArr, i, 4);
    }

    public static int getInt(byte[] bArr) {
        return getInt(bArr, 0);
    }

    public static long getUInt(byte[] bArr, int i) {
        int number = (int) getNumber(bArr, i, 4);
        return number < 0 ? ((long) number) + 4294967296L : number;
    }

    public static long getUInt(byte[] bArr) {
        return getUInt(bArr, 0);
    }

    public static long getLong(byte[] bArr, int i) {
        return getNumber(bArr, i, 8);
    }

    public static long getLong(byte[] bArr) {
        return getLong(bArr, 0);
    }

    public static double getDouble(byte[] bArr, int i) {
        return Double.longBitsToDouble(getNumber(bArr, i, 8));
    }

    public static double getDouble(byte[] bArr) {
        return getDouble(bArr, 0);
    }

    public static void putShort(byte[] bArr, int i, short s) {
        putNumber(bArr, i, s, 2);
    }

    public static void putShortArray(byte[] bArr, int i, short[] sArr) {
        putNumber(bArr, i, sArr.length, 2);
        for (int i2 = 0; i2 < sArr.length; i2++) {
            putNumber(bArr, i + 2 + (i2 * 2), sArr[i2], 2);
        }
    }

    public static void putUShort(byte[] bArr, int i, int i2) {
        putNumber(bArr, i, i2, 2);
    }

    public static void putShort(byte[] bArr, short s) {
        putShort(bArr, 0, s);
    }

    public static void putInt(byte[] bArr, int i, int i2) {
        putNumber(bArr, i, i2, 4);
    }

    public static void putInt(byte[] bArr, int i) {
        putInt(bArr, 0, i);
    }

    public static void putLong(byte[] bArr, int i, long j) {
        putNumber(bArr, i, j, 8);
    }

    public static void putLong(byte[] bArr, long j) {
        putLong(bArr, 0, j);
    }

    public static void putDouble(byte[] bArr, int i, double d) {
        if (Double.isNaN(d)) {
            putNumber(bArr, i, -276939487313920L, 8);
        } else {
            putNumber(bArr, i, Double.doubleToLongBits(d), 8);
        }
    }

    public static void putDouble(byte[] bArr, double d) {
        putDouble(bArr, 0, d);
    }

    public static class BufferUnderrunException extends IOException {
        BufferUnderrunException() {
            super("buffer underrun");
        }
    }

    public static short readShort(InputStream inputStream) throws IOException {
        return getShort(readFromStream(inputStream, 2));
    }

    public static int readInt(InputStream inputStream) throws IOException {
        return getInt(readFromStream(inputStream, 4));
    }

    public static long readLong(InputStream inputStream) throws IOException {
        return getLong(readFromStream(inputStream, 8));
    }

    public static byte[] readFromStream(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        int i2 = inputStream.read(bArr);
        if (i2 == -1) {
            Arrays.fill(bArr, (byte) 0);
        } else if (i2 != i) {
            throw new BufferUnderrunException();
        }
        return bArr;
    }

    private static long getNumber(byte[] bArr, int i, int i2) {
        long j = 0;
        for (int i3 = (i2 + i) - 1; i3 >= i; i3--) {
            j = (j << 8) | ((long) (bArr[i3] & 255));
        }
        return j;
    }

    private static void putNumber(byte[] bArr, int i, long j, int i2) {
        int i3 = i2 + i;
        while (i < i3) {
            bArr[i] = (byte) (255 & j);
            j >>= 8;
            i++;
        }
    }

    public static int getUnsignedByte(byte[] bArr, int i) {
        return (int) getNumber(bArr, i, 1);
    }

    public static int getUnsignedByte(byte[] bArr) {
        return getUnsignedByte(bArr, 0);
    }

    public static byte[] getByteArray(byte[] bArr, int i, int i2) {
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        return bArr2;
    }
}
