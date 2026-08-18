package android.view;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.IBinder;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Surface;
import dalvik.system.CloseGuard;

/* JADX INFO: loaded from: classes.dex */
public class SurfaceControl {
    public static final int BUILT_IN_DISPLAY_ID_HDMI = 1;
    public static final int BUILT_IN_DISPLAY_ID_MAIN = 0;
    public static final int FX_SURFACE_DIM = 131072;
    public static final int FX_SURFACE_MASK = 983040;
    public static final int FX_SURFACE_NORMAL = 0;
    private static final boolean HEADLESS = "1".equals(SystemProperties.get("ro.config.headless", "0"));
    public static final int HIDDEN = 4;
    public static final int NON_PREMULTIPLIED = 256;
    public static final int OPAQUE = 1024;
    public static final int PROTECTED_APP = 2048;
    public static final int SECURE = 128;
    public static final int SURFACE_HIDDEN = 1;
    private static final String TAG = "SurfaceControl";
    private final CloseGuard mCloseGuard;
    private final String mName;
    int mNativeObject;

    private static native void nativeBlankDisplay(IBinder iBinder);

    private static native void nativeCloseTransaction();

    private static native int nativeCreate(SurfaceSession surfaceSession, String str, int i, int i2, int i3, int i4) throws Surface.OutOfResourcesException;

    private static native IBinder nativeCreateDisplay(String str, boolean z);

    private static native void nativeDestroy(int i);

    private static native void nativeDestroyDisplay(IBinder iBinder);

    private static native IBinder nativeGetBuiltInDisplay(int i);

    private static native boolean nativeGetDisplayInfo(IBinder iBinder, PhysicalDisplayInfo physicalDisplayInfo);

    private static native void nativeOpenTransaction();

    private static native void nativeRelease(int i);

    private static native Bitmap nativeScreenshot(IBinder iBinder, int i, int i2, int i3, int i4, boolean z);

    private static native void nativeScreenshot(IBinder iBinder, Surface surface, int i, int i2, int i3, int i4, boolean z);

    private static native void nativeSetAlpha(int i, float f);

    private static native void nativeSetAnimationTransaction();

    private static native void nativeSetDisplayLayerStack(IBinder iBinder, int i);

    private static native void nativeSetDisplayProjection(IBinder iBinder, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9);

    private static native void nativeSetDisplaySurface(IBinder iBinder, int i);

    private static native void nativeSetFlags(int i, int i2, int i3);

    private static native void nativeSetLayer(int i, int i2);

    private static native void nativeSetLayerStack(int i, int i2);

    private static native void nativeSetMatrix(int i, float f, float f2, float f3, float f4);

    private static native void nativeSetPosition(int i, float f, float f2);

    private static native void nativeSetSize(int i, int i2, int i3);

    private static native void nativeSetTransparentRegionHint(int i, Region region);

    private static native void nativeSetWindowCrop(int i, int i2, int i3, int i4, int i5);

    private static native void nativeUnblankDisplay(IBinder iBinder);

