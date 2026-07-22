package org.apache.poi.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
public class HexRead {
    public static byte[] readData(String str) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(str));
        try {
            return readData(fileInputStream, -1);
        } finally {
            fileInputStream.close();
        }
    }

    public static byte[] readData(String str, String str2) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(str));
        try {
            StringBuffer stringBuffer = new StringBuffer();
            boolean z = false;
            for (int i = fileInputStream.read(); i != -1; i = fileInputStream.read()) {
                if (i == 10 || i == 13) {
                    stringBuffer = new StringBuffer();
                } else {
                    if (i == 91) {
                        z = true;
                    } else if (i == 93) {
                        if (stringBuffer.toString().equals(str2)) {
                            return readData(fileInputStream, 91);
                        }
                        stringBuffer = new StringBuffer();
                    } else if (z) {
                        stringBuffer.append((char) i);
                    }
                }
                z = false;
            }
            fileInputStream.close();
            throw new IOException(new StringBuffer().append("Section '").append(str2).append("' not found").toString());
        } finally {
            fileInputStream.close();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:50:0x0009, code lost:
    
        continue;
     */
    /* JADX WARN: Failed to find 'out' block for switch in B:18:0x0037. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:19:0x003a. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:20:0x003d. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0050 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0009 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:6:0x000c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static byte[] readData(java.io.InputStream r9, int r10) throws java.io.IOException {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
            r2 = r1
            r3 = r2
        L8:
            r4 = r3
        L9:
            if (r2 == 0) goto Lc
            goto L14
        Lc:
            int r5 = r9.read()
            r6 = 97
            if (r5 != r10) goto L2f
        L14:
            java.lang.Byte[] r9 = new java.lang.Byte[r1]
            java.lang.Object[] r9 = r0.toArray(r9)
            r7 = r9
            java.lang.Byte[] r7 = (java.lang.Byte[]) r7
            int r9 = r7.length
            byte[] r8 = new byte[r9]
        L20:
            int r9 = r7.length
            if (r1 < r9) goto L24
            return r8
        L24:
            r9 = r7[r1]
            byte r9 = r9.byteValue()
            r8[r1] = r9
            int r1 = r1 + 1
            goto L20
        L2f:
            r7 = -1
            if (r5 == r7) goto L73
            r7 = 35
            if (r5 == r7) goto L6f
            r7 = 2
            switch(r5) {
                case 48: goto L5a;
                case 49: goto L5a;
                case 50: goto L5a;
                case 51: goto L5a;
                case 52: goto L5a;
                case 53: goto L5a;
                case 54: goto L5a;
                case 55: goto L5a;
                case 56: goto L5a;
                case 57: goto L5a;
                default: goto L3a;
            }
        L3a:
            switch(r5) {
                case 65: goto L41;
                case 66: goto L41;
                case 67: goto L41;
                case 68: goto L41;
                case 69: goto L41;
                case 70: goto L41;
                default: goto L3d;
            }
        L3d:
            switch(r5) {
                case 97: goto L43;
                case 98: goto L43;
                case 99: goto L43;
                case 100: goto L43;
                case 101: goto L43;
                case 102: goto L43;
                default: goto L40;
            }
        L40:
            goto L9
        L41:
            r6 = 65
        L43:
            int r3 = r3 << 4
            byte r3 = (byte) r3
            int r5 = r5 + 10
            int r5 = r5 - r6
            byte r5 = (byte) r5
            int r3 = r3 + r5
            byte r3 = (byte) r3
            int r4 = r4 + 1
            if (r4 != r7) goto L9
            java.lang.Byte r4 = new java.lang.Byte
            r4.<init>(r3)
            r0.add(r4)
        L58:
            r3 = r1
            goto L8
        L5a:
            int r3 = r3 << 4
            byte r3 = (byte) r3
            int r5 = r5 + (-48)
            byte r5 = (byte) r5
            int r3 = r3 + r5
            byte r3 = (byte) r3
            int r4 = r4 + 1
            if (r4 != r7) goto L9
            java.lang.Byte r4 = new java.lang.Byte
            r4.<init>(r3)
            r0.add(r4)
            goto L58
        L6f:
            readToEOL(r9)
            goto L9
        L73:
            r2 = 1
            goto L9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.util.HexRead.readData(java.io.InputStream, int):byte[]");
    }

    public static byte[] readFromString(String str) throws IOException {
        return readData(new ByteArrayInputStream(str.getBytes()), -1);
    }

    private static void readToEOL(InputStream inputStream) throws IOException {
        int i = inputStream.read();
        while (i != -1 && i != 10 && i != 13) {
            i = inputStream.read();
        }
    }
}
