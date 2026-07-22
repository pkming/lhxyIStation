package com.amap.api.col.p0003sl;

import android.opengl.EGL14;
import com.autonavi.base.amap.api.mapcore.IGLSurfaceView;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: compiled from: GlesUtility.java */
/* JADX INFO: loaded from: classes2.dex */
public final class dj {
    public static void a(IGLSurfaceView iGLSurfaceView, int i, int i2, int i3) {
        iGLSurfaceView.setEGLContextFactory(new b());
        iGLSurfaceView.setEGLConfigChooser(new a(i, i2, i3));
    }

    /* JADX INFO: compiled from: GlesUtility.java */
    public static class b extends di {
        @Override // com.amap.api.col.p0003sl.di, android.opengl.GLSurfaceView.EGLContextFactory
        public final EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
            try {
                return egl10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, EGL14.EGL_NONE});
            } catch (Throwable th) {
                th.printStackTrace();
                return null;
            }
        }

        @Override // com.amap.api.col.p0003sl.di, android.opengl.GLSurfaceView.EGLContextFactory
        public final void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
            egl10.eglDestroyContext(eGLDisplay, eGLContext);
        }
    }

    /* JADX INFO: compiled from: GlesUtility.java */
    private static class c {
        public int[] a;
        public int[] b;

        private c() {
            this.a = null;
            this.b = new int[1];
        }

        /* synthetic */ c(byte b) {
            this();
        }
    }

    /* JADX INFO: compiled from: GlesUtility.java */
    public static class a extends dh {
        private static int g = 4;
        protected int a;
        protected int b;
        protected int c;
        private int[] h = new int[1];
        protected int d = 0;
        protected int e = 16;
        protected int f = 8;

        public a(int i, int i2, int i3) {
            this.a = i;
            this.b = i2;
            this.c = i3;
        }

        private int[] a(boolean z) {
            return new int[]{EGL14.EGL_RED_SIZE, this.a, EGL14.EGL_GREEN_SIZE, this.b, EGL14.EGL_BLUE_SIZE, this.c, EGL14.EGL_ALPHA_SIZE, this.d, EGL14.EGL_DEPTH_SIZE, this.e, EGL14.EGL_STENCIL_SIZE, this.f, EGL14.EGL_SAMPLE_BUFFERS, z ? 1 : 0, EGL14.EGL_RENDERABLE_TYPE, g, EGL14.EGL_NONE};
        }

        private c a(EGL10 egl10, EGLDisplay eGLDisplay) {
            c cVar = new c((byte) 0);
            cVar.a = a(true);
            egl10.eglChooseConfig(eGLDisplay, cVar.a, null, 0, cVar.b);
            if (cVar.b[0] <= 0) {
                cVar.a = a(false);
                egl10.eglChooseConfig(eGLDisplay, cVar.a, null, 0, cVar.b);
                if (cVar.b[0] <= 0) {
                    return null;
                }
            }
            return cVar;
        }

        @Override // com.amap.api.col.p0003sl.dh, android.opengl.GLSurfaceView.EGLConfigChooser
        public final EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay) {
            c cVarA = a(egl10, eGLDisplay);
            if (cVarA == null || cVarA.a == null) {
                return null;
            }
            EGLConfig[] eGLConfigArr = new EGLConfig[cVarA.b[0]];
            egl10.eglChooseConfig(eGLDisplay, cVarA.a, eGLConfigArr, cVarA.b[0], cVarA.b);
            EGLConfig eGLConfigA = a(egl10, eGLDisplay, eGLConfigArr);
            if (eGLConfigA != null) {
                return eGLConfigA;
            }
            this.a = 8;
            this.b = 8;
            this.c = 8;
            c cVarA2 = a(egl10, eGLDisplay);
            if (cVarA2 == null || cVarA2.a == null) {
                return eGLConfigA;
            }
            EGLConfig[] eGLConfigArr2 = new EGLConfig[cVarA2.b[0]];
            egl10.eglChooseConfig(eGLDisplay, cVarA2.a, eGLConfigArr2, cVarA2.b[0], cVarA2.b);
            return a(egl10, eGLDisplay, eGLConfigArr2);
        }

        private EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
            for (EGLConfig eGLConfig : eGLConfigArr) {
                int iA = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_DEPTH_SIZE);
                int iA2 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_STENCIL_SIZE);
                if (iA >= this.e && iA2 >= this.f) {
                    int iA3 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_RED_SIZE);
                    int iA4 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_GREEN_SIZE);
                    int iA5 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_BLUE_SIZE);
                    int iA6 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_ALPHA_SIZE);
                    if (iA3 == this.a && iA4 == this.b && iA5 == this.c && iA6 == this.d) {
                        return eGLConfig;
                    }
                }
            }
            return null;
        }

        private int a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i) {
            if (egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.h)) {
                return this.h[0];
            }
            return 0;
        }
    }
}
