package org.apache.poi.util;

import org.apache.poi.hssf.record.FilePassRecord;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;

/* JADX INFO: loaded from: classes3.dex */
public class PwdUtils {
    static byte[] seeds = {-69, -1, -1, -70, -1, -1, -71, -128, 0, -66, HSSFErrorConstants.ERROR_VALUE, 0, -65, HSSFErrorConstants.ERROR_VALUE, 0};

    public static short getPasswordHash(byte[] bArr) {
        int i = 0;
        short s = 0;
        while (i < bArr.length) {
            byte b = bArr[i];
            i++;
            int i2 = b << i;
            s = (short) (s ^ ((short) ((i2 >> 15) | (i2 & 32767))));
        }
        return (short) (((short) (((short) bArr.length) ^ s)) ^ (-12725));
    }

    public static short getEncryptionKey(byte[] bArr) {
        int i = 32768;
        int i2 = 0;
        int i3 = 65535;
        for (int i4 = 0; i4 < bArr.length; i4++) {
            byte b = (byte) (bArr[(bArr.length - 1) - i4] & 127);
            for (int i5 = 0; i5 < 8; i5++) {
                i = (i >> 15) | ((i << 1) & 65535);
                if ((i & 1) == 1) {
                    i = (i ^ 4128) & 65535;
                }
                i3 = (i3 >> 15) | ((i3 << 1) & 65535);
                if ((i3 & 1) == 1) {
                    i3 = (i3 ^ 4128) & 65535;
                }
                if ((b & 1) == 1) {
                    i2 = (i2 ^ i) & 65535;
                }
                b = (byte) (b >> 1);
            }
        }
        return (short) ((i2 ^ i3) & 65535 & 65535);
    }

    public static byte[] getKeySequence(byte[] bArr, short s) {
        byte[] bArr2 = new byte[16];
        int length = bArr.length;
        System.arraycopy(bArr, 0, bArr2, 0, length);
        System.arraycopy(seeds, 0, bArr2, length, 16 - length);
        byte b = (byte) (s & 255);
        byte b2 = (byte) ((s >> 8) & 255);
        for (int i = 0; i < 16; i += 2) {
            bArr2[i] = (byte) (bArr2[i] ^ b);
            int i2 = i + 1;
            bArr2[i2] = (byte) (bArr2[i2] ^ b2);
        }
        for (int i3 = 0; i3 < 16; i3++) {
            bArr2[i3] = (byte) ((bArr2[i3] << 2) | ((bArr2[i3] >> 6) & 3));
        }
        return bArr2;
    }

    public static void writeRecord(int i, byte[] bArr, int i2, int i3, byte[] bArr2) {
        int i4 = (i + i2 + i3) & 15;
        for (int i5 = 0; i5 < i3 - i2; i5++) {
            int i6 = i5 + i + i2;
            byte b = (byte) (bArr[i6] ^ bArr2[i4]);
            bArr[i6] = (byte) ((b << 5) | ((b >> 3) & 31));
            i4 = (i4 + 1) & 15;
        }
    }

    public static void readRecord(int i, byte[] bArr, int i2, int i3, byte[] bArr2) {
        int i4 = (i + i2 + i3) & 15;
        for (int i5 = 0; i5 < i3 - i2; i5++) {
            int i6 = i5 + i + i2;
            byte b = bArr[i6];
            bArr[i6] = (byte) (((byte) (((b >> 5) & 7) | (b << 3))) ^ bArr2[i4]);
            i4 = (i4 + 1) & 15;
        }
    }

    public static void encrypt(byte[] bArr, FilePassRecord filePassRecord) {
        byte[] keySequence = getKeySequence(filePassRecord.getPassword().getBytes(), filePassRecord.getKey());
        int i = 0;
        while (i < bArr.length) {
            short s = LittleEndian.getShort(bArr, i + 0);
            short s2 = LittleEndian.getShort(bArr, i + 2);
            if (s == 133) {
                writeRecord(i + 4, bArr, 4, s2, keySequence);
            } else if (s != 2057 && s != 47 && s != 225) {
                writeRecord(i + 4, bArr, 0, s2, keySequence);
            }
            i += s2 + 4;
        }
    }
}
