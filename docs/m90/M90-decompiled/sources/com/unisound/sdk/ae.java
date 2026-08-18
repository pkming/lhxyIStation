package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
public class ae {
    public static boolean a = true;
    private boolean b = true;

    public void a() {
        this.b = true;
    }

    public boolean a(byte[] bArr, int i, int i2) {
        if (this.b && a) {
            int i3 = 0;
            while (i < i2) {
                if (bArr[i] != 0) {
                    i3++;
                }
                i++;
            }
            if (i3 > 1) {
                this.b = false;
                return true;
            }
        }
        return false;
    }

    public boolean b() {
        return (this.b && a) ? false : true;
    }
}
