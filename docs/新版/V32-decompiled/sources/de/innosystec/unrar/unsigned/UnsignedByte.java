package de.innosystec.unrar.unsigned;

/* JADX INFO: loaded from: classes2.dex */
public class UnsignedByte {
    public static short add(byte b, byte b2) {
        return (short) (b + b2);
    }

    public static byte intToByte(int i) {
        return (byte) (i & 255);
    }

    public static byte longToByte(long j) {
        return (byte) (j & 255);
    }

    public static byte shortToByte(short s) {
        return (byte) (s & 255);
    }

    public static short sub(byte b, byte b2) {
        return (short) (b - b2);
    }

    public static void main(String[] strArr) {
        System.out.println((int) add((byte) -2, (byte) 1));
        System.out.println((int) add((byte) -1, (byte) 1));
        System.out.println((int) add((byte) 127, (byte) 1));
        System.out.println((int) add((byte) -1, (byte) -1));
        System.out.println((int) sub((byte) -2, (byte) 1));
        System.out.println((int) sub((byte) 0, (byte) 1));
        System.out.println((int) sub((byte) -128, (byte) 1));
        System.out.println(1);
    }
}
