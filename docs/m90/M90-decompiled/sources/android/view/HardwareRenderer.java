package android.view;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.net.LinkQualityInfo;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.ManagedEGLContext;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.IAssetAtlas;
import android.view.Surface;
import android.view.View;
import com.google.android.gles_jni.EGLImpl;
import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.locks.ReentrantLock;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

/* JADX INFO: loaded from: classes.dex */
public abstract class HardwareRenderer {
    private static final String CACHE_PATH_SHADERS = "com.android.opengl.shaders_cache";
    public static final String DEBUG_DIRTY_REGIONS_PROPERTY = "debug.hwui.show_dirty_regions";
    public static final String DEBUG_OVERDRAW_PROPERTY = "debug.hwui.overdraw";
    public static final String DEBUG_SHOW_LAYERS_UPDATES_PROPERTY = "debug.hwui.show_layers_updates";
    public static final String DEBUG_SHOW_NON_RECTANGULAR_CLIP_PROPERTY = "debug.hwui.show_non_rect_clip";
    static final String LOG_TAG = "HardwareRenderer";
    public static final String OVERDRAW_PROPERTY_COUNT = "count";
    public static final String OVERDRAW_PROPERTY_SHOW = "show";
    static final String PRINT_CONFIG_PROPERTY = "debug.hwui.print_config";
    private static final int PROFILE_FRAME_DATA_COUNT = 3;
    static final String PROFILE_MAXFRAMES_PROPERTY = "debug.hwui.profile.maxframes";
    private static final int PROFILE_MAX_FRAMES = 128;
    public static final String PROFILE_PROPERTY = "debug.hwui.profile";
    public static final String PROFILE_PROPERTY_VISUALIZE_BARS = "visual_bars";
    public static final String PROFILE_PROPERTY_VISUALIZE_LINES = "visual_lines";
    static final boolean RENDER_DIRTY_REGIONS = true;
    static final String RENDER_DIRTY_REGIONS_PROPERTY = "debug.hwui.render_dirty_regions";
    public static boolean sRendererDisabled = false;
    public static boolean sSystemRendererDisabled = false;
    private boolean mEnabled;
    private boolean mRequested = true;

    interface HardwareDrawCallbacks {
        void onHardwarePostDraw(HardwareCanvas hardwareCanvas);

        void onHardwarePreDraw(HardwareCanvas hardwareCanvas);
    }

    private static native void nBeginFrame(int[] iArr);

    private static native long nGetSystemTime();

    private static native boolean nIsBackBufferPreserved();

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean nLoadProperties();

    private static native boolean nPreserveBackBuffer();

    private static native void nSetupShadersDiskCache(String str);

    abstract boolean attachFunctor(View.AttachInfo attachInfo, int i);

    abstract void cancelLayerUpdate(HardwareLayer hardwareLayer);

    public abstract DisplayList createDisplayList(String str);

    abstract HardwareLayer createHardwareLayer(int i, int i2, boolean z);

    abstract HardwareLayer createHardwareLayer(boolean z);

    abstract SurfaceTexture createSurfaceTexture(HardwareLayer hardwareLayer);

    abstract void destroy(boolean z);

    abstract void destroyHardwareResources(View view);

    abstract void destroyLayers(View view);

    abstract void detachFunctor(int i);

    abstract void draw(View view, View.AttachInfo attachInfo, HardwareDrawCallbacks hardwareDrawCallbacks, Rect rect);

    abstract void dumpGfxInfo(PrintWriter printWriter);

    abstract void flushLayerUpdates();

    abstract HardwareCanvas getCanvas();

    abstract long getFrameCount();

    abstract int getHeight();

    abstract int getWidth();

    abstract boolean initialize(Surface surface) throws Surface.OutOfResourcesException;

    abstract void invalidate(Surface surface);

    abstract boolean loadSystemProperties(Surface surface);

    abstract void pushLayerUpdate(HardwareLayer hardwareLayer);

    abstract boolean safelyRun(Runnable runnable);

    abstract void setName(String str);

    abstract void setSurfaceTexture(HardwareLayer hardwareLayer, SurfaceTexture surfaceTexture);

    abstract void setup(int i, int i2);

    abstract void updateSurface(Surface surface) throws Surface.OutOfResourcesException;

    abstract boolean validate();

    public static void disable(boolean z) {
        sRendererDisabled = true;
        if (z) {
            sSystemRendererDisabled = true;
        }
    }

    public static boolean isAvailable() {
        return GLES20Canvas.isAvailable();
    }

    public static void setupDiskCache(File file) {
        nSetupShadersDiskCache(new File(file, CACHE_PATH_SHADERS).getAbsolutePath());
    }

    static void beginFrame(int[] iArr) {
        nBeginFrame(iArr);
    }

    static long getSystemTime() {
        return nGetSystemTime();
    }

    static boolean preserveBackBuffer() {
        return nPreserveBackBuffer();
    }

    static boolean isBackBufferPreserved() {
        return nIsBackBufferPreserved();
    }

    boolean initializeIfNeeded(int i, int i2, Surface surface) throws Surface.OutOfResourcesException {
        if (!isRequested() || isEnabled() || !initialize(surface)) {
            return false;
        }
        setup(i, i2);
        return true;
    }

    static HardwareRenderer createGlRenderer(int i, boolean z) {
        if (i == 2) {
            return Gl20Renderer.create(z);
        }
        throw new IllegalArgumentException("Unknown GL version: " + i);
    }

    static void trimMemory(int i) {
        startTrimMemory(i);
        endTrimMemory();
    }

    static void startTrimMemory(int i) {
        Gl20Renderer.startTrimMemory(i);
    }

    static void endTrimMemory() {
        Gl20Renderer.endTrimMemory();
    }

    boolean isEnabled() {
        return this.mEnabled;
    }

    void setEnabled(boolean z) {
        this.mEnabled = z;
    }

    boolean isRequested() {
        return this.mRequested;
    }

    void setRequested(boolean z) {
        this.mRequested = z;
    }

    abstract class GraphDataProvider {
        public static final int GRAPH_TYPE_BARS = 0;
        public static final int GRAPH_TYPE_LINES = 1;

        abstract int getCurrentFrame();

        abstract float[] getData();

        abstract int getElementCount();

        abstract int getFrameCount();

        abstract int getGraphType();

        abstract int getHorizontaUnitMargin();

        abstract int getHorizontalUnitSize();

        abstract float getThreshold();

        abstract int getVerticalUnitSize();

        abstract void prepare(DisplayMetrics displayMetrics);

        abstract void setupCurrentFramePaint(Paint paint);

        abstract void setupGraphPaint(Paint paint, int i);

        abstract void setupThresholdPaint(Paint paint);

        GraphDataProvider() {
        }
    }

