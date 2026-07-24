package com.lhxy.istationdevice.android11.app.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.lhxy.istationdevice.android11.app.ShellApplication;

/**
 * 旧菜单登录会话。
 */
public final class LegacyAuthSession {
    public static final long SESSION_TIMEOUT_MS = 60_000L;
    private static final String PREFS_NAME = "legacy_auth_session";
    private static final String KEY_LAST_ACTIVE_AT = "last_active_at";
    private static final String KEY_USER_PASSWORD = "user_password";

    private LegacyAuthSession() {
    }

    public static void markAuthenticated(@NonNull Context context, boolean isUserPassword) {
        ShellApplication.isUserPassword = isUserPassword;
        prefs(context)
                .edit()
                .putLong(KEY_LAST_ACTIVE_AT, System.currentTimeMillis())
                .putBoolean(KEY_USER_PASSWORD, isUserPassword)
                .apply();
    }

    public static boolean isValid(@NonNull Context context) {
        SharedPreferences preferences = prefs(context);
        long lastActiveAt = preferences.getLong(KEY_LAST_ACTIVE_AT, 0L);
        if (lastActiveAt <= 0L) {
            return false;
        }
        long idleMs = System.currentTimeMillis() - lastActiveAt;
        if (idleMs < 0L || idleMs > SESSION_TIMEOUT_MS) {
            clear(context);
            return false;
        }
        ShellApplication.isUserPassword = preferences.getBoolean(KEY_USER_PASSWORD, false);
        return true;
    }

    public static void touch(@NonNull Context context) {
        if (!isValid(context)) {
            return;
        }
        prefs(context)
                .edit()
                .putLong(KEY_LAST_ACTIVE_AT, System.currentTimeMillis())
                .apply();
    }

    public static long remainingMs(@NonNull Context context) {
        long lastActiveAt = prefs(context).getLong(KEY_LAST_ACTIVE_AT, 0L);
        if (lastActiveAt <= 0L) {
            return 0L;
        }
        long remainingMs = SESSION_TIMEOUT_MS - (System.currentTimeMillis() - lastActiveAt);
        return Math.max(0L, remainingMs);
    }

    public static void clear(@NonNull Context context) {
        prefs(context).edit().clear().apply();
        ShellApplication.isUserPassword = false;
    }

    private static SharedPreferences prefs(@NonNull Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
