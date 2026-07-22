package de.innosystec.unrar.rarfile;

/* JADX INFO: loaded from: classes2.dex */
public class FileNameDecoder {
    public static int getChar(byte[] bArr, int i) {
        return bArr[i] & 255;
    }

    public static String decode(byte[] bArr, int i) {
        int i2;
        int i3 = i + 1;
        int i4 = getChar(bArr, i);
        StringBuffer stringBuffer = new StringBuffer();
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        while (i3 < bArr.length) {
            if (i5 == 0) {
                i6 = getChar(bArr, i3);
                i3++;
                i5 = 8;
            }
            int i8 = i6 >> 6;
            if (i8 == 0) {
                i2 = i3 + 1;
                stringBuffer.append((char) getChar(bArr, i3));
            } else if (i8 != 1) {
                if (i8 == 2) {
                    stringBuffer.append((char) ((getChar(bArr, i3 + 1) << 8) + getChar(bArr, i3)));
                    i7++;
                    i3 += 2;
                } else if (i8 == 3) {
                    i2 = i3 + 1;
                    int i9 = getChar(bArr, i3);
                    if ((i9 & 128) != 0) {
                        int i10 = i2 + 1;
                        int i11 = getChar(bArr, i2);
                        int i12 = (i9 & 127) + 2;
                        while (i12 > 0 && i7 < bArr.length) {
                            stringBuffer.append((char) ((i4 << 8) + ((getChar(bArr, i7) + i11) & 255)));
                            i12--;
                            i7++;
                        }
                        i3 = i10;
                    } else {
                        int i13 = i9 + 2;
                        while (i13 > 0 && i7 < bArr.length) {
                            stringBuffer.append((char) getChar(bArr, i7));
                            i13--;
                            i7++;
                        }
                        i3 = i2;
                    }
                }
                i6 = (i6 << 2) & 255;
                i5 -= 2;
            } else {
                i2 = i3 + 1;
                stringBuffer.append((char) (getChar(bArr, i3) + (i4 << 8)));
            }
            i7++;
            i3 = i2;
            i6 = (i6 << 2) & 255;
            i5 -= 2;
        }
        return stringBuffer.toString();
    }
}
