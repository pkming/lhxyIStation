package android.view;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Pools;
import android.util.SparseArray;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public final class InputQueue {
    private final SparseArray<ActiveInputEvent> mActiveEventArray = new SparseArray<>(20);
    private final Pools.Pool<ActiveInputEvent> mActiveInputEventPool = new Pools.SimplePool(20);
    private final CloseGuard mCloseGuard;
    private int mPtr;

    public interface Callback {
        void onInputQueueCreated(InputQueue inputQueue);

        void onInputQueueDestroyed(InputQueue inputQueue);
    }

    public interface FinishedInputEventCallback {
        void onFinishedInputEvent(Object obj, boolean z);
    }

    private static native void nativeDispose(int i);

    private static native int nativeInit(WeakReference<InputQueue> weakReference, MessageQueue messageQueue);

    private static native int nativeSendKeyEvent(int i, KeyEvent keyEvent, boolean z);

    private static native int nativeSendMotionEvent(int i, MotionEvent motionEvent);

    public InputQueue() {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mPtr = nativeInit(new WeakReference(this), Looper.myQueue());
        closeGuard.open("dispose");
    }

    protected void finalize() throws Throwable {
        try {
            dispose(true);
        } finally {
            super.finalize();
        }
    }

    public void dispose() {
        dispose(false);
    }

    public void dispose(boolean z) {
        CloseGuard closeGuard = this.mCloseGuard;
        if (closeGuard != null) {
            if (z) {
                closeGuard.warnIfOpen();
            }
            this.mCloseGuard.close();
        }
        int i = this.mPtr;
        if (i != 0) {
            nativeDispose(i);
            this.mPtr = 0;
        }
    }

    public int getNativePtr() {
        return this.mPtr;
    }

    public void sendInputEvent(InputEvent inputEvent, Object obj, boolean z, FinishedInputEventCallback finishedInputEventCallback) {
        int iNativeSendMotionEvent;
        ActiveInputEvent activeInputEventObtainActiveInputEvent = obtainActiveInputEvent(obj, finishedInputEventCallback);
        if (inputEvent instanceof KeyEvent) {
            iNativeSendMotionEvent = nativeSendKeyEvent(this.mPtr, (KeyEvent) inputEvent, z);
        } else {
            iNativeSendMotionEvent = nativeSendMotionEvent(this.mPtr, (MotionEvent) inputEvent);
        }
        this.mActiveEventArray.put(iNativeSendMotionEvent, activeInputEventObtainActiveInputEvent);
    }

    private void finishInputEvent(int i, boolean z) {
        int iIndexOfKey = this.mActiveEventArray.indexOfKey(i);
        if (iIndexOfKey >= 0) {
            ActiveInputEvent activeInputEventValueAt = this.mActiveEventArray.valueAt(iIndexOfKey);
            this.mActiveEventArray.removeAt(iIndexOfKey);
            activeInputEventValueAt.mCallback.onFinishedInputEvent(activeInputEventValueAt.mToken, z);
            recycleActiveInputEvent(activeInputEventValueAt);
        }
    }

    private ActiveInputEvent obtainActiveInputEvent(Object obj, FinishedInputEventCallback finishedInputEventCallback) {
        ActiveInputEvent activeInputEventAcquire = this.mActiveInputEventPool.acquire();
        if (activeInputEventAcquire == null) {
            activeInputEventAcquire = new ActiveInputEvent();
        }
        activeInputEventAcquire.mToken = obj;
        activeInputEventAcquire.mCallback = finishedInputEventCallback;
        return activeInputEventAcquire;
    }

    private void recycleActiveInputEvent(ActiveInputEvent activeInputEvent) {
        activeInputEvent.recycle();
        this.mActiveInputEventPool.release(activeInputEvent);
    }

    private final class ActiveInputEvent {
        public FinishedInputEventCallback mCallback;
        public Object mToken;

        private ActiveInputEvent() {
        }

        public void recycle() {
            this.mToken = null;
            this.mCallback = null;
        }
    }
}
