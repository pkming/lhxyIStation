package android.net.http;

import android.os.SystemClock;

/* JADX INFO: loaded from: classes.dex */
class Timer {
    private long mLast;
    private long mStart;

    public Timer() {
        long jUptimeMillis = SystemClock.uptimeMillis();
        this.mLast = jUptimeMillis;
        this.mStart = jUptimeMillis;
    }

    public void mark(String str) {
        this.mLast = SystemClock.uptimeMillis();
    }
}
