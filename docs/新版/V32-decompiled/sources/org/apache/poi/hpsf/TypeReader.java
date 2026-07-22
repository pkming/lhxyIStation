package org.apache.poi.hpsf;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class TypeReader {
    public static Object read(byte[] bArr, int i, int i2, int i3) {
        int i4 = i2 - 4;
        if (i3 == 0) {
            return null;
        }
        int i5 = 0;
        if (i3 == 11) {
            if (LittleEndian.getUInt(bArr, i) != 0) {
                return new Boolean(true);
            }
            return new Boolean(false);
        }
        if (i3 == 64) {
            return Util.filetimeToDate((int) LittleEndian.getUInt(bArr, i + 4), (int) LittleEndian.getUInt(bArr, i));
        }
        if (i3 == 71) {
            byte[] bArr2 = new byte[i4];
            while (i5 < i4) {
                bArr2[i5] = bArr[i + i5];
                i5++;
            }
            return bArr2;
        }
        if (i3 == 2) {
            return new Integer(LittleEndian.getUShort(bArr, i));
        }
        if (i3 == 3) {
            return new Long(LittleEndian.getUInt(bArr, i));
        }
        if (i3 == 30) {
            int i6 = i + 4;
            long j = i6;
            long uInt = LittleEndian.getUInt(bArr, i) + j;
            do {
                uInt--;
                if (bArr[(int) uInt] != 0) {
                    break;
                }
            } while (j <= uInt);
            return new String(bArr, i6, (int) ((uInt - j) + 1));
        }
        if (i3 == 31) {
            int i7 = i + 4;
            long j2 = i7;
            long uInt2 = ((LittleEndian.getUInt(bArr, i) + j2) - 1) - j2;
            StringBuffer stringBuffer = new StringBuffer((int) uInt2);
            while (i5 <= uInt2) {
                int i8 = (i5 * 2) + i7;
                stringBuffer.append((char) ((bArr[i8 + 1] << 8) + bArr[i8]));
                i5++;
            }
            while (stringBuffer.charAt(stringBuffer.length() - 1) == 0) {
                stringBuffer.setLength(stringBuffer.length() - 1);
            }
            return stringBuffer.toString();
        }
        byte[] bArr3 = new byte[i4];
        while (i5 < i4) {
            bArr3[i5] = bArr[i + i5];
            i5++;
        }
        return bArr3;
    }
}
