package android.graphics;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class SurfaceTexture {
    private int mBufferQueue;
    private EventHandler mEventHandler;
    private int mFrameAvailableListener;
    private OnFrameAvailableListener mOnFrameAvailableListener;
    private int mSurfaceTexture;

    public interface OnFrameAvailableListener {
        void onFrameAvailable(SurfaceTexture surfaceTexture);
    }

    private native int nativeAttachToGLContext(int i);

    private static native void nativeClassInit();

    private native int nativeDetachFromGLContext();

    private native void nativeFinalize();

    private native int nativeGetQueuedCount();

    private native long nativeGetTimestamp();

    private native void nativeGetTransformMatrix(float[] fArr);

    private native void nativeInit(int i, boolean z, Object obj) throws Surface.OutOfResourcesException;

    private native void nativeRelease();

    private native void nativeReleaseTexImage();

    private native void nativeSetDefaultBufferSize(int i, int i2);

    private native void nativeUpdateTexImage();

    @Deprecated
    public static class OutOfResourcesException extends Exception {
        public OutOfResourcesException() {
        }

        public OutOfResourcesException(String str) {
            super(str);
        }
    }

    public SurfaceTexture(int i) {
        init(i, false);
    }

    public SurfaceTexture(int i, boolean z) {
        init(i, z);
    }

    public void setOnFrameAvailableListener(OnFrameAvailableListener onFrameAvailableListener) {
        this.mOnFrameAvailableListener = onFrameAvailableListener;
    }

    public void setDefaultBufferSize(int i, int i2) {
        nativeSetDefaultBufferSize(i, i2);
    }

    public void updateTexImage() {
        nativeUpdateTexImage();
    }

    public void releaseTexImage() {
        nativeReleaseTexImage();
    }

    public void detachFromGLContext() {
        if (nativeDetachFromGLContext() != 0) {
            throw new RuntimeException("Error during detachFromGLContext (see logcat for details)");
        }
    }

    public void attachToGLContext(int i) {
        if (nativeAttachToGLContext(i) != 0) {
            throw new RuntimeException("Error during attachToGLContext (see logcat for details)");
        }
    }

    public void getTransformMatrix(float[] fArr) {
        if (fArr.length != 16) {
            throw new IllegalArgumentException();
        }
        nativeGetTransformMatrix(fArr);
    }

    public long getTimestamp() {
        return nativeGetTimestamp();
    }

    public void release() {
        nativeRelease();
    }

    protected void finalize() throws Throwable {
        try {
            nativeFinalize();
        } finally {
            super.finalize();
        }
    }

    private class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (SurfaceTexture.this.mOnFrameAvailableListener != null) {
                SurfaceTexture.this.mOnFrameAvailableListener.onFrameAvailable(SurfaceTexture.this);
            }
        }
    }

    private static void postEventFromNative(Object obj) {
        EventHandler eventHandler;
        SurfaceTexture surfaceTexture = (SurfaceTexture) ((WeakReference) obj).get();
        if (surfaceTexture == null || (eventHandler = surfaceTexture.mEventHandler) == null) {
            return;
        }
        surfaceTexture.mEventHandler.sendMessage(eventHandler.obtainMessage());
    }

    private void init(int i, boolean z) throws Surface.OutOfResourcesException {
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper != null) {
            this.mEventHandler = new EventHandler(looperMyLooper);
        } else {
            Looper mainLooper = Looper.getMainLooper();
            if (mainLooper != null) {
                this.mEventHandler = new EventHandler(mainLooper);
            } else {
                this.mEventHandler = null;
            }
        }
        nativeInit(i, z, new WeakReference(this));
    }

    static {
        nativeClassInit();
    }
}
