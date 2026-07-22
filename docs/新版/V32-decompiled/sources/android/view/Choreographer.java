package android.view;

import android.hardware.display.DisplayManagerGlobal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.util.TimeUtils;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public final class Choreographer {
    public static final int CALLBACK_ANIMATION = 1;
    public static final int CALLBACK_INPUT = 0;
    private static final int CALLBACK_LAST = 2;
    public static final int CALLBACK_TRAVERSAL = 2;
    private static final boolean DEBUG = false;
    private static final long DEFAULT_FRAME_DELAY = 10;
    private static final int MSG_DO_FRAME = 0;
    private static final int MSG_DO_SCHEDULE_CALLBACK = 2;
    private static final int MSG_DO_SCHEDULE_VSYNC = 1;
    private static final long NANOS_PER_MS = 1000000;
    private static final String TAG = "Choreographer";
    private static volatile long sFrameDelay = 10;
    private CallbackRecord mCallbackPool;
    private final CallbackQueue[] mCallbackQueues;
    private boolean mCallbacksRunning;
    private final FrameDisplayEventReceiver mDisplayEventReceiver;
    private long mFrameIntervalNanos;
    private boolean mFrameScheduled;
    private final FrameHandler mHandler;
    private long mLastFrameTimeNanos;
    private final Object mLock;
    private final Looper mLooper;
    private static final ThreadLocal<Choreographer> sThreadInstance = new ThreadLocal<Choreographer>() { // from class: android.view.Choreographer.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Choreographer initialValue() {
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper == null) {
                throw new IllegalStateException("The current thread must have a looper!");
            }
            return new Choreographer(looperMyLooper);
        }
    };
    private static final boolean USE_VSYNC = SystemProperties.getBoolean("debug.choreographer.vsync", true);
    private static final boolean USE_FRAME_TIME = SystemProperties.getBoolean("debug.choreographer.frametime", true);
    private static final int SKIPPED_FRAME_WARNING_LIMIT = SystemProperties.getInt("debug.choreographer.skipwarning", 30);
    private static final Object FRAME_CALLBACK_TOKEN = new Object() { // from class: android.view.Choreographer.2
        public String toString() {
            return "FRAME_CALLBACK_TOKEN";
        }
    };

    public interface FrameCallback {
        void doFrame(long j);
    }

    private Choreographer(Looper looper) {
        this.mLock = new Object();
        this.mLooper = looper;
        this.mHandler = new FrameHandler(looper);
        this.mDisplayEventReceiver = USE_VSYNC ? new FrameDisplayEventReceiver(looper) : null;
        this.mLastFrameTimeNanos = Long.MIN_VALUE;
        this.mFrameIntervalNanos = (long) (1.0E9f / getRefreshRate());
        this.mCallbackQueues = new CallbackQueue[3];
        for (int i = 0; i <= 2; i++) {
            this.mCallbackQueues[i] = new CallbackQueue();
        }
    }

    private static float getRefreshRate() {
        return DisplayManagerGlobal.getInstance().getDisplayInfo(0).refreshRate;
    }

    public static Choreographer getInstance() {
        return sThreadInstance.get();
    }

    public static long getFrameDelay() {
        return sFrameDelay;
    }

    public static void setFrameDelay(long j) {
        sFrameDelay = j;
    }

    public static long subtractFrameDelay(long j) {
        long j2 = sFrameDelay;
        if (j <= j2) {
            return 0L;
        }
        return j - j2;
    }

    void dump(String str, PrintWriter printWriter) {
        String str2 = str + "  ";
        printWriter.print(str);
        printWriter.println("Choreographer:");
        printWriter.print(str2);
        printWriter.print("mFrameScheduled=");
        printWriter.println(this.mFrameScheduled);
        printWriter.print(str2);
        printWriter.print("mLastFrameTime=");
        printWriter.println(TimeUtils.formatUptime(this.mLastFrameTimeNanos / NANOS_PER_MS));
    }

    public void postCallback(int i, Runnable runnable, Object obj) {
        postCallbackDelayed(i, runnable, obj, 0L);
    }

    public void postCallbackDelayed(int i, Runnable runnable, Object obj, long j) {
        if (runnable == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("callbackType is invalid");
        }
        postCallbackDelayedInternal(i, runnable, obj, j);
    }

    private void postCallbackDelayedInternal(int i, Object obj, Object obj2, long j) {
        synchronized (this.mLock) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            long j2 = j + jUptimeMillis;
            this.mCallbackQueues[i].addCallbackLocked(j2, obj, obj2);
            if (j2 <= jUptimeMillis) {
                scheduleFrameLocked(jUptimeMillis);
            } else {
                Message messageObtainMessage = this.mHandler.obtainMessage(2, obj);
                messageObtainMessage.arg1 = i;
                messageObtainMessage.setAsynchronous(true);
                this.mHandler.sendMessageAtTime(messageObtainMessage, j2);
            }
        }
    }

    public void removeCallbacks(int i, Runnable runnable, Object obj) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("callbackType is invalid");
        }
        removeCallbacksInternal(i, runnable, obj);
    }

    private void removeCallbacksInternal(int i, Object obj, Object obj2) {
        synchronized (this.mLock) {
            this.mCallbackQueues[i].removeCallbacksLocked(obj, obj2);
            if (obj != null && obj2 == null) {
                this.mHandler.removeMessages(2, obj);
            }
        }
    }

    public void postFrameCallback(FrameCallback frameCallback) {
        postFrameCallbackDelayed(frameCallback, 0L);
    }

    public void postFrameCallbackDelayed(FrameCallback frameCallback, long j) {
        if (frameCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        postCallbackDelayedInternal(1, frameCallback, FRAME_CALLBACK_TOKEN, j);
    }

    public void removeFrameCallback(FrameCallback frameCallback) {
        if (frameCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        removeCallbacksInternal(1, frameCallback, FRAME_CALLBACK_TOKEN);
    }

    public long getFrameTime() {
        return getFrameTimeNanos() / NANOS_PER_MS;
    }

    public long getFrameTimeNanos() {
        long jNanoTime;
        synchronized (this.mLock) {
            if (!this.mCallbacksRunning) {
                throw new IllegalStateException("This method must only be called as part of a callback while a frame is in progress.");
            }
            jNanoTime = USE_FRAME_TIME ? this.mLastFrameTimeNanos : System.nanoTime();
        }
        return jNanoTime;
    }

    private void scheduleFrameLocked(long j) {
        if (this.mFrameScheduled) {
            return;
        }
        this.mFrameScheduled = true;
        if (USE_VSYNC) {
            if (isRunningOnLooperThreadLocked()) {
                scheduleVsyncLocked();
                return;
            }
            Message messageObtainMessage = this.mHandler.obtainMessage(1);
            messageObtainMessage.setAsynchronous(true);
            this.mHandler.sendMessageAtFrontOfQueue(messageObtainMessage);
            return;
        }
        long jMax = Math.max((this.mLastFrameTimeNanos / NANOS_PER_MS) + sFrameDelay, j);
        Message messageObtainMessage2 = this.mHandler.obtainMessage(0);
        messageObtainMessage2.setAsynchronous(true);
        this.mHandler.sendMessageAtTime(messageObtainMessage2, jMax);
    }

    void doFrame(long j, int i) {
        synchronized (this.mLock) {
            if (this.mFrameScheduled) {
                long jNanoTime = System.nanoTime();
                long j2 = jNanoTime - j;
                long j3 = this.mFrameIntervalNanos;
                if (j2 >= j3) {
                    long j4 = j2 / j3;
                    if (j4 >= SKIPPED_FRAME_WARNING_LIMIT) {
                        Log.i(TAG, "Skipped " + j4 + " frames!  The application may be doing too much work on its main thread.");
                    }
                    j = jNanoTime - (j2 % this.mFrameIntervalNanos);
                }
                if (j < this.mLastFrameTimeNanos) {
                    scheduleVsyncLocked();
                    return;
                }
                this.mFrameScheduled = false;
                this.mLastFrameTimeNanos = j;
                doCallbacks(0, j);
                doCallbacks(1, j);
                doCallbacks(2, j);
            }
        }
    }

    void doCallbacks(int i, long j) {
        synchronized (this.mLock) {
            CallbackRecord callbackRecordExtractDueCallbacksLocked = this.mCallbackQueues[i].extractDueCallbacksLocked(SystemClock.uptimeMillis());
            if (callbackRecordExtractDueCallbacksLocked == null) {
                return;
            }
            this.mCallbacksRunning = true;
            for (CallbackRecord callbackRecord = callbackRecordExtractDueCallbacksLocked; callbackRecord != null; callbackRecord = callbackRecord.next) {
                try {
                    callbackRecord.run(j);
                } catch (Throwable th) {
                    synchronized (this.mLock) {
                        this.mCallbacksRunning = false;
                        while (true) {
                            CallbackRecord callbackRecord2 = callbackRecordExtractDueCallbacksLocked.next;
                            recycleCallbackLocked(callbackRecordExtractDueCallbacksLocked);
                            if (callbackRecord2 == null) {
                                throw th;
                            }
                            callbackRecordExtractDueCallbacksLocked = callbackRecord2;
                        }
                    }
                }
            }
            synchronized (this.mLock) {
                this.mCallbacksRunning = false;
                while (true) {
                    CallbackRecord callbackRecord3 = callbackRecordExtractDueCallbacksLocked.next;
                    recycleCallbackLocked(callbackRecordExtractDueCallbacksLocked);
                    if (callbackRecord3 != null) {
                        callbackRecordExtractDueCallbacksLocked = callbackRecord3;
                    }
                }
            }
        }
    }

    void doScheduleVsync() {
        synchronized (this.mLock) {
            if (this.mFrameScheduled) {
                scheduleVsyncLocked();
            }
        }
    }

    void doScheduleCallback(int i) {
        synchronized (this.mLock) {
            if (!this.mFrameScheduled) {
                long jUptimeMillis = SystemClock.uptimeMillis();
                if (this.mCallbackQueues[i].hasDueCallbacksLocked(jUptimeMillis)) {
                    scheduleFrameLocked(jUptimeMillis);
                }
            }
        }
    }

    private void scheduleVsyncLocked() {
        this.mDisplayEventReceiver.scheduleVsync();
    }

    private boolean isRunningOnLooperThreadLocked() {
        return Looper.myLooper() == this.mLooper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CallbackRecord obtainCallbackLocked(long j, Object obj, Object obj2) {
        CallbackRecord callbackRecord = this.mCallbackPool;
        if (callbackRecord == null) {
            callbackRecord = new CallbackRecord();
        } else {
            this.mCallbackPool = callbackRecord.next;
            callbackRecord.next = null;
        }
        callbackRecord.dueTime = j;
        callbackRecord.action = obj;
        callbackRecord.token = obj2;
        return callbackRecord;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recycleCallbackLocked(CallbackRecord callbackRecord) {
        callbackRecord.action = null;
        callbackRecord.token = null;
        callbackRecord.next = this.mCallbackPool;
        this.mCallbackPool = callbackRecord;
    }

    private final class FrameHandler extends Handler {
        public FrameHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                Choreographer.this.doFrame(System.nanoTime(), 0);
            } else if (i == 1) {
                Choreographer.this.doScheduleVsync();
            } else {
                if (i != 2) {
                    return;
                }
                Choreographer.this.doScheduleCallback(message.arg1);
            }
        }
    }

    private final class FrameDisplayEventReceiver extends DisplayEventReceiver implements Runnable {
        private int mFrame;
        private boolean mHavePendingVsync;
        private long mTimestampNanos;

        public FrameDisplayEventReceiver(Looper looper) {
            super(looper);
        }

        @Override // android.view.DisplayEventReceiver
        public void onVsync(long j, int i, int i2) {
            if (i != 0) {
                Log.d(Choreographer.TAG, "Received vsync from secondary display, but we don't support this case yet.  Choreographer needs a way to explicitly request vsync for a specific display to ensure it doesn't lose track of its scheduled vsync.");
                scheduleVsync();
                return;
            }
            long jNanoTime = System.nanoTime();
            if (j > jNanoTime) {
                Log.w(Choreographer.TAG, "Frame time is " + ((j - jNanoTime) * 1.0E-6f) + " ms in the future!  Check that graphics HAL is generating vsync timestamps using the correct timebase.");
                j = jNanoTime;
            }
            if (this.mHavePendingVsync) {
                Log.w(Choreographer.TAG, "Already have a pending vsync event.  There should only be one at a time.");
            } else {
                this.mHavePendingVsync = true;
            }
            this.mTimestampNanos = j;
            this.mFrame = i2;
            Message messageObtain = Message.obtain(Choreographer.this.mHandler, this);
            messageObtain.setAsynchronous(true);
            Choreographer.this.mHandler.sendMessageAtTime(messageObtain, j / Choreographer.NANOS_PER_MS);
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mHavePendingVsync = false;
            Choreographer.this.doFrame(this.mTimestampNanos, this.mFrame);
        }
    }

    private static final class CallbackRecord {
        public Object action;
        public long dueTime;
        public CallbackRecord next;
        public Object token;

        private CallbackRecord() {
        }

        public void run(long j) {
            if (this.token == Choreographer.FRAME_CALLBACK_TOKEN) {
                ((FrameCallback) this.action).doFrame(j);
            } else {
                ((Runnable) this.action).run();
            }
        }
    }

    private final class CallbackQueue {
        private CallbackRecord mHead;

        private CallbackQueue() {
        }

        public boolean hasDueCallbacksLocked(long j) {
            CallbackRecord callbackRecord = this.mHead;
            return callbackRecord != null && callbackRecord.dueTime <= j;
        }

        public CallbackRecord extractDueCallbacksLocked(long j) {
            CallbackRecord callbackRecord = this.mHead;
            if (callbackRecord == null || callbackRecord.dueTime > j) {
                return null;
            }
            CallbackRecord callbackRecord2 = callbackRecord.next;
            CallbackRecord callbackRecord3 = callbackRecord;
            while (true) {
                if (callbackRecord2 == null) {
                    break;
                }
                if (callbackRecord2.dueTime > j) {
                    callbackRecord3.next = null;
                    break;
                }
                callbackRecord3 = callbackRecord2;
                callbackRecord2 = callbackRecord2.next;
            }
            this.mHead = callbackRecord2;
            return callbackRecord;
        }

        public void addCallbackLocked(long j, Object obj, Object obj2) {
            CallbackRecord callbackRecordObtainCallbackLocked = Choreographer.this.obtainCallbackLocked(j, obj, obj2);
            CallbackRecord callbackRecord = this.mHead;
            if (callbackRecord == null) {
                this.mHead = callbackRecordObtainCallbackLocked;
                return;
            }
            if (j < callbackRecord.dueTime) {
                callbackRecordObtainCallbackLocked.next = callbackRecord;
                this.mHead = callbackRecordObtainCallbackLocked;
                return;
            }
            while (true) {
                if (callbackRecord.next == null) {
                    break;
                }
                if (j < callbackRecord.next.dueTime) {
                    callbackRecordObtainCallbackLocked.next = callbackRecord.next;
                    break;
                }
                callbackRecord = callbackRecord.next;
            }
            callbackRecord.next = callbackRecordObtainCallbackLocked;
        }

        public void removeCallbacksLocked(Object obj, Object obj2) {
            CallbackRecord callbackRecord = this.mHead;
            CallbackRecord callbackRecord2 = null;
            while (callbackRecord != null) {
                CallbackRecord callbackRecord3 = callbackRecord.next;
                if ((obj == null || callbackRecord.action == obj) && (obj2 == null || callbackRecord.token == obj2)) {
                    if (callbackRecord2 != null) {
                        callbackRecord2.next = callbackRecord3;
                    } else {
                        this.mHead = callbackRecord3;
                    }
                    Choreographer.this.recycleCallbackLocked(callbackRecord);
                } else {
                    callbackRecord2 = callbackRecord;
                }
                callbackRecord = callbackRecord3;
            }
        }
    }
}
