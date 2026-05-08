package com.lhxy.istationdevice.android11.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class AppLogEntry {
    private final long timestamp;
    private final LogCategory category;
    private final LogLevel level;
    private final String tag;
    private final String message;
    private final String traceId;

    public AppLogEntry(long timestamp, LogCategory category, LogLevel level, String tag, String message, String traceId) {
        this.timestamp = timestamp;
        this.category = category;
        this.level = level;
        this.tag = tag;
        this.message = message;
        this.traceId = traceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LogCategory getCategory() {
        return category;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }

    public String getTraceId() {
        return traceId;
    }

    public String toDisplayLine() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        String time = format.format(new Date(timestamp));
        return "[" + time + "]"
            + "[" + tag + "]"
            + "[" + traceId + "]"
            + "[" + level.name() + "]"
            + "[" + category.name() + "] "
            + message;
    }

    public String toStorageLine() {
        return timestamp
                + "\t" + level.name()
                + "\t" + category.name()
                + "\t" + sanitize(traceId)
                + "\t" + sanitize(tag)
                + "\t" + sanitize(message);
    }

    public static AppLogEntry fromStorageLine(String line) {
        if (line == null || line.trim().isEmpty() || line.startsWith("#")) {
            return null;
        }
        String[] parts = line.split("\\t", 6);
        if (parts.length < 6) {
            return null;
        }
        try {
            return new AppLogEntry(
                    Long.parseLong(parts[0]),
                    LogCategory.valueOf(parts[2]),
                    LogLevel.valueOf(parts[1]),
                    restore(parts[4]),
                    restore(parts[5]),
                    restore(parts[3])
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String restore(String value) {
        if (value == null) {
            return "";
        }
        String restored = value.replace("\\r", "\r")
                .replace("\\n", "\n")
                .replace("\\t", "\t");
        return restored.replace("\\\\", "\\");
    }
}

