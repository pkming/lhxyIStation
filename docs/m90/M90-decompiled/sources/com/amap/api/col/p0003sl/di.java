package com.amap.api.col.p0003sl;

import android.opengl.GLSurfaceView;
import com.amap.api.col.p0003sl.y;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: compiled from: GlMapSurfaceEglContextFactory.java */
/* JADX INFO: loaded from: classes2.dex */
public class di implements GLSurfaceView.EGLContextFactory, y.f {
    @Override // android.opengl.GLSurfaceView.EGLContextFactory
    public EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
        return null;
    }

    @Override // android.opengl.GLSurfaceView.EGLContextFactory
    public void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
    }
}
