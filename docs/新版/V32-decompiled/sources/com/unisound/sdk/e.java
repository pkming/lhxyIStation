package com.unisound.sdk;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class e {
    private static final int a = 1024;
    private short[] b = new short[1024];
    private int c;

    /* JADX WARN: Multi-variable type inference failed */
    public ArrayList<short[]> a(short[] sArr, int i) {
        ArrayList<short[]> arrayList = new ArrayList<>(4);
        int i2 = 0;
        while (i > 0) {
            int i3 = this.c;
            if (i <= 1024 - i3) {
                System.arraycopy(sArr, i2, this.b, i3, i);
                this.c += i;
                i2 += i;
                i -= i;
            } else {
                int i4 = 1024 - i3;
                System.arraycopy(sArr, i2, this.b, i3, i4);
                arrayList.add(this.b.clone());
                this.c = 0;
                i2 += i4;
                i -= i4;
            }
        }
        return arrayList;
    }

    public short[] a() {
        int i = this.c;
        if (i <= 0) {
            return null;
        }
        short[] sArr = new short[i];
        System.arraycopy(this.b, 0, sArr, 0, i);
        return sArr;
    }
}
