package android.util;

/* JADX INFO: loaded from: classes.dex */
public final class Slog {
    private Slog() {
    }

    public static int v(String str, String str2) {
        return Log.println_native(3, 2, str, str2);
    }

    public static int v(String str, String str2, Throwable th) {
        return Log.println_native(3, 2, str, str2 + '\n' + Log.getStackTraceString(th));
    }

    public static int d(String str, String str2) {
        return Log.println_native(3, 3, str, str2);
    }

    public static int d(String str, String str2, Throwable th) {
        return Log.println_native(3, 3, str, str2 + '\n' + Log.getStackTraceString(th));
    }

    public static int i(String str, String str2) {
        return Log.println_native(3, 4, str, str2);
    }

    public static int i(String str, String str2, Throwable th) {
        return Log.println_native(3, 4, str, str2 + '\n' + Log.getStackTraceString(th));
    }

    public static int w(String str, String str2) {
        return Log.println_native(3, 5, str, str2);
    }

    public static int w(String str, String str2, Throwable th) {
        return Log.println_native(3, 5, str, str2 + '\n' + Log.getStackTraceString(th));
    }

    public static int w(String str, Throwable th) {
        return Log.println_native(3, 5, str, Log.getStackTraceString(th));
    }

    public static int e(String str, String str2) {
        return Log.println_native(3, 6, str, str2);
    }

    public static int e(String str, String str2, Throwable th) {
        return Log.println_native(3, 6, str, str2 + '\n' + Log.getStackTraceString(th));
    }

    public static int wtf(String str, String str2) {
        return Log.wtf(3, str, str2, null, false);
    }

    public static int wtfStack(String str, String str2) {
        return Log.wtf(3, str, str2, null, true);
    }

    public static int wtf(String str, Throwable th) {
        return Log.wtf(3, str, th.getMessage(), th, false);
    }

    public static int wtf(String str, String str2, Throwable th) {
        return Log.wtf(3, str, str2, th, false);
    }

    public static int println(int i, String str, String str2) {
        return Log.println_native(3, i, str, str2);
    }
}
