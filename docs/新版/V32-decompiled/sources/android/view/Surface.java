package android.view;

import android.content.res.CompatibilityInfo;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import dalvik.system.CloseGuard;

/* JADX INFO: loaded from: classes.dex */
public class Surface implements Parcelable {
    public static final Parcelable.Creator<Surface> CREATOR = new Parcelable.Creator<Surface>() { // from class: android.view.Surface.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Surface createFromParcel(Parcel parcel) {
            try {
                Surface surface = new Surface();
                surface.readFromParcel(parcel);
                return surface;
            } catch (Exception e) {
                Log.e(Surface.TAG, "Exception creating surface from parcel", e);
                return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Surface[] newArray(int i) {
            return new Surface[i];
        }
    };
    public static final int ROTATION_0 = 0;
    public static final int ROTATION_180 = 2;
    public static final int ROTATION_270 = 3;
    public static final int ROTATION_90 = 1;
    private static final String TAG = "Surface";
    private final Canvas mCanvas;
    private final CloseGuard mCloseGuard;
    private Matrix mCompatibleMatrix;
    private int mGenerationId;
    final Object mLock;
    private int mLockedObject;
    private String mName;
    int mNativeObject;

    private static native int nativeCreateFromSurfaceControl(int i);

    private static native int nativeCreateFromSurfaceTexture(SurfaceTexture surfaceTexture) throws OutOfResourcesException;

    private static native int nativeGetDisplayParameter(int i, int i2, int i3, int i4);

    private static native boolean nativeIsConsumerRunningBehind(int i);

    private static native boolean nativeIsValid(int i);

    private static native int nativeLockCanvas(int i, Canvas canvas, Rect rect) throws OutOfResourcesException;

    private static native int nativeReadFromParcel(int i, Parcel parcel);

    private static native void nativeRelease(int i);

    private static native int nativeSetDisplayParameter(int i, int i2, int i3, int i4, int i5);

    private static native void nativeUnlockCanvasAndPost(int i, Canvas canvas);

    private static native void nativeWriteToParcel(int i, Parcel parcel);

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Surface() {
        this.mCloseGuard = CloseGuard.get();
        this.mLock = new Object();
        this.mCanvas = new CompatibleCanvas();
    }

    public Surface(SurfaceTexture surfaceTexture) {
        this.mCloseGuard = CloseGuard.get();
        Object obj = new Object();
        this.mLock = obj;
        this.mCanvas = new CompatibleCanvas();
        if (surfaceTexture == null) {
            throw new IllegalArgumentException("surfaceTexture must not be null");
        }
        synchronized (obj) {
            this.mName = surfaceTexture.toString();
            setNativeObjectLocked(nativeCreateFromSurfaceTexture(surfaceTexture));
        }
    }

    private Surface(int i) {
        this.mCloseGuard = CloseGuard.get();
        Object obj = new Object();
        this.mLock = obj;
        this.mCanvas = new CompatibleCanvas();
        synchronized (obj) {
            setNativeObjectLocked(i);
        }
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            release();
        } finally {
            super.finalize();
        }
    }

    public void release() {
        synchronized (this.mLock) {
            int i = this.mNativeObject;
            if (i != 0) {
                nativeRelease(i);
                setNativeObjectLocked(0);
            }
        }
    }

    public void destroy() {
        release();
    }

    public boolean isValid() {
        synchronized (this.mLock) {
            int i = this.mNativeObject;
            if (i == 0) {
                return false;
            }
            return nativeIsValid(i);
        }
    }

    public int getGenerationId() {
        int i;
        synchronized (this.mLock) {
            i = this.mGenerationId;
        }
        return i;
    }

    public boolean isConsumerRunningBehind() {
        boolean zNativeIsConsumerRunningBehind;
        synchronized (this.mLock) {
            checkNotReleasedLocked();
            zNativeIsConsumerRunningBehind = nativeIsConsumerRunningBehind(this.mNativeObject);
        }
        return zNativeIsConsumerRunningBehind;
    }

    public Canvas lockCanvas(Rect rect) throws OutOfResourcesException, IllegalArgumentException {
        Canvas canvas;
        synchronized (this.mLock) {
            checkNotReleasedLocked();
            if (this.mLockedObject != 0) {
                throw new IllegalStateException("Surface was already locked");
            }
            this.mLockedObject = nativeLockCanvas(this.mNativeObject, this.mCanvas, rect);
            canvas = this.mCanvas;
        }
        return canvas;
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        if (canvas != this.mCanvas) {
            throw new IllegalArgumentException("canvas object must be the same instance that was previously returned by lockCanvas");
        }
        synchronized (this.mLock) {
            checkNotReleasedLocked();
            if (this.mNativeObject != this.mLockedObject) {
                Log.w(TAG, "WARNING: Surface's mNativeObject (0x" + Integer.toHexString(this.mNativeObject) + ") != mLockedObject (0x" + Integer.toHexString(this.mLockedObject) + ")");
            }
            int i = this.mLockedObject;
            if (i == 0) {
                throw new IllegalStateException("Surface was not locked");
            }
            nativeUnlockCanvasAndPost(i, canvas);
            nativeRelease(this.mLockedObject);
            this.mLockedObject = 0;
        }
    }

