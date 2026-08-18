package org.apache.tools.zip;

/* JADX INFO: loaded from: classes3.dex */
public final class ZipShort implements Cloneable {
    private static final int BYTE_1_MASK = 65280;
    private static final int BYTE_1_SHIFT = 8;
    private final int value;

    public static byte[] getBytes(int i) {
        return new byte[]{(byte) (i & 255), (byte) ((i & 65280) >> 8)};
    }

    public ZipShort(int i) {
        this.value = i;
    }

    public ZipShort(byte[] bArr) {
        this(bArr, 0);
    }

    public ZipShort(byte[] bArr, int i) {
        this.value = getValue(bArr, i);
    }

    public byte[] getBytes() {
        int i = this.value;
        return new byte[]{(byte) (i & 255), (byte) ((i & 65280) >> 8)};
    }

    public int getValue() {
        return this.value;
    }

    public static int getValue(byte[] bArr, int i) {
        return ((bArr[i + 1] << 8) & 65280) + (bArr[i] & 255);
    }

    public static int getValue(byte[] bArr) {
        return getValue(bArr, 0);
    }

    public boolean equals(Object obj) {
        return obj != null && (obj instanceof ZipShort) && this.value == ((ZipShort) obj).getValue();
    }

    public int hashCode() {
        return this.value;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "ZipShort value: " + this.value;
    }
}
