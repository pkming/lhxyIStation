package android.os;

/* JADX INFO: loaded from: classes.dex */
public final class SystemClock {
    public static native long currentThreadTimeMicro();

    public static native long currentThreadTimeMillis();

    public static native long currentTimeMicro();

    public static native long elapsedRealtime();

    public static native long elapsedRealtimeNanos();

    public static native boolean setCurrentTimeMillis(long j);

    public static native long uptimeMillis();

    private SystemClock() {
    }

    public static void sleep(long j) {
        long jUptimeMillis = uptimeMillis();
        boolean z = false;
        long jUptimeMillis2 = j;
        do {
            try {
                Thread.sleep(jUptimeMillis2);
            } catch (InterruptedException unused) {
                z = true;
            }
            jUptimeMillis2 = (jUptimeMillis + j) - uptimeMillis();
        } while (jUptimeMillis2 > 0);
        if (z) {
            Thread.currentThread().interrupt();
        }
    }
}
