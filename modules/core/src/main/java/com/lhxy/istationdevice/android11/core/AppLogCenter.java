package com.lhxy.istationdevice.android11.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AppLogCenter {
    private static final int MAX_ENTRIES = 5000;
    private static final List<AppLogEntry> ENTRIES = new CopyOnWriteArrayList<>();
    private static final Object FILE_LOCK = new Object();
    private static File sessionLogDir;
    private static File currentSessionFile;
    private static String currentSessionId = "uninitialized";

    private AppLogCenter() {
    }

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        synchronized (FILE_LOCK) {
            if (sessionLogDir == null) {
                File baseDir = context.getExternalFilesDir("logs");
                if (baseDir == null) {
                    baseDir = new File(context.getFilesDir(), "logs");
                }
                if (!baseDir.exists() && !baseDir.mkdirs()) {
                    throw new IllegalStateException("无法创建日志目录: " + baseDir.getAbsolutePath());
                }
                sessionLogDir = baseDir;
            }
            if (currentSessionFile == null) {
                rotateSessionLocked();
            }
        }
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
            appendToSessionFile(entry);
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
        return buildPlainText(snapshot());
    }

    public static String dumpByTag() {
        return dumpGroupedByKey(loadSessionEntries(), true);
    }

    public static String dumpByTraceId() {
        return dumpGroupedByKey(loadSessionEntries(), false);
    }

    public static String describeSession() {
        synchronized (FILE_LOCK) {
            String logPath = currentSessionFile == null ? "-" : currentSessionFile.getAbsolutePath();
            return "logSession=" + currentSessionId
                    + "\n- entryBuffer=" + ENTRIES.size() + "/" + MAX_ENTRIES
                    + "\n- sessionFile=" + logPath;
        }
    }

    public static File getCurrentSessionFile() {
        synchronized (FILE_LOCK) {
            return currentSessionFile;
        }
    }

    public static String dumpSessionFileText() {
        List<AppLogEntry> entries = loadSessionEntries();
        return entries.isEmpty() ? dumpPlainText() : buildPlainText(entries);
    }

    private static String buildPlainText(List<AppLogEntry> entries) {
        StringBuilder builder = new StringBuilder();
        for (AppLogEntry entry : entries) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(entry.toDisplayLine());
        }
        return builder.toString();
    }

    public static void clear() {
        ENTRIES.clear();
        synchronized (FILE_LOCK) {
            if (sessionLogDir != null) {
                rotateSessionLocked();
            }
        }
    }

    private static void trimIfNeeded() {
        while (ENTRIES.size() > MAX_ENTRIES) {
            ENTRIES.remove(0);
        }
    }

    private static void appendToSessionFile(AppLogEntry entry) {
        synchronized (FILE_LOCK) {
            if (currentSessionFile == null) {
                return;
            }
            try (FileOutputStream outputStream = new FileOutputStream(currentSessionFile, true)) {
                outputStream.write(entry.toStorageLine().getBytes(StandardCharsets.UTF_8));
                outputStream.write('\n');
                outputStream.flush();
            } catch (Exception e) {
                Log.w("AppLogCenter", "append session log failed: " + e.getMessage());
            }
        }
    }

    private static void rotateSessionLocked() {
        currentSessionId = TraceIds.next("log-session");
        currentSessionFile = new File(sessionLogDir, currentSessionId + ".log");
        try (FileOutputStream outputStream = new FileOutputStream(currentSessionFile, false)) {
            outputStream.write(("# session=" + currentSessionId + "\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            throw new IllegalStateException("无法初始化日志会话文件: " + currentSessionFile.getAbsolutePath(), e);
        }
    }

    private static String dumpGroupedByKey(List<AppLogEntry> entries, boolean groupByTag) {
        Map<String, List<AppLogEntry>> grouped = new LinkedHashMap<>();
        for (AppLogEntry entry : entries) {
            String rawKey = groupByTag ? entry.getTag() : entry.getTraceId();
            String key = TextUtils.isEmpty(rawKey) ? (groupByTag ? "NO_TAG" : "NO_TRACE") : rawKey;
            List<AppLogEntry> bucket = grouped.get(key);
            if (bucket == null) {
                bucket = new ArrayList<>();
                grouped.put(key, bucket);
            }
            bucket.add(entry);
        }
        String title = groupByTag ? "module" : "trace";
        StringBuilder builder = new StringBuilder();
        builder.append("# grouped-by-").append(title).append("\n");
        for (Map.Entry<String, List<AppLogEntry>> item : grouped.entrySet()) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append("## ").append(title).append('=').append(item.getKey())
                    .append(" entries=").append(item.getValue().size()).append('\n');
            for (AppLogEntry entry : item.getValue()) {
                builder.append(entry.toDisplayLine()).append('\n');
            }
        }
        return builder.toString().trim();
    }

    private static List<AppLogEntry> loadSessionEntries() {
        synchronized (FILE_LOCK) {
            if (currentSessionFile == null || !currentSessionFile.exists()) {
                return snapshot();
            }
            try {
                List<String> lines = Files.readAllLines(currentSessionFile.toPath(), StandardCharsets.UTF_8);
                List<AppLogEntry> result = new ArrayList<>();
                for (String line : lines) {
                    AppLogEntry entry = AppLogEntry.fromStorageLine(line);
                    if (entry != null) {
                        result.add(entry);
                    }
                }
                return result;
            } catch (Exception e) {
                return snapshot();
            }
        }
    }
}
