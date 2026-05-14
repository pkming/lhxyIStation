package com.lhxy.istationdevice.android11.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AppLogCenter {
    private static final int MAX_ENTRIES = 5000;
    private static final int MAX_SESSION_FILES = 50;
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
            pruneSessionFilesLocked();
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

    public static String dumpSummary() {
        List<AppLogEntry> entries = loadSessionEntries();
        if (entries.isEmpty()) {
            return "log summary:\n- 当前没有日志条目";
        }

        Map<String, Integer> levelCounts = new LinkedHashMap<>();
        Map<String, Integer> categoryCounts = new LinkedHashMap<>();
        Map<String, Integer> tagCounts = new LinkedHashMap<>();
        int errorCount = 0;
        long firstTimestamp = Long.MAX_VALUE;
        long lastTimestamp = 0L;
        for (AppLogEntry entry : entries) {
            increment(levelCounts, entry.getLevel().name());
            increment(categoryCounts, entry.getCategory().name());
            increment(tagCounts, entry.getTag());
            if (entry.getLevel() == LogLevel.ERROR || entry.getCategory() == LogCategory.ERROR) {
                errorCount++;
            }
            firstTimestamp = Math.min(firstTimestamp, entry.getTimestamp());
            lastTimestamp = Math.max(lastTimestamp, entry.getTimestamp());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("log summary:")
                .append("\n- sessionId=").append(currentSessionId)
                .append("\n- currentBuffer=").append(ENTRIES.size()).append('/').append(MAX_ENTRIES)
                .append("\n- sessionEntries=").append(entries.size())
                .append("\n- errorEntries=").append(errorCount)
                .append("\n- firstEntry=").append(formatTimestamp(firstTimestamp))
                .append("\n- lastEntry=").append(formatTimestamp(lastTimestamp))
                .append("\n- durationMs=").append(Math.max(0L, lastTimestamp - firstTimestamp))
                .append("\n- levelCounts=").append(describeCounts(levelCounts))
                .append("\n- categoryCounts=").append(describeCounts(categoryCounts))
                .append("\n- topTags=").append(describeTopCounts(tagCounts, 8));
        return builder.toString();
    }

    public static String dumpRecentErrors(int maxCount) {
        List<AppLogEntry> entries = loadSessionEntries();
        List<AppLogEntry> errors = new ArrayList<>();
        for (int index = entries.size() - 1; index >= 0 && errors.size() < Math.max(1, maxCount); index--) {
            AppLogEntry entry = entries.get(index);
            if (entry.getLevel() == LogLevel.ERROR || entry.getCategory() == LogCategory.ERROR) {
                errors.add(entry);
            }
        }
        Collections.reverse(errors);
        if (errors.isEmpty()) {
            return "recent errors:\n- 当前会话没有 ERROR 级别日志";
        }
        return buildPlainText(errors);
    }

    public static String describeRecentSessions(int maxCount) {
        List<File> sessionFiles = listSessionFiles(maxCount);
        if (sessionFiles.isEmpty()) {
            return "recent sessions:\n- 当前没有历史日志文件";
        }
        StringBuilder builder = new StringBuilder("recent sessions:");
        for (File file : sessionFiles) {
            builder.append("\n- ")
                    .append(file.getName())
                    .append(" / size=")
                    .append(file.length())
                    .append(" / modified=")
                    .append(formatTimestamp(file.lastModified()));
        }
        return builder.toString();
    }

    public static List<File> listSessionFiles(int maxCount) {
        synchronized (FILE_LOCK) {
            if (sessionLogDir == null || !sessionLogDir.exists()) {
                return Collections.emptyList();
            }
            File[] files = sessionLogDir.listFiles((dir, name) -> name != null && name.endsWith(".log"));
            if (files == null || files.length == 0) {
                return Collections.emptyList();
            }
            List<File> result = new ArrayList<>();
            Collections.addAll(result, files);
            result.sort(Comparator.comparingLong(File::lastModified).reversed());
            if (maxCount > 0 && result.size() > maxCount) {
                return new ArrayList<>(result.subList(0, maxCount));
            }
            return result;
        }
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
        pruneSessionFilesLocked();
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

    private static void pruneSessionFilesLocked() {
        if (sessionLogDir == null || !sessionLogDir.exists()) {
            return;
        }
        File[] files = sessionLogDir.listFiles((dir, name) -> name != null && name.endsWith(".log"));
        if (files == null || files.length <= MAX_SESSION_FILES) {
            return;
        }
        List<File> sessionFiles = new ArrayList<>();
        Collections.addAll(sessionFiles, files);
        sessionFiles.sort(Comparator.comparingLong(File::lastModified).reversed());
        for (int index = MAX_SESSION_FILES; index < sessionFiles.size(); index++) {
            File file = sessionFiles.get(index);
            if (file.equals(currentSessionFile)) {
                continue;
            }
            if (!file.delete()) {
                Log.w("AppLogCenter", "delete old session log failed: " + file.getAbsolutePath());
            }
        }
    }

    private static void increment(Map<String, Integer> counts, String key) {
        String safeKey = TextUtils.isEmpty(key) ? "-" : key;
        Integer current = counts.get(safeKey);
        counts.put(safeKey, current == null ? 1 : current + 1);
    }

    private static String describeCounts(Map<String, Integer> counts) {
        if (counts.isEmpty()) {
            return "-";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return builder.toString();
    }

    private static String describeTopCounts(Map<String, Integer> counts, int limit) {
        if (counts.isEmpty()) {
            return "-";
        }
        List<Map.Entry<String, Integer>> items = new ArrayList<>(counts.entrySet());
        items.sort((left, right) -> Integer.compare(right.getValue(), left.getValue()));
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < items.size() && index < limit; index++) {
            Map.Entry<String, Integer> entry = items.get(index);
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return builder.toString();
    }

    private static String formatTimestamp(long timestamp) {
        if (timestamp <= 0L) {
            return "-";
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault())
                .format(new Date(timestamp));
    }
}