    @Deprecated
    public void unlockCanvas(Canvas canvas) {
        throw new UnsupportedOperationException();
    }

    void setCompatibilityTranslator(CompatibilityInfo.Translator translator) {
        if (translator != null) {
            float f = translator.applicationScale;
            Matrix matrix = new Matrix();
            this.mCompatibleMatrix = matrix;
            matrix.setScale(f, f);
        }
    }

    public void copyFrom(SurfaceControl surfaceControl) {
        if (surfaceControl == null) {
            throw new IllegalArgumentException("other must not be null");
        }
        int i = surfaceControl.mNativeObject;
        if (i == 0) {
            throw new NullPointerException("SurfaceControl native object is null. Are you using a released SurfaceControl?");
        }
        int iNativeCreateFromSurfaceControl = nativeCreateFromSurfaceControl(i);
        synchronized (this.mLock) {
            int i2 = this.mNativeObject;
            if (i2 != 0) {
                nativeRelease(i2);
            }
            setNativeObjectLocked(iNativeCreateFromSurfaceControl);
        }
    }

    @Deprecated
    public void transferFrom(Surface surface) {
        int i;
        if (surface == null) {
            throw new IllegalArgumentException("other must not be null");
        }
        if (surface != this) {
            synchronized (surface.mLock) {
                i = surface.mNativeObject;
                surface.setNativeObjectLocked(0);
            }
            synchronized (this.mLock) {
                int i2 = this.mNativeObject;
                if (i2 != 0) {
                    nativeRelease(i2);
                }
                setNativeObjectLocked(i);
            }
        }
    }

    public void readFromParcel(Parcel parcel) {
        if (parcel == null) {
            throw new IllegalArgumentException("source must not be null");
        }
        synchronized (this.mLock) {
            this.mName = parcel.readString();
            setNativeObjectLocked(nativeReadFromParcel(this.mNativeObject, parcel));
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (parcel == null) {
            throw new IllegalArgumentException("dest must not be null");
        }
        synchronized (this.mLock) {
            parcel.writeString(this.mName);
            nativeWriteToParcel(this.mNativeObject, parcel);
        }
        if ((i & 1) != 0) {
            release();
        }
    }

    public String toString() {
        String str;
        synchronized (this.mLock) {
            str = "Surface(name=" + this.mName + ")/@0x" + Integer.toHexString(System.identityHashCode(this));
        }
        return str;
    }

    private void setNativeObjectLocked(int i) {
        int i2 = this.mNativeObject;
        if (i2 != i) {
            if (i2 == 0 && i != 0) {
                this.mCloseGuard.open("release");
            } else if (i2 != 0 && i == 0) {
                this.mCloseGuard.close();
            }
            this.mNativeObject = i;
            this.mGenerationId++;
        }
    }

    private void checkNotReleasedLocked() {
        if (this.mNativeObject == 0) {
            throw new IllegalStateException("Surface has already been released.");
        }
    }

    public static class OutOfResourcesException extends RuntimeException {
        public OutOfResourcesException() {
        }

        public OutOfResourcesException(String str) {
            super(str);
        }
    }

    public static String rotationToString(int i) {
        if (i == 0) {
            return "ROTATION_0";
        }
        if (i == 1) {
            return "ROATATION_90";
        }
        if (i == 2) {
            return "ROATATION_180";
        }
        if (i == 3) {
            return "ROATATION_270";
        }
        throw new IllegalArgumentException("Invalid rotation: " + i);
    }

    private final class CompatibleCanvas extends Canvas {
        private Matrix mOrigMatrix;

        private CompatibleCanvas() {
            this.mOrigMatrix = null;
        }

        @Override // android.graphics.Canvas
        public void setMatrix(Matrix matrix) {
            Matrix matrix2;
            if (Surface.this.mCompatibleMatrix == null || (matrix2 = this.mOrigMatrix) == null || matrix2.equals(matrix)) {
                super.setMatrix(matrix);
                return;
            }
            Matrix matrix3 = new Matrix(Surface.this.mCompatibleMatrix);
            matrix3.preConcat(matrix);
            super.setMatrix(matrix3);
        }

        @Override // android.graphics.Canvas
        public void getMatrix(Matrix matrix) {
            super.getMatrix(matrix);
            if (this.mOrigMatrix == null) {
                this.mOrigMatrix = new Matrix();
            }
            this.mOrigMatrix.set(matrix);
        }
    }

    public static int getDisplayParameter(int i, int i2, int i3, int i4) {
        return nativeGetDisplayParameter(i, i2, i3, i4);
    }

    public static int setDisplayParameter(int i, int i2, int i3, int i4, int i5) {
        return nativeSetDisplayParameter(i, i2, i3, i4, i5);
    }
}