    static abstract class GlRenderer extends HardwareRenderer {
        static final int FUNCTOR_PROCESS_DELAY = 4;
        private static final int OVERDRAW_TYPE_COUNT = 1;
        private static final int PROFILE_DRAW_CURRENT_FRAME_COLOR = -815814067;
        private static final int PROFILE_DRAW_DP_PER_MS = 7;
        private static final int PROFILE_DRAW_MARGIN = 0;
        private static final int PROFILE_DRAW_THRESHOLD_COLOR = -10507699;
        private static final int PROFILE_DRAW_THRESHOLD_STROKE_WIDTH = 2;
        private static final int PROFILE_DRAW_WIDTH = 3;
        static final int SURFACE_STATE_ERROR = 0;
        static final int SURFACE_STATE_SUCCESS = 1;
        static final int SURFACE_STATE_UPDATED = 2;
        static boolean sDirtyRegions;
        static final boolean sDirtyRegionsRequested;
        static EGL10 sEgl;
        static EGLConfig sEglConfig;
        static EGLDisplay sEglDisplay;
        HardwareCanvas mCanvas;
        GraphDataProvider mDebugDataProvider;
        boolean mDebugDirtyRegions;
        HardwareLayer mDebugOverdrawLayer;
        Paint mDebugOverdrawPaint;
        Paint mDebugPaint;
        private boolean mDestroyed;
        boolean mDirtyRegionsEnabled;
        EGLContext mEglContext;
        EGLSurface mEglSurface;
        Thread mEglThread;
        long mFrameCount;
        GL mGl;
        final int mGlVersion;
        String mName;
        float[] mProfileData;
        boolean mProfileEnabled;
        ReentrantLock mProfileLock;
        Paint mProfilePaint;
        float[][] mProfileShapes;
        final boolean mTranslucent;
        boolean mUpdateDirtyRegions;
        private static final int[] PROFILE_DRAW_COLORS = {-817994036, -807651054, -806971392};
        private static final String[] VISUALIZERS = {HardwareRenderer.PROFILE_PROPERTY_VISUALIZE_BARS, HardwareRenderer.PROFILE_PROPERTY_VISUALIZE_LINES};
        private static final String[] OVERDRAW = {HardwareRenderer.OVERDRAW_PROPERTY_SHOW, HardwareRenderer.OVERDRAW_PROPERTY_COUNT};
        static final Object[] sEglLock = new Object[0];
        static final ThreadLocal<ManagedEGLContext> sEglContextStorage = new ThreadLocal<>();
        int mWidth = -1;
        int mHeight = -1;
        int mProfileVisualizerType = -1;
        int mProfileCurrentFrame = -3;
        int mDebugOverdraw = -1;
        private final Rect mRedrawClip = new Rect();
        private final int[] mSurfaceSize = new int[2];
        private final FunctorsRunnable mFunctorsRunnable = new FunctorsRunnable();
        private long mDrawDelta = LinkQualityInfo.UNKNOWN_LONG;

        /* JADX INFO: Access modifiers changed from: private */
        public static int dpToPx(int i, float f) {
            return (int) ((i * f) + 0.5f);
        }

        abstract void countOverdraw(HardwareCanvas hardwareCanvas);

        abstract HardwareCanvas createCanvas();

        abstract ManagedEGLContext createManagedContext(EGLContext eGLContext);

        abstract void drawProfileData(View.AttachInfo attachInfo);

        abstract int[] getConfig(boolean z);

        abstract float getOverdraw(HardwareCanvas hardwareCanvas);

        abstract void initAtlas();

        abstract void initCaches();

        void onPostDraw() {
        }

        int onPreDraw(Rect rect) {
            return 0;
        }

        static {
            boolean zEqualsIgnoreCase = "true".equalsIgnoreCase(SystemProperties.get(HardwareRenderer.RENDER_DIRTY_REGIONS_PROPERTY, "true"));
            sDirtyRegions = zEqualsIgnoreCase;
            sDirtyRegionsRequested = zEqualsIgnoreCase;
        }

        GlRenderer(int i, boolean z) {
            this.mGlVersion = i;
            this.mTranslucent = z;
            loadSystemProperties(null);
        }

        @Override // android.view.HardwareRenderer
        boolean loadSystemProperties(Surface surface) {
            boolean z;
            HardwareLayer hardwareLayer;
            String str = SystemProperties.get(HardwareRenderer.PROFILE_PROPERTY);
            int iSearch = search(VISUALIZERS, str);
            boolean z2 = iSearch >= 0;
            if (iSearch != this.mProfileVisualizerType) {
                this.mProfileVisualizerType = iSearch;
                this.mProfileShapes = (float[][]) null;
                this.mProfilePaint = null;
                if (z2) {
                    this.mDebugDataProvider = new DrawPerformanceDataProvider(iSearch);
                } else {
                    this.mDebugDataProvider = null;
                }
                z = true;
            } else {
                z = false;
            }
            if (!z2) {
                z2 = Boolean.parseBoolean(str);
            }
            if (z2 != this.mProfileEnabled) {
                this.mProfileEnabled = z2;
                if (z2) {
                    Log.d(HardwareRenderer.LOG_TAG, "Profiling hardware renderer");
                    this.mProfileData = new float[SystemProperties.getInt(HardwareRenderer.PROFILE_MAXFRAMES_PROPERTY, 128) * 3];
                    int i = 0;
                    while (true) {
                        float[] fArr = this.mProfileData;
                        if (i >= fArr.length) {
                            break;
                        }
                        fArr[i + 2] = -1.0f;
                        fArr[i + 1] = -1.0f;
                        fArr[i] = -1.0f;
                        i += 3;
                    }
                    this.mProfileLock = new ReentrantLock();
                } else {
                    this.mProfileData = null;
                    this.mProfileLock = null;
                    this.mProfileVisualizerType = -1;
                }
                this.mProfileCurrentFrame = -3;
                z = true;
            }
            boolean z3 = SystemProperties.getBoolean(HardwareRenderer.DEBUG_DIRTY_REGIONS_PROPERTY, false);
            if (z3 != this.mDebugDirtyRegions) {
                this.mDebugDirtyRegions = z3;
                if (z3) {
                    Log.d(HardwareRenderer.LOG_TAG, "Debugging dirty regions");
                }
                z = true;
            }
            int iSearch2 = search(OVERDRAW, SystemProperties.get(HardwareRenderer.DEBUG_OVERDRAW_PROPERTY));
            if (iSearch2 != this.mDebugOverdraw) {
                this.mDebugOverdraw = iSearch2;
                if (iSearch2 != 1 && (hardwareLayer = this.mDebugOverdrawLayer) != null) {
                    hardwareLayer.destroy();
                    this.mDebugOverdrawLayer = null;
                    this.mDebugOverdrawPaint = null;
                }
                z = true;
            }
            if (HardwareRenderer.nLoadProperties()) {
                return true;
            }
            return z;
        }

        private static int search(String[] strArr, String str) {
            for (int i = 0; i < strArr.length; i++) {
                if (strArr[i].equals(str)) {
                    return i;
                }
            }
            return -1;
        }

