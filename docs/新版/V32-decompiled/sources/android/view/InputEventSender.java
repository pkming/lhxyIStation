package android.view;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public abstract class InputEventSender {
    private static final String TAG = "InputEventSender";
    private final CloseGuard mCloseGuard;
    private InputChannel mInputChannel;
    private MessageQueue mMessageQueue;
    private int mSenderPtr;

    private static native void nativeDispose(int i);

    private static native int nativeInit(WeakReference<InputEventSender> weakReference, InputChannel inputChannel, MessageQueue messageQueue);

    private static native boolean nativeSendKeyEvent(int i, int i2, KeyEvent keyEvent);

    private static native boolean nativeSendMotionEvent(int i, int i2, MotionEvent motionEvent);

    public void onInputEventFinished(int i, boolean z) {
    }

    public InputEventSender(InputChannel inputChannel, Looper looper) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        if (inputChannel == null) {
            throw new IllegalArgumentException("inputChannel must not be null");
        }
        if (looper == null) {
            throw new IllegalArgumentException("looper must not be null");
        }
        this.mInputChannel = inputChannel;
        this.mMessageQueue = looper.getQueue();
        this.mSenderPtr = nativeInit(new WeakReference(this), inputChannel, this.mMessageQueue);
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
        int i = this.mSenderPtr;
        if (i != 0) {
            nativeDispose(i);
            this.mSenderPtr = 0;
        }
        this.mInputChannel = null;
        this.mMessageQueue = null;
    }

    public final boolean sendInputEvent(int i, InputEvent inputEvent) {
        if (inputEvent == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        int i2 = this.mSenderPtr;
        if (i2 == 0) {
            Log.w(TAG, "Attempted to send an input event but the input event sender has already been disposed.");
            return false;
        }
        if (inputEvent instanceof KeyEvent) {
            return nativeSendKeyEvent(i2, i, (KeyEvent) inputEvent);
        }
        return nativeSendMotionEvent(i2, i, (MotionEvent) inputEvent);
    }

    private void dispatchInputEventFinished(int i, boolean z) {
        onInputEventFinished(i, z);
    }
}
