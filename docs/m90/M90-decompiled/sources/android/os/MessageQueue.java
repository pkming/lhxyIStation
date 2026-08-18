package android.os;

import android.util.AndroidRuntimeException;
import android.util.Log;
import android.util.Printer;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class MessageQueue {
    private boolean mBlocked;
    Message mMessages;
    private int mNextBarrierToken;
    private IdleHandler[] mPendingIdleHandlers;
    private final boolean mQuitAllowed;
    private boolean mQuitting;
    private final ArrayList<IdleHandler> mIdleHandlers = new ArrayList<>();
    private int mPtr = nativeInit();

    public interface IdleHandler {
        boolean queueIdle();
    }

    private static native void nativeDestroy(int i);

    private static native int nativeInit();

    private static native boolean nativeIsIdling(int i);

    private static native void nativePollOnce(int i, int i2);

    private static native void nativeWake(int i);

    public void addIdleHandler(IdleHandler idleHandler) {
        Objects.requireNonNull(idleHandler, "Can't add a null IdleHandler");
        synchronized (this) {
            this.mIdleHandlers.add(idleHandler);
        }
    }

    public void removeIdleHandler(IdleHandler idleHandler) {
        synchronized (this) {
            this.mIdleHandlers.remove(idleHandler);
        }
    }

    MessageQueue(boolean z) {
        this.mQuitAllowed = z;
    }

    protected void finalize() throws Throwable {
        try {
            dispose();
        } finally {
            super.finalize();
        }
    }

    private void dispose() {
        int i = this.mPtr;
        if (i != 0) {
            nativeDestroy(i);
            this.mPtr = 0;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x0093, code lost:
    
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0094, code lost:
    
        if (r2 >= r3) goto L85;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0096, code lost:
    
        r4 = r12.mPendingIdleHandlers;
        r5 = r4[r2];
        r4[r2] = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x009c, code lost:
    
        r4 = r5.queueIdle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00a1, code lost:
    
        r4 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00a2, code lost:
    
        android.util.Log.wtf("MessageQueue", "IdleHandler threw exception", r4);
        r4 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00ba, code lost:
    
        r2 = 0;
        r3 = 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    android.os.Message next() {
        /*
            r12 = this;
            r0 = -1
            r1 = 0
            r3 = r0
            r2 = r1
        L4:
            if (r2 == 0) goto L9
            android.os.Binder.flushPendingCommands()
        L9:
            int r4 = r12.mPtr
            nativePollOnce(r4, r2)
            monitor-enter(r12)
            long r4 = android.os.SystemClock.uptimeMillis()     // Catch: java.lang.Throwable -> Lbe
            android.os.Message r2 = r12.mMessages     // Catch: java.lang.Throwable -> Lbe
            r6 = 0
            if (r2 == 0) goto L2d
            android.os.Handler r7 = r2.target     // Catch: java.lang.Throwable -> Lbe
            if (r7 != 0) goto L2d
        L1c:
            android.os.Message r7 = r2.next     // Catch: java.lang.Throwable -> Lbe
            if (r7 == 0) goto L29
            boolean r8 = r7.isAsynchronous()     // Catch: java.lang.Throwable -> Lbe
            if (r8 == 0) goto L27
            goto L29
        L27:
            r2 = r7
            goto L1c
        L29:
            r11 = r7
            r7 = r2
            r2 = r11
            goto L2e
        L2d:
            r7 = r6
        L2e:
            if (r2 == 0) goto L56
            long r8 = r2.when     // Catch: java.lang.Throwable -> Lbe
            int r8 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r8 >= 0) goto L42
            long r7 = r2.when     // Catch: java.lang.Throwable -> Lbe
            long r7 = r7 - r4
            r9 = 2147483647(0x7fffffff, double:1.060997895E-314)
            long r7 = java.lang.Math.min(r7, r9)     // Catch: java.lang.Throwable -> Lbe
            int r2 = (int) r7     // Catch: java.lang.Throwable -> Lbe
            goto L57
        L42:
            r12.mBlocked = r1     // Catch: java.lang.Throwable -> Lbe
            if (r7 == 0) goto L4b
            android.os.Message r0 = r2.next     // Catch: java.lang.Throwable -> Lbe
            r7.next = r0     // Catch: java.lang.Throwable -> Lbe
            goto L4f
        L4b:
            android.os.Message r0 = r2.next     // Catch: java.lang.Throwable -> Lbe
            r12.mMessages = r0     // Catch: java.lang.Throwable -> Lbe
        L4f:
            r2.next = r6     // Catch: java.lang.Throwable -> Lbe
            r2.markInUse()     // Catch: java.lang.Throwable -> Lbe
            monitor-exit(r12)     // Catch: java.lang.Throwable -> Lbe
            return r2
        L56:
            r2 = r0
        L57:
            boolean r7 = r12.mQuitting     // Catch: java.lang.Throwable -> Lbe
            if (r7 == 0) goto L60
            r12.dispose()     // Catch: java.lang.Throwable -> Lbe
            monitor-exit(r12)     // Catch: java.lang.Throwable -> Lbe
            return r6
        L60:
            if (r3 >= 0) goto L72
            android.os.Message r7 = r12.mMessages     // Catch: java.lang.Throwable -> Lbe
            if (r7 == 0) goto L6c
            long r7 = r7.when     // Catch: java.lang.Throwable -> Lbe
            int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r4 >= 0) goto L72
        L6c:
            java.util.ArrayList<android.os.MessageQueue$IdleHandler> r3 = r12.mIdleHandlers     // Catch: java.lang.Throwable -> Lbe
            int r3 = r3.size()     // Catch: java.lang.Throwable -> Lbe
        L72:
            if (r3 > 0) goto L79
            r4 = 1
            r12.mBlocked = r4     // Catch: java.lang.Throwable -> Lbe
            monitor-exit(r12)     // Catch: java.lang.Throwable -> Lbe
            goto L4
        L79:
            android.os.MessageQueue$IdleHandler[] r2 = r12.mPendingIdleHandlers     // Catch: java.lang.Throwable -> Lbe
            if (r2 != 0) goto L86
            r2 = 4
            int r2 = java.lang.Math.max(r3, r2)     // Catch: java.lang.Throwable -> Lbe
            android.os.MessageQueue$IdleHandler[] r2 = new android.os.MessageQueue.IdleHandler[r2]     // Catch: java.lang.Throwable -> Lbe
            r12.mPendingIdleHandlers = r2     // Catch: java.lang.Throwable -> Lbe
        L86:
            java.util.ArrayList<android.os.MessageQueue$IdleHandler> r2 = r12.mIdleHandlers     // Catch: java.lang.Throwable -> Lbe
            android.os.MessageQueue$IdleHandler[] r4 = r12.mPendingIdleHandlers     // Catch: java.lang.Throwable -> Lbe
            java.lang.Object[] r2 = r2.toArray(r4)     // Catch: java.lang.Throwable -> Lbe
            android.os.MessageQueue$IdleHandler[] r2 = (android.os.MessageQueue.IdleHandler[]) r2     // Catch: java.lang.Throwable -> Lbe
            r12.mPendingIdleHandlers = r2     // Catch: java.lang.Throwable -> Lbe
            monitor-exit(r12)     // Catch: java.lang.Throwable -> Lbe
            r2 = r1
        L94:
            if (r2 >= r3) goto Lba
            android.os.MessageQueue$IdleHandler[] r4 = r12.mPendingIdleHandlers
            r5 = r4[r2]
            r4[r2] = r6
            boolean r4 = r5.queueIdle()     // Catch: java.lang.Throwable -> La1
            goto Laa
        La1:
            r4 = move-exception
            java.lang.String r7 = "MessageQueue"
            java.lang.String r8 = "IdleHandler threw exception"
            android.util.Log.wtf(r7, r8, r4)
            r4 = r1
        Laa:
            if (r4 != 0) goto Lb7
            monitor-enter(r12)
            java.util.ArrayList<android.os.MessageQueue$IdleHandler> r4 = r12.mIdleHandlers     // Catch: java.lang.Throwable -> Lb4
            r4.remove(r5)     // Catch: java.lang.Throwable -> Lb4
            monitor-exit(r12)     // Catch: java.lang.Throwable -> Lb4
            goto Lb7
        Lb4:
            r0 = move-exception
            monitor-exit(r12)     // Catch: java.lang.Throwable -> Lb4
            throw r0
        Lb7:
            int r2 = r2 + 1
            goto L94
        Lba:
            r2 = r1
            r3 = r2
            goto L4
        Lbe:
            r0 = move-exception
            monitor-exit(r12)     // Catch: java.lang.Throwable -> Lbe
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.MessageQueue.next():android.os.Message");
    }

    void quit(boolean z) {
        if (!this.mQuitAllowed) {
            throw new RuntimeException("Main thread not allowed to quit.");
        }
        synchronized (this) {
            if (this.mQuitting) {
                return;
            }
            this.mQuitting = true;
            if (z) {
                removeAllFutureMessagesLocked();
            } else {
                removeAllMessagesLocked();
            }
            nativeWake(this.mPtr);
        }
    }

    int enqueueSyncBarrier(long j) {
        int i;
        synchronized (this) {
            i = this.mNextBarrierToken;
            this.mNextBarrierToken = i + 1;
            Message messageObtain = Message.obtain();
            messageObtain.when = j;
            messageObtain.arg1 = i;
            Message message = null;
            Message message2 = this.mMessages;
            if (j != 0) {
                while (message2 != null && message2.when <= j) {
                    Message message3 = message2;
                    message2 = message2.next;
                    message = message3;
                }
            }
            if (message != null) {
                messageObtain.next = message2;
                message.next = messageObtain;
            } else {
                messageObtain.next = message2;
                this.mMessages = messageObtain;
            }
        }
        return i;
    }

    void removeSyncBarrier(int i) {
        Message message;
        synchronized (this) {
            Message message2 = null;
            Message message3 = this.mMessages;
            while (true) {
                Message message4 = message3;
                message = message2;
                message2 = message4;
                if (message2 == null || (message2.target == null && message2.arg1 == i)) {
                    break;
                } else {
                    message3 = message2.next;
                }
            }
            if (message2 == null) {
                throw new IllegalStateException("The specified message queue synchronization  barrier token has not been posted or has already been removed.");
            }
            boolean z = false;
            if (message != null) {
                message.next = message2.next;
            } else {
                Message message5 = message2.next;
                this.mMessages = message5;
                if (message5 == null || message5.target != null) {
                    z = true;
                }
            }
            message2.recycle();
            if (z && !this.mQuitting) {
                nativeWake(this.mPtr);
            }
        }
    }

    boolean enqueueMessage(Message message, long j) {
        boolean z;
        Message message2;
        if (message.isInUse()) {
            throw new AndroidRuntimeException(message + " This message is already in use.");
        }
        if (message.target == null) {
            throw new AndroidRuntimeException("Message must have a target.");
        }
        synchronized (this) {
            if (this.mQuitting) {
                RuntimeException runtimeException = new RuntimeException(message.target + " sending message to a Handler on a dead thread");
                Log.w("MessageQueue", runtimeException.getMessage(), runtimeException);
                return false;
            }
            message.when = j;
            Message message3 = this.mMessages;
            if (message3 == null || j == 0 || j < message3.when) {
                message.next = message3;
                this.mMessages = message;
                z = this.mBlocked;
            } else {
                z = this.mBlocked && message3.target == null && message.isAsynchronous();
                while (true) {
                    message2 = message3.next;
                    if (message2 == null || j < message2.when) {
                        break;
                    }
                    if (z && message2.isAsynchronous()) {
                        z = false;
                    }
                    message3 = message2;
                }
                message.next = message2;
                message3.next = message;
            }
            if (z) {
                nativeWake(this.mPtr);
            }
            return true;
        }
    }

    boolean hasMessages(Handler handler, int i, Object obj) {
        if (handler == null) {
            return false;
        }
        synchronized (this) {
            for (Message message = this.mMessages; message != null; message = message.next) {
                if (message.target == handler && message.what == i && (obj == null || message.obj == obj)) {
                    return true;
                }
            }
            return false;
        }
    }

    boolean hasMessages(Handler handler, Runnable runnable, Object obj) {
        if (handler == null) {
            return false;
        }
        synchronized (this) {
            for (Message message = this.mMessages; message != null; message = message.next) {
                if (message.target == handler && message.callback == runnable && (obj == null || message.obj == obj)) {
                    return true;
                }
            }
            return false;
        }
    }

    boolean isIdling() {
        boolean zIsIdlingLocked;
        synchronized (this) {
            zIsIdlingLocked = isIdlingLocked();
        }
        return zIsIdlingLocked;
    }

    private boolean isIdlingLocked() {
        return !this.mQuitting && nativeIsIdling(this.mPtr);
    }

    void removeMessages(Handler handler, int i, Object obj) {
        if (handler == null) {
            return;
        }
        synchronized (this) {
            Message message = this.mMessages;
            while (message != null && message.target == handler && message.what == i && (obj == null || message.obj == obj)) {
                Message message2 = message.next;
                this.mMessages = message2;
                message.recycle();
                message = message2;
            }
            while (message != null) {
                Message message3 = message.next;
                if (message3 != null && message3.target == handler && message3.what == i && (obj == null || message3.obj == obj)) {
                    Message message4 = message3.next;
                    message3.recycle();
                    message.next = message4;
                } else {
                    message = message3;
                }
            }
        }
    }

    void removeMessages(Handler handler, Runnable runnable, Object obj) {
        if (handler == null || runnable == null) {
            return;
        }
        synchronized (this) {
            Message message = this.mMessages;
            while (message != null && message.target == handler && message.callback == runnable && (obj == null || message.obj == obj)) {
                Message message2 = message.next;
                this.mMessages = message2;
                message.recycle();
                message = message2;
            }
            while (message != null) {
                Message message3 = message.next;
                if (message3 != null && message3.target == handler && message3.callback == runnable && (obj == null || message3.obj == obj)) {
                    Message message4 = message3.next;
                    message3.recycle();
                    message.next = message4;
                } else {
                    message = message3;
                }
            }
        }
    }

    void removeCallbacksAndMessages(Handler handler, Object obj) {
        if (handler == null) {
            return;
        }
        synchronized (this) {
            Message message = this.mMessages;
            while (message != null && message.target == handler && (obj == null || message.obj == obj)) {
                Message message2 = message.next;
                this.mMessages = message2;
                message.recycle();
                message = message2;
            }
            while (message != null) {
                Message message3 = message.next;
                if (message3 != null && message3.target == handler && (obj == null || message3.obj == obj)) {
                    Message message4 = message3.next;
                    message3.recycle();
                    message.next = message4;
                } else {
                    message = message3;
                }
            }
        }
    }

    private void removeAllMessagesLocked() {
        Message message = this.mMessages;
        while (message != null) {
            Message message2 = message.next;
            message.recycle();
            message = message2;
        }
        this.mMessages = null;
    }

    private void removeAllFutureMessagesLocked() {
        long jUptimeMillis = SystemClock.uptimeMillis();
        Message message = this.mMessages;
        if (message == null) {
            return;
        }
        if (message.when > jUptimeMillis) {
            removeAllMessagesLocked();
            return;
        }
        while (true) {
            Message message2 = message.next;
            if (message2 == null) {
                return;
            }
            if (message2.when > jUptimeMillis) {
                message.next = null;
                while (true) {
                    Message message3 = message2.next;
                    message2.recycle();
                    if (message3 == null) {
                        return;
                    } else {
                        message2 = message3;
                    }
                }
            } else {
                message = message2;
            }
        }
    }

    void dump(Printer printer, String str) {
        synchronized (this) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            int i = 0;
            for (Message message = this.mMessages; message != null; message = message.next) {
                printer.println(str + "Message " + i + ": " + message.toString(jUptimeMillis));
                i++;
            }
            printer.println(str + "(Total messages: " + i + ", idling=" + isIdlingLocked() + ", quitting=" + this.mQuitting + ")");
        }
    }
}
