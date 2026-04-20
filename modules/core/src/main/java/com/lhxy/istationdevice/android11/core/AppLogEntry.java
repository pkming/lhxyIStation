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

    public String toDisplayLine() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        String time = format.format(new Date(timestamp));
        return time
                + " [" + level.name() + "]"
                + " [" + category.name() + "]"
                + " [" + traceId + "] "
                + tag
                + " - "
                + message;
    }
}

