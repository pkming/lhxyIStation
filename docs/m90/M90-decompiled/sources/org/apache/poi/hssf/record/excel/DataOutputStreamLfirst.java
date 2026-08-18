package org.apache.poi.hssf.record.excel;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/* JADX INFO: loaded from: classes3.dex */
public class DataOutputStreamLfirst extends FilterOutputStream implements DataOutput {
    protected int written;

    public DataOutputStreamLfirst(OutputStream outputStream) {
        super(outputStream);
    }

    private void incCount(int i) {
        int i2 = this.written + i;
        if (i2 < 0) {
            i2 = Integer.MAX_VALUE;
        }
        this.written = i2;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.DataOutput
    public synchronized void write(int i) throws IOException {
        this.out.write(i);
        incCount(1);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.DataOutput
    public synchronized void write(byte[] bArr, int i, int i2) throws IOException {
        this.out.write(bArr, i, i2);
        incCount(i2);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.DataOutput
    public final void writeBoolean(boolean z) throws IOException {
        this.out.write(z ? 1 : 0);
        incCount(1);
    }

    @Override // java.io.DataOutput
    public final void writeByte(int i) throws IOException {
        this.out.write(i);
        incCount(1);
    }

    @Override // java.io.DataOutput
    public final void writeShort(int i) throws IOException {
        OutputStream outputStream = this.out;
        outputStream.write((i >>> 0) & 255);
        outputStream.write((i >>> 8) & 255);
        incCount(2);
    }

    @Override // java.io.DataOutput
    public final void writeChar(int i) throws IOException {
        OutputStream outputStream = this.out;
        outputStream.write((i >>> 0) & 255);
        outputStream.write((i >>> 8) & 255);
        incCount(2);
    }

    @Override // java.io.DataOutput
    public final void writeInt(int i) throws IOException {
        OutputStream outputStream = this.out;
        outputStream.write((i >>> 0) & 255);
        outputStream.write((i >>> 8) & 255);
        outputStream.write((i >>> 16) & 255);
        outputStream.write((i >>> 24) & 255);
        incCount(4);
    }

    @Override // java.io.DataOutput
    public final void writeLong(long j) throws IOException {
        OutputStream outputStream = this.out;
        outputStream.write(((int) (j >>> 0)) & 255);
        outputStream.write(((int) (j >>> 8)) & 255);
        outputStream.write(((int) (j >>> 16)) & 255);
        outputStream.write(((int) (j >>> 24)) & 255);
        outputStream.write(((int) (j >>> 32)) & 255);
        outputStream.write(((int) (j >>> 40)) & 255);
        outputStream.write(((int) (j >>> 48)) & 255);
        outputStream.write(((int) (j >>> 56)) & 255);
        incCount(8);
    }

    @Override // java.io.DataOutput
    public final void writeFloat(float f) throws IOException {
        writeInt(Float.floatToIntBits(f));
    }

    @Override // java.io.DataOutput
    public final void writeDouble(double d) throws IOException {
        writeLong(Double.doubleToLongBits(d));
    }

    @Override // java.io.DataOutput
    public final void writeBytes(String str) throws IOException {
        OutputStream outputStream = this.out;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            outputStream.write((byte) str.charAt(i));
        }
        incCount(length);
    }

    @Override // java.io.DataOutput
    public final void writeChars(String str) throws IOException {
        OutputStream outputStream = this.out;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            outputStream.write((cCharAt >>> 0) & 255);
            outputStream.write((cCharAt >>> '\b') & 255);
        }
        incCount(length * 2);
    }

    @Override // java.io.DataOutput
    public final void writeUTF(String str) throws IOException {
        writeUTF(str, this);
    }

    static int writeUTF(String str, DataOutput dataOutput) throws IOException {
        int i;
        int length = str.length();
        char[] cArr = new char[length];
        str.getChars(0, length, cArr, 0);
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3++) {
            char c = cArr[i3];
            i2 = (c < 1 || c > 127) ? c > 2047 ? i2 + 3 : i2 + 2 : i2 + 1;
        }
        if (i2 > 65535) {
            throw new UTFDataFormatException();
        }
        int i4 = i2 + 2;
        byte[] bArr = new byte[i4];
        bArr[0] = (byte) ((i2 >>> 0) & 255);
        bArr[1] = (byte) ((i2 >>> 8) & 255);
        int i5 = 2;
        for (int i6 = 0; i6 < length; i6++) {
            char c2 = cArr[i6];
            if (c2 >= 1 && c2 <= 127) {
                i = i5 + 1;
                bArr[i5] = (byte) c2;
            } else if (c2 > 2047) {
                int i7 = i5 + 1;
                bArr[i5] = (byte) (((c2 >> 0) & 63) | 128);
                int i8 = i7 + 1;
                bArr[i7] = (byte) (((c2 >> 6) & 63) | 128);
                i = i8 + 1;
                bArr[i8] = (byte) (((c2 >> '\f') & 15) | 224);
            } else {
                int i9 = i5 + 1;
                bArr[i5] = (byte) (((c2 >> 0) & 63) | 128);
                i5 = i9 + 1;
                bArr[i9] = (byte) (((c2 >> 6) & 31) | 192);
            }
            i5 = i;
        }
        dataOutput.write(bArr);
        return i4;
    }

    public final int size() {
        return this.written;
    }
}
