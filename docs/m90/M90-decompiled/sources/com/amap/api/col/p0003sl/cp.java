package com.amap.api.col.p0003sl;

import android.opengl.GLES20;

/* JADX INFO: compiled from: GlShader.java */
/* JADX INFO: loaded from: classes2.dex */
public class cp {
    public int a;
    public int b;
    public int c;
    private boolean d;

    public final void a() {
        int i = this.a;
        if (i >= 0) {
            GLES20.glDeleteProgram(i);
        }
        int i2 = this.b;
        if (i2 >= 0) {
            GLES20.glDeleteShader(i2);
        }
        int i3 = this.c;
        if (i3 >= 0) {
            GLES20.glDeleteShader(i3);
        }
        this.d = true;
    }
}
