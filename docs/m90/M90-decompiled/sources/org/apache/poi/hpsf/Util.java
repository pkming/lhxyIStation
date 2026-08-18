package org.apache.poi.hpsf;

import android.widget.ExpandableListView;
import java.util.Date;

/* JADX INFO: loaded from: classes3.dex */
public class Util {
    public static final long EPOCH_DIFF = 11644473600000L;

    public static boolean equal(byte[] bArr, byte[] bArr2) {
        if (bArr.length != bArr2.length) {
            return false;
        }
        for (int i = 0; i < bArr.length; i++) {
            if (bArr[i] != bArr2[i]) {
                return false;
            }
        }
        return true;
    }

    public static void copy(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        for (int i4 = 0; i4 < i2; i4++) {
            bArr2[i3 + i4] = bArr[i + i4];
        }
    }

    public static byte[] cat(byte[][] bArr) {
        int length = 0;
        for (byte[] bArr2 : bArr) {
            length += bArr2.length;
        }
        byte[] bArr3 = new byte[length];
        int i = 0;
        for (int i2 = 0; i2 < bArr.length; i2++) {
            int i3 = 0;
            while (i3 < bArr[i2].length) {
                bArr3[i] = bArr[i2][i3];
                i3++;
                i++;
            }
        }
        return bArr3;
    }

    public static byte[] copy(byte[] bArr, int i, int i2) {
        byte[] bArr2 = new byte[i2];
        copy(bArr, i, i2, bArr2, 0);
        return bArr2;
    }

    public static Date filetimeToDate(int i, int i2) {
        return new Date((((((long) i2) & ExpandableListView.PACKED_POSITION_VALUE_NULL) | (((long) i) << 32)) / 10000) - EPOCH_DIFF);
    }
}
