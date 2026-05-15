package com.lhxy.istationdevice.android11.domain.upgrade;

import android.content.Context;
import android.content.SharedPreferences;

public final class TinkerHotUpdateStateStore {
    public static final String PREFS_NAME = "tinker_hot_update";
    public static final String KEY_LAST_PATCH_VERSION = "last_patch_version";
    public static final String KEY_LAST_PATCH_MD5 = "last_patch_md5";
    public static final String KEY_PENDING_PATCH_VERSION = "pending_patch_version";
    public static final String KEY_PENDING_PATCH_MD5 = "pending_patch_md5";
    public static final String KEY_PENDING_STARTED_AT = "pending_started_at";
    public static final String KEY_PENDING_TRACE_ID = "pending_trace_id";
    public static final String KEY_PROGRESS_PERCENT = "progress_percent";

    private TinkerHotUpdateStateStore() {
    }

    public static String getLastPatchVersion(Context context) {
        return prefs(context).getString(KEY_LAST_PATCH_VERSION, "");
    }

    public static String getLastPatchMd5(Context context) {
        return prefs(context).getString(KEY_LAST_PATCH_MD5, "");
    }

    public static String getPendingPatchVersion(Context context) {
        return prefs(context).getString(KEY_PENDING_PATCH_VERSION, "");
    }

    public static String getPendingPatchMd5(Context context) {
        return prefs(context).getString(KEY_PENDING_PATCH_MD5, "");
    }

    public static long getPendingStartedAt(Context context) {
        return prefs(context).getLong(KEY_PENDING_STARTED_AT, 0L);
    }

    public static String getPendingTraceId(Context context) {
        return prefs(context).getString(KEY_PENDING_TRACE_ID, "");
    }

    public static int getProgressPercent(Context context) {
        return clampPercent(prefs(context).getInt(KEY_PROGRESS_PERCENT, 0));
    }

    public static boolean isProcessing(Context context) {
        return getProgressPercent(context) > 0
                || !getPendingPatchVersion(context).isEmpty()
                || !getPendingPatchMd5(context).isEmpty();
    }

    public static void startProgress(Context context) {
        startProgress(context, "");
    }

    public static void startProgress(Context context, String traceId) {
        SharedPreferences preferences = prefs(context);
        SharedPreferences.Editor editor = preferences.edit()
                .putInt(KEY_PROGRESS_PERCENT, 5);
        if (preferences.getLong(KEY_PENDING_STARTED_AT, 0L) <= 0L) {
            editor.putLong(KEY_PENDING_STARTED_AT, System.currentTimeMillis());
        }
        if (!safe(traceId).isEmpty()) {
            editor.putString(KEY_PENDING_TRACE_ID, safe(traceId));
        }
        editor.apply();
    }

    public static void setProgressPercent(Context context, int progressPercent) {
        SharedPreferences preferences = prefs(context);
        SharedPreferences.Editor editor = preferences.edit()
                .putInt(KEY_PROGRESS_PERCENT, clampPercent(progressPercent));
        if (preferences.getLong(KEY_PENDING_STARTED_AT, 0L) <= 0L) {
            editor.putLong(KEY_PENDING_STARTED_AT, System.currentTimeMillis());
        }
        editor.apply();
    }

    public static void markPending(Context context, String patchVersion, String patchMd5) {
        commitNow(prefs(context).edit()
                .putString(KEY_PENDING_PATCH_VERSION, safe(patchVersion))
                .putString(KEY_PENDING_PATCH_MD5, safe(patchMd5))
                .putLong(KEY_PENDING_STARTED_AT, System.currentTimeMillis())
                .putInt(KEY_PROGRESS_PERCENT, 99)
        );
    }

    public static void markApplied(Context context, String patchVersion, String patchMd5) {
        commitNow(prefs(context).edit()
                .putString(KEY_LAST_PATCH_VERSION, safe(patchVersion))
                .putString(KEY_LAST_PATCH_MD5, safe(patchMd5))
                .remove(KEY_PENDING_PATCH_VERSION)
                .remove(KEY_PENDING_PATCH_MD5)
                .remove(KEY_PENDING_STARTED_AT)
                .remove(KEY_PENDING_TRACE_ID)
                .remove(KEY_PROGRESS_PERCENT)
        );
    }

    public static void clearPending(Context context) {
        commitNow(prefs(context).edit()
                .remove(KEY_PENDING_PATCH_VERSION)
                .remove(KEY_PENDING_PATCH_MD5)
                .remove(KEY_PENDING_STARTED_AT)
                .remove(KEY_PENDING_TRACE_ID)
                .remove(KEY_PROGRESS_PERCENT)
        );
    }

    public static SharedPreferences.OnSharedPreferenceChangeListener registerListener(
            Context context,
            Runnable listener
    ) {
        SharedPreferences.OnSharedPreferenceChangeListener prefsListener = (sharedPreferences, key) -> {
            if (KEY_PENDING_PATCH_VERSION.equals(key)
                    || KEY_PENDING_PATCH_MD5.equals(key)
                    || KEY_PENDING_STARTED_AT.equals(key)
                    || KEY_PENDING_TRACE_ID.equals(key)
                    || KEY_PROGRESS_PERCENT.equals(key)) {
                listener.run();
            }
        };
        prefs(context).registerOnSharedPreferenceChangeListener(prefsListener);
        return prefsListener;
    }

    public static void unregisterListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (listener == null) {
            return;
        }
        prefs(context).unregisterOnSharedPreferenceChangeListener(listener);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static void commitNow(SharedPreferences.Editor editor) {
        editor.commit();
    }

    private static int clampPercent(int value) {
        if (value <= 0) {
            return 0;
        }
        return Math.min(value, 99);
    }
}