package android.net.http;

import android.os.SystemClock;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
class HttpLog {
    private static final boolean DEBUG = false;
    private static final String LOGTAG = "http";
    static final boolean LOGV = false;

    HttpLog() {
    }

    static void v(String str) {
        Log.v(LOGTAG, SystemClock.uptimeMillis() + " " + Thread.currentThread().getName() + " " + str);
    }

    static void e(String str) {
        Log.e(LOGTAG, str);
    }
}
