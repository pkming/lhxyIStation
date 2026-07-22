package de.innosystec.unrar.unpack.vm;

/* JADX INFO: loaded from: classes2.dex */
public class BitInput {
    public static final int MAX_SIZE = 32768;
    protected int inAddr;
    protected int inBit;
    protected byte[] inBuf = new byte[32768];

    public void InitBitInput() {
        this.inAddr = 0;
        this.inBit = 0;
    }

    public void addbits(int i) {
        int i2 = i + this.inBit;
        this.inAddr += i2 >> 3;
        this.inBit = i2 & 7;
    }

    public int getbits() {
        byte[] bArr = this.inBuf;
        int i = this.inAddr;
        return (((((bArr[i] & 255) << 16) + ((bArr[i + 1] & 255) << 8)) + (bArr[i + 2] & 255)) >>> (8 - this.inBit)) & 65535;
    }

    public void faddbits(int i) {
        addbits(i);
    }

    public int fgetbits() {
        return getbits();
    }

    public boolean Overflow(int i) {
        return this.inAddr + i >= 32768;
    }

    public byte[] getInBuf() {
        return this.inBuf;
    }
}
