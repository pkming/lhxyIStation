package com.lhxy.istationdevice.android11.core;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public final class TraceIds {
    private static final AtomicInteger SEQUENCE = new AtomicInteger(1);

    private TraceIds() {
    }

    public static String next(String prefix) {
        String safePrefix = prefix == null || prefix.trim().isEmpty() ? "trace" : prefix.trim();
        return String.format(Locale.US, "%s-%d-%04d", safePrefix, System.currentTimeMillis(), SEQUENCE.getAndIncrement());
    }
}

