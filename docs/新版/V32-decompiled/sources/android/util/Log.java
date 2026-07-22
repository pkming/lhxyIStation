package android.util;

import com.android.internal.os.RuntimeInit;
import com.android.internal.util.FastPrintWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class Log {
    public static final int ASSERT = 7;
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static final int LOG_ID_EVENTS = 2;
    public static final int LOG_ID_MAIN = 0;
    public static final int LOG_ID_RADIO = 1;
    public static final int LOG_ID_SYSTEM = 3;
    public static final int VERBOSE = 2;
    public static final int WARN = 5;
    private static TerribleFailureHandler sWtfHandler = new TerribleFailureHandler() { // from class: android.util.Log.1
        @Override // android.util.Log.TerribleFailureHandler
        public void onTerribleFailure(String str, TerribleFailure terribleFailure) {
            RuntimeInit.wtf(str, terribleFailure);
        }
    };

    public interface TerribleFailureHandler {
        void onTerribleFailure(String str, TerribleFailure terribleFailure);
    }

    public static native boolean isLoggable(String str, int i);

    public static native int println_native(int i, int i2, String str, String str2);

    private static class TerribleFailure extends Exception {
        TerribleFailure(String str, Throwable th) {
            super(str, th);
        }
    }

    private Log() {
    }

    public static int v(String str, String str2) {
        return println_native(0, 2, str, str2);
    }

    public static int v(String str, String str2, Throwable th) {
        return println_native(0, 2, str, str2 + '\n' + getStackTraceString(th));
    }

    public static int d(String str, String str2) {
        return println_native(0, 3, str, str2);
    }

    public static int d(String str, String str2, Throwable th) {
        return println_native(0, 3, str, str2 + '\n' + getStackTraceString(th));
    }

    public static int i(String str, String str2) {
        return println_native(0, 4, str, str2);
    }

    public static int i(String str, String str2, Throwable th) {
        return println_native(0, 4, str, str2 + '\n' + getStackTraceString(th));
    }

    public static int w(String str, String str2) {
        return println_native(0, 5, str, str2);
    }

    public static int w(String str, String str2, Throwable th) {
        return println_native(0, 5, str, str2 + '\n' + getStackTraceString(th));
    }

    public static int w(String str, Throwable th) {
        return println_native(0, 5, str, getStackTraceString(th));
    }

    public static int e(String str, String str2) {
        return println_native(0, 6, str, str2);
    }

    public static int e(String str, String str2, Throwable th) {
        return println_native(0, 6, str, str2 + '\n' + getStackTraceString(th));
    }

    public static int wtf(String str, String str2) {
        return wtf(0, str, str2, null, false);
    }

    public static int wtfStack(String str, String str2) {
        return wtf(0, str, str2, null, true);
    }

    public static int wtf(String str, Throwable th) {
        return wtf(0, str, th.getMessage(), th, false);
    }

    public static int wtf(String str, String str2, Throwable th) {
        return wtf(0, str, str2, th, false);
    }

    static int wtf(int i, String str, String str2, Throwable th, boolean z) {
        TerribleFailure terribleFailure = new TerribleFailure(str2, th);
        StringBuilder sbAppend = new StringBuilder().append(str2).append('\n');
        if (z) {
            th = terribleFailure;
        }
        int iPrintln_native = println_native(i, 7, str, sbAppend.append(getStackTraceString(th)).toString());
        sWtfHandler.onTerribleFailure(str, terribleFailure);
        return iPrintln_native;
    }

    public static TerribleFailureHandler setWtfHandler(TerribleFailureHandler terribleFailureHandler) {
        Objects.requireNonNull(terribleFailureHandler, "handler == null");
        TerribleFailureHandler terribleFailureHandler2 = sWtfHandler;
        sWtfHandler = terribleFailureHandler;
        return terribleFailureHandler2;
    }

    public static String getStackTraceString(Throwable th) {
        if (th == null) {
            return "";
        }
        for (Throwable cause = th; cause != null; cause = cause.getCause()) {
            if (cause instanceof UnknownHostException) {
                return "";
            }
        }
        StringWriter stringWriter = new StringWriter();
        FastPrintWriter fastPrintWriter = new FastPrintWriter(stringWriter, false, 256);
        th.printStackTrace((PrintWriter) fastPrintWriter);
        fastPrintWriter.flush();
        return stringWriter.toString();
    }

    public static int println(int i, String str, String str2) {
        return println_native(0, i, str, str2);
    }
}