    public SurfaceControl(SurfaceSession surfaceSession, String str, int i, int i2, int i3, int i4) throws Surface.OutOfResourcesException {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        if (surfaceSession == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        if (str == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if ((i4 & 4) == 0) {
            Log.w(TAG, "Surfaces should always be created with the HIDDEN flag set to ensure that they are not made visible prematurely before all of the surface's properties have been configured.  Set the other properties and make the surface visible within a transaction.  New surface name: " + str, new Throwable());
        }
        checkHeadless();
        this.mName = str;
        int iNativeCreate = nativeCreate(surfaceSession, str, i, i2, i3, i4);
        this.mNativeObject = iNativeCreate;
        if (iNativeCreate == 0) {
            throw new Surface.OutOfResourcesException("Couldn't allocate SurfaceControl native object");
        }
        closeGuard.open("release");
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            int i = this.mNativeObject;
            if (i != 0) {
                nativeRelease(i);
            }
        } finally {
            super.finalize();
        }
    }

    public String toString() {
        return "Surface(name=" + this.mName + ")";
    }

    public void release() {
        int i = this.mNativeObject;
        if (i != 0) {
            nativeRelease(i);
            this.mNativeObject = 0;
        }
        this.mCloseGuard.close();
    }

    public void destroy() {
        int i = this.mNativeObject;
        if (i != 0) {
            nativeDestroy(i);
            this.mNativeObject = 0;
        }
        this.mCloseGuard.close();
    }

    private void checkNotReleased() {
        if (this.mNativeObject == 0) {
            throw new NullPointerException("mNativeObject is null. Have you called release() already?");
        }
    }

    public static void openTransaction() {
        nativeOpenTransaction();
    }

    public static void closeTransaction() {
        nativeCloseTransaction();
    }

    public static void setAnimationTransaction() {
        nativeSetAnimationTransaction();
    }

    public void setLayer(int i) {
        checkNotReleased();
        nativeSetLayer(this.mNativeObject, i);
    }

    public void setPosition(float f, float f2) {
        checkNotReleased();
        nativeSetPosition(this.mNativeObject, f, f2);
    }

    public void setSize(int i, int i2) {
        checkNotReleased();
        nativeSetSize(this.mNativeObject, i, i2);
    }

    public void hide() {
        checkNotReleased();
        nativeSetFlags(this.mNativeObject, 1, 1);
    }

    public void show() {
        checkNotReleased();
        nativeSetFlags(this.mNativeObject, 0, 1);
    }

    public void setTransparentRegionHint(Region region) {
        checkNotReleased();
        nativeSetTransparentRegionHint(this.mNativeObject, region);
    }

    public void setAlpha(float f) {
        checkNotReleased();
        nativeSetAlpha(this.mNativeObject, f);
    }

    public void setMatrix(float f, float f2, float f3, float f4) {
        checkNotReleased();
        nativeSetMatrix(this.mNativeObject, f, f2, f3, f4);
    }

    public void setFlags(int i, int i2) {
        checkNotReleased();
        nativeSetFlags(this.mNativeObject, i, i2);
    }

    public void setWindowCrop(Rect rect) {
        checkNotReleased();
        if (rect != null) {
            nativeSetWindowCrop(this.mNativeObject, rect.left, rect.top, rect.right, rect.bottom);
        } else {
            nativeSetWindowCrop(this.mNativeObject, 0, 0, 0, 0);
        }
    }

    public void setLayerStack(int i) {
        checkNotReleased();
        nativeSetLayerStack(this.mNativeObject, i);
    }

    public static final class PhysicalDisplayInfo {
        public float density;
        public int height;
        public float refreshRate;
        public boolean secure;
        public int width;
        public float xDpi;
        public float yDpi;

        public int hashCode() {
            return 0;
        }

        public PhysicalDisplayInfo() {
        }

        public PhysicalDisplayInfo(PhysicalDisplayInfo physicalDisplayInfo) {
            copyFrom(physicalDisplayInfo);
        }

        public boolean equals(Object obj) {
            return (obj instanceof PhysicalDisplayInfo) && equals((PhysicalDisplayInfo) obj);
        }

        public boolean equals(PhysicalDisplayInfo physicalDisplayInfo) {
            return physicalDisplayInfo != null && this.width == physicalDisplayInfo.width && this.height == physicalDisplayInfo.height && this.refreshRate == physicalDisplayInfo.refreshRate && this.density == physicalDisplayInfo.density && this.xDpi == physicalDisplayInfo.xDpi && this.yDpi == physicalDisplayInfo.yDpi && this.secure == physicalDisplayInfo.secure;
        }

        public void copyFrom(PhysicalDisplayInfo physicalDisplayInfo) {
            this.width = physicalDisplayInfo.width;
            this.height = physicalDisplayInfo.height;
            this.refreshRate = physicalDisplayInfo.refreshRate;
            this.density = physicalDisplayInfo.density;
            this.xDpi = physicalDisplayInfo.xDpi;
            this.yDpi = physicalDisplayInfo.yDpi;
            this.secure = physicalDisplayInfo.secure;
        }

        public String toString() {
            return "PhysicalDisplayInfo{" + this.width + " x " + this.height + ", " + this.refreshRate + " fps, density " + this.density + ", " + this.xDpi + " x " + this.yDpi + " dpi, secure " + this.secure + "}";
        }
    }

    public static void unblankDisplay(IBinder iBinder) {
        if (iBinder == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        nativeUnblankDisplay(iBinder);
    }

    public static void blankDisplay(IBinder iBinder) {
        if (iBinder == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        nativeBlankDisplay(iBinder);
    }

    public static boolean getDisplayInfo(IBinder iBinder, PhysicalDisplayInfo physicalDisplayInfo) {
        if (iBinder == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        if (physicalDisplayInfo == null) {
            throw new IllegalArgumentException("outInfo must not be null");
        }
        return nativeGetDisplayInfo(iBinder, physicalDisplayInfo);
    }

    public static void setDisplayProjection(IBinder iBinder, int i, Rect rect, Rect rect2) {
        if (iBinder == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        if (rect == null) {
            throw new IllegalArgumentException("layerStackRect must not be null");
        }
        if (rect2 == null) {
            throw new IllegalArgumentException("displayRect must not be null");
        }
        nativeSetDisplayProjection(iBinder, i, rect.left, rect.top, rect.right, rect.bottom, rect2.left, rect2.top, rect2.right, rect2.bottom);
    }

    public static void setDisplayLayerStack(IBinder iBinder, int i) {
        if (iBinder == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        nativeSetDisplayLayerStack(iBinder, i);
    }

    public static void setDisplaySurface(IBinder iBinder, Surface surface) {
        if (iBinder == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        if (surface != null) {
            synchronized (surface.mLock) {
                nativeSetDisplaySurface(iBinder, surface.mNativeObject);
            }
            return;
        }
        nativeSetDisplaySurface(iBinder, 0);
    }

    public static IBinder createDisplay(String str, boolean z) {
        if (str == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        return nativeCreateDisplay(str, z);
    }

    public static void destroyDisplay(IBinder iBinder) {
        if (iBinder == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        nativeDestroyDisplay(iBinder);
    }

    public static IBinder getBuiltInDisplay(int i) {
        return nativeGetBuiltInDisplay(i);
    }

    public static void screenshot(IBinder iBinder, Surface surface, int i, int i2, int i3, int i4) {
        screenshot(iBinder, surface, i, i2, i3, i4, false);
    }

    public static void screenshot(IBinder iBinder, Surface surface, int i, int i2) {
        screenshot(iBinder, surface, i, i2, 0, 0, true);
    }

    public static void screenshot(IBinder iBinder, Surface surface) {
        screenshot(iBinder, surface, 0, 0, 0, 0, true);
    }

    public static Bitmap screenshot(int i, int i2, int i3, int i4) {
        return nativeScreenshot(getBuiltInDisplay(0), i, i2, i3, i4, false);
    }

    public static Bitmap screenshot(int i, int i2) {
        return nativeScreenshot(getBuiltInDisplay(0), i, i2, 0, 0, true);
    }

    private static void screenshot(IBinder iBinder, Surface surface, int i, int i2, int i3, int i4, boolean z) {
        if (iBinder == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        if (surface == null) {
            throw new IllegalArgumentException("consumer must not be null");
        }
        nativeScreenshot(iBinder, surface, i, i2, i3, i4, z);
    }

    private static void checkHeadless() {
        if (HEADLESS) {
            throw new UnsupportedOperationException("Device is headless");
        }
    }
}