        @Override // android.view.HardwareRenderer
        void dumpGfxInfo(PrintWriter printWriter) {
            float[] fArr;
            if (this.mProfileEnabled) {
                printWriter.printf("\n\tDraw\tProcess\tExecute\n", new Object[0]);
                this.mProfileLock.lock();
                int i = 0;
                while (true) {
                    try {
                        fArr = this.mProfileData;
                        if (i >= fArr.length || fArr[i] < 0.0f) {
                            break;
                        }
                        int i2 = i + 1;
                        int i3 = i + 2;
                        printWriter.printf("\t%3.2f\t%3.2f\t%3.2f\n", Float.valueOf(fArr[i]), Float.valueOf(this.mProfileData[i2]), Float.valueOf(this.mProfileData[i3]));
                        float[] fArr2 = this.mProfileData;
                        fArr2[i3] = -1.0f;
                        fArr2[i2] = -1.0f;
                        fArr2[i] = -1.0f;
                        i += 3;
                    } finally {
                        this.mProfileLock.unlock();
                    }
                }
                this.mProfileCurrentFrame = fArr.length;
            }
        }

        @Override // android.view.HardwareRenderer
        long getFrameCount() {
            return this.mFrameCount;
        }

        boolean hasDirtyRegions() {
            return this.mDirtyRegionsEnabled;
        }

        void checkEglErrors() {
            if (isEnabled()) {
                checkEglErrorsForced();
            }
        }

        private void checkEglErrorsForced() {
            int iEglGetError = sEgl.eglGetError();
            if (iEglGetError != 12288) {
                Log.w(HardwareRenderer.LOG_TAG, "EGL error: " + GLUtils.getEGLErrorString(iEglGetError));
                fallback(iEglGetError != 12302);
            }
        }

        private void fallback(boolean z) {
            destroy(true);
            if (z) {
                setRequested(false);
                Log.w(HardwareRenderer.LOG_TAG, "Mountain View, we've had a problem here. Switching back to software rendering.");
            }
        }

        @Override // android.view.HardwareRenderer
        boolean initialize(Surface surface) throws Surface.OutOfResourcesException {
            if (!isRequested() || isEnabled()) {
                return false;
            }
            boolean zInitializeEgl = initializeEgl();
            GL glCreateEglSurface = createEglSurface(surface);
            this.mGl = glCreateEglSurface;
            this.mDestroyed = false;
            if (glCreateEglSurface == null) {
                return false;
            }
            if (sEgl.eglGetError() != 12288) {
                destroy(true);
                setRequested(false);
            } else {
                if (this.mCanvas == null) {
                    HardwareCanvas hardwareCanvasCreateCanvas = createCanvas();
                    this.mCanvas = hardwareCanvasCreateCanvas;
                    hardwareCanvasCreateCanvas.setName(this.mName);
                }
                setEnabled(true);
                if (zInitializeEgl) {
                    initAtlas();
                }
            }
            return this.mCanvas != null;
        }

        @Override // android.view.HardwareRenderer
        void updateSurface(Surface surface) throws Surface.OutOfResourcesException {
            if (isRequested() && isEnabled()) {
                createEglSurface(surface);
            }
        }

