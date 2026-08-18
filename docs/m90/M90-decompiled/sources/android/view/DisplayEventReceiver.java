package android.view;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import dalvik.system.CloseGuard;

/* JADX INFO: loaded from: classes.dex */
public abstract class DisplayEventReceiver {
    private static final String TAG = "DisplayEventReceiver";
    private final CloseGuard mCloseGuard;
    private MessageQueue mMessageQueue;
    private int mReceiverPtr;

    private static native void nativeDispose(int i);

    private static native int nativeInit(DisplayEventReceiver displayEventReceiver, MessageQueue messageQueue);

    private static native void nativeScheduleVsync(int i);

    public void onHotplug(long j, int i, boolean z) {
    }

    public void onVsync(long j, int i, int i2) {
    }

    public DisplayEventReceiver(Looper looper) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        if (looper == null) {
            throw new IllegalArgumentException("looper must not be null");
        }
        MessageQueue queue = looper.getQueue();
        this.mMessageQueue = queue;
        this.mReceiverPtr = nativeInit(this, queue);
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

    private void dispose(boolean z) {
        CloseGuard closeGuard = this.mCloseGuard;
        if (closeGuard != null) {
            if (z) {
                closeGuard.warnIfOpen();
            }
            this.mCloseGuard.close();
        }
        int i = this.mReceiverPtr;
        if (i != 0) {
            nativeDispose(i);
            this.mReceiverPtr = 0;
        }
        this.mMessageQueue = null;
    }

    public void scheduleVsync() {
        int i = this.mReceiverPtr;
        if (i == 0) {
            Log.w(TAG, "Attempted to schedule a vertical sync pulse but the display event receiver has already been disposed.");
        } else {
            nativeScheduleVsync(i);
        }
    }

    private void dispatchVsync(long j, int i, int i2) {
        onVsync(j, i, i2);
    }

    private void dispatchHotplug(long j, int i, boolean z) {
        onHotplug(j, i, z);
    }
}
