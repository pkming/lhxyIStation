package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/* JADX INFO: loaded from: classes3.dex */
public class UUEncoder {
    protected static final int DEFAULT_MODE = 644;
    private static final int INPUT_BUFFER_SIZE = 4500;
    private static final int MAX_CHARS_PER_LINE = 45;
    private String name;
    private OutputStream out;

    public UUEncoder(String str) {
        this.name = str;
    }

    public void encode(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.out = outputStream;
        encodeBegin();
        byte[] bArr = new byte[INPUT_BUFFER_SIZE];
        while (true) {
            int i = 0;
            int i2 = inputStream.read(bArr, 0, INPUT_BUFFER_SIZE);
            if (i2 == -1) {
                outputStream.flush();
                encodeEnd();
                return;
            } else {
                while (i2 > 0) {
                    int i3 = i2 <= 45 ? i2 : 45;
                    encodeLine(bArr, i, i3, outputStream);
                    i += i3;
                    i2 -= i3;
                }
            }
        }
    }

    private void encodeString(String str) throws IOException {
        PrintStream printStream = new PrintStream(this.out);
        printStream.print(str);
        printStream.flush();
    }

    private void encodeBegin() throws IOException {
        encodeString("begin 644 " + this.name + "\n");
    }

    private void encodeEnd() throws IOException {
        encodeString(" \nend\n");
    }

    private void encodeLine(byte[] bArr, int i, int i2, OutputStream outputStream) throws IOException {
        byte b;
        outputStream.write((byte) ((i2 & 63) + 32));
        int i3 = 0;
        while (i3 < i2) {
            int i4 = i3 + 1;
            byte b2 = bArr[i3 + i];
            byte b3 = 1;
            if (i4 < i2) {
                int i5 = i4 + 1;
                byte b4 = bArr[i4 + i];
                if (i5 < i2) {
                    int i6 = i5 + 1;
                    b = bArr[i5 + i];
                    b3 = b4;
                    i4 = i6;
                } else {
                    b3 = b4;
                    i4 = i5;
                    b = 1;
                }
            } else {
                b = 1;
            }
            byte b5 = (byte) (((b2 >>> 2) & 63) + 32);
            outputStream.write(b5);
            outputStream.write((byte) ((((b2 << 4) & 48) | ((b3 >>> 4) & 15)) + 32));
            outputStream.write((byte) ((((b3 << 2) & 60) | ((b >>> 6) & 3)) + 32));
            outputStream.write((byte) ((b & 63) + 32));
            i3 = i4;
        }
        outputStream.write(10);
    }
}
