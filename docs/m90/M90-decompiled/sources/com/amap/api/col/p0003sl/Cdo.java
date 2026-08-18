package com.amap.api.col.p0003sl;

import android.graphics.Bitmap;

/* JADX INFO: renamed from: com.amap.api.col.3sl.do, reason: invalid class name */
/* JADX INFO: compiled from: PureScreenCheckTool.java */
/* JADX INFO: loaded from: classes2.dex */
public final class Cdo {
    private static boolean a = false;
    private static boolean b = false;
    private static boolean c = false;
    private boolean d = false;
    private int e = 0;
    private int f = 20;

    public static void a(boolean z) {
        a = z;
    }

    public static boolean a() {
        return a;
    }

    public static void b(boolean z) {
        b = z;
    }

    public static boolean b() {
        return b;
    }

    public static void c(boolean z) {
        c = z;
    }

    public static boolean c() {
        return c;
    }

    public final boolean d() {
        return this.d;
    }

    public final void e() {
        this.e++;
    }

    public final boolean f() {
        return this.e >= this.f;
    }

    public final boolean a(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int i = -1;
                for (int i2 = (int) (width / 4.0f); i2 < (width * 3) / 4.0f; i2++) {
                    for (int i3 = (int) (height / 4.0f); i3 < (height * 3) / 4.0f; i3++) {
                        int pixel = bitmap.getPixel(i2, i3);
                        if (i == -1) {
                            i = pixel;
                        }
                        if (pixel != i) {
                            return false;
                        }
                        if (pixel != -16777216) {
                            return false;
                        }
                    }
                }
            } finally {
                try {
                } finally {
                }
            }
        }
        return true;
    }

    public static void g() {
        jw.c(new Exception("BlackScreen"), "PureScreenCheckTool", "uploadInfo");
    }
}
