package android.text.format;

import android.content.Context;
import android.net.NetworkUtils;

/* JADX INFO: loaded from: classes.dex */
public final class Formatter {
    public static String formatFileSize(Context context, long j) {
        return formatFileSize(context, j, false);
    }

    public static String formatShortFileSize(Context context, long j) {
        return formatFileSize(context, j, true);
    }

    private static String formatFileSize(Context context, long j, boolean z) {
        if (context == null) {
            return "";
        }
        float f = j;
        int i = 17039434;
        if (f > 900.0f) {
            i = 17039435;
            f /= 1024.0f;
        }
        if (f > 900.0f) {
            i = 17039436;
            f /= 1024.0f;
        }
        if (f > 900.0f) {
            i = 17039437;
            f /= 1024.0f;
        }
        if (f > 900.0f) {
            i = 17039438;
            f /= 1024.0f;
        }
        if (f > 900.0f) {
            i = 17039439;
            f /= 1024.0f;
        }
        String str = f < 1.0f ? String.format("%.2f", Float.valueOf(f)) : f < 10.0f ? z ? String.format("%.1f", Float.valueOf(f)) : String.format("%.2f", Float.valueOf(f)) : (f >= 100.0f || z) ? String.format("%.0f", Float.valueOf(f)) : String.format("%.2f", Float.valueOf(f));
        return context.getResources().getString(17039440, str, context.getString(i));
    }

    @Deprecated
    public static String formatIpAddress(int i) {
        return NetworkUtils.intToInetAddress(i).getHostAddress();
    }
}
