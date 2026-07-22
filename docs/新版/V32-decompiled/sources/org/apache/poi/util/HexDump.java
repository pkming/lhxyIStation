package org.apache.poi.util;

import android.text.format.DateFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/* JADX INFO: loaded from: classes3.dex */
public class HexDump {
    public static final String EOL = System.getProperty("line.separator");
    private static final char[] _hexcodes = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.CAPITAL_AM_PM, 'B', 'C', 'D', DateFormat.DAY, 'F'};
    private static final int[] _shifts = {28, 24, 20, 16, 12, 8, 4, 0};

    private HexDump() {
    }

    public static synchronized void dump(byte[] bArr, long j, OutputStream outputStream, int i, int i2) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (i >= 0) {
            if (bArr.length == 0 || i < bArr.length) {
                if (bArr.length == 0) {
                    return;
                }
                if (outputStream == null) {
                    throw new IllegalArgumentException("cannot write to nullstream");
                }
                long j2 = j + ((long) i);
                StringBuffer stringBuffer = new StringBuffer(74);
                int iMin = Math.min(bArr.length, i2 + i);
                while (i < iMin) {
                    int i3 = iMin - i;
                    if (i3 > 16) {
                        i3 = 16;
                    }
                    stringBuffer.append(dump(j2)).append(' ');
                    for (int i4 = 0; i4 < 16; i4++) {
                        if (i4 < i3) {
                            stringBuffer.append(dump(bArr[i4 + i]));
                        } else {
                            stringBuffer.append("  ");
                        }
                        stringBuffer.append(' ');
                    }
                    for (int i5 = 0; i5 < i3; i5++) {
                        int i6 = i5 + i;
                        if (bArr[i6] >= 32 && bArr[i6] < 127) {
                            stringBuffer.append((char) bArr[i6]);
                        } else {
                            stringBuffer.append('.');
                        }
                    }
                    stringBuffer.append(EOL);
                    outputStream.write(stringBuffer.toString().getBytes());
                    outputStream.flush();
                    stringBuffer.setLength(0);
                    j2 += (long) i3;
                    i += 16;
                }
                return;
            }
        }
        throw new ArrayIndexOutOfBoundsException(new StringBuffer().append("illegal index: ").append(i).append(" into array of length ").append(bArr.length).toString());
    }

    public static synchronized void dump(byte[] bArr, long j, OutputStream outputStream, int i) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        dump(bArr, j, outputStream, i, bArr.length - i);
    }

    public static String dump(byte[] bArr, long j, int i) {
        if (i < 0 || i >= bArr.length) {
            throw new ArrayIndexOutOfBoundsException(new StringBuffer().append("illegal index: ").append(i).append(" into array of length ").append(bArr.length).toString());
        }
        long j2 = j + ((long) i);
        StringBuffer stringBuffer = new StringBuffer(74);
        for (int i2 = i; i2 < bArr.length; i2 += 16) {
            int length = bArr.length - i2;
            int i3 = length > 16 ? 16 : length;
            stringBuffer.append(dump(j2)).append(' ');
            for (int i4 = 0; i4 < 16; i4++) {
                if (i4 < i3) {
                    stringBuffer.append(dump(bArr[i4 + i2]));
                } else {
                    stringBuffer.append("  ");
                }
                stringBuffer.append(' ');
            }
            for (int i5 = 0; i5 < i3; i5++) {
                int i6 = i5 + i2;
                if (bArr[i6] >= 32 && bArr[i6] < 127) {
                    stringBuffer.append((char) bArr[i6]);
                } else {
                    stringBuffer.append('.');
                }
            }
            stringBuffer.append(EOL);
            j2 += (long) i3;
        }
        return stringBuffer.toString();
    }

    private static String dump(long j) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.setLength(0);
        for (int i = 0; i < 8; i++) {
            stringBuffer.append(_hexcodes[((int) (j >> _shifts[i])) & 15]);
        }
        return stringBuffer.toString();
    }

    private static String dump(byte b) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.setLength(0);
        for (int i = 0; i < 2; i++) {
            stringBuffer.append(_hexcodes[(b >> _shifts[i + 6]) & 15]);
        }
        return stringBuffer.toString();
    }

    public static String toHex(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        for (byte b : bArr) {
            stringBuffer.append(toHex(b));
            stringBuffer.append(", ");
        }
        stringBuffer.append(']');
        return stringBuffer.toString();
    }

    public static String toHex(short s) {
        return toHex(s, 4);
    }

    public static String toHex(byte b) {
        return toHex(b, 2);
    }

    public static String toHex(int i) {
        return toHex(i, 8);
    }

    private static String toHex(long j, int i) {
        StringBuffer stringBuffer = new StringBuffer(i);
        for (int i2 = 0; i2 < i; i2++) {
            stringBuffer.append(_hexcodes[(int) ((j >> _shifts[(8 - i) + i2]) & 15)]);
        }
        return stringBuffer.toString();
    }

    public static void dump(InputStream inputStream, PrintStream printStream, int i, int i2) throws IOException {
        int i3;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (i2 != -1) {
            while (true) {
                int i4 = i2 - 1;
                if (i2 <= 0 || (i3 = inputStream.read()) == -1) {
                    break;
                }
                byteArrayOutputStream.write(i3);
                i2 = i4;
            }
        } else {
            int i5 = inputStream.read();
            while (i5 != -1) {
                byteArrayOutputStream.write(i5);
                i5 = inputStream.read();
            }
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        dump(byteArray, 0L, printStream, i, byteArray.length);
    }
}
