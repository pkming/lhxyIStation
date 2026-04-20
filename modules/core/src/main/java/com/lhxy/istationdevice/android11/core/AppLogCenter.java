package com.lhxy.istationdevice.android11.core;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AppLogCenter {
    private static final int MAX_ENTRIES = 500;
    private static final List<AppLogEntry> ENTRIES = new CopyOnWriteArrayList<>();

    private AppLogCenter() {
    }

    public static void log(LogCategory category, LogLevel level, String tag, String message, String traceId) {
        String safeTraceId = TextUtils.isEmpty(traceId) ? "NO_TRACE" : traceId;
        String safeMessage = message == null ? "" : message;
        AppLogEntry entry = new AppLogEntry(
                System.currentTimeMillis(),
                category,
                level,
                tag == null ? "NO_TAG" : tag,
                safeMessage,
                safeTraceId
        );
        ENTRIES.add(entry);
        trimIfNeeded();
        Log.println(
                level.getAndroidPriority(),
                tag == null ? "NO_TAG" : tag,
                "[" + category.name() + "][" + safeTraceId + "] " + safeMessage
        );
    }

    public static List<AppLogEntry> snapshot() {
        return new ArrayList<>(ENTRIES);
    }

    public static String dumpPlainText() {
        StringBuilder builder = new StringBuilder();
        for (AppLogEntry entry : snapshot()) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(entry.toDisplayLine());
        }
        return builder.toString();
    }

    public static void clear() {
        ENTRIES.clear();
    }

    private static void trimIfNeeded() {
        while (ENTRIES.size() > MAX_ENTRIES) {
            ENTRIES.remove(0);
        }
    }
}
