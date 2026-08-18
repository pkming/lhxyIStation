package android.util;

import android.os.SystemClock;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes.dex */
public abstract class TimedRemoteCaller<T> {
    public static final long DEFAULT_CALL_TIMEOUT_MILLIS = 5000;
    private static final int UNDEFINED_SEQUENCE = -1;
    private final long mCallTimeoutMillis;
    private T mResult;
    private int mSequenceCounter;
    private final Object mLock = new Object();
    private int mReceivedSequence = -1;
    private int mAwaitedSequence = -1;

    public TimedRemoteCaller(long j) {
        this.mCallTimeoutMillis = j;
    }

    public final int onBeforeRemoteCall() {
        int i;
        synchronized (this.mLock) {
            i = this.mSequenceCounter;
            this.mSequenceCounter = i + 1;
            this.mAwaitedSequence = i;
        }
        return i;
    }

    public final T getResultTimed(int i) throws TimeoutException {
        T t;
        synchronized (this.mLock) {
            if (!waitForResultTimedLocked(i)) {
                throw new TimeoutException("No reponse for sequence: " + i);
            }
            t = this.mResult;
            this.mResult = null;
        }
        return t;
    }

    public final void onRemoteMethodResult(T t, int i) {
        synchronized (this.mLock) {
            if (i == this.mAwaitedSequence) {
                this.mReceivedSequence = i;
                this.mResult = t;
                this.mLock.notifyAll();
            }
        }
    }

    private boolean waitForResultTimedLocked(int i) {
        long jUptimeMillis;
        long jUptimeMillis2 = SystemClock.uptimeMillis();
        while (this.mReceivedSequence != i) {
            try {
                jUptimeMillis = this.mCallTimeoutMillis - (SystemClock.uptimeMillis() - jUptimeMillis2);
            } catch (InterruptedException unused) {
            }
            if (jUptimeMillis <= 0) {
                return false;
            }
            this.mLock.wait(jUptimeMillis);
        }
        return true;
    }
}