        boolean initializeEgl() {
            synchronized (sEglLock) {
                if (sEgl == null && sEglConfig == null) {
                    EGL10 egl10 = (EGL10) EGLContext.getEGL();
                    sEgl = egl10;
                    EGLDisplay eGLDisplayEglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                    sEglDisplay = eGLDisplayEglGetDisplay;
                    if (eGLDisplayEglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                        throw new RuntimeException("eglGetDisplay failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
                    }
                    if (!sEgl.eglInitialize(sEglDisplay, new int[2])) {
                        throw new RuntimeException("eglInitialize failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
                    }
                    checkEglErrorsForced();
                    sEglConfig = loadEglConfig();
                }
            }
            ThreadLocal<ManagedEGLContext> threadLocal = sEglContextStorage;
            ManagedEGLContext managedEGLContext = threadLocal.get();
            this.mEglContext = managedEGLContext != null ? managedEGLContext.getContext() : null;
            this.mEglThread = Thread.currentThread();
            if (this.mEglContext != null) {
                return false;
            }
            EGLContext eGLContextCreateContext = createContext(sEgl, sEglDisplay, sEglConfig);
            this.mEglContext = eGLContextCreateContext;
            threadLocal.set(createManagedContext(eGLContextCreateContext));
            return true;
        }

        private EGLConfig loadEglConfig() {
            EGLConfig eGLConfigChooseEglConfig = chooseEglConfig();
            if (eGLConfigChooseEglConfig == null) {
                if (sDirtyRegions) {
                    sDirtyRegions = false;
                    eGLConfigChooseEglConfig = chooseEglConfig();
                    if (eGLConfigChooseEglConfig == null) {
                        throw new RuntimeException("eglConfig not initialized");
                    }
                } else {
                    throw new RuntimeException("eglConfig not initialized");
                }
            }
            return eGLConfigChooseEglConfig;
        }

        private EGLConfig chooseEglConfig() {
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            int[] iArr = new int[1];
            int[] config = getConfig(sDirtyRegions);
            String str = SystemProperties.get(HardwareRenderer.PRINT_CONFIG_PROPERTY, "");
            if ("all".equalsIgnoreCase(str)) {
                sEgl.eglChooseConfig(sEglDisplay, config, null, 0, iArr);
                int i = iArr[0];
                EGLConfig[] eGLConfigArr2 = new EGLConfig[i];
                sEgl.eglChooseConfig(sEglDisplay, config, eGLConfigArr2, iArr[0], iArr);
                for (int i2 = 0; i2 < i; i2++) {
                    printConfig(eGLConfigArr2[i2]);
                }
            }
            if (!sEgl.eglChooseConfig(sEglDisplay, config, eGLConfigArr, 1, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
            }
            if (iArr[0] <= 0) {
                return null;
            }
            if ("choice".equalsIgnoreCase(str)) {
                printConfig(eGLConfigArr[0]);
            }
            return eGLConfigArr[0];
        }

        private static void printConfig(EGLConfig eGLConfig) {
            int[] iArr = new int[1];
            Log.d(HardwareRenderer.LOG_TAG, "EGL configuration " + eGLConfig + ":");
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_RED_SIZE, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  RED_SIZE = " + iArr[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_GREEN_SIZE, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  GREEN_SIZE = " + iArr[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_BLUE_SIZE, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  BLUE_SIZE = " + iArr[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_ALPHA_SIZE, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  ALPHA_SIZE = " + iArr[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_DEPTH_SIZE, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  DEPTH_SIZE = " + iArr[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_STENCIL_SIZE, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  STENCIL_SIZE = " + iArr[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_SAMPLE_BUFFERS, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  SAMPLE_BUFFERS = " + iArr[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_SAMPLES, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  SAMPLES = " + iArr[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_SURFACE_TYPE, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  SURFACE_TYPE = 0x" + Integer.toHexString(iArr[0]));
            sEgl.eglGetConfigAttrib(sEglDisplay, eGLConfig, EGL14.EGL_CONFIG_CAVEAT, iArr);
            Log.d(HardwareRenderer.LOG_TAG, "  CONFIG_CAVEAT = 0x" + Integer.toHexString(iArr[0]));
        }

        GL createEglSurface(Surface surface) throws Surface.OutOfResourcesException {
            if (sEgl == null) {
                throw new RuntimeException("egl not initialized");
            }
            if (sEglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            }
            if (sEglConfig == null) {
                throw new RuntimeException("eglConfig not initialized");
            }
            if (Thread.currentThread() != this.mEglThread) {
                throw new IllegalStateException("HardwareRenderer cannot be used from multiple threads");
            }
            destroySurface();
            if (!createSurface(surface)) {
                return null;
            }
            initCaches();
            return this.mEglContext.getGL();
        }

        private void enableDirtyRegions() {
            if (sDirtyRegions) {
                boolean zPreserveBackBuffer = preserveBackBuffer();
                this.mDirtyRegionsEnabled = zPreserveBackBuffer;
                if (zPreserveBackBuffer) {
                    return;
                }
                Log.w(HardwareRenderer.LOG_TAG, "Backbuffer cannot be preserved");
                return;
            }
            if (sDirtyRegionsRequested) {
                this.mDirtyRegionsEnabled = isBackBufferPreserved();
            }
        }

        EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
            int[] iArr = {12440, this.mGlVersion, EGL14.EGL_NONE};
            EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
            if (this.mGlVersion == 0) {
                iArr = null;
            }
            EGLContext eGLContextEglCreateContext = egl10.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
            if (eGLContextEglCreateContext == null || eGLContextEglCreateContext == EGL10.EGL_NO_CONTEXT) {
                throw new IllegalStateException("Could not create an EGL context. eglCreateContext failed with error: " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
            }
            return eGLContextEglCreateContext;
        }

        @Override // android.view.HardwareRenderer
        void destroy(boolean z) {
            if (z && this.mCanvas != null) {
                this.mCanvas = null;
            }
            if (!isEnabled() || this.mDestroyed) {
                setEnabled(false);
                return;
            }
            destroySurface();
            setEnabled(false);
            this.mDestroyed = true;
            this.mGl = null;
        }

        void destroySurface() {
            EGLSurface eGLSurface = this.mEglSurface;
            if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                return;
            }
            if (this.mEglSurface.equals(sEgl.eglGetCurrentSurface(EGL14.EGL_DRAW))) {
                sEgl.eglMakeCurrent(sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            }
            sEgl.eglDestroySurface(sEglDisplay, this.mEglSurface);
            this.mEglSurface = null;
        }

        @Override // android.view.HardwareRenderer
        void invalidate(Surface surface) {
            sEgl.eglMakeCurrent(sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            EGLSurface eGLSurface = this.mEglSurface;
            if (eGLSurface != null && eGLSurface != EGL10.EGL_NO_SURFACE) {
                sEgl.eglDestroySurface(sEglDisplay, this.mEglSurface);
                this.mEglSurface = null;
                setEnabled(false);
            }
            if (surface.isValid() && createSurface(surface)) {
                this.mUpdateDirtyRegions = true;
                if (this.mCanvas != null) {
                    setEnabled(true);
                }
            }
        }

        private boolean createSurface(Surface surface) {
            EGLSurface eGLSurfaceEglCreateWindowSurface = sEgl.eglCreateWindowSurface(sEglDisplay, sEglConfig, surface, null);
            this.mEglSurface = eGLSurfaceEglCreateWindowSurface;
            if (eGLSurfaceEglCreateWindowSurface == null || eGLSurfaceEglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                int iEglGetError = sEgl.eglGetError();
                if (iEglGetError == 12299) {
                    Log.e(HardwareRenderer.LOG_TAG, "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                    return false;
                }
                if (iEglGetError == 12291) {
                    Log.e(HardwareRenderer.LOG_TAG, "createWindowSurface returned EGL_BAD_ALLOC.");
                    return false;
                }
                throw new RuntimeException("createWindowSurface failed " + GLUtils.getEGLErrorString(iEglGetError));
            }
            EGL10 egl10 = sEgl;
            EGLDisplay eGLDisplay = sEglDisplay;
            EGLSurface eGLSurface = this.mEglSurface;
            if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.mEglContext)) {
                throw new IllegalStateException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
            }
            enableDirtyRegions();
            return true;
        }

        @Override // android.view.HardwareRenderer
        boolean validate() {
            return checkRenderContext() != 0;
        }

        @Override // android.view.HardwareRenderer
        void setup(int i, int i2) {
            if (validate()) {
                this.mCanvas.setViewport(i, i2);
                this.mWidth = i;
                this.mHeight = i2;
            }
        }

        @Override // android.view.HardwareRenderer
        int getWidth() {
            return this.mWidth;
        }

        @Override // android.view.HardwareRenderer
        int getHeight() {
            return this.mHeight;
        }

        @Override // android.view.HardwareRenderer
        HardwareCanvas getCanvas() {
            return this.mCanvas;
        }

        @Override // android.view.HardwareRenderer
        void setName(String str) {
            this.mName = str;
        }

        boolean canDraw() {
            return (this.mGl == null || this.mCanvas == null) ? false : true;
        }

        class FunctorsRunnable implements Runnable {
            View.AttachInfo attachInfo;

            FunctorsRunnable() {
            }

            @Override // java.lang.Runnable
            public void run() {
                GlRenderer glRenderer;
                HardwareRenderer hardwareRenderer = this.attachInfo.mHardwareRenderer;
                if (hardwareRenderer == null || !hardwareRenderer.isEnabled() || hardwareRenderer != (glRenderer = GlRenderer.this) || glRenderer.checkRenderContext() == 0) {
                    return;
                }
                GlRenderer.this.handleFunctorStatus(this.attachInfo, GlRenderer.this.mCanvas.invokeFunctors(GlRenderer.this.mRedrawClip));
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:53:0x00f5  */
        /* JADX WARN: Removed duplicated region for block: B:57:0x0110  */
        /* JADX WARN: Removed duplicated region for block: B:62:0x012c  */
        @Override // android.view.HardwareRenderer
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        void draw(android.view.View r22, android.view.View.AttachInfo r23, android.view.HardwareRenderer.HardwareDrawCallbacks r24, android.graphics.Rect r25) throws java.lang.Throwable {
            /*
                Method dump skipped, instruction units count: 317
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.HardwareRenderer.GlRenderer.draw(android.view.View, android.view.View$AttachInfo, android.view.HardwareRenderer$HardwareDrawCallbacks, android.graphics.Rect):void");
        }

        private void debugOverdraw(View.AttachInfo attachInfo, Rect rect, HardwareCanvas hardwareCanvas, DisplayList displayList) {
            if (this.mDebugOverdraw == 1) {
                HardwareLayer hardwareLayer = this.mDebugOverdrawLayer;
                if (hardwareLayer == null) {
                    this.mDebugOverdrawLayer = createHardwareLayer(this.mWidth, this.mHeight, true);
                } else if (hardwareLayer.getWidth() != this.mWidth || this.mDebugOverdrawLayer.getHeight() != this.mHeight) {
                    this.mDebugOverdrawLayer.resize(this.mWidth, this.mHeight);
                }
                if (!this.mDebugOverdrawLayer.isValid()) {
                    this.mDebugOverdraw = -1;
                    return;
                }
                HardwareCanvas hardwareCanvasStart = this.mDebugOverdrawLayer.start(hardwareCanvas, rect);
                countOverdraw(hardwareCanvasStart);
                int iSave = hardwareCanvasStart.save();
                hardwareCanvasStart.drawDisplayList(displayList, null, 1);
                hardwareCanvasStart.restoreToCount(iSave);
                this.mDebugOverdrawLayer.end(hardwareCanvas);
                drawOverdrawCounter(hardwareCanvas, getOverdraw(hardwareCanvasStart), attachInfo.mRootView.getResources().getDisplayMetrics().density);
            }
        }

        private void drawOverdrawCounter(HardwareCanvas hardwareCanvas, float f, float f2) {
            String str = String.format("%.2fx", Float.valueOf(f));
            Paint paint = setupPaint(f2);
            paint.setColor(Color.HSBtoColor(0.28f - ((f * 0.28f) / 3.5f), 0.8f, 1.0f));
            hardwareCanvas.drawText(str, f2 * 4.0f, this.mHeight - paint.getFontMetrics().bottom, paint);
        }

        private Paint setupPaint(float f) {
            if (this.mDebugOverdrawPaint == null) {
                Paint paint = new Paint();
                this.mDebugOverdrawPaint = paint;
                paint.setAntiAlias(true);
                this.mDebugOverdrawPaint.setShadowLayer(3.0f * f, 0.0f, 0.0f, -16777216);
                this.mDebugOverdrawPaint.setTextSize(f * 20.0f);
            }
            return this.mDebugOverdrawPaint;
        }

        private DisplayList buildDisplayList(View view, HardwareCanvas hardwareCanvas) {
            if (this.mDrawDelta <= 0) {
                return view.mDisplayList;
            }
            view.mRecreateDisplayList = (view.mPrivateFlags & Integer.MIN_VALUE) == Integer.MIN_VALUE;
            view.mPrivateFlags &= Integer.MAX_VALUE;
            long jStartBuildDisplayListProfiling = startBuildDisplayListProfiling();
            hardwareCanvas.clearLayerUpdates();
            Trace.traceBegin(8L, "getDisplayList");
            DisplayList displayList = view.getDisplayList();
            Trace.traceEnd(8L);
            endBuildDisplayListProfiling(jStartBuildDisplayListProfiling);
            return displayList;
        }

        /* JADX WARN: Removed duplicated region for block: B:14:0x002d  */
        /* JADX WARN: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private android.graphics.Rect beginFrame(android.view.HardwareCanvas r6, android.graphics.Rect r7, int r8) {
            /*
                r5 = this;
                r0 = 0
                r1 = 2
                if (r8 != r1) goto L9
                beginFrame(r0)
            L7:
                r7 = r0
                goto L28
            L9:
                int[] r8 = r5.mSurfaceSize
                beginFrame(r8)
                r1 = 1
                r2 = r8[r1]
                int r3 = r5.mHeight
                r4 = 0
                if (r2 != r3) goto L1c
                r2 = r8[r4]
                int r3 = r5.mWidth
                if (r2 == r3) goto L28
            L1c:
                r7 = r8[r4]
                r5.mWidth = r7
                r8 = r8[r1]
                r5.mHeight = r8
                r6.setViewport(r7, r8)
                goto L7
            L28:
                android.view.HardwareRenderer$GraphDataProvider r6 = r5.mDebugDataProvider
                if (r6 == 0) goto L2d
                goto L2e
            L2d:
                r0 = r7
            L2e:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.HardwareRenderer.GlRenderer.beginFrame(android.view.HardwareCanvas, android.graphics.Rect, int):android.graphics.Rect");
        }

        private long startBuildDisplayListProfiling() {
            if (!this.mProfileEnabled) {
                return 0L;
            }
            int i = this.mProfileCurrentFrame + 3;
            this.mProfileCurrentFrame = i;
            if (i >= this.mProfileData.length) {
                this.mProfileCurrentFrame = 0;
            }
            return System.nanoTime();
        }

        private void endBuildDisplayListProfiling(long j) {
            if (this.mProfileEnabled) {
                this.mProfileData[this.mProfileCurrentFrame] = (System.nanoTime() - j) * 1.0E-6f;
            }
        }

        private int prepareFrame(Rect rect) {
            Trace.traceBegin(8L, "prepareFrame");
            try {
                return onPreDraw(rect);
            } finally {
                Trace.traceEnd(8L);
            }
        }

        /* JADX WARN: Finally extract failed */
        private int drawDisplayList(View.AttachInfo attachInfo, HardwareCanvas hardwareCanvas, DisplayList displayList, int i, View view) {
            long jNanoTime = this.mProfileEnabled ? System.nanoTime() : 0L;
            view.getClass().getName().equals("com.softwinner.fireplayer.floatwindow.Window");
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10241, 9728.0f);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10240, 9729.0f);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10242, 33071);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10243, 33071);
            Trace.traceBegin(8L, "drawDisplayList");
            try {
                int iDrawDisplayList = hardwareCanvas.drawDisplayList(displayList, this.mRedrawClip, 1) | i;
                Trace.traceEnd(8L);
                if (this.mProfileEnabled) {
                    this.mProfileData[this.mProfileCurrentFrame + 1] = (System.nanoTime() - jNanoTime) * 1.0E-6f;
                }
                handleFunctorStatus(attachInfo, iDrawDisplayList);
                return iDrawDisplayList;
            } catch (Throwable th) {
                Trace.traceEnd(8L);
                throw th;
            }
        }

        private void swapBuffers(int i) {
            if ((i & 4) == 4) {
                long jNanoTime = this.mProfileEnabled ? System.nanoTime() : 0L;
                sEgl.eglSwapBuffers(sEglDisplay, this.mEglSurface);
                if (this.mProfileEnabled) {
                    this.mProfileData[this.mProfileCurrentFrame + 2] = (System.nanoTime() - jNanoTime) * 1.0E-6f;
                }
                checkEglErrors();
            }
        }

        private void debugDirtyRegions(Rect rect, HardwareCanvas hardwareCanvas) {
            if (this.mDebugDirtyRegions) {
                if (this.mDebugPaint == null) {
                    Paint paint = new Paint();
                    this.mDebugPaint = paint;
                    paint.setColor(2147418112);
                }
                if (rect == null || (this.mFrameCount & 1) != 0) {
                    return;
                }
                hardwareCanvas.drawRect(rect, this.mDebugPaint);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleFunctorStatus(View.AttachInfo attachInfo, int i) {
            if ((i & 1) != 0) {
                if (this.mRedrawClip.isEmpty()) {
                    attachInfo.mViewRootImpl.invalidate();
                } else {
                    attachInfo.mViewRootImpl.invalidateChildInParent(null, this.mRedrawClip);
                    this.mRedrawClip.setEmpty();
                }
            }
            if ((i & 2) != 0 || attachInfo.mHandler.hasCallbacks(this.mFunctorsRunnable)) {
                attachInfo.mHandler.removeCallbacks(this.mFunctorsRunnable);
                this.mFunctorsRunnable.attachInfo = attachInfo;
                attachInfo.mHandler.postDelayed(this.mFunctorsRunnable, 4L);
            }
        }

        @Override // android.view.HardwareRenderer
        void detachFunctor(int i) {
            HardwareCanvas hardwareCanvas = this.mCanvas;
            if (hardwareCanvas != null) {
                hardwareCanvas.detachFunctor(i);
            }
        }

        @Override // android.view.HardwareRenderer
        boolean attachFunctor(View.AttachInfo attachInfo, int i) {
            HardwareCanvas hardwareCanvas = this.mCanvas;
            if (hardwareCanvas == null) {
                return false;
            }
            hardwareCanvas.attachFunctor(i);
            this.mFunctorsRunnable.attachInfo = attachInfo;
            attachInfo.mHandler.removeCallbacks(this.mFunctorsRunnable);
            attachInfo.mHandler.postDelayed(this.mFunctorsRunnable, 0L);
            return true;
        }

        int checkRenderContext() {
            if (this.mEglThread != Thread.currentThread()) {
                throw new IllegalStateException("Hardware acceleration can only be used with a single UI thread.\nOriginal thread: " + this.mEglThread + "\nCurrent thread: " + Thread.currentThread());
            }
            return checkRenderContextUnsafe();
        }

        private int checkRenderContextUnsafe() {
            if (this.mEglSurface.equals(sEgl.eglGetCurrentSurface(EGL14.EGL_DRAW)) && this.mEglContext.equals(sEgl.eglGetCurrentContext())) {
                return 1;
            }
            EGL10 egl10 = sEgl;
            EGLDisplay eGLDisplay = sEglDisplay;
            EGLSurface eGLSurface = this.mEglSurface;
            if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.mEglContext)) {
                Log.e(HardwareRenderer.LOG_TAG, "eglMakeCurrent failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
                fallback(true);
                return 0;
            }
            if (!this.mUpdateDirtyRegions) {
                return 2;
            }
            enableDirtyRegions();
            this.mUpdateDirtyRegions = false;
            return 2;
        }

        class DrawPerformanceDataProvider extends GraphDataProvider {
            private final int mGraphType;
            private int mHorizontalMargin;
            private int mHorizontalUnit;
            private int mThresholdStroke;
            private int mVerticalUnit;

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getElementCount() {
                return 3;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            float getThreshold() {
                return 16.0f;
            }

            DrawPerformanceDataProvider(int i) {
                super();
                this.mGraphType = i;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            void prepare(DisplayMetrics displayMetrics) {
                float f = displayMetrics.density;
                this.mVerticalUnit = GlRenderer.dpToPx(7, f);
                this.mHorizontalUnit = GlRenderer.dpToPx(3, f);
                this.mHorizontalMargin = GlRenderer.dpToPx(0, f);
                this.mThresholdStroke = GlRenderer.dpToPx(2, f);
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getGraphType() {
                return this.mGraphType;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getVerticalUnitSize() {
                return this.mVerticalUnit;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getHorizontalUnitSize() {
                return this.mHorizontalUnit;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getHorizontaUnitMargin() {
                return this.mHorizontalMargin;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            float[] getData() {
                return GlRenderer.this.mProfileData;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getFrameCount() {
                return GlRenderer.this.mProfileData.length / 3;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getCurrentFrame() {
                return GlRenderer.this.mProfileCurrentFrame / 3;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            void setupGraphPaint(Paint paint, int i) {
                paint.setColor(GlRenderer.PROFILE_DRAW_COLORS[i]);
                if (this.mGraphType == 1) {
                    paint.setStrokeWidth(this.mThresholdStroke);
                }
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            void setupThresholdPaint(Paint paint) {
                paint.setColor(GlRenderer.PROFILE_DRAW_THRESHOLD_COLOR);
                paint.setStrokeWidth(this.mThresholdStroke);
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            void setupCurrentFramePaint(Paint paint) {
                paint.setColor(GlRenderer.PROFILE_DRAW_CURRENT_FRAME_COLOR);
                if (this.mGraphType == 1) {
                    paint.setStrokeWidth(this.mThresholdStroke);
                }
            }
        }
    }

    static class Gl20Renderer extends GlRenderer {
        private static EGLSurface sPbuffer;
        private static final Object[] sPbufferLock = new Object[0];
        private DisplayMetrics mDisplayMetrics;
        private GLES20Canvas mGlCanvas;

        static class Gl20RendererEglContext extends ManagedEGLContext {
            final Handler mHandler;

            public Gl20RendererEglContext(EGLContext eGLContext) {
                super(eGLContext);
                this.mHandler = new Handler();
            }

            @Override // android.opengl.ManagedEGLContext
            public void onTerminate(final EGLContext eGLContext) {
                if (this.mHandler.getLooper() != Looper.myLooper()) {
                    this.mHandler.post(new Runnable() { // from class: android.view.HardwareRenderer.Gl20Renderer.Gl20RendererEglContext.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Gl20RendererEglContext.this.onTerminate(eGLContext);
                        }
                    });
                    return;
                }
                synchronized (GlRenderer.sEglLock) {
                    if (GlRenderer.sEgl == null) {
                        return;
                    }
                    if (EGLImpl.getInitCount(GlRenderer.sEglDisplay) == 1) {
                        Gl20Renderer.usePbufferSurface(eGLContext);
                        GLES20Canvas.terminateCaches();
                        GlRenderer.sEgl.eglDestroyContext(GlRenderer.sEglDisplay, eGLContext);
                        GlRenderer.sEglContextStorage.set(null);
                        GlRenderer.sEglContextStorage.remove();
                        GlRenderer.sEgl.eglDestroySurface(GlRenderer.sEglDisplay, Gl20Renderer.sPbuffer);
                        GlRenderer.sEgl.eglMakeCurrent(GlRenderer.sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                        GlRenderer.sEgl.eglReleaseThread();
                        GlRenderer.sEgl.eglTerminate(GlRenderer.sEglDisplay);
                        GlRenderer.sEgl = null;
                        GlRenderer.sEglDisplay = null;
                        GlRenderer.sEglConfig = null;
                        EGLSurface unused = Gl20Renderer.sPbuffer = null;
                    }
                }
            }
        }

        Gl20Renderer(boolean z) {
            super(2, z);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        HardwareCanvas createCanvas() {
            GLES20Canvas gLES20Canvas = new GLES20Canvas(this.mTranslucent);
            this.mGlCanvas = gLES20Canvas;
            return gLES20Canvas;
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        ManagedEGLContext createManagedContext(EGLContext eGLContext) {
            return new Gl20RendererEglContext(this.mEglContext);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        int[] getConfig(boolean z) {
            return new int[]{EGL14.EGL_RENDERABLE_TYPE, 4, EGL14.EGL_RED_SIZE, 8, EGL14.EGL_GREEN_SIZE, 8, EGL14.EGL_BLUE_SIZE, 8, EGL14.EGL_ALPHA_SIZE, 8, EGL14.EGL_DEPTH_SIZE, 0, EGL14.EGL_CONFIG_CAVEAT, EGL14.EGL_NONE, EGL14.EGL_STENCIL_SIZE, GLES20Canvas.getStencilSize(), EGL14.EGL_SURFACE_TYPE, (z ? 1024 : 0) | 4, EGL14.EGL_NONE};
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void initCaches() {
            if (GLES20Canvas.initCaches()) {
                initAtlas();
            }
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void initAtlas() {
            GraphicBuffer buffer;
            IBinder service = ServiceManager.getService("assetatlas");
            if (service == null) {
                return;
            }
            IAssetAtlas iAssetAtlasAsInterface = IAssetAtlas.Stub.asInterface(service);
            try {
                if (!iAssetAtlasAsInterface.isCompatible(Process.myPpid()) || (buffer = iAssetAtlasAsInterface.getBuffer()) == null) {
                    return;
                }
                int[] map = iAssetAtlasAsInterface.getMap();
                if (map != null) {
                    GLES20Canvas.initAtlas(buffer, map);
                }
                if (iAssetAtlasAsInterface.getClass() != service.getClass()) {
                    buffer.destroy();
                }
            } catch (RemoteException e) {
                Log.w(HardwareRenderer.LOG_TAG, "Could not acquire atlas", e);
            }
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        boolean canDraw() {
            return super.canDraw() && this.mGlCanvas != null;
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        int onPreDraw(Rect rect) {
            return this.mGlCanvas.onPreDraw(rect);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void onPostDraw() {
            this.mGlCanvas.onPostDraw();
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void drawProfileData(View.AttachInfo attachInfo) {
            int i;
            int i2;
            int i3;
            int i4;
            int i5;
            boolean z;
            if (this.mDebugDataProvider != null) {
                GraphDataProvider graphDataProvider = this.mDebugDataProvider;
                initProfileDrawData(attachInfo, graphDataProvider);
                int verticalUnitSize = graphDataProvider.getVerticalUnitSize();
                int horizontaUnitMargin = graphDataProvider.getHorizontaUnitMargin();
                int horizontalUnitSize = graphDataProvider.getHorizontalUnitSize();
                float[] data = graphDataProvider.getData();
                int elementCount = graphDataProvider.getElementCount();
                int graphType = graphDataProvider.getGraphType();
                int frameCount = graphDataProvider.getFrameCount() * elementCount;
                if (graphType == 1) {
                    frameCount -= elementCount;
                }
                int i6 = 0;
                int i7 = 0;
                int i8 = 0;
                int i9 = 0;
                while (i6 < frameCount && data[i6] >= 0.0f) {
                    int i10 = i7 * 4;
                    if (i6 == graphDataProvider.getCurrentFrame() * elementCount) {
                        i9 = i10;
                    }
                    int i11 = i8 + horizontaUnitMargin;
                    int i12 = i11 + horizontalUnitSize;
                    int i13 = this.mHeight;
                    GraphDataProvider graphDataProvider2 = graphDataProvider;
                    int i14 = frameCount;
                    float f = verticalUnitSize;
                    int i15 = (int) (i13 - (data[i6] * f));
                    if (graphType == 0) {
                        i = verticalUnitSize;
                        i2 = horizontaUnitMargin;
                        i3 = graphType;
                        i4 = i9;
                        int i16 = i13;
                        int i17 = i15;
                        int i18 = 0;
                        while (i18 < elementCount) {
                            float[] fArr = this.mProfileShapes[i18];
                            fArr[i10] = i11;
                            float f2 = i17;
                            fArr[i10 + 1] = f2;
                            int i19 = horizontalUnitSize;
                            fArr[i10 + 2] = i12;
                            fArr[i10 + 3] = i16;
                            int i20 = i18 < elementCount + (-1) ? (int) (f2 - (data[(i6 + i18) + 1] * f)) : i17;
                            i18++;
                            horizontalUnitSize = i19;
                            int i21 = i17;
                            i17 = i20;
                            i16 = i21;
                        }
                    } else if (graphType != 1) {
                        i = verticalUnitSize;
                        i2 = horizontaUnitMargin;
                        i5 = horizontalUnitSize;
                        i3 = graphType;
                        i4 = i9;
                        z = true;
                        i7++;
                        i6 += elementCount;
                        i8 = i12;
                        graphDataProvider = graphDataProvider2;
                        horizontalUnitSize = i5;
                        frameCount = i14;
                        i9 = i4;
                        verticalUnitSize = i;
                        graphType = i3;
                        horizontaUnitMargin = i2;
                    } else {
                        int i22 = i15;
                        int i23 = 0;
                        while (i23 < elementCount) {
                            int i24 = i9;
                            float[] fArr2 = this.mProfileShapes[i23];
                            int i25 = verticalUnitSize;
                            fArr2[i10] = (i11 + i12) * 0.5f;
                            int i26 = graphType;
                            fArr2[i10 + 1] = i10 == 0 ? i22 : fArr2[i10 - 1];
                            int i27 = horizontaUnitMargin;
                            fArr2[i10 + 2] = fArr2[i10] + horizontalUnitSize;
                            float f3 = i22;
                            fArr2[i10 + 3] = f3;
                            if (i23 < elementCount - 1) {
                                i22 = (int) (f3 - (data[(i6 + i23) + 1] * f));
                            }
                            i23++;
                            i9 = i24;
                            verticalUnitSize = i25;
                            graphType = i26;
                            horizontaUnitMargin = i27;
                        }
                        i = verticalUnitSize;
                        i2 = horizontaUnitMargin;
                        i3 = graphType;
                        i4 = i9;
                    }
                    i5 = horizontalUnitSize;
                    z = true;
                    i7++;
                    i6 += elementCount;
                    i8 = i12;
                    graphDataProvider = graphDataProvider2;
                    horizontalUnitSize = i5;
                    frameCount = i14;
                    i9 = i4;
                    verticalUnitSize = i;
                    graphType = i3;
                    horizontaUnitMargin = i2;
                }
                int i28 = graphType;
                drawGraph(i28, i7);
                drawCurrentFrame(i28, i9);
                drawThreshold(i8 + horizontaUnitMargin, verticalUnitSize);
            }
        }

        private void drawGraph(int i, int i2) {
            for (int i3 = 0; i3 < this.mProfileShapes.length; i3++) {
                this.mDebugDataProvider.setupGraphPaint(this.mProfilePaint, i3);
                if (i == 0) {
                    this.mGlCanvas.drawRects(this.mProfileShapes[i3], i2 * 4, this.mProfilePaint);
                } else if (i == 1) {
                    this.mGlCanvas.drawLines(this.mProfileShapes[i3], 0, i2 * 4, this.mProfilePaint);
                }
            }
        }

        private void drawCurrentFrame(int i, int i2) {
            if (i2 >= 0) {
                this.mDebugDataProvider.setupCurrentFramePaint(this.mProfilePaint);
                if (i == 0) {
                    this.mGlCanvas.drawRect(this.mProfileShapes[2][i2], this.mProfileShapes[2][i2 + 1], this.mProfileShapes[2][i2 + 2], this.mProfileShapes[0][i2 + 3], this.mProfilePaint);
                } else {
                    if (i != 1) {
                        return;
                    }
                    this.mGlCanvas.drawLine(this.mProfileShapes[2][i2], this.mProfileShapes[2][i2 + 1], this.mProfileShapes[2][i2], this.mHeight, this.mProfilePaint);
                }
            }
        }

        private void drawThreshold(int i, int i2) {
            float threshold = this.mDebugDataProvider.getThreshold();
            if (threshold > 0.0f) {
                this.mDebugDataProvider.setupThresholdPaint(this.mProfilePaint);
                float f = (int) (this.mHeight - (threshold * i2));
                this.mGlCanvas.drawLine(0.0f, f, i, f, this.mProfilePaint);
            }
        }

        private void initProfileDrawData(View.AttachInfo attachInfo, GraphDataProvider graphDataProvider) {
            if (this.mProfileShapes == null) {
                int elementCount = graphDataProvider.getElementCount();
                int frameCount = graphDataProvider.getFrameCount();
                this.mProfileShapes = new float[elementCount][];
                for (int i = 0; i < elementCount; i++) {
                    this.mProfileShapes[i] = new float[frameCount * 4];
                }
                this.mProfilePaint = new Paint();
            }
            this.mProfilePaint.reset();
            if (graphDataProvider.getGraphType() == 1) {
                this.mProfilePaint.setAntiAlias(true);
            }
            if (this.mDisplayMetrics == null) {
                this.mDisplayMetrics = new DisplayMetrics();
            }
            attachInfo.mDisplay.getMetrics(this.mDisplayMetrics);
            graphDataProvider.prepare(this.mDisplayMetrics);
        }

        @Override // android.view.HardwareRenderer.GlRenderer, android.view.HardwareRenderer
        void destroy(boolean z) {
            try {
                super.destroy(z);
            } finally {
                if (z && this.mGlCanvas != null) {
                    this.mGlCanvas = null;
                }
            }
        }

        @Override // android.view.HardwareRenderer
        void pushLayerUpdate(HardwareLayer hardwareLayer) {
            this.mGlCanvas.pushLayerUpdate(hardwareLayer);
        }

        @Override // android.view.HardwareRenderer
        void cancelLayerUpdate(HardwareLayer hardwareLayer) {
            this.mGlCanvas.cancelLayerUpdate(hardwareLayer);
        }

        @Override // android.view.HardwareRenderer
        void flushLayerUpdates() {
            this.mGlCanvas.flushLayerUpdates();
        }

        @Override // android.view.HardwareRenderer
        public DisplayList createDisplayList(String str) {
            return new GLES20DisplayList(str);
        }

        @Override // android.view.HardwareRenderer
        HardwareLayer createHardwareLayer(boolean z) {
            return new GLES20TextureLayer(z);
        }

        @Override // android.view.HardwareRenderer
        public HardwareLayer createHardwareLayer(int i, int i2, boolean z) {
            return new GLES20RenderLayer(i, i2, z);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void countOverdraw(HardwareCanvas hardwareCanvas) {
            ((GLES20Canvas) hardwareCanvas).setCountOverdrawEnabled(true);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        float getOverdraw(HardwareCanvas hardwareCanvas) {
            return ((GLES20Canvas) hardwareCanvas).getOverdraw();
        }

        @Override // android.view.HardwareRenderer
        public SurfaceTexture createSurfaceTexture(HardwareLayer hardwareLayer) {
            return ((GLES20TextureLayer) hardwareLayer).getSurfaceTexture();
        }

        @Override // android.view.HardwareRenderer
        void setSurfaceTexture(HardwareLayer hardwareLayer, SurfaceTexture surfaceTexture) {
            ((GLES20TextureLayer) hardwareLayer).setSurfaceTexture(surfaceTexture);
        }

        @Override // android.view.HardwareRenderer
        boolean safelyRun(Runnable runnable) {
            boolean z = !isEnabled() || checkRenderContext() == 0;
            if (z) {
                Gl20RendererEglContext gl20RendererEglContext = (Gl20RendererEglContext) sEglContextStorage.get();
                if (gl20RendererEglContext == null) {
                    return false;
                }
                usePbufferSurface(gl20RendererEglContext.getContext());
            }
            try {
                runnable.run();
                return true;
            } finally {
                if (z) {
                    sEgl.eglMakeCurrent(sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                }
            }
        }

        @Override // android.view.HardwareRenderer
        void destroyLayers(final View view) {
            if (view != null) {
                safelyRun(new Runnable() { // from class: android.view.HardwareRenderer.Gl20Renderer.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (Gl20Renderer.this.mCanvas != null) {
                            Gl20Renderer.this.mCanvas.clearLayerUpdates();
                        }
                        Gl20Renderer.destroyHardwareLayer(view);
                        GLES20Canvas.flushCaches(0);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void destroyHardwareLayer(View view) {
            view.destroyLayer(true);
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    destroyHardwareLayer(viewGroup.getChildAt(i));
                }
            }
        }

        @Override // android.view.HardwareRenderer
        void destroyHardwareResources(final View view) {
            if (view != null) {
                safelyRun(new Runnable() { // from class: android.view.HardwareRenderer.Gl20Renderer.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (Gl20Renderer.this.mCanvas != null) {
                            Gl20Renderer.this.mCanvas.clearLayerUpdates();
                        }
                        Gl20Renderer.destroyResources(view);
                        GLES20Canvas.flushCaches(0);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void destroyResources(View view) {
            view.destroyHardwareResources();
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    destroyResources(viewGroup.getChildAt(i));
                }
            }
        }

        static HardwareRenderer create(boolean z) {
            if (GLES20Canvas.isAvailable()) {
                return new Gl20Renderer(z);
            }
            return null;
        }

        static void startTrimMemory(int i) {
            Gl20RendererEglContext gl20RendererEglContext;
            if (sEgl == null || sEglConfig == null || (gl20RendererEglContext = (Gl20RendererEglContext) sEglContextStorage.get()) == null) {
                return;
            }
            usePbufferSurface(gl20RendererEglContext.getContext());
            if (i >= 80) {
                GLES20Canvas.flushCaches(2);
            } else if (i >= 20) {
                GLES20Canvas.flushCaches(1);
            }
        }

        static void endTrimMemory() {
            if (sEgl == null || sEglDisplay == null) {
                return;
            }
            sEgl.eglMakeCurrent(sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void usePbufferSurface(EGLContext eGLContext) {
            synchronized (sPbufferLock) {
                if (sPbuffer == null) {
                    sPbuffer = sEgl.eglCreatePbufferSurface(sEglDisplay, sEglConfig, new int[]{EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE});
                }
            }
            EGL10 egl10 = sEgl;
            EGLDisplay eGLDisplay = sEglDisplay;
            EGLSurface eGLSurface = sPbuffer;
            egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, eGLContext);
        }
    }
}
