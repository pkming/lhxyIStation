package org.apache.poi.hpsf;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class Property {
    private static int CP_UNICODE = 1200;
    private int id;
    private long type;
    private Object value;

    public int getID() {
        return this.id;
    }

    public long getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    public Property(int i, byte[] bArr, long j, int i2, int i3) {
        this.id = i;
        if (i == 0) {
            this.value = readDictionary(bArr, j, i2, i3);
            return;
        }
        int i4 = (int) j;
        long uInt = LittleEndian.getUInt(bArr, i4);
        this.type = uInt;
        try {
            this.value = TypeReader.read(bArr, i4 + 4, i2, (int) uInt);
        } catch (Throwable th) {
            th.printStackTrace();
            this.value = "*** null ***";
        }
    }

    protected Map readDictionary(byte[] bArr, long j, int i, int i2) {
        long j2;
        if (j < 0 || j > bArr.length) {
            throw new HPSFRuntimeException(new StringBuffer().append("Illegal offset ").append(j).append(" while HPSF stream contains ").append(i).append(" bytes.").toString());
        }
        int i3 = (int) j;
        long uInt = LittleEndian.getUInt(bArr, i3);
        int i4 = i3 + 4;
        HashMap map = new HashMap((int) uInt, 1.0f);
        for (int i5 = 0; i5 < uInt; i5++) {
            Long l = new Long(LittleEndian.getUInt(bArr, i4));
            int i6 = i4 + 4;
            long uInt2 = LittleEndian.getUInt(bArr, i6);
            int i7 = i6 + 4;
            StringBuffer stringBuffer = new StringBuffer((int) uInt2);
            for (int i8 = 0; i8 < uInt2; i8++) {
                if (i2 == CP_UNICODE) {
                    int i9 = (i8 * 2) + i7;
                    stringBuffer.append((char) ((bArr[i9 + 1] << 8) + bArr[i9]));
                } else {
                    stringBuffer.append((char) bArr[i7 + i8]);
                }
            }
            while (stringBuffer.charAt(stringBuffer.length() - 1) == 0) {
                stringBuffer.setLength(stringBuffer.length() - 1);
            }
            if (i2 == CP_UNICODE) {
                if (uInt2 % 2 == 1) {
                    uInt2++;
                }
                j2 = i7;
                uInt2 += uInt2;
            } else {
                j2 = i7;
            }
            i4 = (int) (j2 + uInt2);
            map.put(l, stringBuffer.toString());
        }
        return map;
    }
}
