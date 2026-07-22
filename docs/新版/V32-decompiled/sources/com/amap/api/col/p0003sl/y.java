package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLDebugHelper;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;
import android.view.TextureView;
import com.amap.api.maps.MapsInitializer;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: compiled from: GLTextureView.java */
/* JADX INFO: loaded from: classes2.dex */
public class y extends TextureView implements TextureView.SurfaceTextureListener {
    private static final j a = new j(0);
    private final WeakReference<y> b;
    private i c;
    private GLSurfaceView.Renderer d;
    private boolean e;
    private e f;
    private f g;
    private g h;
    private k i;
    private int j;
    private int k;
    private boolean l;

    /* JADX INFO: compiled from: GLTextureView.java */
    public interface e {
        EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay);
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    public interface f {
        EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig);

        void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext);
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    public interface g {
        EGLSurface a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj);

        void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface);
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    public interface k {
        GL a();
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public y(Context context) {
        super(context, null);
        this.b = new WeakReference<>(this);
        a();
    }

    protected void finalize() throws Throwable {
        try {
            i iVar = this.c;
            if (iVar != null) {
                iVar.g();
            }
        } finally {
            super.finalize();
        }
    }

    private void a() {
        setSurfaceTextureListener(this);
    }

    public void setRenderer(GLSurfaceView.Renderer renderer) {
        e();
        if (this.f == null) {
            this.f = new m();
        }
        byte b2 = 0;
        if (this.g == null) {
            this.g = new c(this, b2);
        }
        if (this.h == null) {
            this.h = new d(b2);
        }
        this.d = renderer;
        i iVar = new i(this.b);
        this.c = iVar;
        iVar.start();
    }

    public final void a(f fVar) {
        e();
        this.g = fVar;
    }

    public final void a(e eVar) {
        e();
        this.f = eVar;
    }

    public void setRenderMode(int i2) {
        this.c.a(i2);
    }

    public void requestRender() {
        this.c.b();
    }

    public int getRenderMode() {
        return this.c.a();
    }

    public void b() {
        this.c.e();
    }

    public void c() {
        this.c.f();
    }

    public void queueEvent(Runnable runnable) {
        this.c.a(runnable);
    }

    @Override // android.view.TextureView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.e && this.d != null) {
            i iVar = this.c;
            int iA = iVar != null ? iVar.a() : 1;
            i iVar2 = new i(this.b);
            this.c = iVar2;
            if (iA != 1) {
                iVar2.a(iA);
            }
            this.c.start();
        }
        this.e = false;
    }

    @Override // android.view.TextureView, android.view.View
    protected void onDetachedFromWindow() {
        i iVar = this.c;
        if (iVar != null) {
            iVar.g();
        }
        this.e = true;
        super.onDetachedFromWindow();
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    private class c implements f {
        private c() {
        }

        /* synthetic */ c(y yVar, byte b) {
            this();
        }

        @Override // com.amap.api.col.3sl.y.f
        public final EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
            int[] iArr = {12440, y.this.k, EGL14.EGL_NONE};
            EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
            if (y.this.k == 0) {
                iArr = null;
            }
            return egl10.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
        }

        @Override // com.amap.api.col.3sl.y.f
        public final void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
            if (egl10.eglDestroyContext(eGLDisplay, eGLContext)) {
                return;
            }
            Log.e("DefaultContextFactory", "display:" + eGLDisplay + " context: " + eGLContext);
            h.a("eglDestroyContex", egl10.eglGetError());
        }
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    private static class d implements g {
        private d() {
        }

        /* synthetic */ d(byte b) {
            this();
        }

        @Override // com.amap.api.col.3sl.y.g
        public final EGLSurface a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj) {
            try {
                return egl10.eglCreateWindowSurface(eGLDisplay, eGLConfig, obj, null);
            } catch (IllegalArgumentException e) {
                Log.e("GLSurfaceView", "eglCreateWindowSurface", e);
                return null;
            }
        }

        @Override // com.amap.api.col.3sl.y.g
        public final void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface) {
            egl10.eglDestroySurface(eGLDisplay, eGLSurface);
        }
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    private abstract class a implements e {
        protected int[] a;

        abstract EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

        public a(int[] iArr) {
            this.a = a(iArr);
        }

        @Override // com.amap.api.col.3sl.y.e
        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay) {
            int[] iArr = new int[1];
            if (!egl10.eglChooseConfig(eGLDisplay, this.a, null, 0, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            int i = iArr[0];
            if (i <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] eGLConfigArr = new EGLConfig[i];
            if (!egl10.eglChooseConfig(eGLDisplay, this.a, eGLConfigArr, i, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            EGLConfig eGLConfigA = a(egl10, eGLDisplay, eGLConfigArr);
            if (eGLConfigA != null) {
                return eGLConfigA;
            }
            throw new IllegalArgumentException("No config chosen");
        }

        private int[] a(int[] iArr) {
            if (y.this.k != 2 && y.this.k != 3) {
                return iArr;
            }
            int length = iArr.length;
            int[] iArr2 = new int[length + 2];
            int i = length - 1;
            System.arraycopy(iArr, 0, iArr2, 0, i);
            iArr2[i] = 12352;
            if (y.this.k == 2) {
                iArr2[length] = 4;
            } else {
                iArr2[length] = 64;
            }
            iArr2[length + 1] = 12344;
            return iArr2;
        }
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    private class b extends a {
        protected int c;
        protected int d;
        protected int e;
        protected int f;
        protected int g;
        protected int h;
        private int[] j;

        public b() {
            super(new int[]{EGL14.EGL_RED_SIZE, 8, EGL14.EGL_GREEN_SIZE, 8, EGL14.EGL_BLUE_SIZE, 8, EGL14.EGL_ALPHA_SIZE, 0, EGL14.EGL_DEPTH_SIZE, 16, EGL14.EGL_STENCIL_SIZE, 0, EGL14.EGL_NONE});
            this.j = new int[1];
            this.c = 8;
            this.d = 8;
            this.e = 8;
            this.f = 0;
            this.g = 16;
            this.h = 0;
        }

        @Override // com.amap.api.col.3sl.y.a
        public final EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
            for (EGLConfig eGLConfig : eGLConfigArr) {
                int iA = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_DEPTH_SIZE);
                int iA2 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_STENCIL_SIZE);
                if (iA >= this.g && iA2 >= this.h) {
                    int iA3 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_RED_SIZE);
                    int iA4 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_GREEN_SIZE);
                    int iA5 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_BLUE_SIZE);
                    int iA6 = a(egl10, eGLDisplay, eGLConfig, EGL14.EGL_ALPHA_SIZE);
                    if (iA3 == this.c && iA4 == this.d && iA5 == this.e && iA6 == this.f) {
                        return eGLConfig;
                    }
                }
            }
            return null;
        }

        private int a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i) {
            if (egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.j)) {
                return this.j[0];
            }
            return 0;
        }
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    private class m extends b {
        public m() {
            super();
        }
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    private static class h {
        EGL10 a;
        EGLDisplay b;
        EGLSurface c;
        EGLConfig d;
        EGLContext e;
        private WeakReference<y> f;

        public h(WeakReference<y> weakReference) {
            this.f = weakReference;
        }

        public final void a() {
            EGL10 egl10 = (EGL10) EGLContext.getEGL();
            this.a = egl10;
            EGLDisplay eGLDisplayEglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.b = eGLDisplayEglGetDisplay;
            if (eGLDisplayEglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }
            if (!this.a.eglInitialize(this.b, new int[2])) {
                throw new RuntimeException("eglInitialize failed");
            }
            y yVar = this.f.get();
            if (yVar != null) {
                this.d = yVar.f.chooseConfig(this.a, this.b);
                this.e = yVar.g.createContext(this.a, this.b, this.d);
            } else {
                this.d = null;
                this.e = null;
            }
            EGLContext eGLContext = this.e;
            if (eGLContext == null || eGLContext == EGL10.EGL_NO_CONTEXT) {
                this.e = null;
                a("createContext");
            }
            this.c = null;
        }

        public final boolean b() {
            if (this.a == null) {
                throw new RuntimeException("egl not initialized");
            }
            if (this.b == null) {
                throw new RuntimeException("eglDisplay not initialized");
            }
            if (this.d == null) {
                throw new RuntimeException("mEglConfig not initialized");
            }
            g();
            y yVar = this.f.get();
            if (yVar != null) {
                this.c = yVar.h.a(this.a, this.b, this.d, yVar.getSurfaceTexture());
            } else {
                this.c = null;
            }
            EGLSurface eGLSurface = this.c;
            if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                if (this.a.eglGetError() == 12299) {
                    Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                }
                return false;
            }
            EGL10 egl10 = this.a;
            EGLDisplay eGLDisplay = this.b;
            EGLSurface eGLSurface2 = this.c;
            if (egl10.eglMakeCurrent(eGLDisplay, eGLSurface2, eGLSurface2, this.e)) {
                return true;
            }
            a("EGLHelper", "eglMakeCurrent", this.a.eglGetError());
            return false;
        }

        final GL c() {
            GL gl = this.e.getGL();
            y yVar = this.f.get();
            if (yVar == null) {
                return gl;
            }
            if (yVar.i != null) {
                gl = yVar.i.a();
            }
            if ((yVar.j & 3) != 0) {
                return GLDebugHelper.wrap(gl, (yVar.j & 1) != 0 ? 1 : 0, (yVar.j & 2) != 0 ? new l() : null);
            }
            return gl;
        }

        public final int d() {
            if (this.a.eglSwapBuffers(this.b, this.c)) {
                return 12288;
            }
            return this.a.eglGetError();
        }

        public final void e() {
            g();
        }

        private void g() {
            EGLSurface eGLSurface = this.c;
            if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                return;
            }
            EGL10 egl10 = this.a;
            EGLDisplay eGLDisplay = this.b;
            EGLSurface eGLSurface2 = EGL10.EGL_NO_SURFACE;
            egl10.eglMakeCurrent(eGLDisplay, eGLSurface2, eGLSurface2, EGL10.EGL_NO_CONTEXT);
            y yVar = this.f.get();
            if (yVar != null) {
                yVar.h.a(this.a, this.b, this.c);
            }
            this.c = null;
        }

        public final void f() {
            if (this.e != null) {
                y yVar = this.f.get();
                if (yVar != null) {
                    yVar.g.destroyContext(this.a, this.b, this.e);
                }
                this.e = null;
            }
            EGLDisplay eGLDisplay = this.b;
            if (eGLDisplay != null) {
                this.a.eglTerminate(eGLDisplay);
                this.b = null;
            }
        }

        private void a(String str) {
            a(str, this.a.eglGetError());
        }

        public static void a(String str, int i) {
            throw new RuntimeException(b(str, i));
        }

        public static void a(String str, String str2, int i) {
            Log.w(str, b(str2, i));
        }

        private static String b(String str, int i) {
            return str + " failed: " + i;
        }
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    static class i extends Thread {
        private boolean a;
        private boolean b;
        private boolean c;
        private boolean d;
        private boolean e;
        private boolean f;
        private boolean g;
        private boolean h;
        private boolean i;
        private boolean j;
        private boolean k;
        private boolean p;
        private h s;
        private WeakReference<y> t;
        private ArrayList<Runnable> q = new ArrayList<>();
        private boolean r = true;
        private int l = 0;
        private int m = 0;
        private boolean o = true;
        private int n = 1;

        static /* synthetic */ boolean a(i iVar) {
            iVar.b = true;
            return true;
        }

        i(WeakReference<y> weakReference) {
            this.t = weakReference;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            setName("GLThread " + getId());
            try {
                m();
            } catch (InterruptedException unused) {
            } catch (Throwable th) {
                y.a.a(this);
                throw th;
            }
            y.a.a(this);
        }

        private void k() {
            if (this.i) {
                this.i = false;
                this.s.e();
            }
        }

        private void l() {
            if (this.h) {
                this.s.f();
                this.h = false;
                y.a.c(this);
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:170:0x0222 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private void m() throws java.lang.InterruptedException {
            /*
                Method dump skipped, instruction units count: 557
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.3sl.y.i.m():void");
        }

        private boolean n() {
            return this.h && this.i && o();
        }

        private boolean o() {
            if (this.d || !this.e || this.f || this.l <= 0 || this.m <= 0) {
                return false;
            }
            return this.o || this.n == 1;
        }

        public final void a(int i) {
            if (i >= 0 && i <= 1) {
                synchronized (y.a) {
                    this.n = i;
                    y.a.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("renderMode");
        }

        public final int a() {
            int i;
            synchronized (y.a) {
                i = this.n;
            }
            return i;
        }

        public final void b() {
            synchronized (y.a) {
                this.o = true;
                y.a.notifyAll();
            }
        }

        public final void c() {
            synchronized (y.a) {
                this.e = true;
                this.j = false;
                y.a.notifyAll();
                while (this.g && !this.j && !this.b) {
                    try {
                        y.a.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public final void d() {
            synchronized (y.a) {
                this.e = false;
                y.a.notifyAll();
                while (!this.g && !this.b) {
                    try {
                        if (MapsInitializer.getTextureViewDestorySync()) {
                            y.a.wait();
                        } else {
                            y.a.wait(FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
                        }
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public final void e() {
            synchronized (y.a) {
                this.c = true;
                y.a.notifyAll();
                while (!this.b && !this.d) {
                    try {
                        if (MapsInitializer.getTextureViewDestorySync()) {
                            y.a.wait();
                        } else {
                            y.a.wait(FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
                        }
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public final void f() {
            synchronized (y.a) {
                this.c = false;
                this.o = true;
                this.p = false;
                y.a.notifyAll();
                while (!this.b && this.d && !this.p) {
                    try {
                        y.a.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public final void a(int i, int i2) {
            synchronized (y.a) {
                this.l = i;
                this.m = i2;
                this.r = true;
                this.o = true;
                this.p = false;
                y.a.notifyAll();
                while (!this.b && !this.d && !this.p && n()) {
                    try {
                        y.a.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public final void g() {
            synchronized (y.a) {
                this.a = true;
                y.a.notifyAll();
                while (!this.b) {
                    try {
                        y.a.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public final void h() {
            this.k = true;
            y.a.notifyAll();
        }

        public final void a(Runnable runnable) {
            if (runnable != null) {
                synchronized (y.a) {
                    this.q.add(runnable);
                    y.a.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("r must not be null");
        }

        public final int i() {
            int i;
            synchronized (y.a) {
                i = this.l;
            }
            return i;
        }

        public final int j() {
            int i;
            synchronized (y.a) {
                i = this.m;
            }
            return i;
        }
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    static class l extends Writer {
        private StringBuilder a = new StringBuilder();

        l() {
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public final void close() {
            a();
        }

        @Override // java.io.Writer, java.io.Flushable
        public final void flush() {
            a();
        }

        @Override // java.io.Writer
        public final void write(char[] cArr, int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                char c = cArr[i + i3];
                if (c == '\n') {
                    a();
                } else {
                    this.a.append(c);
                }
            }
        }

        private void a() {
            if (this.a.length() > 0) {
                Log.v("GLSurfaceView", this.a.toString());
                StringBuilder sb = this.a;
                sb.delete(0, sb.length());
            }
        }
    }

    private void e() {
        if (this.c != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }

    /* JADX INFO: compiled from: GLTextureView.java */
    private static class j {
        private static String a = "GLThreadManager";
        private boolean b;
        private int c;
        private boolean d;
        private boolean e;
        private boolean f;
        private i g;

        private j() {
        }

        /* synthetic */ j(byte b) {
            this();
        }

        public final synchronized void a(i iVar) {
            i.a(iVar);
            if (this.g == iVar) {
                this.g = null;
            }
            notifyAll();
        }

        public final boolean b(i iVar) {
            i iVar2 = this.g;
            if (iVar2 == iVar || iVar2 == null) {
                this.g = iVar;
                notifyAll();
                return true;
            }
            c();
            if (this.e) {
                return true;
            }
            i iVar3 = this.g;
            if (iVar3 == null) {
                return false;
            }
            iVar3.h();
            return false;
        }

        public final void c(i iVar) {
            if (this.g == iVar) {
                this.g = null;
            }
            notifyAll();
        }

        public final synchronized boolean a() {
            return this.f;
        }

        public final synchronized boolean b() {
            c();
            return !this.e;
        }

        public final synchronized void a(GL10 gl10) {
            if (!this.d && gl10 != null) {
                c();
                String strGlGetString = gl10.glGetString(7937);
                if (this.c < 131072) {
                    this.e = !strGlGetString.startsWith("Q3Dimension MSM7500 ");
                    notifyAll();
                }
                this.f = this.e ? false : true;
                this.d = true;
            }
        }

        private void c() {
            if (this.b) {
                return;
            }
            this.c = 131072;
            this.e = true;
            this.b = true;
        }
    }

    private static boolean f() {
        return Build.VERSION.SDK_INT < 23;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i2, int i3) {
        this.c.c();
        if (f() || MapsInitializer.getTextureSizeChangedInvoked()) {
            onSurfaceTextureSizeChanged(surfaceTexture, i2, i3);
        } else {
            if (this.c.i() == i2 && this.c.j() == i3) {
                return;
            }
            onSurfaceTextureSizeChanged(surfaceTexture, i2, i3);
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        this.c.d();
        return true;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i2, int i3) {
        this.c.a(i2, i3);
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i2, int i3, int i4, int i5) {
        onSurfaceTextureSizeChanged(getSurfaceTexture(), i4 - i2, i5 - i3);
        super.onLayout(z, i2, i3, i4, i5);
    }
}
